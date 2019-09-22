package fr.magistry.koktai.utils

object Tailo {

    fun fromZhuyin(s: String): String {
        return s.map {
            when (it) {
                ' ' -> " "
                'ㄅ' -> "p"
                'ㄆ' -> "ph"
                'ㄇ' -> "m"
                'ㄈ' -> "f"
                'ㄉ' -> "t"
                'ㄊ' -> "th"
                'ㄋ' -> "n"
                'ㄌ' -> "l"
                'ㄍ' -> "k"
                'ㄎ' -> "kh"
                'ㄏ' -> "h"
                'ㄐ' -> "ts"
                'ㄑ' -> "tsh"
                'ㄒ' -> "s"
                'ㄖ' -> "l"
                'ㄗ' -> "ts"
                'ㄘ' -> "tsh"
                'ㄙ' -> "s"
                'ㄚ' -> "a"
                'ㄛ' -> "oo"
                'ㄜ' -> "o"
                'ㄝ' -> "e"
                'ㄞ' -> "ai"
                'ㄟ' -> "ei"
                'ㄠ' -> "au"
                'ㄡ' -> "ou"
                'ㄢ' -> "an"
                'ㄣ' -> "n"
                'ㄤ' -> "ang"
                'ㄥ' -> "ng"
                'ㄦ' -> "er"
                'ㄧ' -> "i"
                'ㄨ' -> "u"
                'ㄩ' -> "ü"
                'ㄫ' -> "ng"
                'ㆠ' -> "b"
                'ㆡ' -> "j"
                'ㆢ' -> "j"
                'ㆣ' -> "g"
                'ㆤ' -> "e"
                'ㆥ' -> "enn"
                'ㆦ' -> "oo"
                'ㆧ' -> "onn"
                'ㆨ' -> "ir"
                'ㆩ' -> "ann"
                'ㆪ' -> "inn"
                'ㆫ' -> "unn"
                'ㆬ' -> "m"
                'ㆭ' -> "ng"
                'ㆮ' -> "ainn"
                'ㆯ' -> "aunn"
                'ㆰ' -> "am"
                'ㆱ' -> "om"
                'ㆲ' -> "ong"
                'ㆴ' -> "p"
                'ㆵ' -> "t"
                'ㆶ' -> "k"
                'ㆷ' -> "h"
                else -> ""
            }
        }.joinToString("")
            .replace("ook","ok")
    }

}