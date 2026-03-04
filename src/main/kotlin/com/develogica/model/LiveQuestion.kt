package com.develogica.model

import kotlinx.serialization.Serializable

data class LiveQuestion(
    val text: String? = null,
    val image: String? = null,
    val answer: String,
    val options: List<Option>,
    val moreInfo: String? = null,
    val tags: List<String>,
    val questionType: QuestionType = QuestionType.IS_TEXT,
    val questionMode: QuestionMode = QuestionMode.MCQ
)

@Serializable
data class Option(val text: String, val isCorrect: Boolean = false)

val sampleQuestions = listOf(
    LiveQuestion(
        text = "ርእሰ ከተማ ኤርትራ መን ትበሃል?", options = listOf(
            Option(text = "ኣስመራ", isCorrect = true),
            Option(text = "ኣዲስ ኣበባ", isCorrect = false),
            Option(text = "ኣንካራ", isCorrect = false),
            Option(text = "ምባባነ", isCorrect = false)
        ), answer = "ኣስመራ", tags = emptyList()
    ),

    LiveQuestion(
        text = "ኤርትራ ኣበይ ትርከብ?",
        options = listOf(
            Option(text = "ኣፍሪቃ", isCorrect = true),
            Option(text = "ኤስያ", isCorrect = false),
            Option(text = "ኣውሮጳ", isCorrect = false),
            Option(text = "ኣውስትራልያ", isCorrect = false)
        ),
        answer = "ኣፍሪቃ", tags = emptyList(),
    ),
)

enum class QuestionMode {
    MCQ, QA
}
