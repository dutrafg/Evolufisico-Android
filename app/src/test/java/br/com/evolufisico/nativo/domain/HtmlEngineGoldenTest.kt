package br.com.evolufisico.nativo.domain
import org.junit.Assert.*
import org.junit.Test
/** Expected values executed from EF70 in reference/app.html; regenerate with qa/html/generate_ef70_cases.cjs. */
class HtmlEngineGoldenTest { @Test fun fortyEightVectorsMatchPinnedJavascript(){
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",9.0),HtmlAnalysis.Point(20468L,25.0,60.7,90.0,89.5,"BIA",null),HtmlAnalysis.Point(20473L,25.0,61.4,90.0,89.0,"BIA",null),HtmlAnalysis.Point(20517L,25.0,62.1,90.0,88.5,"BIA",null)));
assertEquals("score case 0",Integer.valueOf(66),result.trajectory);assertEquals("quality case 0",Integer.valueOf(45),result.sufficiency);
assertNull(result.per30["Gordura"]);
assertEquals(0.9167416741674173,result.per30["Massa magra"]!!,1e-8);
assertEquals(0.0,result.per30["Cintura"]!!,1e-8);
assertEquals(-0.6548154815481548,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,24.4,60.7,89.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,23.8,61.4,88.0,89.0,"Pollock",null)));
assertEquals("score case 1",Integer.valueOf(68),result.trajectory);assertEquals("quality case 1",Integer.valueOf(54),result.sufficiency);
assertEquals(-1.285714285714285,result.per30["Gordura"]!!,1e-8);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(-2.142857142857143,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,23.8,60.7,88.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,22.6,61.4,86.0,89.0,"Pollock",null)));
assertEquals("score case 2",Integer.valueOf(73),result.trajectory);assertEquals("quality case 2",Integer.valueOf(54),result.sufficiency);
assertEquals(-2.57142857142857,result.per30["Gordura"]!!,1e-8);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(-4.285714285714286,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,23.2,60.7,87.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,21.4,61.4,84.0,89.0,"Pollock",null),HtmlAnalysis.Point(20496L,19.6,62.1,81.0,88.5,"Pollock",null)));
assertEquals("score case 3",Integer.valueOf(82),result.trajectory);assertEquals("quality case 3",Integer.valueOf(62),result.sufficiency);
assertEquals(-3.857142857142856,result.per30["Gordura"]!!,1e-8);
assertEquals(1.5,result.per30["Massa magra"]!!,1e-8);
assertEquals(-6.428571428571428,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,22.6,60.7,90.0,89.5,"BIA",null),HtmlAnalysis.Point(20482L,20.2,61.4,90.0,89.0,"BIA",null)));
assertEquals("score case 4",Integer.valueOf(64),result.trajectory);assertEquals("quality case 4",Integer.valueOf(45),result.sufficiency);
assertNull(result.per30["Gordura"]);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(0.0,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,25.0,60.7,89.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,25.0,61.4,88.0,89.0,"Pollock",null)));
assertEquals("score case 5",Integer.valueOf(64),result.trajectory);assertEquals("quality case 5",Integer.valueOf(54),result.sufficiency);
assertEquals(0.0,result.per30["Gordura"]!!,1e-8);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(-2.142857142857143,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",9.0),HtmlAnalysis.Point(20468L,24.4,60.7,88.0,89.5,"Pollock",9.0),HtmlAnalysis.Point(20482L,23.8,61.4,86.0,89.0,"Pollock",8.999999999999996),HtmlAnalysis.Point(20496L,23.2,62.1,84.0,88.5,"Pollock",9.000000000000004)));
assertEquals("score case 6",Integer.valueOf(73),result.trajectory);assertEquals("quality case 6",Integer.valueOf(40),result.sufficiency);
assertEquals(-1.2857142857142858,result.per30["Gordura"]!!,1e-8);
assertEquals(1.5,result.per30["Massa magra"]!!,1e-8);
assertEquals(-4.285714285714286,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,23.8,60.7,87.0,89.5,"Pollock",null),HtmlAnalysis.Point(20473L,22.6,61.4,84.0,89.0,"Pollock",null)));
assertEquals("score case 7",null,result.trajectory);assertEquals("quality case 7",null,result.sufficiency);
assertNull(result.per30["Gordura"]);
assertNull(result.per30["Massa magra"]);
assertNull(result.per30["Cintura"]);
assertNull(result.per30["Peso"]);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,23.2,60.7,90.0,89.5,"BIA",null),HtmlAnalysis.Point(20482L,21.4,61.4,90.0,89.0,"BIA",null)));
assertEquals("score case 8",Integer.valueOf(64),result.trajectory);assertEquals("quality case 8",Integer.valueOf(45),result.sufficiency);
assertNull(result.per30["Gordura"]);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(0.0,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,22.6,60.7,89.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,20.2,61.4,88.0,89.0,"Pollock",null),HtmlAnalysis.Point(20496L,17.8,62.1,87.0,88.5,"Pollock",null)));
assertEquals("score case 9",Integer.valueOf(78),result.trajectory);assertEquals("quality case 9",Integer.valueOf(62),result.sufficiency);
assertEquals(-5.142857142857143,result.per30["Gordura"]!!,1e-8);
assertEquals(1.5,result.per30["Massa magra"]!!,1e-8);
assertEquals(-2.142857142857143,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,25.0,60.7,88.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,25.0,61.4,86.0,89.0,"Pollock",null)));
assertEquals("score case 10",Integer.valueOf(65),result.trajectory);assertEquals("quality case 10",Integer.valueOf(54),result.sufficiency);
assertEquals(0.0,result.per30["Gordura"]!!,1e-8);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(-4.285714285714286,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,24.4,60.7,87.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,23.8,61.4,84.0,89.0,"Pollock",null)));
assertEquals("score case 11",Integer.valueOf(70),result.trajectory);assertEquals("quality case 11",Integer.valueOf(54),result.sufficiency);
assertEquals(-1.285714285714285,result.per30["Gordura"]!!,1e-8);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(-6.428571428571428,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",9.0),HtmlAnalysis.Point(20468L,23.8,60.7,90.0,89.5,"BIA",null),HtmlAnalysis.Point(20482L,22.6,61.4,90.0,89.0,"BIA",null),HtmlAnalysis.Point(20496L,21.4,62.1,90.0,88.5,"BIA",null)));
assertEquals("score case 12",Integer.valueOf(66),result.trajectory);assertEquals("quality case 12",Integer.valueOf(45),result.sufficiency);
assertNull(result.per30["Gordura"]);
assertEquals(1.5,result.per30["Massa magra"]!!,1e-8);
assertEquals(0.0,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,23.2,60.7,89.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,21.4,61.4,88.0,89.0,"Pollock",null)));
assertEquals("score case 13",Integer.valueOf(76),result.trajectory);assertEquals("quality case 13",Integer.valueOf(54),result.sufficiency);
assertEquals(-3.8571428571428585,result.per30["Gordura"]!!,1e-8);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(-2.142857142857143,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,22.6,60.7,88.0,89.5,"Pollock",null),HtmlAnalysis.Point(20473L,20.2,61.4,86.0,89.0,"Pollock",null)));
assertEquals("score case 14",null,result.trajectory);assertEquals("quality case 14",null,result.sufficiency);
assertNull(result.per30["Gordura"]);
assertNull(result.per30["Massa magra"]);
assertNull(result.per30["Cintura"]);
assertNull(result.per30["Peso"]);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,25.0,60.7,87.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,25.0,61.4,84.0,89.0,"Pollock",null),HtmlAnalysis.Point(20517L,25.0,62.1,81.0,88.5,"Pollock",null)));
assertEquals("score case 15",Integer.valueOf(69),result.trajectory);assertEquals("quality case 15",Integer.valueOf(66),result.sufficiency);
assertEquals(0.0,result.per30["Gordura"]!!,1e-8);
assertEquals(0.9720670391061454,result.per30["Massa magra"]!!,1e-8);
assertEquals(-4.1660015961691945,result.per30["Cintura"]!!,1e-8);
assertEquals(-0.6943335993615324,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,24.4,60.7,90.0,89.5,"BIA",null),HtmlAnalysis.Point(20482L,23.8,61.4,90.0,89.0,"BIA",null)));
assertEquals("score case 16",Integer.valueOf(64),result.trajectory);assertEquals("quality case 16",Integer.valueOf(45),result.sufficiency);
assertNull(result.per30["Gordura"]);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(0.0,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,23.8,60.7,89.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,22.6,61.4,88.0,89.0,"Pollock",null)));
assertEquals("score case 17",Integer.valueOf(72),result.trajectory);assertEquals("quality case 17",Integer.valueOf(54),result.sufficiency);
assertEquals(-2.57142857142857,result.per30["Gordura"]!!,1e-8);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(-2.142857142857143,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",9.0),HtmlAnalysis.Point(20468L,23.2,60.7,88.0,89.5,"Pollock",9.000000000000004),HtmlAnalysis.Point(20482L,21.4,61.4,86.0,89.0,"Pollock",9.0),HtmlAnalysis.Point(20496L,19.6,62.1,84.0,88.5,"Pollock",9.0)));
assertEquals("score case 18",Integer.valueOf(80),result.trajectory);assertEquals("quality case 18",Integer.valueOf(40),result.sufficiency);
assertEquals(-3.857142857142856,result.per30["Gordura"]!!,1e-8);
assertEquals(1.5,result.per30["Massa magra"]!!,1e-8);
assertEquals(-4.285714285714286,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,22.6,60.7,87.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,20.2,61.4,84.0,89.0,"Pollock",null)));
assertEquals("score case 19",Integer.valueOf(79),result.trajectory);assertEquals("quality case 19",Integer.valueOf(54),result.sufficiency);
assertEquals(-5.142857142857144,result.per30["Gordura"]!!,1e-8);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(-6.428571428571428,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,25.0,60.7,90.0,89.5,"BIA",null),HtmlAnalysis.Point(20482L,25.0,61.4,90.0,89.0,"BIA",null)));
assertEquals("score case 20",Integer.valueOf(64),result.trajectory);assertEquals("quality case 20",Integer.valueOf(45),result.sufficiency);
assertNull(result.per30["Gordura"]);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(0.0,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,24.4,60.7,89.0,89.5,"Pollock",null),HtmlAnalysis.Point(20473L,23.8,61.4,88.0,89.0,"Pollock",null),HtmlAnalysis.Point(20496L,23.2,62.1,87.0,88.5,"Pollock",null)));
assertEquals("score case 21",Integer.valueOf(72),result.trajectory);assertEquals("quality case 21",Integer.valueOf(62),result.sufficiency);
assertEquals(-1.2888767422793117,result.per30["Gordura"]!!,1e-8);
assertEquals(1.5036895326591972,result.per30["Massa magra"]!!,1e-8);
assertEquals(-2.1481279037988523,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0740639518994262,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,23.8,60.7,88.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,22.6,61.4,86.0,89.0,"Pollock",null)));
assertEquals("score case 22",Integer.valueOf(73),result.trajectory);assertEquals("quality case 22",Integer.valueOf(54),result.sufficiency);
assertEquals(-2.57142857142857,result.per30["Gordura"]!!,1e-8);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(-4.285714285714286,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,23.2,60.7,87.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,21.4,61.4,84.0,89.0,"Pollock",null)));
assertEquals("score case 23",Integer.valueOf(78),result.trajectory);assertEquals("quality case 23",Integer.valueOf(54),result.sufficiency);
assertEquals(-3.8571428571428585,result.per30["Gordura"]!!,1e-8);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(-6.428571428571428,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",9.0),HtmlAnalysis.Point(20468L,22.6,60.7,90.0,89.5,"BIA",null),HtmlAnalysis.Point(20482L,20.2,61.4,90.0,89.0,"BIA",null),HtmlAnalysis.Point(20496L,17.8,62.1,90.0,88.5,"BIA",null)));
assertEquals("score case 24",Integer.valueOf(66),result.trajectory);assertEquals("quality case 24",Integer.valueOf(45),result.sufficiency);
assertNull(result.per30["Gordura"]);
assertEquals(1.5,result.per30["Massa magra"]!!,1e-8);
assertEquals(0.0,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,25.0,60.7,89.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,25.0,61.4,88.0,89.0,"Pollock",null)));
assertEquals("score case 25",Integer.valueOf(64),result.trajectory);assertEquals("quality case 25",Integer.valueOf(54),result.sufficiency);
assertEquals(0.0,result.per30["Gordura"]!!,1e-8);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(-2.142857142857143,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,24.4,60.7,88.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,23.8,61.4,86.0,89.0,"Pollock",null)));
assertEquals("score case 26",Integer.valueOf(69),result.trajectory);assertEquals("quality case 26",Integer.valueOf(54),result.sufficiency);
assertEquals(-1.285714285714285,result.per30["Gordura"]!!,1e-8);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(-4.285714285714286,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,23.8,60.7,87.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,22.6,61.4,84.0,89.0,"Pollock",null),HtmlAnalysis.Point(20496L,21.4,62.1,81.0,88.5,"Pollock",null)));
assertEquals("score case 27",Integer.valueOf(81),result.trajectory);assertEquals("quality case 27",Integer.valueOf(62),result.sufficiency);
assertEquals(-2.571428571428572,result.per30["Gordura"]!!,1e-8);
assertEquals(1.5,result.per30["Massa magra"]!!,1e-8);
assertEquals(-6.428571428571428,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,23.2,60.7,90.0,89.5,"BIA",null),HtmlAnalysis.Point(20473L,21.4,61.4,90.0,89.0,"BIA",null)));
assertEquals("score case 28",null,result.trajectory);assertEquals("quality case 28",null,result.sufficiency);
assertNull(result.per30["Gordura"]);
assertNull(result.per30["Massa magra"]);
assertNull(result.per30["Cintura"]);
assertNull(result.per30["Peso"]);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,22.6,60.7,89.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,20.2,61.4,88.0,89.0,"Pollock",null)));
assertEquals("score case 29",Integer.valueOf(77),result.trajectory);assertEquals("quality case 29",Integer.valueOf(54),result.sufficiency);
assertEquals(-5.142857142857144,result.per30["Gordura"]!!,1e-8);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(-2.142857142857143,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",9.0),HtmlAnalysis.Point(20468L,25.0,60.7,88.0,89.5,"Pollock",9.0),HtmlAnalysis.Point(20482L,25.0,61.4,86.0,89.0,"Pollock",9.0),HtmlAnalysis.Point(20517L,25.0,62.1,84.0,88.5,"Pollock",9.0)));
assertEquals("score case 30",Integer.valueOf(67),result.trajectory);assertEquals("quality case 30",Integer.valueOf(40),result.sufficiency);
assertEquals(0.0,result.per30["Gordura"]!!,1e-8);
assertEquals(0.9720670391061454,result.per30["Massa magra"]!!,1e-8);
assertEquals(-2.7773343974461295,result.per30["Cintura"]!!,1e-8);
assertEquals(-0.6943335993615324,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,24.4,60.7,87.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,23.8,61.4,84.0,89.0,"Pollock",null)));
assertEquals("score case 31",Integer.valueOf(70),result.trajectory);assertEquals("quality case 31",Integer.valueOf(54),result.sufficiency);
assertEquals(-1.285714285714285,result.per30["Gordura"]!!,1e-8);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(-6.428571428571428,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,23.8,60.7,90.0,89.5,"BIA",null),HtmlAnalysis.Point(20482L,22.6,61.4,90.0,89.0,"BIA",null)));
assertEquals("score case 32",Integer.valueOf(64),result.trajectory);assertEquals("quality case 32",Integer.valueOf(45),result.sufficiency);
assertNull(result.per30["Gordura"]);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(0.0,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,23.2,60.7,89.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,21.4,61.4,88.0,89.0,"Pollock",null),HtmlAnalysis.Point(20496L,19.6,62.1,87.0,88.5,"Pollock",null)));
assertEquals("score case 33",Integer.valueOf(78),result.trajectory);assertEquals("quality case 33",Integer.valueOf(62),result.sufficiency);
assertEquals(-3.857142857142856,result.per30["Gordura"]!!,1e-8);
assertEquals(1.5,result.per30["Massa magra"]!!,1e-8);
assertEquals(-2.142857142857143,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,22.6,60.7,88.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,20.2,61.4,86.0,89.0,"Pollock",null)));
assertEquals("score case 34",Integer.valueOf(78),result.trajectory);assertEquals("quality case 34",Integer.valueOf(54),result.sufficiency);
assertEquals(-5.142857142857144,result.per30["Gordura"]!!,1e-8);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(-4.285714285714286,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,25.0,60.7,87.0,89.5,"Pollock",null),HtmlAnalysis.Point(20473L,25.0,61.4,84.0,89.0,"Pollock",null)));
assertEquals("score case 35",null,result.trajectory);assertEquals("quality case 35",null,result.sufficiency);
assertNull(result.per30["Gordura"]);
assertNull(result.per30["Massa magra"]);
assertNull(result.per30["Cintura"]);
assertNull(result.per30["Peso"]);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",9.0),HtmlAnalysis.Point(20468L,24.4,60.7,90.0,89.5,"BIA",null),HtmlAnalysis.Point(20482L,23.8,61.4,90.0,89.0,"BIA",null),HtmlAnalysis.Point(20496L,23.2,62.1,90.0,88.5,"BIA",null)));
assertEquals("score case 36",Integer.valueOf(66),result.trajectory);assertEquals("quality case 36",Integer.valueOf(45),result.sufficiency);
assertNull(result.per30["Gordura"]);
assertEquals(1.5,result.per30["Massa magra"]!!,1e-8);
assertEquals(0.0,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,23.8,60.7,89.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,22.6,61.4,88.0,89.0,"Pollock",null)));
assertEquals("score case 37",Integer.valueOf(72),result.trajectory);assertEquals("quality case 37",Integer.valueOf(54),result.sufficiency);
assertEquals(-2.57142857142857,result.per30["Gordura"]!!,1e-8);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(-2.142857142857143,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,23.2,60.7,88.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,21.4,61.4,86.0,89.0,"Pollock",null)));
assertEquals("score case 38",Integer.valueOf(77),result.trajectory);assertEquals("quality case 38",Integer.valueOf(54),result.sufficiency);
assertEquals(-3.8571428571428585,result.per30["Gordura"]!!,1e-8);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(-4.285714285714286,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,22.6,60.7,87.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,20.2,61.4,84.0,89.0,"Pollock",null),HtmlAnalysis.Point(20496L,17.8,62.1,81.0,88.5,"Pollock",null)));
assertEquals("score case 39",Integer.valueOf(82),result.trajectory);assertEquals("quality case 39",Integer.valueOf(62),result.sufficiency);
assertEquals(-5.142857142857143,result.per30["Gordura"]!!,1e-8);
assertEquals(1.5,result.per30["Massa magra"]!!,1e-8);
assertEquals(-6.428571428571428,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,25.0,60.7,90.0,89.5,"BIA",null),HtmlAnalysis.Point(20482L,25.0,61.4,90.0,89.0,"BIA",null)));
assertEquals("score case 40",Integer.valueOf(64),result.trajectory);assertEquals("quality case 40",Integer.valueOf(45),result.sufficiency);
assertNull(result.per30["Gordura"]);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(0.0,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,24.4,60.7,89.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,23.8,61.4,88.0,89.0,"Pollock",null)));
assertEquals("score case 41",Integer.valueOf(68),result.trajectory);assertEquals("quality case 41",Integer.valueOf(54),result.sufficiency);
assertEquals(-1.285714285714285,result.per30["Gordura"]!!,1e-8);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(-2.142857142857143,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",9.0),HtmlAnalysis.Point(20468L,23.8,60.7,88.0,89.5,"Pollock",8.999999999999996),HtmlAnalysis.Point(20473L,22.6,61.4,86.0,89.0,"Pollock",9.0),HtmlAnalysis.Point(20496L,21.4,62.1,84.0,88.5,"Pollock",9.0)));
assertEquals("score case 42",Integer.valueOf(79),result.trajectory);assertEquals("quality case 42",Integer.valueOf(40),result.sufficiency);
assertEquals(-2.5777534845586234,result.per30["Gordura"]!!,1e-8);
assertEquals(1.5036895326591972,result.per30["Massa magra"]!!,1e-8);
assertEquals(-4.296255807597705,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0740639518994262,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,23.2,60.7,87.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,21.4,61.4,84.0,89.0,"Pollock",null)));
assertEquals("score case 43",Integer.valueOf(78),result.trajectory);assertEquals("quality case 43",Integer.valueOf(54),result.sufficiency);
assertEquals(-3.8571428571428585,result.per30["Gordura"]!!,1e-8);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(-6.428571428571428,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,22.6,60.7,90.0,89.5,"BIA",null),HtmlAnalysis.Point(20482L,20.2,61.4,90.0,89.0,"BIA",null)));
assertEquals("score case 44",Integer.valueOf(64),result.trajectory);assertEquals("quality case 44",Integer.valueOf(45),result.sufficiency);
assertNull(result.per30["Gordura"]);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(0.0,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,25.0,60.7,89.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,25.0,61.4,88.0,89.0,"Pollock",null),HtmlAnalysis.Point(20517L,25.0,62.1,87.0,88.5,"Pollock",null)));
assertEquals("score case 45",Integer.valueOf(65),result.trajectory);assertEquals("quality case 45",Integer.valueOf(66),result.sufficiency);
assertEquals(0.0,result.per30["Gordura"]!!,1e-8);
assertEquals(0.9720670391061454,result.per30["Massa magra"]!!,1e-8);
assertEquals(-1.3886671987230648,result.per30["Cintura"]!!,1e-8);
assertEquals(-0.6943335993615324,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,24.4,60.7,88.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,23.8,61.4,86.0,89.0,"Pollock",null)));
assertEquals("score case 46",Integer.valueOf(69),result.trajectory);assertEquals("quality case 46",Integer.valueOf(54),result.sufficiency);
assertEquals(-1.285714285714285,result.per30["Gordura"]!!,1e-8);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(-4.285714285714286,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
run { val result=HtmlAnalysis.analyze(listOf(HtmlAnalysis.Point(20454L,25.0,60.0,90.0,90.0,"Pollock",null),HtmlAnalysis.Point(20468L,23.8,60.7,87.0,89.5,"Pollock",null),HtmlAnalysis.Point(20482L,22.6,61.4,84.0,89.0,"Pollock",null)));
assertEquals("score case 47",Integer.valueOf(74),result.trajectory);assertEquals("quality case 47",Integer.valueOf(54),result.sufficiency);
assertEquals(-2.57142857142857,result.per30["Gordura"]!!,1e-8);
assertEquals(1.4999999999999984,result.per30["Massa magra"]!!,1e-8);
assertEquals(-6.428571428571428,result.per30["Cintura"]!!,1e-8);
assertEquals(-1.0714285714285714,result.per30["Peso"]!!,1e-8);
}
}}
