package fr.magistry.koktai.components

object SearchPage {
    val template = """
        <div class="ui grid">
            <div class="eight wide column">
                <div class="ui inverted segment" style="min-height:10em">
                <h2>字</h2>
                    <sinograms-page :sino='query'/>
                </div>
            </div>
            <div class="eight wide column">
                <div class="ui inverted segment" style="min-height:10em">
                <h2>詞</h2>
                    <words-page :sino='query'/>
                </div>
            </div>
        </div>
    """.trimIndent()

    val props = arrayOf("query")
}