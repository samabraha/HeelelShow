package com.develogica.model

data class QuestionDTO(
    val text: String? = null,
    val image: String? = null,
    val answer: String,
    val options: List<Option>,
    val moreInfo: String? = null,
    val tags: List<String>,
    val questionType: QuestionType = QuestionType.IS_TEXT
) {
    fun toLiveQuestion() = LiveQuestion(
        questionType = questionType,
        text = text,
        image = image,
        options = options,
        answer = answer,
        tags = tags,
        moreInfo = moreInfo
    )
}

enum class QuestionType {
    IS_TEXT, IS_IMAGE
}
