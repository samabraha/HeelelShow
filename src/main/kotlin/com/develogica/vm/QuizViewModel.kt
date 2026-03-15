package com.develogica.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.develogica.data.QuizRepository
import com.develogica.model.LiveQuestion
import com.develogica.model.QuestionDTO
import com.develogica.model.QuestionMode
import com.develogica.model.sampleQuestions
import com.develogica.util.ColorUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.skiko.currentNanoTime
import kotlin.collections.plusAssign
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class QuizViewModel(
    private val repository: QuizRepository,
    vararg tags: String,
    var quizMode: QuizMode = QuizMode.TimedReveal,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private val random = Random(currentNanoTime())
    var questions by mutableStateOf(repository.filterQuestions(*tags).shuffled())
        private set
    var selectedQuestions: MutableList<LiveQuestion> = mutableStateListOf()
        private set

    val tags = questions.flatMap { it.tags }.toSet()
    var mode: Mode by mutableStateOf(Mode.Selection)
        private set

    val questionDuration: Duration = 7.seconds
    val answerDuration: Duration = 3.seconds

    private var questionCounter = 0

    var uiState by mutableStateOf(createNewUiState())
        private set

    fun handleSelectionAction(selectionAction: SelectionAction) {
        coroutineScope.launch {
            when (selectionAction) {
                is SelectionAction.LoadQuestions -> startSelection(selectionAction.tag)
                SelectionAction.ClearSelection -> clearSelection()
                is SelectionAction.StartQuiz -> startQuiz(selectionAction.mode)
                is SelectionAction.AddQuestion -> addQuestion(selectionAction.question)
                SelectionAction.SelectAll -> selectAllQuestions()
                is SelectionAction.ChangeMode -> switchQuestionMode(selectionAction.question, selectionAction.checked)
                is SelectionAction.RemoveQuestion -> removeQuestion(selectionAction.question)
            }
        }
    }

    fun handleQuizAction(quizAction: QuizAction) {
        coroutineScope.launch {
            when (quizAction) {
                QuizAction.ShowAnswer -> showAnswer()
                QuizAction.NextQuestion -> setNextQuestion()
                QuizAction.StartTimer -> startTimer()
            }
        }
    }

    private fun startSelection(tag: String) {
        println("Loading questions for tag: $tag")
        questions = repository.filterQuestions(tag).shuffled()
        println("Loaded ${questions.size} questions.")
        mode = Mode.Selection
    }

    private fun clearSelection() {
        selectedQuestions.clear()
    }

    private fun startQuiz(quizMode: QuizMode) {
        this.quizMode = quizMode
        mode = Mode.Playing
        setNextQuestion()
    }

    private fun addQuestion(questionDTO: QuestionDTO) {
        val question = questionDTO.toLiveQuestion()

        if (question !in selectedQuestions) {
            selectedQuestions += question
        }
    }

    private fun selectAllQuestions() {
        selectedQuestions.clear()
        selectedQuestions = questions.map(QuestionDTO::toLiveQuestion).shuffled().toMutableList()

    }

    private fun removeQuestion(question: LiveQuestion) {
        if (question in selectedQuestions) {
            selectedQuestions -= question
        }
    }

    private fun switchQuestionMode(question: LiveQuestion, checked: Boolean) {
        val mode = if (checked) QuestionMode.QA else QuestionMode.MCQ

        val index = selectedQuestions.indexOf(question)
        if (index != -1) {
            selectedQuestions[index] = question.copy(questionMode = mode)
        }
    }

    private fun createNewUiState(question: LiveQuestion? = null): UIState {
        val id = ++questionCounter
        val q = question ?: sampleQuestions.random(random)
        val isTimerRunning = quizMode == QuizMode.TimedReveal

        return UIState(
            question = q,
            questionId = id,
            isTimerRunning = isTimerRunning,
            textBackColor = ColorUtil.randomColor(saturation = 1f, lightness = .9f),
            textColor = ColorUtil.randomColor(saturation = 1.0f, lightness = .25f),
            optionBackColors = setOptionColors(saturation = .75f, lightness = .2f),
            backgroundColorA = ColorUtil.randomColor(saturation = 1f, lightness = .25f),
            backgroundColorB = ColorUtil.randomColor(saturation = 1f, lightness = .50f),
        )
    }

    private fun setNextQuestion() {
        if (selectedQuestions.isEmpty()) {
            mode = Mode.Selection
            return
        }

        val question = selectedQuestions.removeFirst()
        uiState = createNewUiState(question)
    }

    private suspend fun showAnswer() {
        uiState = uiState.copy(showAnswer = true, isTimerRunning = false)
        delay(answerDuration)
    }

    private fun startTimer() {
        uiState = uiState.copy(isTimerRunning = true)
    }

    private fun setOptionColors(saturation: Float, lightness: Float): List<Color> {
        val colors = mutableSetOf<Color>()
        val start = random.nextInt(360)

        for (i in 0..3) {
            colors += ColorUtil.randomColor(
                hue = (start + i * 80) % 360f, saturation = saturation, lightness = lightness
            )
        }
        return colors.toList()
    }
}

data class UIState(
    val question: LiveQuestion,
    val questionId: Int,
    val optionBackColors: List<Color> = emptyList(),
    val textBackColor: Color,
    val textColor: Color,
    val showAnswer: Boolean = false,
    val isTimerRunning: Boolean = false,
    val backgroundColorA: Color,
    val backgroundColorB: Color,
)

sealed class SelectionAction {
    object ClearSelection : SelectionAction()
    data class LoadQuestions(val tag: String) : SelectionAction()
    data class StartQuiz(val mode: QuizMode) : SelectionAction()
    data class AddQuestion(val question: QuestionDTO) : SelectionAction()
    object SelectAll : SelectionAction()
    data class ChangeMode(val question: LiveQuestion, val checked: Boolean) : SelectionAction()
    data class RemoveQuestion(val question: LiveQuestion) : SelectionAction()
}

sealed class QuizAction {
    object ShowAnswer : QuizAction()
    object NextQuestion : QuizAction()
    object StartTimer : QuizAction()
}

enum class Mode {
    Playing, Selection
}

enum class QuizMode(val title: String) {
    TimedReveal("Timed Reveal"),
    ManualReveal("Manual Reveal"),
    TriggeredTimedReveal("Triggered Time"),
}