package fr.magistry.koktai.components

import fr.magistry.koktai.components.TextComponent.text_component
import fr.magistry.koktai.components.TextSpanComponent.text_span_component
import fr.magistry.koktai.utils.Tailo
import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.stream.createHTML
import kotlin.browser.document
import kotlin.math.min

abstract class TextElem {
    abstract val s: String
    abstract val key: String
}

data class RawText(override val key: String, override val s: String): TextElem()
data class GText(override val key: String,val ref: String, override val s: String): TextElem()
data class RubyText(override val key: String,val ref: String, val parts: Array<String>): TextElem() {
    override val s: String = parts.joinToString("\n")
}

val tones: List<String> = "˫ˋˊ˪ㆷㆵㆶ".toList().map {it -> it.toString() }

fun analyzeRuby(s: String): Array<String> {
    val hasDot: Boolean = s.contains('\u0358')
    val (toneIdx, tone) = s.findAnyOf(tones) ?: Pair(s.length, null)
    val segmental = s.substring(0,toneIdx)
    val result = listOfNotNull(segmental, tone).toTypedArray()
    if(hasDot) result[result.lastIndex] = "\u2024" + result[result.lastIndex]
    return result
}

data class IDSText(override val key: String,val ref: String, override val s: String): TextElem()
data class MappedText(override val key: String,val ref: String, override val s: String): TextElem()


class TextData(val content: Array<TextElem> ) {
    companion object {

        fun splitTone(s: String): String =
            s.replace(Regex("(?=[˫ˋˊ˪ㆷㆵㆶ])"),"\n")

        fun parseJson(json: Any): TextData = when(json) {
            is Array<dynamic> -> TextData(json.mapIndexed { i,e ->
                if(e is String) RawText(i.toString(),e)
                else {
                    val txt: String = e.g["#text"]
                    val ref: String = e.g.ref
                    val type = ref.split("-")[0]
                    when(type) {
                        "ruby" ->  RubyText(i.toString(),ref, analyzeRuby(txt)) //splitTone(txt))
                        "mapped" ->  MappedText(i.toString(),ref, splitTone(txt))
                        "hj" -> IDSText(i.toString(),ref, splitTone(txt))
                        else -> GText(i.toString(), ref, txt)
                    }

                }
            }.toTypedArray())
            else -> TextData(emptyArray())
        }
    }

    override fun toString(): String =
        content
            .map {it.s}
            .joinToString("")
}


object TextComponent {
    class Component(consumer: TagConsumer<*>) :
        HTMLTag("text-component", consumer, emptyMap(),
            inlineTag = true, emptyTag = false) {
    }

    fun SPAN.text_component(block: Component.() -> Unit = {}) {
        Component(consumer).visit(block)
    }


    val template = createHTML()
        .span {
            span() {
                attributes["v-if"] = "simple"
                attributes["v-bind:style"] = "style"
                +"{{text}}"
            }
            span("with_popup ruby") {
                attributes["v-else-if"] = "isRuby"
                attributes["v-bind:style"] = "style"
                attributes["v-bind:data-html"] = "popup"
                p("ruby") {
                    attributes["v-for"] = "l in text"
                    +"{{l}}"
                }
            }
            span("with_popup ruby") {
                attributes["v-else-if"] = "isImg"
                attributes["v-bind:data-html"] = "popup"
                img {
                    attributes["v-bind:style"] = "styleImg"
                    attributes["v-bind:src"] = "imgSrc"
                }
            }
            span("with_popup") {
                attributes["v-else"] = ""
                attributes["v-bind:style"] = "style"
                attributes["v-bind:data-html"] = "popup"
                +"{{text}}"
            }
        }

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
        val isImg = { ->
            val self = js("this")
            self.elem is IDSText
        }
        val imgSrc = { ->
            val self = js("this")
            val ref: String = self.elem.ref
            val (_, font, code) = ref.split("-")
            "http://koktai-beta.magistry.fr/assets/img/${font.drop(1).toLowerCase()}/${code.drop(1).toLowerCase()}.png"
            //(document.create.img { src = "http://koktai-beta.magistry.fr/assets/img/${font.drop(1).toLowerCase()}/${code.drop(1).toLowerCase()}.png"})
        }
        val text = { ->
            val self = js("this")
            when(self.elem) {
                is RubyText -> self.elem.parts
                is IDSText -> {
                    val ref: String = self.elem.ref
                    val (_, font, code) = ref.split("-")
                    (document.create.img { src = "http://koktai-beta.magistry.fr/assets/img/${font.drop(1).toLowerCase()}/${code.drop(1).toLowerCase()}.png"})
                    //"<img src='http://koktai-beta.magistry.fr/assets/img/${font.drop(1).toLowerCase()}/${code.drop(1).toLowerCase()}.png'></img>"
                }
                else -> self.elem.s
            }

        }
        val popup = { ->
            val self = js("this")
            val ref: String = self.elem.ref
            val(_, font, code) = ref.split("-")
            if(self.elem is RubyText)
                "<div class='content'><img src='http://koktai-beta.magistry.fr/assets/img/${font.drop(1).toLowerCase()}/${code.drop(1).toLowerCase()}.png'></img></div>" +
                        "<span class=\"ui small\">${Tailo.fromZhuyin((self.elem.parts as Array<String>).joinToString(""))}</span>"
            else
                ""
                self.elem.s
                //"<div class='content'><img src='http://koktai-beta.magistry.fr/assets/img/${font.drop(1).toLowerCase()}/${code.drop(1).toLowerCase()}.png'></img></div>"
        }
        val styleImg = { ->
            """
                display : inline-block;
                position: relative; 
                top:0px;
                margin: 0px;
                padding: 0px;
                height: 1.1em;
                vertical-align: middle;
                padding-bottom: 0.2em;
            """.trimIndent()
        }
        val style = {->
            val self = js("this")
            when(self.elem) {
                is RubyText -> {
                    val txt: String = self.elem.parts[0]
                    val l = txt.length
                    val fs = min(0.5, (1.0 / l)).toString()
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
                }
                is RawText -> """ 
                    display : inline-block;
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

object TextSpanComponent {
    class Component(consumer: TagConsumer<*>) :
        HTMLTag("text-span-component", consumer, emptyMap(),
            inlineTag = true, emptyTag = false) {
    }

    fun P.text_span_component(block: TextSpanComponent.Component.() -> Unit = {}) {
        TextSpanComponent.Component(consumer).visit(block)
    }
    fun DIV.text_span_component(block: TextSpanComponent.Component.() -> Unit = {}) {
        TextSpanComponent.Component(consumer).visit(block)
    }

    val template = createHTML()
        .span {
            text_component {
                attributes["v-for"] = "t in text.content"
                attributes["v-bind:key"] = "t.key"
                attributes["v-bind:elem"] = "t"
            }
        }

    val props = arrayOf("text")
}

data class WordData(val form: String, val reading: String, val definition: TextData)

object WordCardComponent {
    class Component(consumer: TagConsumer<*>) :
        HTMLTag("word-component", consumer, emptyMap(),
            inlineTag = true, emptyTag = false) {
    }

    fun DIV.word_component(block: Component.() -> Unit = {}) {
        Component(consumer).visit(block)
    }

    val template = createHTML()
        .div("ui stackable card") {
            div("content") {
                div("large header") { +"{{wd.form}}"}
                div("meta") {+"{{wd.reading}}"}
                div("description") {
                    text_span_component() {
                        attributes["v-bind:text"] = "wd.definition"
                    }
                }
            }
        }

    val props = arrayOf("word")

    val computed = object {
        val wd = { ->
            val self = js("this")
            val w = self.word
            WordData(w.form, w.reading, TextData.parseJson(w.definition))
        }
    }
}

data class SinogramData(val pron: TextData,
                        val fanqie: TextData,
                        val guoyin: TextData,
                        val taikam: TextData,
                        val pumin: TextData,
                        val words: Array<String>)


object SinogramCardComponent {
    class Component(consumer: TagConsumer<*>) :
        HTMLTag("sinogram-component", consumer, emptyMap(),
            inlineTag = true, emptyTag = false) {
    }

    fun DIV.sinogram_component(block: Component.() -> Unit = {}) {
        Component(consumer).visit(block)
    }


    class RouterLinkComponent(consumer: TagConsumer<*>) :
        HTMLTag("router-link", consumer, emptyMap(),
            inlineTag = true, emptyTag = false) {
    }

    private fun P.router_link(block: RouterLinkComponent.() -> Unit = {}) {
        RouterLinkComponent(consumer).visit(block)
    }


    val template = createHTML()
        .div("ui stackable card") {
            div("content") {
                div("large header") {+"{{sino.form}}"}
                div("huge meta") {
                    text_span_component {
                        attributes["v-bind:text"] = "e.fanqie"
                    }
                }
                div("ui segment") {
                    p { +"國音："
                        text_span_component {attributes["v-bind:text"] = "e.guoyin"}
                    }
                    p { +"台甘："
                        text_span_component {attributes["v-bind:text"] = "e.taikam"}
                    }
                    p { +"普閩："
                        text_span_component {attributes["v-bind:text"] = "e.pumin"}
                    }
                }
                div("ui segment") {
                    h2 {+"詞："}
                    p(){
                        attributes["v-for"] = "w in e.words"
                        router_link {
                            attributes["v-bind:to"] = "{name: 'words', params: {sino: w} }"
                            +"{{w}}"
                        }
                    }
                }
            }
        }



    val template2  = """
        <div class="ui stackable card">
        <div class="content">
            <div class="large header">{{sino.form}}</div>
            <div class="huge meta"><text-span-component :text="e.pron"/></div>
            <div class="description">
                <div class="ui segment">
                    <text-span-component :text="e.fanqie"/>
                </div>
                <div class="ui segment">
                    <p>國音：<text-span-component :text="e.guoyin"/></p>
                    <p>台甘：<text-span-component :text="e.taikam"/></p>
                    <p>普閩：<text-span-component :text="e.pumin"/></p>
                </div>
                <div class="ui segment">
                <h2>詞:</h2>
                <p v-for="w in e.words">
                    <router-link :to="{name: 'words', params: {sino: w}  }">{{w}}</router-link>
                </p>
                </div>
            </div>
        </div>
        </div>
    """.trimIndent()

    val props = arrayOf("sino")

    val computed = object {
        val e = { ->
            val self = js("this")
            val s = self.sino
            console.log(self.sino)
            val sd = SinogramData(
                TextData.parseJson(s?.pron),
                TextData.parseJson(s?.fanqie),
                TextData.parseJson(s?.guoyin),
                TextData.parseJson(s?.taikam),
                TextData.parseJson(s?.pumin),
                s.words
            )
            console.log(sd)
            sd
        }
    }

}