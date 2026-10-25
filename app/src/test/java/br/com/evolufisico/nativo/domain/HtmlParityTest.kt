package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.Evaluation
import org.junit.Assert.*
import org.junit.Test

class HtmlParityTest {
    private fun history(vararg dates:String)=dates.map{Evaluation(studentId="same",date=it)}
    @Test fun nineHtmlModulesInVisibleOrder(){assertEquals(listOf("Início","Organização","Avaliações","Evolução","Calculadora","Formulários","Análises","Cobranças","Configurações"),HtmlParity.navigation.map{HtmlParity.modules[it]})}
    @Test fun damagedLayoutDropsForeignCardsAndDuplicates(){assertEquals(listOf("tarefas","agenda","calendario","retornos"),HtmlParity.panelOrder("treinos,tarefas,tarefas,notas"))}
    @Test fun movingPanelPreservesIdentityAndContents(){val order=HtmlParity.move(HtmlParity.panels,"tarefas",-1);assertEquals(listOf("agenda","tarefas","calendario","retornos"),order)}
    @Test fun panelMovesAtBoundariesAreSafe(){assertEquals(HtmlParity.panels,HtmlParity.move(HtmlParity.panels,"agenda",-1));assertEquals(HtmlParity.panels,HtmlParity.move(HtmlParity.panels,"retornos",1))}
    @Test fun unknownPanelCannotEnterLayout(){assertEquals(HtmlParity.panels,HtmlParity.move(HtmlParity.panels,"treinos",1))}
    @Test fun threeDatesAtExactly21DaysUnlock(){assertTrue(HtmlParity.eligible(history("2026-10-01","2026-10-10","2026-10-22")))}
    @Test fun shortTimelineStaysBlocked(){assertFalse(HtmlParity.eligible(history("2026-10-01","2026-10-10","2026-10-21")))}
    @Test fun duplicateDateDoesNotCountTwice(){assertFalse(HtmlParity.eligible(history("2026-10-01","2026-10-01","2026-10-22")))}
    @Test fun twoVisitsDoNotUnlock(){assertFalse(HtmlParity.eligible(history("2026-01-01","2026-10-22")))}
    @Test fun differentClientsCannotUnlock(){assertFalse(HtmlParity.eligible(history("2026-10-01","2026-10-10","2026-10-22").mapIndexed{i,e->e.copy(studentId="client$i")}))}
    @Test fun malformedDatesCannotCount(){assertFalse(HtmlParity.eligible(history("bad","2026-10-01","2026-10-22")))}
    @Test fun fourDatesRemainEligible(){assertTrue(HtmlParity.eligible(history("2026-10-01","2026-10-08","2026-10-15","2026-10-22")))}
    @Test fun individualBackupExcludesFinancialAndRetiredModules(){assertTrue(HtmlParity.individualBackupKind("evaluation"));assertTrue(HtmlParity.individualBackupKind("note"));assertTrue(HtmlParity.individualBackupKind("organizationGoal"));listOf("charge","training","habit","exercise").forEach{assertFalse(HtmlParity.individualBackupKind(it))}}
    @Test fun alphabeticalSortNormalizesAccents(){assertEquals(listOf("Álvaro","Bruno","Érica"),listOf("Érica","Bruno","Álvaro").sortedBy(HtmlParity::sortName))}
}
