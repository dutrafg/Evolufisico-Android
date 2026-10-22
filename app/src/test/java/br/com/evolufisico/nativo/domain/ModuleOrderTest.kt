package br.com.evolufisico.nativo.domain

import org.junit.Assert.*
import org.junit.Test

class ModuleOrderTest {
    @Test fun absentPreferenceKeepsEveryOriginalRoute(){
        assertEquals(ModuleOrder.defaults,ModuleOrder.parse(null))
        assertEquals((0..15).toSet(),ModuleOrder.defaults.toSet())
    }
    @Test fun damagedPreferenceCannotHideOrDuplicateModules(){
        val result=ModuleOrder.parse("12,12,-1,999,abc,8")
        assertEquals(listOf(12,8),result.take(2))
        assertEquals(16,result.size)
        assertEquals((0..15).toSet(),result.toSet())
    }
    @Test fun moveChangesPositionWithoutChangingRouteIdentity(){
        val result=ModuleOrder.move(ModuleOrder.defaults,12,-1)
        assertEquals(12,result[ModuleOrder.defaults.indexOf(12)-1])
        assertEquals(ModuleOrder.defaults.toSet(),result.toSet())
    }
    @Test fun boundaryButtonsCannotLoseModules(){
        assertEquals(ModuleOrder.defaults,ModuleOrder.move(ModuleOrder.defaults,0,-1))
        assertEquals(ModuleOrder.defaults,ModuleOrder.move(ModuleOrder.defaults,7,1))
        assertEquals(ModuleOrder.defaults,ModuleOrder.move(ModuleOrder.defaults,999,1))
    }
    @Test fun inverseMovesRestoreSavedOrder(){
        ModuleOrder.defaults.drop(1).forEach{id->
            assertEquals(ModuleOrder.defaults,ModuleOrder.move(ModuleOrder.move(ModuleOrder.defaults,id,-1),id,1))
        }
    }
    @Test fun savedOrderRoundTripsAndAppendsNewMissingModules(){
        val reversed=ModuleOrder.defaults.reversed()
        assertEquals(reversed,ModuleOrder.parse(reversed.joinToString(",")))
        assertEquals(listOf(7,12,0,8),ModuleOrder.parse("7,12").take(4))
    }
}
