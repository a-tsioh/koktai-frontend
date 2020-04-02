package fr.magistry.koktai.components

import fr.magistry.koktai.components.SinogramCardComponent.sinogram_component
import fr.magistry.koktai.components.WordCardComponent.word_component
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import kotlin.browser.window


object SinogramsPage {

    class Component(consumer: TagConsumer<*>) :
        HTMLTag("sinograms-page", consumer, emptyMap(),
            inlineTag = true, emptyTag = false) {
    }

    fun DIV.sinograms_page(block: Component.() -> Unit = {}) {
        Component(consumer).visit(block)
    }

    val template = createHTML()
        .div("ui inverted segment") {
            style { unsafe { +"min-height:10em" } }
            h2 { +"字"}
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
                sinogram_component {
                    attributes["v-for"] = "item in entries"
                    attributes["v-bind:sino"] = "item"
                    attributes["v-bind:key"] = "item.key"
                }

            }
        }

    val props = arrayOf("sino")
    fun data(): dynamic  {
        return object {
            var loading = true
            val entries = emptyArray<dynamic>()
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
            vue.entries.length = 0
            for(zi in query) {
                window
                    .fetch("https://data.koktai.net/koktai/sino.xq?q=$zi")
                    .then {
                        it.json().then { d ->
                            val data: Array<dynamic> = d.asDynamic()
                            if (data.size == 0) vue.entries.length = 0
                            else {
                                val newEntries: Array<dynamic> = data[0]["sinogram"]
                                console.log(newEntries)
                                newEntries.indices.forEach { i ->
                                    console.log(i)
                                    val w = newEntries[i]
                                    w["key"] = vue.sino + i
                                    //w["form"] = vue.sino
                                }
                                for (e in newEntries) vue.entries.push(e)
                            }
                            vue.loading = false
                        }
                    }
            }
        }
    }

    fun created() {
        this.asDynamic().fetchData(this.asDynamic().sino)
    }
}
