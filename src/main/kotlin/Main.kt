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

class MyComponent(val data: () -> dynamic){
    public val template = createHTML().div {
            span { text("euh") }
            br { }
            span { text ("{{message}}") }
        }
}


external class VueRouter(routes: dynamic)
data class RouteDef(val path: String, val component: dynamic)


fun main(args: Array<String>) {
    println("coucou12")
    window
        .fetch( "http://search.magistry.fr:8080/exist/rest/db/apps/Koktai/test.xq")
        .then {

            it.json().then{data -> console.log(data)}
        }
   // Vue.component("MyComp", Component() { -> VMessage("hallo") })


    val Foo = MyComponent() { -> VMessage("Fooo")}
    val Bar = MyComponent() { -> VMessage("Barr")}

    val router = VueRouter(object {
     val routes =   arrayOf(
         RouteDef("/foo", Foo),
         RouteDef("/bar", Bar)
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