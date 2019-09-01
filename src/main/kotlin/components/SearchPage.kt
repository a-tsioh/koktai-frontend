package fr.magistry.koktai.components

object SearchPage {
    val template = """
        <div class="ui grid">
            <div class="eight wide column">
                <sinograms-page :sino='query'/>
            </div>
            <div class="eight wide column">
                <words-page :sino='query'/>
            </div>
        </div>
    """.trimIndent()

    val props = arrayOf("query")
}