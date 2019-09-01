package fr.magistry.koktai.components

import kotlin.browser.window


object WordsPage {
    val template = """
        <div>
            <div v-if="loading" class="ui inverted container">
                    <div class="ui large inverted indeterminate text active loader">查 「{{ sino }}」 中</div>
            </div>
            <div v-else class="ui Huge two stackable cards">
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
                .fetch("http://search.magistry.fr/koktai/test.xq?q=$query")
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
