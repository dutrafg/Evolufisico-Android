package br.com.evolufisico.android

/** Critérios para distribuição beta que preserve dados de usuários reais.
 * Sem comprovação documentada, cada critério permanece pendente.
 */
data class BetaEvidence(
    val stableSigningKeyVerified:Boolean=false,
    val encryptedLocalData:Boolean=false,
    val protectedPortableBackups:Boolean=false,
    val testedRestoreRoundTrip:Boolean=false,
    val testedInPlaceUpgrade:Boolean=false,
    val physicalAndroidTest:Boolean=false,
    val physicalTabletTest:Boolean=false,
    val accessibilityReview:Boolean=false,
    val securityAudit:Boolean=false,
    val successfulCi:Boolean=false
)
data class BetaCriterion(val code:String,val title:String,val passed:Boolean)
object BetaReadiness {
    fun criteria(e:BetaEvidence):List<BetaCriterion> = listOf(
        BetaCriterion("signing","Assinatura permanente verificada",e.stableSigningKeyVerified),
        BetaCriterion("local","Dados locais protegidos",e.encryptedLocalData),
        BetaCriterion("backup","Backup portátil protegido",e.protectedPortableBackups),
        BetaCriterion("restore","Restauração testada ponta a ponta",e.testedRestoreRoundTrip),
        BetaCriterion("update","Atualização sobre a instalação anterior",e.testedInPlaceUpgrade),
        BetaCriterion("phone","Teste em Android físico",e.physicalAndroidTest),
        BetaCriterion("tablet","Teste em tablet físico",e.physicalTabletTest),
        BetaCriterion("accessibility","Acessibilidade revisada",e.accessibilityReview),
        BetaCriterion("security","Revisão de segurança concluída",e.securityAudit),
        BetaCriterion("ci","CI do candidato aprovado",e.successfulCi)
    )
    fun pending(e:BetaEvidence):List<BetaCriterion> = criteria(e).filterNot { it.passed }
    fun canRelease(e:BetaEvidence):Boolean = pending(e).isEmpty()
}
