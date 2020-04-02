package fr.magistry.koktai.components

import fr.magistry.koktai.components.SinogramsPage.sinograms_page
import fr.magistry.koktai.components.WordsPage.words_page
import kotlinx.html.*
import kotlinx.html.stream.createHTML

object SearchPage {

    val template: String = createHTML()
        .div("ui grid") {
            div("eight wide column") {
                sinograms_page() {attributes["v-bind:sino"] = "query" }
            }
            div("eight wide column") {
                words_page() {attributes["v-bind:sino"] = "query"}
            }
        }

    val props = arrayOf("query")
}