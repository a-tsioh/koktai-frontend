package fr.magistry.koktai

import kotlinx.html.*
import kotlinx.html.stream.createHTML
import kotlin.browser.window

//external class Vue(p: VParams) {}
data class VParams(val el: String, val data: VMessage)



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
        <div>
            <span>{{word.form}}|{{word.reading}}</span>
            <div>{{ word.definition}}</div>
        </div>
    """.trimIndent()
    val props = arrayOf("word")
}

class MyComponent(val data: () -> dynamic){
    /*val template = createHTML().div {
            span { text("{{ sino }}") }
            br { }
            span { text ("{{message}}") }
        } */
    val template = """
        <div>
            <span>{{ sino }}</span>
            <div>{{ message }}</div>
            <word-component
              v-for="item in words"
              v-bind:word="item"
              v-bind:key="item.key"></word-component>
        </div>
    """.trimIndent()
    val props = arrayOf("sino")
    var sino: dynamic = ""
    var message: String = ""
    var words: dynamic = null
    object methods {
        fun fetchData(query: String, vue: dynamic) {
            window
                .fetch("http://search.magistry.fr:8080/exist/rest/db/apps/Koktai/test.xq?q=$query")
                .then {
                    it.json().then { data ->
                        console.log(data)
                        vue.message = data.asDynamic()[0]["word"][0]["reading"]
                        val words: Array<dynamic> = data.asDynamic()[0]["word"]
                        words.indices.forEach { i ->
                            words[i]["key"] = i
                            val defElems: Array<dynamic> = words[i]["definition"]
                            words[i]["definition"] = defElems.joinToString(",", limit = 5, truncated = "。。。")
                        }
                        vue.words = words
                        console.log(vue)

                    }
                }
        }
    }
    private val watch = {  ->
        val it = this
        object  {
            val `$route` = { -> methods.fetchData(it.sino, it) }
        }
    }


    //fun fetchData( query: String) {methods.fetchData(query)}

    fun created() {
        console.log("created !")
        console.log(this)
        methods.fetchData(this.sino, this)
    }

}


external class VueRouter(routes: dynamic)
data class RouteDef(val path: String, val component: dynamic) {
    val props = true
}


fun main(args: Array<String>) {
    println("coucou12")
   //Vue.component("MyComp", Component() { -> VMessage("hallo") })
    Vue.component("WordComponent", WordComponent())


    val Foo = MyComponent() { -> VMessage("loading")}
    val Bar = MyComponent() { -> VMessage("Barr")}

    val router = VueRouter(object {
     val routes =   arrayOf(
         RouteDef("/foo/:sino", Foo),
         RouteDef("/bar/:sino", Bar)
     )
    })

    val app = Vue( object {
        val el = "#coucou"
        val data = object {
            val message = "plop"
        }
        val router = router
    })//.asDynamic().`$mount`("#coucou")
}