package br.com.evolufisico.nativo.domain

import org.junit.Test
import org.junit.Assert.*

class DashboardPackingTest {
 @Test fun fourIndicatorsStayOnOneRowDespiteRounding(){for(width in 600..1200){val cells=DashboardPacking.pack(width,12,List(4){3},List(4){96});assertTrue(cells.all{it.y==0});assertTrue(cells.last().x+cells.last().width<=width)}}
 @Test fun shorterColumnGetsNextPanelWithoutWaitingForTallNeighbor(){val cells=DashboardPacking.pack(700,12,List(6){6},listOf(210,210,385,175,200,140));assertEquals(6,cells.size);assertEquals(cells[3].x,cells[4].x);assertEquals(cells[3].y+175+12,cells[4].y);assertTrue(cells[4].y<cells[2].y+385)}
 @Test fun phoneUsesTwoSmallIndicatorsAndOnePanel(){assertEquals(6,DashboardPacking.span("homeStats","small",380f,1f));assertEquals(12,DashboardPacking.span("homeCards","medium",380f,1f));assertEquals(12,DashboardPacking.span("homeStats","small",260f,1f))}
 @Test fun largeFontReducesDensityInsteadOfClippingText(){assertEquals(3,DashboardPacking.span("homeStats","small",700f,1f));assertEquals(6,DashboardPacking.span("homeStats","small",700f,1.8f));assertEquals(12,DashboardPacking.span("homeCards","medium",700f,1.8f))}
 @Test fun fullWidthPanelStartsBelowBothColumns(){val cells=DashboardPacking.pack(700,12,listOf(6,6,12),listOf(100,300,80));assertEquals(312,cells.last().y);assertEquals(0,cells.last().x);assertEquals(700,cells.last().width)}
 @Test fun mixedSpansNeverOverlapOrExceedScreen(){val random=java.util.Random(12);repeat(100){val width=600+random.nextInt(600);val spans=List(20){listOf(3,4,6,8,12)[random.nextInt(5)]};val heights=List(20){60+random.nextInt(700)};val cells=DashboardPacking.pack(width,12,spans,heights);cells.forEach{assertTrue(it.x>=0&&it.x+it.width<=width)};for(i in cells.indices)for(j in 0 until i){val a=cells[i];val b=cells[j];assertFalse(a.x<b.x+b.width&&a.x+a.width>b.x&&a.y<b.y+b.height&&a.y+a.height>b.y)}}}
 @Test fun movingAcrossSeveralSlotsPreservesEveryCardExactlyOnce(){val order=listOf("a","b","c","d");assertEquals(listOf("b","c","d","a"),DashboardPacking.move(order,"a","d"));assertEquals(listOf("d","a","b","c"),DashboardPacking.move(order,"d","a"));assertEquals(listOf("a","b","c","d"),order)}
 @Test fun missingDropTargetDoesNotChangeLayout(){val order=listOf("a","b");assertEquals(order,DashboardPacking.move(order,"a","missing"));assertEquals(order,DashboardPacking.move(order,"a","a"))}
 @Test fun emptyDashboardHasNoPhantomCells(){assertTrue(DashboardPacking.pack(700,12,emptyList(),emptyList()).isEmpty())}
}
