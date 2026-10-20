package br.com.evolufisico.android

import org.junit.Assert.*
import org.junit.Test

class WorkspaceRecentsTest {
    @Test fun tracksOnlyLastThreeUniqueModules() {
        var recent=emptyList<String>()
        for(route in listOf("FORMS","REPORTS","GOALS","NOTES","GOALS")) {
            recent=WorkspaceRecents.open(recent,route)
        }
        assertEquals(listOf("GOALS","NOTES","REPORTS"),recent)
        assertEquals(recent,WorkspaceRecents.parse(WorkspaceRecents.serialize(recent)))
    }
    @Test fun ignoresUnknownRoutesAndDuplicates() {
        assertEquals(listOf("FORMS"),WorkspaceRecents.parse("FORMS,INVALID,FORMS"))
        assertEquals(emptyList<String>(),WorkspaceRecents.open(emptyList(),"BILLING"))
    }
}
