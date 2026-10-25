package br.com.evolufisico.nativo.domain

/** Ephemeral local index. Nothing is persisted or sent over the network. */
object WorkspaceSearch {
    data class Entry(val id:String,val category:String,val title:String,val detail:String,val module:Int,val client:String="",val section:String="",val date:String="",val search:String="",val keywords:String="")
    fun find(entries:List<Entry>,query:String,category:String="Tudo"):List<Entry> {
        val words=normalize(query.take(200)).split(' ').filter{it.isNotBlank()}
        return entries.asSequence().filter{category=="Tudo"||it.category==category}.filter{e->val text=normalize(e.title+" "+e.detail+" "+e.keywords);words.all{text.contains(it)}}.take(60).toList()
    }
    private fun normalize(s:String)=java.text.Normalizer.normalize(s,java.text.Normalizer.Form.NFD).replace(Regex("\\p{M}+"),"").lowercase(java.util.Locale.ROOT)
}
