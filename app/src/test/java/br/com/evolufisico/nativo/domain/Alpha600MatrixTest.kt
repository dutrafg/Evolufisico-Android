package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.Appointment
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test

/** 450 separately reported regression vectors mapped to Alpha stages A151-A600. */
class Alpha600MatrixTest {
    @Test fun alphaA151_goalProgressVector001() {
        val result=GoalTracking.progress(100.0, 100.0000000000, 90.0)!!
        assertEquals(0.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(90.0 - 100.0000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA152_goalProgressVector002() {
        val result=GoalTracking.progress(100.5, 104.5700000000, 111.5)!!
        assertEquals(37.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(111.5 - 104.5700000000), result.remaining, 0.000001)
    }

    @Test fun alphaA153_goalProgressVector003() {
        val result=GoalTracking.progress(101.0, 92.1200000000, 89.0)!!
        assertEquals(74.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(89.0 - 92.1200000000), result.remaining, 0.000001)
    }

    @Test fun alphaA154_goalProgressVector004() {
        val result=GoalTracking.progress(101.5, 102.8000000000, 114.5)!!
        assertEquals(10.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(114.5 - 102.8000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA155_goalProgressVector005() {
        val result=GoalTracking.progress(102.0, 95.4200000000, 88.0)!!
        assertEquals(47.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(88.0 - 95.4200000000), result.remaining, 0.000001)
    }

    @Test fun alphaA156_goalProgressVector006() {
        val result=GoalTracking.progress(102.5, 115.1000000000, 117.5)!!
        assertEquals(84.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(117.5 - 115.1000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA157_goalProgressVector007() {
        val result=GoalTracking.progress(103.0, 99.8000000000, 87.0)!!
        assertEquals(20.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(87.0 - 99.8000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA158_goalProgressVector008() {
        val result=GoalTracking.progress(103.5, 113.1900000000, 120.5)!!
        assertEquals(57.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(120.5 - 113.1900000000), result.remaining, 0.000001)
    }

    @Test fun alphaA159_goalProgressVector009() {
        val result=GoalTracking.progress(104.0, 87.0800000000, 86.0)!!
        assertEquals(94.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(86.0 - 87.0800000000), result.remaining, 0.000001)
    }

    @Test fun alphaA160_goalProgressVector010() {
        val result=GoalTracking.progress(104.5, 110.2000000000, 123.5)!!
        assertEquals(30.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(123.5 - 110.2000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA161_goalProgressVector011() {
        val result=GoalTracking.progress(105.0, 91.6000000000, 85.0)!!
        assertEquals(67.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(85.0 - 91.6000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA162_goalProgressVector012() {
        val result=GoalTracking.progress(105.5, 106.1300000000, 126.5)!!
        assertEquals(3.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(126.5 - 106.1300000000), result.remaining, 0.000001)
    }

    @Test fun alphaA163_goalProgressVector013() {
        val result=GoalTracking.progress(106.0, 97.2000000000, 84.0)!!
        assertEquals(40.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(84.0 - 97.2000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA164_goalProgressVector014() {
        val result=GoalTracking.progress(106.5, 124.2100000000, 129.5)!!
        assertEquals(77.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(129.5 - 124.2100000000), result.remaining, 0.000001)
    }

    @Test fun alphaA165_goalProgressVector015() {
        val result=GoalTracking.progress(107.0, 103.8800000000, 83.0)!!
        assertEquals(13.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(83.0 - 103.8800000000), result.remaining, 0.000001)
    }

    @Test fun alphaA166_goalProgressVector016() {
        val result=GoalTracking.progress(107.5, 120.0000000000, 132.5)!!
        assertEquals(50.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(132.5 - 120.0000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA167_goalProgressVector017() {
        val result=GoalTracking.progress(108.0, 85.3800000000, 82.0)!!
        assertEquals(87.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(82.0 - 85.3800000000), result.remaining, 0.000001)
    }

    @Test fun alphaA168_goalProgressVector018() {
        val result=GoalTracking.progress(108.5, 114.7100000000, 135.5)!!
        assertEquals(23.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(135.5 - 114.7100000000), result.remaining, 0.000001)
    }

    @Test fun alphaA169_goalProgressVector019() {
        val result=GoalTracking.progress(109.0, 92.2000000000, 81.0)!!
        assertEquals(60.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(81.0 - 92.2000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA170_goalProgressVector020() {
        val result=GoalTracking.progress(109.5, 137.6300000000, 138.5)!!
        assertEquals(97.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(138.5 - 137.6300000000), result.remaining, 0.000001)
    }

    @Test fun alphaA171_goalProgressVector021() {
        val result=GoalTracking.progress(110.0, 100.1000000000, 80.0)!!
        assertEquals(33.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(80.0 - 100.1000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA172_goalProgressVector022() {
        val result=GoalTracking.progress(110.5, 132.2000000000, 141.5)!!
        assertEquals(70.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(141.5 - 132.2000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA173_goalProgressVector023() {
        val result=GoalTracking.progress(111.0, 109.0800000000, 79.0)!!
        assertEquals(6.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(79.0 - 109.0800000000), result.remaining, 0.000001)
    }

    @Test fun alphaA174_goalProgressVector024() {
        val result=GoalTracking.progress(111.5, 125.6900000000, 144.5)!!
        assertEquals(43.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(144.5 - 125.6900000000), result.remaining, 0.000001)
    }

    @Test fun alphaA175_goalProgressVector025() {
        val result=GoalTracking.progress(112.0, 84.8000000000, 78.0)!!
        assertEquals(80.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(78.0 - 84.8000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA176_goalProgressVector026() {
        val result=GoalTracking.progress(112.5, 118.1000000000, 147.5)!!
        assertEquals(16.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(147.5 - 118.1000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA177_goalProgressVector027() {
        val result=GoalTracking.progress(113.0, 93.9200000000, 77.0)!!
        assertEquals(53.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(77.0 - 93.9200000000), result.remaining, 0.000001)
    }

    @Test fun alphaA178_goalProgressVector028() {
        val result=GoalTracking.progress(113.5, 146.8000000000, 150.5)!!
        assertEquals(90.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(150.5 - 146.8000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA179_goalProgressVector029() {
        val result=GoalTracking.progress(114.0, 104.1200000000, 76.0)!!
        assertEquals(26.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(76.0 - 104.1200000000), result.remaining, 0.000001)
    }

    @Test fun alphaA180_goalProgressVector030() {
        val result=GoalTracking.progress(114.5, 139.0700000000, 153.5)!!
        assertEquals(63.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(153.5 - 139.0700000000), result.remaining, 0.000001)
    }

    @Test fun alphaA181_goalProgressVector031() {
        val result=GoalTracking.progress(115.0, 75.0000000000, 75.0)!!
        assertEquals(100.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(75.0 - 75.0000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA182_goalProgressVector032() {
        val result=GoalTracking.progress(115.5, 119.1000000000, 125.5)!!
        assertEquals(36.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(125.5 - 119.1000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA183_goalProgressVector033() {
        val result=GoalTracking.progress(116.0, 107.9700000000, 105.0)!!
        assertEquals(73.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(105.0 - 107.9700000000), result.remaining, 0.000001)
    }

    @Test fun alphaA184_goalProgressVector034() {
        val result=GoalTracking.progress(116.5, 117.5800000000, 128.5)!!
        assertEquals(9.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(128.5 - 117.5800000000), result.remaining, 0.000001)
    }

    @Test fun alphaA185_goalProgressVector035() {
        val result=GoalTracking.progress(117.0, 111.0200000000, 104.0)!!
        assertEquals(46.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(104.0 - 111.0200000000), result.remaining, 0.000001)
    }

    @Test fun alphaA186_goalProgressVector036() {
        val result=GoalTracking.progress(117.5, 129.1200000000, 131.5)!!
        assertEquals(83.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(131.5 - 129.1200000000), result.remaining, 0.000001)
    }

    @Test fun alphaA187_goalProgressVector037() {
        val result=GoalTracking.progress(118.0, 115.1500000000, 103.0)!!
        assertEquals(19.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(103.0 - 115.1500000000), result.remaining, 0.000001)
    }

    @Test fun alphaA188_goalProgressVector038() {
        val result=GoalTracking.progress(118.5, 127.4600000000, 134.5)!!
        assertEquals(56.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(134.5 - 127.4600000000), result.remaining, 0.000001)
    }

    @Test fun alphaA189_goalProgressVector039() {
        val result=GoalTracking.progress(119.0, 103.1900000000, 102.0)!!
        assertEquals(93.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(102.0 - 103.1900000000), result.remaining, 0.000001)
    }

    @Test fun alphaA190_goalProgressVector040() {
        val result=GoalTracking.progress(119.5, 124.7200000000, 137.5)!!
        assertEquals(29.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(137.5 - 124.7200000000), result.remaining, 0.000001)
    }

    @Test fun alphaA191_goalProgressVector041() {
        val result=GoalTracking.progress(120.0, 107.4600000000, 101.0)!!
        assertEquals(66.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(101.0 - 107.4600000000), result.remaining, 0.000001)
    }

    @Test fun alphaA192_goalProgressVector042() {
        val result=GoalTracking.progress(120.5, 120.9000000000, 140.5)!!
        assertEquals(2.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(140.5 - 120.9000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA193_goalProgressVector043() {
        val result=GoalTracking.progress(121.0, 112.8100000000, 100.0)!!
        assertEquals(39.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(100.0 - 112.8100000000), result.remaining, 0.000001)
    }

    @Test fun alphaA194_goalProgressVector044() {
        val result=GoalTracking.progress(121.5, 138.2200000000, 143.5)!!
        assertEquals(76.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(143.5 - 138.2200000000), result.remaining, 0.000001)
    }

    @Test fun alphaA195_goalProgressVector045() {
        val result=GoalTracking.progress(122.0, 119.2400000000, 99.0)!!
        assertEquals(12.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(99.0 - 119.2400000000), result.remaining, 0.000001)
    }

    @Test fun alphaA196_goalProgressVector046() {
        val result=GoalTracking.progress(122.5, 134.2600000000, 146.5)!!
        assertEquals(49.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(146.5 - 134.2600000000), result.remaining, 0.000001)
    }

    @Test fun alphaA197_goalProgressVector047() {
        val result=GoalTracking.progress(123.0, 101.5000000000, 98.0)!!
        assertEquals(86.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(98.0 - 101.5000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA198_goalProgressVector048() {
        val result=GoalTracking.progress(123.5, 129.2200000000, 149.5)!!
        assertEquals(22.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(149.5 - 129.2200000000), result.remaining, 0.000001)
    }

    @Test fun alphaA199_goalProgressVector049() {
        val result=GoalTracking.progress(124.0, 108.0700000000, 97.0)!!
        assertEquals(59.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(97.0 - 108.0700000000), result.remaining, 0.000001)
    }

    @Test fun alphaA200_goalProgressVector050() {
        val result=GoalTracking.progress(124.5, 151.3800000000, 152.5)!!
        assertEquals(96.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(152.5 - 151.3800000000), result.remaining, 0.000001)
    }

    @Test fun alphaA201_goalProgressVector051() {
        val result=GoalTracking.progress(125.0, 115.7200000000, 96.0)!!
        assertEquals(32.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(96.0 - 115.7200000000), result.remaining, 0.000001)
    }

    @Test fun alphaA202_goalProgressVector052() {
        val result=GoalTracking.progress(125.5, 146.2000000000, 155.5)!!
        assertEquals(69.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(155.5 - 146.2000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA203_goalProgressVector053() {
        val result=GoalTracking.progress(126.0, 124.4500000000, 95.0)!!
        assertEquals(5.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(95.0 - 124.4500000000), result.remaining, 0.000001)
    }

    @Test fun alphaA204_goalProgressVector054() {
        val result=GoalTracking.progress(126.5, 139.9400000000, 158.5)!!
        assertEquals(42.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(158.5 - 139.9400000000), result.remaining, 0.000001)
    }

    @Test fun alphaA205_goalProgressVector055() {
        val result=GoalTracking.progress(127.0, 100.9300000000, 94.0)!!
        assertEquals(79.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(94.0 - 100.9300000000), result.remaining, 0.000001)
    }

    @Test fun alphaA206_goalProgressVector056() {
        val result=GoalTracking.progress(127.5, 132.6000000000, 161.5)!!
        assertEquals(15.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(161.5 - 132.6000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA207_goalProgressVector057() {
        val result=GoalTracking.progress(128.0, 109.8000000000, 93.0)!!
        assertEquals(52.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(93.0 - 109.8000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA208_goalProgressVector058() {
        val result=GoalTracking.progress(128.5, 160.5400000000, 164.5)!!
        assertEquals(89.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(164.5 - 160.5400000000), result.remaining, 0.000001)
    }

    @Test fun alphaA209_goalProgressVector059() {
        val result=GoalTracking.progress(129.0, 119.7500000000, 92.0)!!
        assertEquals(25.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(92.0 - 119.7500000000), result.remaining, 0.000001)
    }

    @Test fun alphaA210_goalProgressVector060() {
        val result=GoalTracking.progress(129.5, 153.0600000000, 167.5)!!
        assertEquals(62.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(167.5 - 153.0600000000), result.remaining, 0.000001)
    }

    @Test fun alphaA211_goalProgressVector061() {
        val result=GoalTracking.progress(130.0, 91.3900000000, 91.0)!!
        assertEquals(99.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(91.0 - 91.3900000000), result.remaining, 0.000001)
    }

    @Test fun alphaA212_goalProgressVector062() {
        val result=GoalTracking.progress(130.5, 144.5000000000, 170.5)!!
        assertEquals(35.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(170.5 - 144.5000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA213_goalProgressVector063() {
        val result=GoalTracking.progress(131.0, 123.8000000000, 121.0)!!
        assertEquals(72.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(121.0 - 123.8000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA214_goalProgressVector064() {
        val result=GoalTracking.progress(131.5, 132.3800000000, 142.5)!!
        assertEquals(8.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(142.5 - 132.3800000000), result.remaining, 0.000001)
    }

    @Test fun alphaA215_goalProgressVector065() {
        val result=GoalTracking.progress(132.0, 126.6000000000, 120.0)!!
        assertEquals(45.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(120.0 - 126.6000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA216_goalProgressVector066() {
        val result=GoalTracking.progress(132.5, 143.1600000000, 145.5)!!
        assertEquals(82.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(145.5 - 143.1600000000), result.remaining, 0.000001)
    }

    @Test fun alphaA217_goalProgressVector067() {
        val result=GoalTracking.progress(133.0, 130.4800000000, 119.0)!!
        assertEquals(18.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(119.0 - 130.4800000000), result.remaining, 0.000001)
    }

    @Test fun alphaA218_goalProgressVector068() {
        val result=GoalTracking.progress(133.5, 141.7500000000, 148.5)!!
        assertEquals(55.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(148.5 - 141.7500000000), result.remaining, 0.000001)
    }

    @Test fun alphaA219_goalProgressVector069() {
        val result=GoalTracking.progress(134.0, 119.2800000000, 118.0)!!
        assertEquals(92.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(118.0 - 119.2800000000), result.remaining, 0.000001)
    }

    @Test fun alphaA220_goalProgressVector070() {
        val result=GoalTracking.progress(134.5, 139.2600000000, 151.5)!!
        assertEquals(28.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(151.5 - 139.2600000000), result.remaining, 0.000001)
    }

    @Test fun alphaA221_goalProgressVector071() {
        val result=GoalTracking.progress(135.0, 123.3000000000, 117.0)!!
        assertEquals(65.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(117.0 - 123.3000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA222_goalProgressVector072() {
        val result=GoalTracking.progress(135.5, 135.6900000000, 154.5)!!
        assertEquals(1.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(154.5 - 135.6900000000), result.remaining, 0.000001)
    }

    @Test fun alphaA223_goalProgressVector073() {
        val result=GoalTracking.progress(136.0, 128.4000000000, 116.0)!!
        assertEquals(38.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(116.0 - 128.4000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA224_goalProgressVector074() {
        val result=GoalTracking.progress(136.5, 152.2500000000, 157.5)!!
        assertEquals(75.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(157.5 - 152.2500000000), result.remaining, 0.000001)
    }

    @Test fun alphaA225_goalProgressVector075() {
        val result=GoalTracking.progress(137.0, 134.5800000000, 115.0)!!
        assertEquals(11.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(115.0 - 134.5800000000), result.remaining, 0.000001)
    }

    @Test fun alphaA226_goalProgressVector076() {
        val result=GoalTracking.progress(137.5, 148.5400000000, 160.5)!!
        assertEquals(48.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(160.5 - 148.5400000000), result.remaining, 0.000001)
    }

    @Test fun alphaA227_goalProgressVector077() {
        val result=GoalTracking.progress(138.0, 117.6000000000, 114.0)!!
        assertEquals(85.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(114.0 - 117.6000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA228_goalProgressVector078() {
        val result=GoalTracking.progress(138.5, 143.7500000000, 163.5)!!
        assertEquals(21.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(163.5 - 143.7500000000), result.remaining, 0.000001)
    }

    @Test fun alphaA229_goalProgressVector079() {
        val result=GoalTracking.progress(139.0, 123.9200000000, 113.0)!!
        assertEquals(58.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(113.0 - 123.9200000000), result.remaining, 0.000001)
    }

    @Test fun alphaA230_goalProgressVector080() {
        val result=GoalTracking.progress(139.5, 165.1500000000, 166.5)!!
        assertEquals(95.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(166.5 - 165.1500000000), result.remaining, 0.000001)
    }

    @Test fun alphaA231_goalProgressVector081() {
        val result=GoalTracking.progress(140.0, 131.3200000000, 112.0)!!
        assertEquals(31.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(112.0 - 131.3200000000), result.remaining, 0.000001)
    }

    @Test fun alphaA232_goalProgressVector082() {
        val result=GoalTracking.progress(140.5, 160.2200000000, 169.5)!!
        assertEquals(68.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(169.5 - 160.2200000000), result.remaining, 0.000001)
    }

    @Test fun alphaA233_goalProgressVector083() {
        val result=GoalTracking.progress(141.0, 139.8000000000, 111.0)!!
        assertEquals(4.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(111.0 - 139.8000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA234_goalProgressVector084() {
        val result=GoalTracking.progress(141.5, 154.2100000000, 172.5)!!
        assertEquals(41.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(172.5 - 154.2100000000), result.remaining, 0.000001)
    }

    @Test fun alphaA235_goalProgressVector085() {
        val result=GoalTracking.progress(142.0, 117.0400000000, 110.0)!!
        assertEquals(78.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(110.0 - 117.0400000000), result.remaining, 0.000001)
    }

    @Test fun alphaA236_goalProgressVector086() {
        val result=GoalTracking.progress(142.5, 147.1200000000, 175.5)!!
        assertEquals(14.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(175.5 - 147.1200000000), result.remaining, 0.000001)
    }

    @Test fun alphaA237_goalProgressVector087() {
        val result=GoalTracking.progress(143.0, 125.6600000000, 109.0)!!
        assertEquals(51.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(109.0 - 125.6600000000), result.remaining, 0.000001)
    }

    @Test fun alphaA238_goalProgressVector088() {
        val result=GoalTracking.progress(143.5, 174.3000000000, 178.5)!!
        assertEquals(88.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(178.5 - 174.3000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA239_goalProgressVector089() {
        val result=GoalTracking.progress(144.0, 135.3600000000, 108.0)!!
        assertEquals(24.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(108.0 - 135.3600000000), result.remaining, 0.000001)
    }

    @Test fun alphaA240_goalProgressVector090() {
        val result=GoalTracking.progress(144.5, 167.0700000000, 181.5)!!
        assertEquals(61.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(181.5 - 167.0700000000), result.remaining, 0.000001)
    }

    @Test fun alphaA241_goalProgressVector091() {
        val result=GoalTracking.progress(145.0, 107.7600000000, 107.0)!!
        assertEquals(98.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(107.0 - 107.7600000000), result.remaining, 0.000001)
    }

    @Test fun alphaA242_goalProgressVector092() {
        val result=GoalTracking.progress(145.5, 158.7600000000, 184.5)!!
        assertEquals(34.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(184.5 - 158.7600000000), result.remaining, 0.000001)
    }

    @Test fun alphaA243_goalProgressVector093() {
        val result=GoalTracking.progress(146.0, 117.6000000000, 106.0)!!
        assertEquals(71.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(106.0 - 117.6000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA244_goalProgressVector094() {
        val result=GoalTracking.progress(146.5, 147.2000000000, 156.5)!!
        assertEquals(7.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(156.5 - 147.2000000000), result.remaining, 0.000001)
    }

    @Test fun alphaA245_goalProgressVector095() {
        val result=GoalTracking.progress(147.0, 142.1600000000, 136.0)!!
        assertEquals(44.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(136.0 - 142.1600000000), result.remaining, 0.000001)
    }

    @Test fun alphaA246_goalProgressVector096() {
        val result=GoalTracking.progress(147.5, 157.2200000000, 159.5)!!
        assertEquals(81.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(159.5 - 157.2200000000), result.remaining, 0.000001)
    }

    @Test fun alphaA247_goalProgressVector097() {
        val result=GoalTracking.progress(148.0, 145.7900000000, 135.0)!!
        assertEquals(17.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(135.0 - 145.7900000000), result.remaining, 0.000001)
    }

    @Test fun alphaA248_goalProgressVector098() {
        val result=GoalTracking.progress(148.5, 156.0600000000, 162.5)!!
        assertEquals(54.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(162.5 - 156.0600000000), result.remaining, 0.000001)
    }

    @Test fun alphaA249_goalProgressVector099() {
        val result=GoalTracking.progress(149.0, 135.3500000000, 134.0)!!
        assertEquals(91.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(134.0 - 135.3500000000), result.remaining, 0.000001)
    }

    @Test fun alphaA250_goalProgressVector100() {
        val result=GoalTracking.progress(149.5, 153.8200000000, 165.5)!!
        assertEquals(27.0, result.percent, 0.000001)
        assertEquals(kotlin.math.abs(165.5 - 153.8200000000), result.remaining, 0.000001)
    }

    @Test fun alphaA251_installmentDistributionVector001() {
        val parts=Financial.installments(0L,1)
        assertEquals(1,parts.size)
        assertEquals(0L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA252_installmentDistributionVector002() {
        val parts=Financial.installments(138L,2)
        assertEquals(2,parts.size)
        assertEquals(138L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA253_installmentDistributionVector003() {
        val parts=Financial.installments(276L,3)
        assertEquals(3,parts.size)
        assertEquals(276L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA254_installmentDistributionVector004() {
        val parts=Financial.installments(414L,4)
        assertEquals(4,parts.size)
        assertEquals(414L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA255_installmentDistributionVector005() {
        val parts=Financial.installments(552L,5)
        assertEquals(5,parts.size)
        assertEquals(552L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA256_installmentDistributionVector006() {
        val parts=Financial.installments(690L,6)
        assertEquals(6,parts.size)
        assertEquals(690L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA257_installmentDistributionVector007() {
        val parts=Financial.installments(828L,7)
        assertEquals(7,parts.size)
        assertEquals(828L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA258_installmentDistributionVector008() {
        val parts=Financial.installments(959L,8)
        assertEquals(8,parts.size)
        assertEquals(959L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA259_installmentDistributionVector009() {
        val parts=Financial.installments(1097L,9)
        assertEquals(9,parts.size)
        assertEquals(1097L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA260_installmentDistributionVector010() {
        val parts=Financial.installments(1235L,10)
        assertEquals(10,parts.size)
        assertEquals(1235L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA261_installmentDistributionVector011() {
        val parts=Financial.installments(1373L,11)
        assertEquals(11,parts.size)
        assertEquals(1373L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA262_installmentDistributionVector012() {
        val parts=Financial.installments(1511L,12)
        assertEquals(12,parts.size)
        assertEquals(1511L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA263_installmentDistributionVector013() {
        val parts=Financial.installments(1649L,13)
        assertEquals(13,parts.size)
        assertEquals(1649L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA264_installmentDistributionVector014() {
        val parts=Financial.installments(1787L,14)
        assertEquals(14,parts.size)
        assertEquals(1787L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA265_installmentDistributionVector015() {
        val parts=Financial.installments(1918L,15)
        assertEquals(15,parts.size)
        assertEquals(1918L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA266_installmentDistributionVector016() {
        val parts=Financial.installments(2056L,16)
        assertEquals(16,parts.size)
        assertEquals(2056L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA267_installmentDistributionVector017() {
        val parts=Financial.installments(2194L,17)
        assertEquals(17,parts.size)
        assertEquals(2194L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA268_installmentDistributionVector018() {
        val parts=Financial.installments(2332L,18)
        assertEquals(18,parts.size)
        assertEquals(2332L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA269_installmentDistributionVector019() {
        val parts=Financial.installments(2470L,19)
        assertEquals(19,parts.size)
        assertEquals(2470L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA270_installmentDistributionVector020() {
        val parts=Financial.installments(2608L,20)
        assertEquals(20,parts.size)
        assertEquals(2608L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA271_installmentDistributionVector021() {
        val parts=Financial.installments(2746L,21)
        assertEquals(21,parts.size)
        assertEquals(2746L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA272_installmentDistributionVector022() {
        val parts=Financial.installments(2877L,22)
        assertEquals(22,parts.size)
        assertEquals(2877L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA273_installmentDistributionVector023() {
        val parts=Financial.installments(3015L,23)
        assertEquals(23,parts.size)
        assertEquals(3015L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA274_installmentDistributionVector024() {
        val parts=Financial.installments(3153L,24)
        assertEquals(24,parts.size)
        assertEquals(3153L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA275_installmentDistributionVector025() {
        val parts=Financial.installments(3291L,1)
        assertEquals(1,parts.size)
        assertEquals(3291L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA276_installmentDistributionVector026() {
        val parts=Financial.installments(3429L,2)
        assertEquals(2,parts.size)
        assertEquals(3429L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA277_installmentDistributionVector027() {
        val parts=Financial.installments(3567L,3)
        assertEquals(3,parts.size)
        assertEquals(3567L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA278_installmentDistributionVector028() {
        val parts=Financial.installments(3705L,4)
        assertEquals(4,parts.size)
        assertEquals(3705L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA279_installmentDistributionVector029() {
        val parts=Financial.installments(3836L,5)
        assertEquals(5,parts.size)
        assertEquals(3836L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA280_installmentDistributionVector030() {
        val parts=Financial.installments(3974L,6)
        assertEquals(6,parts.size)
        assertEquals(3974L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA281_installmentDistributionVector031() {
        val parts=Financial.installments(4112L,7)
        assertEquals(7,parts.size)
        assertEquals(4112L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA282_installmentDistributionVector032() {
        val parts=Financial.installments(4250L,8)
        assertEquals(8,parts.size)
        assertEquals(4250L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA283_installmentDistributionVector033() {
        val parts=Financial.installments(4388L,9)
        assertEquals(9,parts.size)
        assertEquals(4388L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA284_installmentDistributionVector034() {
        val parts=Financial.installments(4526L,10)
        assertEquals(10,parts.size)
        assertEquals(4526L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA285_installmentDistributionVector035() {
        val parts=Financial.installments(4664L,11)
        assertEquals(11,parts.size)
        assertEquals(4664L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA286_installmentDistributionVector036() {
        val parts=Financial.installments(4795L,12)
        assertEquals(12,parts.size)
        assertEquals(4795L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA287_installmentDistributionVector037() {
        val parts=Financial.installments(4933L,13)
        assertEquals(13,parts.size)
        assertEquals(4933L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA288_installmentDistributionVector038() {
        val parts=Financial.installments(5071L,14)
        assertEquals(14,parts.size)
        assertEquals(5071L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA289_installmentDistributionVector039() {
        val parts=Financial.installments(5209L,15)
        assertEquals(15,parts.size)
        assertEquals(5209L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA290_installmentDistributionVector040() {
        val parts=Financial.installments(5347L,16)
        assertEquals(16,parts.size)
        assertEquals(5347L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA291_installmentDistributionVector041() {
        val parts=Financial.installments(5485L,17)
        assertEquals(17,parts.size)
        assertEquals(5485L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA292_installmentDistributionVector042() {
        val parts=Financial.installments(5623L,18)
        assertEquals(18,parts.size)
        assertEquals(5623L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA293_installmentDistributionVector043() {
        val parts=Financial.installments(5754L,19)
        assertEquals(19,parts.size)
        assertEquals(5754L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA294_installmentDistributionVector044() {
        val parts=Financial.installments(5892L,20)
        assertEquals(20,parts.size)
        assertEquals(5892L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA295_installmentDistributionVector045() {
        val parts=Financial.installments(6030L,21)
        assertEquals(21,parts.size)
        assertEquals(6030L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA296_installmentDistributionVector046() {
        val parts=Financial.installments(6168L,22)
        assertEquals(22,parts.size)
        assertEquals(6168L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA297_installmentDistributionVector047() {
        val parts=Financial.installments(6306L,23)
        assertEquals(23,parts.size)
        assertEquals(6306L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA298_installmentDistributionVector048() {
        val parts=Financial.installments(6444L,24)
        assertEquals(24,parts.size)
        assertEquals(6444L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA299_installmentDistributionVector049() {
        val parts=Financial.installments(6582L,1)
        assertEquals(1,parts.size)
        assertEquals(6582L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA300_installmentDistributionVector050() {
        val parts=Financial.installments(6713L,2)
        assertEquals(2,parts.size)
        assertEquals(6713L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA301_installmentDistributionVector051() {
        val parts=Financial.installments(6851L,3)
        assertEquals(3,parts.size)
        assertEquals(6851L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA302_installmentDistributionVector052() {
        val parts=Financial.installments(6989L,4)
        assertEquals(4,parts.size)
        assertEquals(6989L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA303_installmentDistributionVector053() {
        val parts=Financial.installments(7127L,5)
        assertEquals(5,parts.size)
        assertEquals(7127L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA304_installmentDistributionVector054() {
        val parts=Financial.installments(7265L,6)
        assertEquals(6,parts.size)
        assertEquals(7265L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA305_installmentDistributionVector055() {
        val parts=Financial.installments(7403L,7)
        assertEquals(7,parts.size)
        assertEquals(7403L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA306_installmentDistributionVector056() {
        val parts=Financial.installments(7541L,8)
        assertEquals(8,parts.size)
        assertEquals(7541L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA307_installmentDistributionVector057() {
        val parts=Financial.installments(7672L,9)
        assertEquals(9,parts.size)
        assertEquals(7672L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA308_installmentDistributionVector058() {
        val parts=Financial.installments(7810L,10)
        assertEquals(10,parts.size)
        assertEquals(7810L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA309_installmentDistributionVector059() {
        val parts=Financial.installments(7948L,11)
        assertEquals(11,parts.size)
        assertEquals(7948L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA310_installmentDistributionVector060() {
        val parts=Financial.installments(8086L,12)
        assertEquals(12,parts.size)
        assertEquals(8086L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA311_installmentDistributionVector061() {
        val parts=Financial.installments(8224L,13)
        assertEquals(13,parts.size)
        assertEquals(8224L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA312_installmentDistributionVector062() {
        val parts=Financial.installments(8362L,14)
        assertEquals(14,parts.size)
        assertEquals(8362L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA313_installmentDistributionVector063() {
        val parts=Financial.installments(8500L,15)
        assertEquals(15,parts.size)
        assertEquals(8500L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA314_installmentDistributionVector064() {
        val parts=Financial.installments(8631L,16)
        assertEquals(16,parts.size)
        assertEquals(8631L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA315_installmentDistributionVector065() {
        val parts=Financial.installments(8769L,17)
        assertEquals(17,parts.size)
        assertEquals(8769L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA316_installmentDistributionVector066() {
        val parts=Financial.installments(8907L,18)
        assertEquals(18,parts.size)
        assertEquals(8907L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA317_installmentDistributionVector067() {
        val parts=Financial.installments(9045L,19)
        assertEquals(19,parts.size)
        assertEquals(9045L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA318_installmentDistributionVector068() {
        val parts=Financial.installments(9183L,20)
        assertEquals(20,parts.size)
        assertEquals(9183L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA319_installmentDistributionVector069() {
        val parts=Financial.installments(9321L,21)
        assertEquals(21,parts.size)
        assertEquals(9321L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA320_installmentDistributionVector070() {
        val parts=Financial.installments(9459L,22)
        assertEquals(22,parts.size)
        assertEquals(9459L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA321_installmentDistributionVector071() {
        val parts=Financial.installments(9590L,23)
        assertEquals(23,parts.size)
        assertEquals(9590L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA322_installmentDistributionVector072() {
        val parts=Financial.installments(9728L,24)
        assertEquals(24,parts.size)
        assertEquals(9728L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA323_installmentDistributionVector073() {
        val parts=Financial.installments(9866L,1)
        assertEquals(1,parts.size)
        assertEquals(9866L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA324_installmentDistributionVector074() {
        val parts=Financial.installments(10004L,2)
        assertEquals(2,parts.size)
        assertEquals(10004L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA325_installmentDistributionVector075() {
        val parts=Financial.installments(10142L,3)
        assertEquals(3,parts.size)
        assertEquals(10142L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA326_installmentDistributionVector076() {
        val parts=Financial.installments(10280L,4)
        assertEquals(4,parts.size)
        assertEquals(10280L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA327_installmentDistributionVector077() {
        val parts=Financial.installments(10418L,5)
        assertEquals(5,parts.size)
        assertEquals(10418L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA328_installmentDistributionVector078() {
        val parts=Financial.installments(10549L,6)
        assertEquals(6,parts.size)
        assertEquals(10549L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA329_installmentDistributionVector079() {
        val parts=Financial.installments(10687L,7)
        assertEquals(7,parts.size)
        assertEquals(10687L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA330_installmentDistributionVector080() {
        val parts=Financial.installments(10825L,8)
        assertEquals(8,parts.size)
        assertEquals(10825L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA331_installmentDistributionVector081() {
        val parts=Financial.installments(10963L,9)
        assertEquals(9,parts.size)
        assertEquals(10963L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA332_installmentDistributionVector082() {
        val parts=Financial.installments(11101L,10)
        assertEquals(10,parts.size)
        assertEquals(11101L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA333_installmentDistributionVector083() {
        val parts=Financial.installments(11239L,11)
        assertEquals(11,parts.size)
        assertEquals(11239L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA334_installmentDistributionVector084() {
        val parts=Financial.installments(11377L,12)
        assertEquals(12,parts.size)
        assertEquals(11377L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA335_installmentDistributionVector085() {
        val parts=Financial.installments(11508L,13)
        assertEquals(13,parts.size)
        assertEquals(11508L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA336_installmentDistributionVector086() {
        val parts=Financial.installments(11646L,14)
        assertEquals(14,parts.size)
        assertEquals(11646L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA337_installmentDistributionVector087() {
        val parts=Financial.installments(11784L,15)
        assertEquals(15,parts.size)
        assertEquals(11784L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA338_installmentDistributionVector088() {
        val parts=Financial.installments(11922L,16)
        assertEquals(16,parts.size)
        assertEquals(11922L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA339_installmentDistributionVector089() {
        val parts=Financial.installments(12060L,17)
        assertEquals(17,parts.size)
        assertEquals(12060L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA340_installmentDistributionVector090() {
        val parts=Financial.installments(12198L,18)
        assertEquals(18,parts.size)
        assertEquals(12198L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA341_installmentDistributionVector091() {
        val parts=Financial.installments(12336L,19)
        assertEquals(19,parts.size)
        assertEquals(12336L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA342_installmentDistributionVector092() {
        val parts=Financial.installments(12467L,20)
        assertEquals(20,parts.size)
        assertEquals(12467L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA343_installmentDistributionVector093() {
        val parts=Financial.installments(12605L,21)
        assertEquals(21,parts.size)
        assertEquals(12605L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA344_installmentDistributionVector094() {
        val parts=Financial.installments(12743L,22)
        assertEquals(22,parts.size)
        assertEquals(12743L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA345_installmentDistributionVector095() {
        val parts=Financial.installments(12881L,23)
        assertEquals(23,parts.size)
        assertEquals(12881L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA346_installmentDistributionVector096() {
        val parts=Financial.installments(13019L,24)
        assertEquals(24,parts.size)
        assertEquals(13019L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA347_installmentDistributionVector097() {
        val parts=Financial.installments(13157L,1)
        assertEquals(1,parts.size)
        assertEquals(13157L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA348_installmentDistributionVector098() {
        val parts=Financial.installments(13295L,2)
        assertEquals(2,parts.size)
        assertEquals(13295L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA349_installmentDistributionVector099() {
        val parts=Financial.installments(13426L,3)
        assertEquals(3,parts.size)
        assertEquals(13426L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA350_installmentDistributionVector100() {
        val parts=Financial.installments(13564L,4)
        assertEquals(4,parts.size)
        assertEquals(13564L,parts.sum())
        assertTrue(parts.maxOrNull()!!-parts.minOrNull()!! <= 1L)
    }

    @Test fun alphaA351_agendaOverlapVector001() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="10:00",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA352_agendaOverlapVector002() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="10:03",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA353_agendaOverlapVector003() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="10:06",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA354_agendaOverlapVector004() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="10:09",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA355_agendaOverlapVector005() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="10:12",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA356_agendaOverlapVector006() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="10:15",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA357_agendaOverlapVector007() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="10:18",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA358_agendaOverlapVector008() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="10:21",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA359_agendaOverlapVector009() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="10:24",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA360_agendaOverlapVector010() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="10:27",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA361_agendaOverlapVector011() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="10:30",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA362_agendaOverlapVector012() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="10:33",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA363_agendaOverlapVector013() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="10:36",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA364_agendaOverlapVector014() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="10:39",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA365_agendaOverlapVector015() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="10:42",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA366_agendaOverlapVector016() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="10:45",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA367_agendaOverlapVector017() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="10:48",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA368_agendaOverlapVector018() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="10:51",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA369_agendaOverlapVector019() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="10:54",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA370_agendaOverlapVector020() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="10:57",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA371_agendaOverlapVector021() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="11:00",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA372_agendaOverlapVector022() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="11:03",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA373_agendaOverlapVector023() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="11:06",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA374_agendaOverlapVector024() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="11:09",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA375_agendaOverlapVector025() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="11:12",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA376_agendaOverlapVector026() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="11:15",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA377_agendaOverlapVector027() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="11:18",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA378_agendaOverlapVector028() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="11:21",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA379_agendaOverlapVector029() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="11:24",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA380_agendaOverlapVector030() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="11:27",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA381_agendaOverlapVector031() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="11:30",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA382_agendaOverlapVector032() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="11:33",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA383_agendaOverlapVector033() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="11:36",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA384_agendaOverlapVector034() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="11:39",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA385_agendaOverlapVector035() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="11:42",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA386_agendaOverlapVector036() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="11:45",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA387_agendaOverlapVector037() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="11:48",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA388_agendaOverlapVector038() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="11:51",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA389_agendaOverlapVector039() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="11:54",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA390_agendaOverlapVector040() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="11:57",durationMinutes=20)
        assertEquals(true,Agenda.overlaps(first,second))
    }

    @Test fun alphaA391_agendaOverlapVector041() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="12:00",durationMinutes=20)
        assertEquals(false,Agenda.overlaps(first,second))
    }

    @Test fun alphaA392_agendaOverlapVector042() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="12:03",durationMinutes=20)
        assertEquals(false,Agenda.overlaps(first,second))
    }

    @Test fun alphaA393_agendaOverlapVector043() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="12:06",durationMinutes=20)
        assertEquals(false,Agenda.overlaps(first,second))
    }

    @Test fun alphaA394_agendaOverlapVector044() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="12:09",durationMinutes=20)
        assertEquals(false,Agenda.overlaps(first,second))
    }

    @Test fun alphaA395_agendaOverlapVector045() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="12:12",durationMinutes=20)
        assertEquals(false,Agenda.overlaps(first,second))
    }

    @Test fun alphaA396_agendaOverlapVector046() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="12:15",durationMinutes=20)
        assertEquals(false,Agenda.overlaps(first,second))
    }

    @Test fun alphaA397_agendaOverlapVector047() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="12:18",durationMinutes=20)
        assertEquals(false,Agenda.overlaps(first,second))
    }

    @Test fun alphaA398_agendaOverlapVector048() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="12:21",durationMinutes=20)
        assertEquals(false,Agenda.overlaps(first,second))
    }

    @Test fun alphaA399_agendaOverlapVector049() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="12:24",durationMinutes=20)
        assertEquals(false,Agenda.overlaps(first,second))
    }

    @Test fun alphaA400_agendaOverlapVector050() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="10:00",durationMinutes=120)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="12:27",durationMinutes=20)
        assertEquals(false,Agenda.overlaps(first,second))
    }

    @Test fun alphaA401_bmiVector001() {
        assertEquals(23.668639053254,Metrics.bmi(40.0000,130.0000)!!,0.00000001)
    }

    @Test fun alphaA402_bmiVector002() {
        assertEquals(24.291318951709,Metrics.bmi(41.7500,131.1000)!!,0.00000001)
    }

    @Test fun alphaA403_bmiVector003() {
        assertEquals(24.890083104268,Metrics.bmi(43.5000,132.2000)!!,0.00000001)
    }

    @Test fun alphaA404_bmiVector004() {
        assertEquals(25.465856336552,Metrics.bmi(45.2500,133.3000)!!,0.00000001)
    }

    @Test fun alphaA405_bmiVector005() {
        assertEquals(26.019522392290,Metrics.bmi(47.0000,134.4000)!!,0.00000001)
    }

    @Test fun alphaA406_bmiVector006() {
        assertEquals(26.551926035866,Metrics.bmi(48.7500,135.5000)!!,0.00000001)
    }

    @Test fun alphaA407_bmiVector007() {
        assertEquals(27.063875032423,Metrics.bmi(50.5000,136.6000)!!,0.00000001)
    }

    @Test fun alphaA408_bmiVector008() {
        assertEquals(27.556142013544,Metrics.bmi(52.2500,137.7000)!!,0.00000001)
    }

    @Test fun alphaA409_bmiVector009() {
        assertEquals(28.029466235913,Metrics.bmi(54.0000,138.8000)!!,0.00000001)
    }

    @Test fun alphaA410_bmiVector010() {
        assertEquals(28.484555239855,Metrics.bmi(55.7500,139.9000)!!,0.00000001)
    }

    @Test fun alphaA411_bmiVector011() {
        assertEquals(28.922086414164,Metrics.bmi(57.5000,141.0000)!!,0.00000001)
    }

    @Test fun alphaA412_bmiVector012() {
        assertEquals(29.342708473134,Metrics.bmi(59.2500,142.1000)!!,0.00000001)
    }

    @Test fun alphaA413_bmiVector013() {
        assertEquals(29.747042851347,Metrics.bmi(61.0000,143.2000)!!,0.00000001)
    }

    @Test fun alphaA414_bmiVector014() {
        assertEquals(30.135685021340,Metrics.bmi(62.7500,144.3000)!!,0.00000001)
    }

    @Test fun alphaA415_bmiVector015() {
        assertEquals(30.509205738947,Metrics.bmi(64.5000,145.4000)!!,0.00000001)
    }

    @Test fun alphaA416_bmiVector016() {
        assertEquals(30.868152220760,Metrics.bmi(66.2500,146.5000)!!,0.00000001)
    }

    @Test fun alphaA417_bmiVector017() {
        assertEquals(31.213049257864,Metrics.bmi(68.0000,147.6000)!!,0.00000001)
    }

    @Test fun alphaA418_bmiVector018() {
        assertEquals(31.544400269722,Metrics.bmi(69.7500,148.7000)!!,0.00000001)
    }

    @Test fun alphaA419_bmiVector019() {
        assertEquals(31.862688301803,Metrics.bmi(71.5000,149.8000)!!,0.00000001)
    }

    @Test fun alphaA420_bmiVector020() {
        assertEquals(32.168376970341,Metrics.bmi(73.2500,150.9000)!!,0.00000001)
    }

    @Test fun alphaA421_bmiVector021() {
        assertEquals(32.461911357341,Metrics.bmi(75.0000,152.0000)!!,0.00000001)
    }

    @Test fun alphaA422_bmiVector022() {
        assertEquals(32.743718858804,Metrics.bmi(76.7500,153.1000)!!,0.00000001)
    }

    @Test fun alphaA423_bmiVector023() {
        assertEquals(33.014209988880,Metrics.bmi(78.5000,154.2000)!!,0.00000001)
    }

    @Test fun alphaA424_bmiVector024() {
        assertEquals(33.273779142544,Metrics.bmi(80.2500,155.3000)!!,0.00000001)
    }

    @Test fun alphaA425_bmiVector025() {
        assertEquals(33.522805319170,Metrics.bmi(82.0000,156.4000)!!,0.00000001)
    }

    @Test fun alphaA426_bmiVector026() {
        assertEquals(33.761652809272,Metrics.bmi(83.7500,157.5000)!!,0.00000001)
    }

    @Test fun alphaA427_bmiVector027() {
        assertEquals(33.990671846501,Metrics.bmi(85.5000,158.6000)!!,0.00000001)
    }

    @Test fun alphaA428_bmiVector028() {
        assertEquals(34.210199226869,Metrics.bmi(87.2500,159.7000)!!,0.00000001)
    }

    @Test fun alphaA429_bmiVector029() {
        assertEquals(34.420558897057,Metrics.bmi(89.0000,160.8000)!!,0.00000001)
    }

    @Test fun alphaA430_bmiVector030() {
        assertEquals(34.622062513520,Metrics.bmi(90.7500,161.9000)!!,0.00000001)
    }

    @Test fun alphaA431_bmiVector031() {
        assertEquals(34.815009974030,Metrics.bmi(92.5000,163.0000)!!,0.00000001)
    }

    @Test fun alphaA432_bmiVector032() {
        assertEquals(34.999689923172,Metrics.bmi(94.2500,164.1000)!!,0.00000001)
    }

    @Test fun alphaA433_bmiVector033() {
        assertEquals(35.176380233219,Metrics.bmi(96.0000,165.2000)!!,0.00000001)
    }

    @Test fun alphaA434_bmiVector034() {
        assertEquals(35.345348461745,Metrics.bmi(97.7500,166.3000)!!,0.00000001)
    }

    @Test fun alphaA435_bmiVector035() {
        assertEquals(35.506852287212,Metrics.bmi(99.5000,167.4000)!!,0.00000001)
    }

    @Test fun alphaA436_bmiVector036() {
        assertEquals(35.661139923747,Metrics.bmi(101.2500,168.5000)!!,0.00000001)
    }

    @Test fun alphaA437_bmiVector037() {
        assertEquals(35.808450516198,Metrics.bmi(103.0000,169.6000)!!,0.00000001)
    }

    @Test fun alphaA438_bmiVector038() {
        assertEquals(35.949014516538,Metrics.bmi(104.7500,170.7000)!!,0.00000001)
    }

    @Test fun alphaA439_bmiVector039() {
        assertEquals(36.083054042590,Metrics.bmi(106.5000,171.8000)!!,0.00000001)
    }

    @Test fun alphaA440_bmiVector040() {
        assertEquals(36.210783220007,Metrics.bmi(108.2500,172.9000)!!,0.00000001)
    }

    @Test fun alphaA441_bmiVector041() {
        assertEquals(36.332408508389,Metrics.bmi(110.0000,174.0000)!!,0.00000001)
    }

    @Test fun alphaA442_bmiVector042() {
        assertEquals(36.448129012352,Metrics.bmi(111.7500,175.1000)!!,0.00000001)
    }

    @Test fun alphaA443_bmiVector043() {
        assertEquals(36.558136778323,Metrics.bmi(113.5000,176.2000)!!,0.00000001)
    }

    @Test fun alphaA444_bmiVector044() {
        assertEquals(36.662617077813,Metrics.bmi(115.2500,177.3000)!!,0.00000001)
    }

    @Test fun alphaA445_bmiVector045() {
        assertEquals(36.761748677834,Metrics.bmi(117.0000,178.4000)!!,0.00000001)
    }

    @Test fun alphaA446_bmiVector046() {
        assertEquals(36.855704099130,Metrics.bmi(118.7500,179.5000)!!,0.00000001)
    }

    @Test fun alphaA447_bmiVector047() {
        assertEquals(36.944649862830,Metrics.bmi(120.5000,180.6000)!!,0.00000001)
    }

    @Test fun alphaA448_bmiVector048() {
        assertEquals(37.028746726098,Metrics.bmi(122.2500,181.7000)!!,0.00000001)
    }

    @Test fun alphaA449_bmiVector049() {
        assertEquals(37.108149907349,Metrics.bmi(124.0000,182.8000)!!,0.00000001)
    }

    @Test fun alphaA450_bmiVector050() {
        assertEquals(37.183009301518,Metrics.bmi(125.7500,183.9000)!!,0.00000001)
    }

    @Test fun alphaA451_pollockMassReconciliationVector001() {
        val folds=Anthropometry.sites.keys.associateWith { 5.0000 }
        val result=Anthropometry.pollock7("Masculino",18,45.0000,folds)!!
        assertEquals(7*5.0000,result.totalMm,0.000001)
        assertEquals(45.0000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA452_pollockMassReconciliationVector002() {
        val folds=Anthropometry.sites.keys.associateWith { 5.6000 }
        val result=Anthropometry.pollock7("Feminino",19,45.7000,folds)!!
        assertEquals(7*5.6000,result.totalMm,0.000001)
        assertEquals(45.7000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA453_pollockMassReconciliationVector003() {
        val folds=Anthropometry.sites.keys.associateWith { 6.2000 }
        val result=Anthropometry.pollock7("Masculino",20,46.4000,folds)!!
        assertEquals(7*6.2000,result.totalMm,0.000001)
        assertEquals(46.4000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA454_pollockMassReconciliationVector004() {
        val folds=Anthropometry.sites.keys.associateWith { 6.8000 }
        val result=Anthropometry.pollock7("Feminino",21,47.1000,folds)!!
        assertEquals(7*6.8000,result.totalMm,0.000001)
        assertEquals(47.1000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA455_pollockMassReconciliationVector005() {
        val folds=Anthropometry.sites.keys.associateWith { 7.4000 }
        val result=Anthropometry.pollock7("Masculino",22,47.8000,folds)!!
        assertEquals(7*7.4000,result.totalMm,0.000001)
        assertEquals(47.8000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA456_pollockMassReconciliationVector006() {
        val folds=Anthropometry.sites.keys.associateWith { 8.0000 }
        val result=Anthropometry.pollock7("Feminino",23,48.5000,folds)!!
        assertEquals(7*8.0000,result.totalMm,0.000001)
        assertEquals(48.5000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA457_pollockMassReconciliationVector007() {
        val folds=Anthropometry.sites.keys.associateWith { 8.6000 }
        val result=Anthropometry.pollock7("Masculino",24,49.2000,folds)!!
        assertEquals(7*8.6000,result.totalMm,0.000001)
        assertEquals(49.2000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA458_pollockMassReconciliationVector008() {
        val folds=Anthropometry.sites.keys.associateWith { 9.2000 }
        val result=Anthropometry.pollock7("Feminino",25,49.9000,folds)!!
        assertEquals(7*9.2000,result.totalMm,0.000001)
        assertEquals(49.9000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA459_pollockMassReconciliationVector009() {
        val folds=Anthropometry.sites.keys.associateWith { 9.8000 }
        val result=Anthropometry.pollock7("Masculino",26,50.6000,folds)!!
        assertEquals(7*9.8000,result.totalMm,0.000001)
        assertEquals(50.6000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA460_pollockMassReconciliationVector010() {
        val folds=Anthropometry.sites.keys.associateWith { 10.4000 }
        val result=Anthropometry.pollock7("Feminino",27,51.3000,folds)!!
        assertEquals(7*10.4000,result.totalMm,0.000001)
        assertEquals(51.3000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA461_pollockMassReconciliationVector011() {
        val folds=Anthropometry.sites.keys.associateWith { 11.0000 }
        val result=Anthropometry.pollock7("Masculino",28,52.0000,folds)!!
        assertEquals(7*11.0000,result.totalMm,0.000001)
        assertEquals(52.0000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA462_pollockMassReconciliationVector012() {
        val folds=Anthropometry.sites.keys.associateWith { 11.6000 }
        val result=Anthropometry.pollock7("Feminino",29,52.7000,folds)!!
        assertEquals(7*11.6000,result.totalMm,0.000001)
        assertEquals(52.7000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA463_pollockMassReconciliationVector013() {
        val folds=Anthropometry.sites.keys.associateWith { 12.2000 }
        val result=Anthropometry.pollock7("Masculino",30,53.4000,folds)!!
        assertEquals(7*12.2000,result.totalMm,0.000001)
        assertEquals(53.4000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA464_pollockMassReconciliationVector014() {
        val folds=Anthropometry.sites.keys.associateWith { 12.8000 }
        val result=Anthropometry.pollock7("Feminino",31,54.1000,folds)!!
        assertEquals(7*12.8000,result.totalMm,0.000001)
        assertEquals(54.1000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA465_pollockMassReconciliationVector015() {
        val folds=Anthropometry.sites.keys.associateWith { 13.4000 }
        val result=Anthropometry.pollock7("Masculino",32,54.8000,folds)!!
        assertEquals(7*13.4000,result.totalMm,0.000001)
        assertEquals(54.8000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA466_pollockMassReconciliationVector016() {
        val folds=Anthropometry.sites.keys.associateWith { 14.0000 }
        val result=Anthropometry.pollock7("Feminino",33,55.5000,folds)!!
        assertEquals(7*14.0000,result.totalMm,0.000001)
        assertEquals(55.5000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA467_pollockMassReconciliationVector017() {
        val folds=Anthropometry.sites.keys.associateWith { 14.6000 }
        val result=Anthropometry.pollock7("Masculino",34,56.2000,folds)!!
        assertEquals(7*14.6000,result.totalMm,0.000001)
        assertEquals(56.2000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA468_pollockMassReconciliationVector018() {
        val folds=Anthropometry.sites.keys.associateWith { 15.2000 }
        val result=Anthropometry.pollock7("Feminino",35,56.9000,folds)!!
        assertEquals(7*15.2000,result.totalMm,0.000001)
        assertEquals(56.9000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA469_pollockMassReconciliationVector019() {
        val folds=Anthropometry.sites.keys.associateWith { 15.8000 }
        val result=Anthropometry.pollock7("Masculino",36,57.6000,folds)!!
        assertEquals(7*15.8000,result.totalMm,0.000001)
        assertEquals(57.6000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA470_pollockMassReconciliationVector020() {
        val folds=Anthropometry.sites.keys.associateWith { 16.4000 }
        val result=Anthropometry.pollock7("Feminino",37,58.3000,folds)!!
        assertEquals(7*16.4000,result.totalMm,0.000001)
        assertEquals(58.3000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA471_pollockMassReconciliationVector021() {
        val folds=Anthropometry.sites.keys.associateWith { 17.0000 }
        val result=Anthropometry.pollock7("Masculino",38,59.0000,folds)!!
        assertEquals(7*17.0000,result.totalMm,0.000001)
        assertEquals(59.0000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA472_pollockMassReconciliationVector022() {
        val folds=Anthropometry.sites.keys.associateWith { 17.6000 }
        val result=Anthropometry.pollock7("Feminino",39,59.7000,folds)!!
        assertEquals(7*17.6000,result.totalMm,0.000001)
        assertEquals(59.7000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA473_pollockMassReconciliationVector023() {
        val folds=Anthropometry.sites.keys.associateWith { 18.2000 }
        val result=Anthropometry.pollock7("Masculino",40,60.4000,folds)!!
        assertEquals(7*18.2000,result.totalMm,0.000001)
        assertEquals(60.4000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA474_pollockMassReconciliationVector024() {
        val folds=Anthropometry.sites.keys.associateWith { 18.8000 }
        val result=Anthropometry.pollock7("Feminino",41,61.1000,folds)!!
        assertEquals(7*18.8000,result.totalMm,0.000001)
        assertEquals(61.1000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA475_pollockMassReconciliationVector025() {
        val folds=Anthropometry.sites.keys.associateWith { 19.4000 }
        val result=Anthropometry.pollock7("Masculino",42,61.8000,folds)!!
        assertEquals(7*19.4000,result.totalMm,0.000001)
        assertEquals(61.8000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA476_pollockMassReconciliationVector026() {
        val folds=Anthropometry.sites.keys.associateWith { 20.0000 }
        val result=Anthropometry.pollock7("Feminino",43,62.5000,folds)!!
        assertEquals(7*20.0000,result.totalMm,0.000001)
        assertEquals(62.5000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA477_pollockMassReconciliationVector027() {
        val folds=Anthropometry.sites.keys.associateWith { 20.6000 }
        val result=Anthropometry.pollock7("Masculino",44,63.2000,folds)!!
        assertEquals(7*20.6000,result.totalMm,0.000001)
        assertEquals(63.2000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA478_pollockMassReconciliationVector028() {
        val folds=Anthropometry.sites.keys.associateWith { 21.2000 }
        val result=Anthropometry.pollock7("Feminino",45,63.9000,folds)!!
        assertEquals(7*21.2000,result.totalMm,0.000001)
        assertEquals(63.9000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA479_pollockMassReconciliationVector029() {
        val folds=Anthropometry.sites.keys.associateWith { 21.8000 }
        val result=Anthropometry.pollock7("Masculino",46,64.6000,folds)!!
        assertEquals(7*21.8000,result.totalMm,0.000001)
        assertEquals(64.6000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA480_pollockMassReconciliationVector030() {
        val folds=Anthropometry.sites.keys.associateWith { 22.4000 }
        val result=Anthropometry.pollock7("Feminino",47,65.3000,folds)!!
        assertEquals(7*22.4000,result.totalMm,0.000001)
        assertEquals(65.3000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA481_pollockMassReconciliationVector031() {
        val folds=Anthropometry.sites.keys.associateWith { 23.0000 }
        val result=Anthropometry.pollock7("Masculino",48,66.0000,folds)!!
        assertEquals(7*23.0000,result.totalMm,0.000001)
        assertEquals(66.0000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA482_pollockMassReconciliationVector032() {
        val folds=Anthropometry.sites.keys.associateWith { 23.6000 }
        val result=Anthropometry.pollock7("Feminino",49,66.7000,folds)!!
        assertEquals(7*23.6000,result.totalMm,0.000001)
        assertEquals(66.7000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA483_pollockMassReconciliationVector033() {
        val folds=Anthropometry.sites.keys.associateWith { 24.2000 }
        val result=Anthropometry.pollock7("Masculino",50,67.4000,folds)!!
        assertEquals(7*24.2000,result.totalMm,0.000001)
        assertEquals(67.4000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA484_pollockMassReconciliationVector034() {
        val folds=Anthropometry.sites.keys.associateWith { 24.8000 }
        val result=Anthropometry.pollock7("Feminino",51,68.1000,folds)!!
        assertEquals(7*24.8000,result.totalMm,0.000001)
        assertEquals(68.1000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA485_pollockMassReconciliationVector035() {
        val folds=Anthropometry.sites.keys.associateWith { 25.4000 }
        val result=Anthropometry.pollock7("Masculino",52,68.8000,folds)!!
        assertEquals(7*25.4000,result.totalMm,0.000001)
        assertEquals(68.8000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA486_pollockMassReconciliationVector036() {
        val folds=Anthropometry.sites.keys.associateWith { 26.0000 }
        val result=Anthropometry.pollock7("Feminino",53,69.5000,folds)!!
        assertEquals(7*26.0000,result.totalMm,0.000001)
        assertEquals(69.5000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA487_pollockMassReconciliationVector037() {
        val folds=Anthropometry.sites.keys.associateWith { 26.6000 }
        val result=Anthropometry.pollock7("Masculino",54,70.2000,folds)!!
        assertEquals(7*26.6000,result.totalMm,0.000001)
        assertEquals(70.2000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA488_pollockMassReconciliationVector038() {
        val folds=Anthropometry.sites.keys.associateWith { 27.2000 }
        val result=Anthropometry.pollock7("Feminino",55,70.9000,folds)!!
        assertEquals(7*27.2000,result.totalMm,0.000001)
        assertEquals(70.9000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA489_pollockMassReconciliationVector039() {
        val folds=Anthropometry.sites.keys.associateWith { 27.8000 }
        val result=Anthropometry.pollock7("Masculino",56,71.6000,folds)!!
        assertEquals(7*27.8000,result.totalMm,0.000001)
        assertEquals(71.6000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA490_pollockMassReconciliationVector040() {
        val folds=Anthropometry.sites.keys.associateWith { 28.4000 }
        val result=Anthropometry.pollock7("Feminino",57,72.3000,folds)!!
        assertEquals(7*28.4000,result.totalMm,0.000001)
        assertEquals(72.3000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA491_pollockMassReconciliationVector041() {
        val folds=Anthropometry.sites.keys.associateWith { 29.0000 }
        val result=Anthropometry.pollock7("Masculino",58,73.0000,folds)!!
        assertEquals(7*29.0000,result.totalMm,0.000001)
        assertEquals(73.0000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA492_pollockMassReconciliationVector042() {
        val folds=Anthropometry.sites.keys.associateWith { 29.6000 }
        val result=Anthropometry.pollock7("Feminino",59,73.7000,folds)!!
        assertEquals(7*29.6000,result.totalMm,0.000001)
        assertEquals(73.7000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA493_pollockMassReconciliationVector043() {
        val folds=Anthropometry.sites.keys.associateWith { 30.2000 }
        val result=Anthropometry.pollock7("Masculino",60,74.4000,folds)!!
        assertEquals(7*30.2000,result.totalMm,0.000001)
        assertEquals(74.4000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA494_pollockMassReconciliationVector044() {
        val folds=Anthropometry.sites.keys.associateWith { 30.8000 }
        val result=Anthropometry.pollock7("Feminino",61,75.1000,folds)!!
        assertEquals(7*30.8000,result.totalMm,0.000001)
        assertEquals(75.1000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA495_pollockMassReconciliationVector045() {
        val folds=Anthropometry.sites.keys.associateWith { 31.4000 }
        val result=Anthropometry.pollock7("Masculino",62,75.8000,folds)!!
        assertEquals(7*31.4000,result.totalMm,0.000001)
        assertEquals(75.8000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA496_pollockMassReconciliationVector046() {
        val folds=Anthropometry.sites.keys.associateWith { 32.0000 }
        val result=Anthropometry.pollock7("Feminino",63,76.5000,folds)!!
        assertEquals(7*32.0000,result.totalMm,0.000001)
        assertEquals(76.5000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA497_pollockMassReconciliationVector047() {
        val folds=Anthropometry.sites.keys.associateWith { 32.6000 }
        val result=Anthropometry.pollock7("Masculino",64,77.2000,folds)!!
        assertEquals(7*32.6000,result.totalMm,0.000001)
        assertEquals(77.2000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA498_pollockMassReconciliationVector048() {
        val folds=Anthropometry.sites.keys.associateWith { 33.2000 }
        val result=Anthropometry.pollock7("Feminino",65,77.9000,folds)!!
        assertEquals(7*33.2000,result.totalMm,0.000001)
        assertEquals(77.9000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA499_pollockMassReconciliationVector049() {
        val folds=Anthropometry.sites.keys.associateWith { 33.8000 }
        val result=Anthropometry.pollock7("Masculino",66,78.6000,folds)!!
        assertEquals(7*33.8000,result.totalMm,0.000001)
        assertEquals(78.6000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA500_pollockMassReconciliationVector050() {
        val folds=Anthropometry.sites.keys.associateWith { 34.4000 }
        val result=Anthropometry.pollock7("Feminino",67,79.3000,folds)!!
        assertEquals(7*34.4000,result.totalMm,0.000001)
        assertEquals(79.3000,result.fatMassKg+result.leanMassKg,0.000001)
        assertTrue(result.fatPercent in 3.0..60.0)
    }

    @Test fun alphaA501_timelineEndpointVector001() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(1L).toString() to 20.0,
            start.plusDays(2L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA502_timelineEndpointVector002() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(1L).toString() to 20.0,
            start.plusDays(3L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA503_timelineEndpointVector003() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(2L).toString() to 20.0,
            start.plusDays(4L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA504_timelineEndpointVector004() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(2L).toString() to 20.0,
            start.plusDays(5L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA505_timelineEndpointVector005() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(3L).toString() to 20.0,
            start.plusDays(6L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA506_timelineEndpointVector006() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(3L).toString() to 20.0,
            start.plusDays(7L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA507_timelineEndpointVector007() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(4L).toString() to 20.0,
            start.plusDays(8L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA508_timelineEndpointVector008() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(4L).toString() to 20.0,
            start.plusDays(9L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA509_timelineEndpointVector009() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(5L).toString() to 20.0,
            start.plusDays(10L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA510_timelineEndpointVector010() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(5L).toString() to 20.0,
            start.plusDays(11L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA511_timelineEndpointVector011() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(6L).toString() to 20.0,
            start.plusDays(12L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA512_timelineEndpointVector012() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(6L).toString() to 20.0,
            start.plusDays(13L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA513_timelineEndpointVector013() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(7L).toString() to 20.0,
            start.plusDays(14L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA514_timelineEndpointVector014() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(7L).toString() to 20.0,
            start.plusDays(15L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA515_timelineEndpointVector015() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(8L).toString() to 20.0,
            start.plusDays(16L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA516_timelineEndpointVector016() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(8L).toString() to 20.0,
            start.plusDays(17L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA517_timelineEndpointVector017() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(9L).toString() to 20.0,
            start.plusDays(18L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA518_timelineEndpointVector018() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(9L).toString() to 20.0,
            start.plusDays(19L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA519_timelineEndpointVector019() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(10L).toString() to 20.0,
            start.plusDays(20L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA520_timelineEndpointVector020() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(10L).toString() to 20.0,
            start.plusDays(21L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA521_timelineEndpointVector021() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(11L).toString() to 20.0,
            start.plusDays(22L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA522_timelineEndpointVector022() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(11L).toString() to 20.0,
            start.plusDays(23L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA523_timelineEndpointVector023() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(12L).toString() to 20.0,
            start.plusDays(24L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA524_timelineEndpointVector024() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(12L).toString() to 20.0,
            start.plusDays(25L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA525_timelineEndpointVector025() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(13L).toString() to 20.0,
            start.plusDays(26L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA526_timelineEndpointVector026() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(13L).toString() to 20.0,
            start.plusDays(27L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA527_timelineEndpointVector027() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(14L).toString() to 20.0,
            start.plusDays(28L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA528_timelineEndpointVector028() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(14L).toString() to 20.0,
            start.plusDays(29L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA529_timelineEndpointVector029() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(15L).toString() to 20.0,
            start.plusDays(30L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA530_timelineEndpointVector030() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(15L).toString() to 20.0,
            start.plusDays(31L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA531_timelineEndpointVector031() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(16L).toString() to 20.0,
            start.plusDays(32L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA532_timelineEndpointVector032() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(16L).toString() to 20.0,
            start.plusDays(33L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA533_timelineEndpointVector033() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(17L).toString() to 20.0,
            start.plusDays(34L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA534_timelineEndpointVector034() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(17L).toString() to 20.0,
            start.plusDays(35L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA535_timelineEndpointVector035() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(18L).toString() to 20.0,
            start.plusDays(36L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA536_timelineEndpointVector036() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(18L).toString() to 20.0,
            start.plusDays(37L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA537_timelineEndpointVector037() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(19L).toString() to 20.0,
            start.plusDays(38L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA538_timelineEndpointVector038() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(19L).toString() to 20.0,
            start.plusDays(39L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA539_timelineEndpointVector039() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(20L).toString() to 20.0,
            start.plusDays(40L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA540_timelineEndpointVector040() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(20L).toString() to 20.0,
            start.plusDays(41L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA541_timelineEndpointVector041() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(21L).toString() to 20.0,
            start.plusDays(42L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA542_timelineEndpointVector042() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(21L).toString() to 20.0,
            start.plusDays(43L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA543_timelineEndpointVector043() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(22L).toString() to 20.0,
            start.plusDays(44L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA544_timelineEndpointVector044() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(22L).toString() to 20.0,
            start.plusDays(45L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA545_timelineEndpointVector045() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(23L).toString() to 20.0,
            start.plusDays(46L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA546_timelineEndpointVector046() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(23L).toString() to 20.0,
            start.plusDays(47L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA547_timelineEndpointVector047() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(24L).toString() to 20.0,
            start.plusDays(48L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA548_timelineEndpointVector048() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(24L).toString() to 20.0,
            start.plusDays(49L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA549_timelineEndpointVector049() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(25L).toString() to 20.0,
            start.plusDays(50L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA550_timelineEndpointVector050() {
        val start=LocalDate.of(2020,1,1)
        val points=EvaluationTimeline.points(listOf(
            start.toString() to 10.0,
            start.plusDays(25L).toString() to 20.0,
            start.plusDays(51L).toString() to 30.0))
        assertEquals(3,points.size)
        assertEquals(0.0f,points.first().x,0.000001f)
        assertEquals(1.0f,points.last().x,0.000001f)
        assertTrue(points.all { it.y in 0.0f..1.0f })
    }

    @Test fun alphaA551_adherenceVector001() {
        val result=Metrics.adherence(true,0,false,0)
        assertEquals(0.0000000000,result!!,0.000001)
    }

    @Test fun alphaA552_adherenceVector002() {
        val result=Metrics.adherence(false,1,true,3)
        assertEquals(3.0000000000,result!!,0.000001)
    }

    @Test fun alphaA553_adherenceVector003() {
        val result=Metrics.adherence(true,2,true,6)
        assertEquals(4.0000000000,result!!,0.000001)
    }

    @Test fun alphaA554_adherenceVector004() {
        val result=Metrics.adherence(false,3,false,9)
        assertNull(result)
    }

    @Test fun alphaA555_adherenceVector005() {
        val result=Metrics.adherence(true,4,true,12)
        assertEquals(8.0000000000,result!!,0.000001)
    }

    @Test fun alphaA556_adherenceVector006() {
        val result=Metrics.adherence(false,5,true,15)
        assertEquals(15.0000000000,result!!,0.000001)
    }

    @Test fun alphaA557_adherenceVector007() {
        val result=Metrics.adherence(true,6,false,18)
        assertEquals(6.0000000000,result!!,0.000001)
    }

    @Test fun alphaA558_adherenceVector008() {
        val result=Metrics.adherence(false,7,true,21)
        assertEquals(21.0000000000,result!!,0.000001)
    }

    @Test fun alphaA559_adherenceVector009() {
        val result=Metrics.adherence(true,8,true,24)
        assertEquals(16.0000000000,result!!,0.000001)
    }

    @Test fun alphaA560_adherenceVector010() {
        val result=Metrics.adherence(false,9,false,27)
        assertNull(result)
    }

    @Test fun alphaA561_adherenceVector011() {
        val result=Metrics.adherence(true,10,true,30)
        assertEquals(20.0000000000,result!!,0.000001)
    }

    @Test fun alphaA562_adherenceVector012() {
        val result=Metrics.adherence(false,11,true,33)
        assertEquals(33.0000000000,result!!,0.000001)
    }

    @Test fun alphaA563_adherenceVector013() {
        val result=Metrics.adherence(true,12,false,36)
        assertEquals(12.0000000000,result!!,0.000001)
    }

    @Test fun alphaA564_adherenceVector014() {
        val result=Metrics.adherence(false,13,true,39)
        assertEquals(39.0000000000,result!!,0.000001)
    }

    @Test fun alphaA565_adherenceVector015() {
        val result=Metrics.adherence(true,14,true,42)
        assertEquals(28.0000000000,result!!,0.000001)
    }

    @Test fun alphaA566_adherenceVector016() {
        val result=Metrics.adherence(false,15,false,45)
        assertNull(result)
    }

    @Test fun alphaA567_adherenceVector017() {
        val result=Metrics.adherence(true,16,true,48)
        assertEquals(32.0000000000,result!!,0.000001)
    }

    @Test fun alphaA568_adherenceVector018() {
        val result=Metrics.adherence(false,17,true,51)
        assertEquals(51.0000000000,result!!,0.000001)
    }

    @Test fun alphaA569_adherenceVector019() {
        val result=Metrics.adherence(true,18,false,54)
        assertEquals(18.0000000000,result!!,0.000001)
    }

    @Test fun alphaA570_adherenceVector020() {
        val result=Metrics.adherence(false,19,true,57)
        assertEquals(57.0000000000,result!!,0.000001)
    }

    @Test fun alphaA571_adherenceVector021() {
        val result=Metrics.adherence(true,20,true,60)
        assertEquals(40.0000000000,result!!,0.000001)
    }

    @Test fun alphaA572_adherenceVector022() {
        val result=Metrics.adherence(false,21,false,63)
        assertNull(result)
    }

    @Test fun alphaA573_adherenceVector023() {
        val result=Metrics.adherence(true,22,true,66)
        assertEquals(44.0000000000,result!!,0.000001)
    }

    @Test fun alphaA574_adherenceVector024() {
        val result=Metrics.adherence(false,23,true,69)
        assertEquals(69.0000000000,result!!,0.000001)
    }

    @Test fun alphaA575_adherenceVector025() {
        val result=Metrics.adherence(true,24,false,72)
        assertEquals(24.0000000000,result!!,0.000001)
    }

    @Test fun alphaA576_adherenceVector026() {
        val result=Metrics.adherence(false,25,true,75)
        assertEquals(75.0000000000,result!!,0.000001)
    }

    @Test fun alphaA577_adherenceVector027() {
        val result=Metrics.adherence(true,26,true,78)
        assertEquals(52.0000000000,result!!,0.000001)
    }

    @Test fun alphaA578_adherenceVector028() {
        val result=Metrics.adherence(false,27,false,81)
        assertNull(result)
    }

    @Test fun alphaA579_adherenceVector029() {
        val result=Metrics.adherence(true,28,true,84)
        assertEquals(56.0000000000,result!!,0.000001)
    }

    @Test fun alphaA580_adherenceVector030() {
        val result=Metrics.adherence(false,29,true,87)
        assertEquals(87.0000000000,result!!,0.000001)
    }

    @Test fun alphaA581_adherenceVector031() {
        val result=Metrics.adherence(true,30,false,90)
        assertEquals(30.0000000000,result!!,0.000001)
    }

    @Test fun alphaA582_adherenceVector032() {
        val result=Metrics.adherence(false,31,true,93)
        assertEquals(93.0000000000,result!!,0.000001)
    }

    @Test fun alphaA583_adherenceVector033() {
        val result=Metrics.adherence(true,32,true,96)
        assertEquals(64.0000000000,result!!,0.000001)
    }

    @Test fun alphaA584_adherenceVector034() {
        val result=Metrics.adherence(false,33,false,99)
        assertNull(result)
    }

    @Test fun alphaA585_adherenceVector035() {
        val result=Metrics.adherence(true,34,true,1)
        assertEquals(17.5000000000,result!!,0.000001)
    }

    @Test fun alphaA586_adherenceVector036() {
        val result=Metrics.adherence(false,35,true,4)
        assertEquals(4.0000000000,result!!,0.000001)
    }

    @Test fun alphaA587_adherenceVector037() {
        val result=Metrics.adherence(true,36,false,7)
        assertEquals(36.0000000000,result!!,0.000001)
    }

    @Test fun alphaA588_adherenceVector038() {
        val result=Metrics.adherence(false,37,true,10)
        assertEquals(10.0000000000,result!!,0.000001)
    }

    @Test fun alphaA589_adherenceVector039() {
        val result=Metrics.adherence(true,38,true,13)
        assertEquals(25.5000000000,result!!,0.000001)
    }

    @Test fun alphaA590_adherenceVector040() {
        val result=Metrics.adherence(false,39,false,16)
        assertNull(result)
    }

    @Test fun alphaA591_adherenceVector041() {
        val result=Metrics.adherence(true,40,true,19)
        assertEquals(29.5000000000,result!!,0.000001)
    }

    @Test fun alphaA592_adherenceVector042() {
        val result=Metrics.adherence(false,41,true,22)
        assertEquals(22.0000000000,result!!,0.000001)
    }

    @Test fun alphaA593_adherenceVector043() {
        val result=Metrics.adherence(true,42,false,25)
        assertEquals(42.0000000000,result!!,0.000001)
    }

    @Test fun alphaA594_adherenceVector044() {
        val result=Metrics.adherence(false,43,true,28)
        assertEquals(28.0000000000,result!!,0.000001)
    }

    @Test fun alphaA595_adherenceVector045() {
        val result=Metrics.adherence(true,44,true,31)
        assertEquals(37.5000000000,result!!,0.000001)
    }

    @Test fun alphaA596_adherenceVector046() {
        val result=Metrics.adherence(false,45,false,34)
        assertNull(result)
    }

    @Test fun alphaA597_adherenceVector047() {
        val result=Metrics.adherence(true,46,true,37)
        assertEquals(41.5000000000,result!!,0.000001)
    }

    @Test fun alphaA598_adherenceVector048() {
        val result=Metrics.adherence(false,47,true,40)
        assertEquals(40.0000000000,result!!,0.000001)
    }

    @Test fun alphaA599_adherenceVector049() {
        val result=Metrics.adherence(true,48,false,43)
        assertEquals(48.0000000000,result!!,0.000001)
    }

    @Test fun alphaA600_adherenceVector050() {
        val result=Metrics.adherence(false,49,true,46)
        assertEquals(46.0000000000,result!!,0.000001)
    }
}
