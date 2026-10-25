package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.Intake
import java.io.ByteArrayOutputStream

/** Offline PDF 1.4 AcroForm. Client identity is read-only; response fields remain editable. */
object FillableIntakePdf {
    fun generate(item:Intake):ByteArray {
        require(item.studentId.isNotBlank() && item.student.isNotBlank()){"Escolha um aluno cadastrado."}
        val questions=item.answers.keys.filter{it.startsWith("_label_")}.sortedBy{it.substringAfterLast('_').toIntOrNull() ?: 0}
        require(questions.size in 1..60)
        fun hex(text:String)="<FEFF"+text.toByteArray(Charsets.UTF_16BE).joinToString(""){"%02X".format(it.toInt() and 255)}+">"
        fun literal(text:String)="("+text.replace("\\","\\\\").replace("(","\\(").replace(")","\\)").replace("\r"," ").replace("\n"," ")+")"
        val objects=mutableListOf<String?>()
        fun reserve():Int {objects.add(null);return objects.size}
        fun set(id:Int,value:String){objects[id-1]=value}
        val catalog=reserve();val pages=reserve();val font=reserve();val form=reserve()
        set(font,"<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>")
        val pageIds=mutableListOf<Int>();val fieldIds=mutableListOf<Int>()
        fun wrap(label:String):List<String> {
            val result=mutableListOf<String>();var rest=label.replace("\n"," ").trim()
            while(rest.isNotEmpty()) {
                val end=if(rest.length<=50)rest.length else rest.lastIndexOf(' ',50).takeIf{it>0} ?: 50
                result.add(rest.take(end));rest=rest.drop(end).trimStart()
            }
            return result.ifEmpty{listOf("")}
        }
        val titleLines=wrap(item.answers["_template"].orEmpty())
        val dateY=minOf(725,780-titleLines.size*12-10);val nameY=dateY-23;val idY=nameY-37;val questionY=idY-35
        val batches=mutableListOf<MutableList<String>>();var used=0
        questions.forEach{key->val height=maxOf(116,wrap(item.answers.getValue(key)).size*12+80)
            if(batches.isEmpty() || used+height>questionY-70){batches.add(mutableListOf());used=0}
            batches.last().add(key);used+=height
        }
        batches.forEachIndexed{pageIndex,keys->
            val page=reserve();val stream=reserve();pageIds.add(page);val annots=mutableListOf<Int>()
            val content=StringBuilder()
            fun text(x:Int,y:Int,size:Int,label:String){content.append("BT /Helv $size Tf $x $y Td ${literal(label)} Tj ET\n")}
            fun field(name:String,label:String,value:String,x:Int,y:Int,width:Int,height:Int,readonly:Boolean=false){
                val id=reserve();fieldIds.add(id);annots.add(id)
                set(id,"<< /Type /Annot /Subtype /Widget /FT /Tx /Ff ${if(readonly)1 else 4096} /F 4 /T ${literal(name)} /TU ${hex(label)} /V ${hex(value)} /Rect [$x $y ${x+width} ${y+height}] /P $page 0 R /DA (/Helv 10 Tf 0 g) /MK << /BC [.4 .5 .45] /BG [1 1 1] >> /BS << /W .5 /S /S >> >>")
            }
            text(40,800,18,"EvoluFísico — Anamnese")
            titleLines.forEachIndexed{i,l->text(40,780-i*12,10,l)}
            text(40,dateY,9,"Página ${pageIndex+1} · ${item.date} · Respostas autodeclaradas; não concede aptidão.")
            text(40,nameY,10,"Aluno:");field("nome_cliente_$pageIndex","Nome do aluno",item.student,78,nameY-9,474,18,true)
            field("id_cliente_$pageIndex","Identificador do aluno",item.studentId,40,idY,512,18,true)
            var usedHeight=0
            keys.forEach{key->
                val number=key.substringAfterLast('_');val label=item.answers.getValue(key)
                val lines=wrap(label);val top=questionY-usedHeight
                lines.forEachIndexed{i,l->text(40,top-i*12,10,l)}
                field("resposta_$number",label,item.answers["response_$number"].orEmpty(),40,top-lines.size*12-64,512,50)
                usedHeight+=maxOf(116,lines.size*12+80)
            }
            val raw=content.toString().toByteArray(charset("windows-1252"))
            set(stream,"<< /Length ${raw.size} >>\nstream\n"+String(raw,charset("windows-1252"))+"endstream")
            set(page,"<< /Type /Page /Parent $pages 0 R /MediaBox [0 0 595 842] /Resources << /Font << /Helv $font 0 R >> >> /Contents $stream 0 R /Annots [${annots.joinToString(" "){"$it 0 R"}}] >>")
        }
        set(pages,"<< /Type /Pages /Count ${pageIds.size} /Kids [${pageIds.joinToString(" "){"$it 0 R"}}] >>")
        set(form,"<< /Fields [${fieldIds.joinToString(" "){"$it 0 R"}}] /NeedAppearances true /DR << /Font << /Helv $font 0 R >> >> /DA (/Helv 10 Tf 0 g) >>")
        set(catalog,"<< /Type /Catalog /Pages $pages 0 R /AcroForm $form 0 R >>")
        return ByteArrayOutputStream().use{out->
            fun write(text:String){out.write(text.toByteArray(charset("windows-1252")))}
            write("%PDF-1.4\n%âãÏÓ\n");val offsets=mutableListOf<Int>()
            objects.forEachIndexed{i,obj->offsets.add(out.size());write("${i+1} 0 obj\n${requireNotNull(obj)}\nendobj\n")}
            val xref=out.size();write("xref\n0 ${objects.size+1}\n0000000000 65535 f \n")
            offsets.forEach{write(it.toString().padStart(10,'0')+" 00000 n \n")}
            write("trailer\n<< /Size ${objects.size+1} /Root $catalog 0 R >>\nstartxref\n$xref\n%%EOF\n");out.toByteArray()
        }
    }
}
