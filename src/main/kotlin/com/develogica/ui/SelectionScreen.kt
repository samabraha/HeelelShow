package com.develogica.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.develogica.model.LiveQuestion
import com.develogica.model.Option
import com.develogica.model.QuestionDTO
import com.develogica.model.QuestionMode
import com.develogica.model.QuestionType
import com.develogica.ui.question.ImageView
import com.develogica.vm.QuizMode
import com.develogica.vm.QuizViewModel
import com.develogica.vm.SelectionAction

@Composable
fun SelectionScreen(quizViewModel: QuizViewModel, modifier: Modifier = Modifier) {
    val questions = quizViewModel.questions
    val selection = quizViewModel.selectedQuestions

    val timerSeconds by remember { mutableStateOf(quizViewModel.questionDuration.inWholeSeconds.toInt()) }

    val startSelectionAction: (QuizMode) -> Unit =
        { mode -> quizViewModel.handleSelectionAction(SelectionAction.StartQuiz(mode)) }

    var quizMode by remember { mutableStateOf(quizViewModel.quizMode) }

    var tag by remember { mutableStateOf("") }

    Row(modifier = modifier) {
        Column(
            modifier = Modifier.weight(1f).padding(10.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(value = tag, onValueChange = { tag = it }, label = { Text("Tag") }, singleLine = true)

            Button(onClick = { quizViewModel.handleSelectionAction(SelectionAction.SelectQuestions) }) { Text("Load Questions") }

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "Loaded: ${questions.size}", fontWeight = FontWeight.Bold)
            Text(text = "Selected: ${selection.size}", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { quizViewModel.handleSelectionAction(SelectionAction.ClearSelection) }) {
                Text("Clear Selection")
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuizMode.entries.forEach { mode ->
                    Tab(
                        selected = mode == quizMode,
                        onClick = { quizMode = mode },
                        text = { Text(mode.title) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Column {
                when (quizMode) {
                    QuizMode.TimedReveal -> TimedPreferences(timerSeconds = timerSeconds)
                    QuizMode.ManualReveal -> ManualPreferences()
                    QuizMode.TriggeredTimedReveal -> TriggeredTimedPreferences(timerSeconds = timerSeconds)
                }
            }

            Button(
                onClick = { startSelectionAction(quizMode) }, enabled = selection.isNotEmpty()
            ) { Text("Start") }
        }

        val action: (SelectionAction) -> Unit = { action -> quizViewModel.handleSelectionAction(action) }

        QuestionList(questions = questions, select = action, modifier = Modifier.weight(2f))
        SelectedList(questions = selection, action = action, modifier = Modifier.weight(2f))
    }
}

@Composable
fun ManualPreferences(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = "Manual Preferences", fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TriggeredTimedPreferences(timerSeconds: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = "Triggered Timed Preferences", fontWeight = FontWeight.Bold)
        PickTimerSeconds(seconds = timerSeconds)
    }
}

@Composable
fun TimedPreferences(timerSeconds: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = "Timed Preferences", fontWeight = FontWeight.Bold)
        PickTimerSeconds(seconds = timerSeconds)
    }
}

@Composable
fun PickTimerSeconds(seconds: Int = 0, modifier: Modifier = Modifier) {
    var state by remember { mutableStateOf(seconds) }

    TextField(
        value = state.toString(),
        onValueChange = { state = it.toIntOrNull() ?: 0 },
        label = { Text("Seconds") },
        modifier = modifier
    )
}

@Composable
fun QuestionList(questions: List<QuestionDTO>, select: (SelectionAction) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = modifier) {
        items(questions) { question ->
            QuestionBox(question, action = select)
        }
    }
}

@Composable
fun SelectedList(questions: List<LiveQuestion>, action: (SelectionAction) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = modifier) {
        items(questions) { question ->
            SelectedQuestionBox(question, action)
        }
    }
}

@Composable
fun QuestionBox(question: QuestionDTO, action: (SelectionAction) -> Unit, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth().shadow(4.dp)
            .clickable(onClick = { action(SelectionAction.AddQuestion(question)) }).padding(16.dp)
    ) {
        if (question.questionType == QuestionType.IS_TEXT && !question.text.isNullOrBlank()) {
            Text(text = question.text, modifier.fillMaxWidth())
        }

        if (question.questionType == QuestionType.IS_IMAGE && !question.image.isNullOrBlank()) {
            ImageView(question.image)
        }

        FlowRow(maxItemsInEachRow = 2, horizontalArrangement = Arrangement.SpaceBetween) {
            question.options.forEach { option ->
                OptionBox(option, modifier = Modifier.weight(1f).widthIn(min = 200.dp, max = 400.dp))
            }
        }

        if (!question.moreInfo.isNullOrBlank()) {
            Text(text = question.moreInfo)
        }
    }
}


@Composable
fun SelectedQuestionBox(question: LiveQuestion, action: (SelectionAction) -> Unit, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth().shadow(4.dp).padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.clickable(onClick = { SelectionAction.RemoveQuestion(question) })
        ) {
            AnimatedVisibility(question.questionType == QuestionType.IS_TEXT && !question.text.isNullOrBlank()) {
                Text(text = question.text ?: "", modifier = modifier.fillMaxWidth())
            }

            AnimatedVisibility(question.questionType == QuestionType.IS_IMAGE && !question.image.isNullOrBlank()) {
                ImageView(question.image)
            }
        }

        FlowRow(maxItemsInEachRow = 2, horizontalArrangement = Arrangement.SpaceBetween) {
            question.options.forEach { option ->
                OptionBox(option, modifier = Modifier.weight(1f).widthIn(min = 200.dp, max = 400.dp))
            }
        }

        if (!question.moreInfo.isNullOrBlank()) {
            Text(text = question.moreInfo)
        }

        Spacer(modifier = Modifier.height(10.dp))

        val checked = question.questionMode == QuestionMode.QA

        Switch(
            checked = checked,
            onCheckedChange = {
                action(SelectionAction.ChangeMode(question, checked.not()))
            },
        )
    }
}


@Composable
fun OptionBox(option: Option, modifier: Modifier) {
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 2.dp)) {
        Text(
            text = option.text,
            fontWeight = if (option.isCorrect) FontWeight.Black else FontWeight.Normal,
        )
    }
}
