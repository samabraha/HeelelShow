package com.develogica.data

import com.develogica.util.Config
import com.develogica.model.QuestionDTO
import com.develogica.model.QuestionType
import kotlin.io.path.Path

class QuizRepository(val config: Config, val quizDao: QuizDao) {
    private val questions: List<QuestionDTO>
    private val tags: Set<String>

    init {
        println("Initializing QuizRepository")
        questions = quizDao.getQuestions().prepareImages(true)
//            .removeWordyQuestions(maxText = 100, maxOption = 75)
        println("Loaded ${questions.size} questions.")
        quizDao.closeDatabase()
        tags = questions.flatMap { it.tags }.toSet()
        println("Discovered ${tags.size} tags.")
    }

    /**
     * Filters questions to remove questions with missing images.
     */
    private fun List<QuestionDTO>.prepareImages(logMissingImage: Boolean = false): List<QuestionDTO> {
        val beforeSize = size
        return map { question ->
            if (question.questionType == QuestionType.IS_IMAGE && question.image != null) {
                question.copy(image = Path(config.imageRoot, question.image).toString())
            } else question
        }.filter { q ->
            if (q.questionType == QuestionType.IS_IMAGE && q.image != null) {
                imageExists(q.image).also {
                    if (!it && logMissingImage) {
                        println("Missing image: ${q.image}")
                    }
                }
            } else true
        }.also { x ->
            println("Removed ${beforeSize - x.size} questions without images.")
        }
    }

    private fun imageExists(imagePath: String): Boolean {
        return Path(imagePath).toFile().exists()
    }

    fun filterQuestions(vararg hasTags: String, ensureAllTags: Boolean = false): List<QuestionDTO> {
        println("Filtering questions for tags: ${hasTags.toList()}")
        if (hasTags.isEmpty()) {
            println("No tags specified, returning all questions.")
            return questions
        }

        if (hasTags.none { tags.contains(it) }) {
            println("None of the ${questions.size} questions contain any of the specified tags: ${hasTags.toSet()}")
            return emptyList()
        }

        return if (ensureAllTags) {
            questions.filter { q ->
                hasTags.all { t -> q.tags.contains(t) }
            }
        } else {
            questions.filter { q ->
                hasTags.any { t -> q.tags.contains(t) }
            }
        }.also {
            val modifier = if (ensureAllTags) "all" else "any of"
            println("Filtered to ${it.size} questions containing $modifier ${hasTags.size} tags: $hasTags")
        }
    }

    fun List<QuestionDTO>.removeWordyQuestions(maxText: Int, maxOption: Int): List<QuestionDTO> {
        return filter {
            it.questionType == QuestionType.IS_TEXT
        }.filter { q ->
            (q.text?.length ?: 0) < maxText && q.options.all { it.text.length < maxOption }
        }.also {
            println("Removed ${this.size - it.size} wordy questions.")
        }
    }
}
