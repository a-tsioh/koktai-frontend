package fr.magistry.koktai

import kotlinx.html.*
import kotlinx.html.stream.createHTML
import kotlin.browser.window



data class VMessage(val message: String)
external class Vue(params: dynamic) {
    companion object {
        fun component(name: String, params: Any)
    }
}


class CompMeth() {

}

class WordComponent() {
    val template = """
        <div class="ui card">
        <div class="content">
            <div class="large header">{{word.form}}</div>
            <div class="meta">{{word.reading}}</div>
            <div class="description">{{word.definition}}</div>
        </div>
        </div>
    """.trimIndent()
    val props = arrayOf("word")
}

object MyComponent {
    val template = """
        <div>
            <span>{{ sino }}</span>
            <div>{{ message }}</div>
            <div class="ui cards">
                <word-component
                  v-for="item in words"
                  v-bind:word="item"
                  v-bind:key="item.key"></word-component>
            </div>
        </div>
    """.trimIndent()
    val props = arrayOf("sino")
    fun data(): dynamic  {
        return object {
            val message = "Loading..."
            val words = emptyArray<dynamic>()
            val self = null
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
                vue.message = "loading..."
                vue.words.length = 0
                window
                    .fetch("http://search.magistry.fr:8080/exist/rest/db/apps/Koktai/test.xq?q=$query")
                    .then {
                        it.json().then { d ->
                            val newWords: Array<dynamic> = d.asDynamic()[0]["word"]
                            newWords.indices.forEach { i ->
                                val w = newWords[i]
                                w["key"] = w["form"] + i
                                val defElems: Array<dynamic> = w["definition"]
                                w["definition"] = defElems.joinToString("")//, limit = 5, truncated = "。。。")

                            }
                            vue.message = newWords[0]["key"]
                            vue.words = newWords
                            console.log(vue.words)
                        }
                    }
            }
        }

    fun created() {
        this.asDynamic().fetchData(this.asDynamic().sino)
    }
}


external class VueRouter(routes: dynamic)
data class RouteDef(val path: String, val component: dynamic) {
    val props = true
}


fun main(args: Array<String>) {
    // déclaration des web-components mis à dispo
    Vue.component("WordComponent", WordComponent())
    Vue.component("MyComponent", MyComponent)

    // config des routes de l'app
    val router = VueRouter(object {
     val routes =   arrayOf(
         RouteDef("/foo/:sino", MyComponent)

     )
    })

    // création de l'appli
    val app = Vue( object {
        val el = "#main"
        val data = object {
            val message = "plop"
        }
        val router = router
    })
}
