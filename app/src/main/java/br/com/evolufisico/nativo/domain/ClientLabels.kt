package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.Evaluation
import java.util.Locale

/** Homonyms need an unambiguous label even when imported IDs share their first six characters. */
object ClientLabels {
    fun choices(students:List<Evaluation>):List<Pair<String,String>> {
        val unique=students.distinctBy{it.studentId}
        val groups=unique.groupBy{it.name.trim().lowercase(Locale.ROOT)}
        return unique.map{student->
            val peers=groups.getValue(student.name.trim().lowercase(Locale.ROOT))
            val label=if(peers.size==1)student.name else {
                val maximum=peers.maxOf{it.studentId.length}.coerceAtLeast(6)
                val size=(6..maximum).firstOrNull{length->peers.none{it.studentId!=student.studentId && it.studentId.take(length)==student.studentId.take(length)}} ?: maximum
                "${student.name} · ${student.studentId.take(size)}"
            }
            student.studentId to label
        }
    }
}
