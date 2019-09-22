package fr.magistry.koktai.components

import kotlin.browser.window


object SinogramsPage {
    val template = """
        <div class="ui inverted segment" style="min-height:10em">
            <h2>字</h2>
            <div v-if="loading" class="ui container">
                <div class="ui active dimmer">
                    <div class="ui large  indeterminate text  loader">查 「{{ sino }}」 中</div>
                </div>
            </div>
            <div v-else class="ui two stackable cards">
                <sinogram-component
                  v-for="item in entries"
                  v-bind:sino="item"
                  v-bind:key="item.key"></sinogram-component>
            </div>
        </div>
    """.trimIndent()
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
