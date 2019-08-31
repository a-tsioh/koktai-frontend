package fr.magistry.koktai.components

import kotlin.browser.window


object WordsPage {
    val template = """
        <div>
            <span>{{ sino }}</span>
            <div>{{ message }}</div>
            <div class="ui Huge cards">
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
                        }
                        vue.message = newWords[0]["key"]
                        vue.words = newWords
                    }
                }
        }
    }

    fun created() {
        this.asDynamic().fetchData(this.asDynamic().sino)
    }
}
