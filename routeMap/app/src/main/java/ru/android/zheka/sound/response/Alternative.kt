package ru.android.zheka.sound.response

class Alternative {
    lateinit var transcript:String
    lateinit var confidence:String
    lateinit var words: ArrayList<Word>

    override fun toString(): String {
        val sb = StringBuilder()
        for (word:Word in words)
            sb.append(word.word).append(" ")
        return sb.toString()
    }
}
