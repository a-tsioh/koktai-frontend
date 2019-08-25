package fr.magistry.koktai

import kotlinx.html.*
import kotlinx.html.dom.*
import kotlinx.html.stream.createHTML
import org.w3c.dom.Element
import kotlin.browser.document
import org.w3c.fetch.RequestInit
import kotlin.browser.window

//external class Vue(p: VParams) {}
data class VParams(val el: String, val data: VMessage)



data class VMessage(val message: String)
external object Vue {
    fun component(name: String, params: Any)
}

class Component( val data: () -> dynamic){
    public val template = createHTML().div {
            span { text("euh") }
            span { text ("{{message}}") }
        }
}

fun main(args: Array<String>) {
    println("coucou12")
    window
        .fetch( "http://search.magistry.fr:8080/exist/rest/db/apps/Koktai/test.xq")
        .then {

            it.json().then{data -> console.log(data)}
        }
    Vue.component("MyComp", Component() { -> VMessage("hallo") })
}