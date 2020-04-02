package fr.magistry.koktai.components

import fr.magistry.koktai.components.WordCardComponent.word_component
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import kotlin.browser.window


object WordsPage {

    class Component(consumer: TagConsumer<*>) :
        HTMLTag("words-page", consumer, emptyMap(),
            inlineTag = true, emptyTag = false) {
    }

    fun DIV.words_page(block: Component.() -> Unit = {}) {
        Component(consumer).visit(block)
    }

    val template = createHTML()
        .div("ui inverted segment") {
            style {unsafe {+"min-height:10em"} }
            h2 {+"詞"}
            div("ui container") {
                attributes["v-if"] = "loading"
                div("ui active dimmer") {
                    div("ui large indeterminate text loader") {
                        +"查 「{{ sino }}」 中"
                    }
                }
            }
            div("ui Huge two stackable cards") {
                attributes["v-else"] = ""
                word_component {
                    attributes["v-for"] = "item in words"
                    attributes["v-bind:word"] = "item"
                    attributes["v-bind:key"] = "item.key"
                }

            }
        }

    val props = arrayOf("sino")
    fun data(): dynamic  {
        return object {
            var loading = true
            val words = emptyArray<dynamic>()
        }
    }
    val watch  = object {
        val sino = fun (to: String, _: dynamic) {
            val self = js("this")
            self.fetchData(to)
        }
    }

    fun get_self() = this.asDynamic().self

    val methods = object {
        val fetchData = { query: String ->
            val vue = js("this")
            vue.loading = true
            vue.words.length = 0
            window
                .fetch("https://data.koktai.net/koktai/test.xq?q=$query")
                .then {
                    it.json().then { d ->
                        val data: Array<dynamic> = d.asDynamic()
                        if(data.size == 0) vue.words.length = 0
                        else {
                            val newWords: Array<dynamic> = data[0]["word"]
                            newWords.indices.forEach { i ->
                                val w = newWords[i]
                                w["key"] = w["form"] + i
                            }
                            vue.words = newWords
                        }
                        vue.loading = false
                    }
                }
        }
    }

    fun created() {
        this.asDynamic().fetchData(this.asDynamic().sino)
    }
}
