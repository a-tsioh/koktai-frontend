package fr.magistry.koktai

import kotlin.browser.window

import fr.magistry.koktai.components.WordCardComponent
import fr.magistry.koktai.components.TextComponent
import fr.magistry.koktai.components.WordsPage


external class Vue(params: dynamic) {
    companion object {
        fun component(name: String, params: Any)
    }
}



external class VueRouter(routes: dynamic)
data class RouteDef(val path: String, val component: dynamic) {
    val props = true
}


fun main(args: Array<String>) {
    // déclaration des web-components mis à dispo
    Vue.component("WordComponent", WordCardComponent())
    Vue.component("WordsPage", WordsPage)
    Vue.component("TextComponent", TextComponent())

    // config des routes de l'app
    val router = VueRouter(object {
     val routes =   arrayOf(
         RouteDef("/words/:sino", WordsPage)

     )
    })

    // création de l'appli
    val app = Vue( object {
        val el = "#main"
        val data = object {
            val message = "plop24"
        }
        val router = router
    })
}
