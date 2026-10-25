package br.com.evolufisico.nativo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.com.evolufisico.nativo.data.AppRepository
import br.com.evolufisico.nativo.domain.WorkspaceSearch

@Composable internal fun WorkspaceSearchDialog(repo:AppRepository,onDismiss:()->Unit,onOpen:(WorkspaceSearch.Entry)->Unit) {
    var query by remember{mutableStateOf("")}
    var category by remember{mutableStateOf("Tudo")}
    val evaluations=repo.evaluations.toList();val appointments=repo.appointments.toList();val tasks=repo.tasks.toList();val notes=repo.notes.toList();val goals=repo.organizationGoals.toList()
    val entries=remember(evaluations,appointments,tasks,notes,goals){buildList {
        listOf("Início" to 0,"Organização" to 8,"Avaliações" to 1,"Evolução" to 2,"Calculadora" to 3,"Formulários" to 4,"Análises" to 5,"Cobranças" to 6,"Configurações" to 7).forEach{(name,index)->add(WorkspaceSearch.Entry("module-$index","Módulos",name,"Abrir módulo",index))}
        listOf("Agenda e calendário" to "agenda","Notas" to "notes","Tarefas" to "tasks","Metas" to "goals").forEach{(name,section)->add(WorkspaceSearch.Entry("org-$section","Módulos",name,"Organização",8,section=section))}
        listOf("Aparência, temas e efeitos visuais" to "Personalização","Dados profissionais" to "Geral","Backup e restauração" to "Dados e privacidade","Armazenamento e segurança" to "Dados e privacidade","Notificações e lembretes" to "Notificações","Manutenção e diagnóstico" to "Manutenção").forEachIndexed{i,(name,section)->add(WorkspaceSearch.Entry("settings-$i","Configurações",name,section,7,section=section,keywords="paleta cores 3d blur sombras avançada essencial liquid glass som vibração avisos"))}
        evaluations.forEach{e->add(WorkspaceSearch.Entry("evaluation-${e.id}","Avaliações",e.name,e.date,1,client=e.studentId))}
        br.com.evolufisico.nativo.domain.WorkspaceRules.latest(evaluations).forEach{e->add(WorkspaceSearch.Entry("client-${e.studentId}","Clientes",e.name,"ID ${e.studentId.take(6)} · ${e.email}",1,client=e.studentId,keywords=e.phone))}
        appointments.forEach{a->add(WorkspaceSearch.Entry("agenda-${a.id}","Agenda",a.type+" · "+a.student,"${a.date} ${a.time} · ${a.student}",8,section="agenda",date=a.date))}
        tasks.forEach{t->add(WorkspaceSearch.Entry("task-${t.id}","Tarefas",t.title,"${t.dueDate} · ${t.student}",8,section="tasks",search=t.title,keywords=t.notes))}
        notes.forEach{n->add(WorkspaceSearch.Entry("note-${n.id}","Notas",n.title,n.student,8,section="notes",search=n.title,keywords=n.body))}
        goals.forEach{g->add(WorkspaceSearch.Entry("goal-${g.id}","Metas",g.title,"${g.due} · ${g.student}",8,section="goals",search=g.title,keywords=g.progress))}
    }
    }
    val found=WorkspaceSearch.find(entries,query,category)
    Dialog(onDismissRequest=onDismiss,properties=DialogProperties(usePlatformDefaultWidth=false)) {
        Surface(Modifier.widthIn(max=680.dp).fillMaxWidth().fillMaxHeight(.88f).windowInsetsPadding(WindowInsets.safeDrawing).imePadding(),shape=MaterialTheme.shapes.large) {
            Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                Row {Text("Pesquisar",Modifier.weight(1f),style=MaterialTheme.typography.titleLarge);TextButton(onClick=onDismiss){Text("Fechar")}}
                Field("Buscar em todo o aplicativo",query,{query=it.take(200)})
                ReferenceSectionTabs(listOf("Tudo","Módulos","Configurações","Avaliações","Clientes","Agenda","Tarefas","Metas","Notas"),category,{category=it})
                Text("Busca local · até 60 resultados",style=MaterialTheme.typography.bodySmall)
                if(found.isEmpty())Text("Nenhum resultado encontrado.")
                LazyColumn(Modifier.weight(1f)) {items(found,key={it.id}){entry->TextButton(onClick={onOpen(entry)},modifier=Modifier.fillMaxWidth()) {Column(Modifier.fillMaxWidth()){Text(entry.title);Text("${entry.category} · ${entry.detail}",style=MaterialTheme.typography.bodySmall)}}}}
            }
        }
    }
}
