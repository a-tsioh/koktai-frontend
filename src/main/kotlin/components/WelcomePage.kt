package fr.magistry.koktai.components

object WelcomePage {
    val template = """
        <div class="ui transparent main text container">
          <div class="ui transparent vertical masthead center aligned segment">
            <h1 class="ui inverted  huge header">國臺對照活用辭典</h1>
            <h2 class="ui inverted huge header">原作者為吳守禮教授</h2>
              <div class="ui huge segment">
                <p>除原始資料外，此檔案庫內轉換格式、重新編排的編輯著作權（如果有的話）皆以 CC0 釋出，衍生著作物應以原始資料之授權為準。</p>
              <p>《吳守禮國台對照活用辭典》作者：吳守禮（Ngo Shiu-Le、Wu Shouli） ，本網站資料由吳守禮家族授權中華民國維基媒體協會，以創用CC 姓名標示-相同方式分享 3.0 台灣 授權條款釋出。</p>
                <div class="ui image"><img src="./assets/images/by-sa.svg"></div>
              </div>
          </div>
        </div>
        """.trimIndent()
}