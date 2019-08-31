package fr.magistry.koktai.components

import kotlin.browser.document
import kotlin.browser.window
import kotlin.math.min

abstract class TextElem {
    abstract val s: String
    abstract val key: String
}

data class RawText(override val key: String, override val s: String): TextElem()
data class GText(override val key: String,val ref: String, override val s: String): TextElem()
data class RubyText(override val key: String,val ref: String, override val s: String): TextElem()
data class IDSText(override val key: String,val ref: String, override val s: String): TextElem()
data class MappedText(override val key: String,val ref: String, override val s: String): TextElem()


class TextData(val content: Array<TextElem> ) {
    companion object {

        fun splitTone(s: String): String =
            s.replace(Regex("(?=[˫ˋˊ˪ㆷㆵㆶ])"),"\n")

        fun parseJson(json: Array<dynamic>): TextData = TextData(json.mapIndexed { i,e ->
            if(e is String) RawText(i.toString(),e)
            else {
                val txt: String = e.g["#text"]
                val ref: String = e.g.ref
                val type = ref.split("-")[0]
                console.log(ref)
                when(type) {
                    "ruby" ->  RubyText(i.toString(),ref, splitTone(txt))
                    "mapped" ->  MappedText(i.toString(),ref, splitTone(txt))
                    "hj" -> IDSText(i.toString(),ref, splitTone(txt))
                    else -> GText(i.toString(), ref, txt)
                }

            }
        }.toTypedArray())
    }

    override fun toString(): String =
        content
            .map {it.s}
            .joinToString("")
}


class TextComponent() {
    val template = """
        <span>
            <span v-if="simple" :style="style">{{text}}</span>
            <pre v-else-if="isRuby"
                    class="with_popup"
                    :style="style"
                    v-bind:data-html="popup">{{text}}</pre>
            <span v-else :style="style" class="with_popup" :data-html="popup">{{text}}</span>
        </span>
    """.trimIndent()

    val props = arrayOf("elem")

    val computed = object {
        val simple = { ->
            val self = js("this")
            self.elem is RawText
        }
        val isRuby = {->
            val self = js("this")
            self.elem is RubyText
        }
        val text = { ->
            val self = js("this")
            self.elem.s
        }
        val popup = { ->
            val self = js("this")
            val ref: String = self.elem.ref
            val(_, font, code) = ref.split("-")
            "<div class='content'><img src='http://koktai-beta.magistry.fr/assets/img/${font.drop(1).toLowerCase()}/${code.drop(1).toLowerCase()}.png'></img></div>"
        }

        val style = {->
            val self = js("this")
            val txt: String = self.elem.s
            val l = txt.replace("\n.*".toRegex(), "").length
            val fs = min(0.5, (1.0 / l)).toString()
            when(self.elem) {
                is RubyText ->
                """font-size: ${fs}em;
                position: relative;
                top: 0.2em;
                display: inline-block;
                writing-mode: vertical-lr;
                margin: 0px;
                margin-right: 0.2em;
                padding-top:0.4em;
                line-height:1em;
                vertical-align: center;
                text-align: center;
                text-orientation: upright;
                """.trimIndent()
                is RawText -> """ 
                    display : inline -block;
                    position: relative; 
                    top: 0 px;
                    """.trimIndent()
                else -> ""
            }
        }
    }

    fun mounted() {
        val jq = js("$")
        val self = js("this")
        jq(".with_popup", self.`$el`).popup()
    }
}

data class WordData(val form: String, val reading: String, val definition: TextData)

class WordCardComponent() {
    val template = """
        <div class="ui card">
        <div class="content">
            <div class="large header">{{wd.form}}</div>
            <div class="meta">{{wd.reading}}</div>
            <div class="description">
                <text-component 
                    v-for="t in wd.definition.content"
                    v-bind:key="t.key"
                    v-bind:elem="t"></text-component>
            </div>
        </div>
        </div>
    """.trimIndent()
    val props = arrayOf("word")

    val computed = object {
        val wd = { ->
            val self = js("this")
            val w = self.word
            WordData(w.form, w.reading, TextData.parseJson(w.definition))
        }
    }
}