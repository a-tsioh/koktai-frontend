package fr.magistry.koktai

import fr.magistry.koktai.components.*


external class Vue(params: dynamic) {
    companion object {
        fun component(name: String, params: Any)
    }
}



external class VueRouter(routes: dynamic)
data class RouteDef(val name: String, val path: String, val component: dynamic) {
    val props = true
}


fun main(args: Array<String>) {
    // déclaration des web-components mis à dispo
    Vue.component("WordComponent", WordCardComponent)
    Vue.component("SinogramComponent", SinogramCardComponent)
    Vue.component("WordsPage", WordsPage)
    Vue.component("SinogramsPage", SinogramsPage)
    Vue.component("TextComponent", TextComponent)
    Vue.component("TextSpanComponent", TextSpanComponent)
    Vue.component("WelcomePage", WelcomePage)

    // config des routes de l'app
    val router = VueRouter(object {
     val routes =   arrayOf(
         RouteDef("root", "/", WelcomePage),
         RouteDef("search","/search/:query", SearchPage),
         RouteDef("words","/words/:sino", WordsPage),
         RouteDef("sinograms","/sino/:sino", SinogramsPage)
     )
    })

    // création de l'appli
    val app = Vue( object {
        val el = "#main"
        val data = object {
            val message = "plop25"
            val query = ""
        }
        val router = router
        val methods = object {
            val search = { ev: dynamic ->
                val self = js("this")
                console.log(ev)
                console.log(self.query)
                self.`$router`.push( object { val name =  "search"; val params = object { val query = self.query } })

            }
        }
    })
}
