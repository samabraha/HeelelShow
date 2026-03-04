package ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import model.*
import ui.question.ImageView
import vm.SelectionAction
import vm.QuizMode
import vm.QuizViewModel

@Composable
fun SelectionScreen(quizViewModel: QuizViewModel, modifier: Modifier = Modifier) {
    val questions = quizViewModel.questions
    val selection = quizViewModel.selectedQuestions

    val startSelectionAction: (QuizMode) -> Unit = { mode -> quizViewModel.handleSelectionAction(SelectionAction.StartQuiz(mode)) }


    var tag = ""
    Row(modifier = modifier) {
        Column(
            modifier = Modifier.weight(1f).padding(10.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(value = tag, onValueChange = { tag = it }, label = { Text("Tag") })

            Button(onClick = { quizViewModel.handleSelectionAction(SelectionAction.SelectQuestions) }) { Text("Load Questions") }
            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "Loaded: ${questions.size}", fontWeight = FontWeight.Bold)
            Text(text = "Selected: ${selection.size}", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(10.dp))

            Button(onClick = { quizViewModel.handleSelectionAction(SelectionAction.ClearSelection) }) { Text("Clear Selection") }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { startSelectionAction(QuizMode.ManualReveal) }) { Text("Start in Manual Mode") }
                Button(onClick = { startSelectionAction(QuizMode.TimedReveal) }) { Text("Start in Timed Reveal Mode") }
                Button(onClick = { startSelectionAction(QuizMode.TriggeredTimedReveal) }) { Text("Start in Triggered Timed Reveal Mode") }
            }
        }

        val action: (SelectionAction) -> Unit = { action -> quizViewModel.handleSelectionAction(action) }

        QuestionList(questions = questions, select = action, modifier = Modifier.weight(2f))
        SelectedList(questions = selection, action = action, modifier = Modifier.weight(2f))
    }
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
        modifier = modifier.fillMaxWidth().shadow(4.dp).clickable(onClick = { action(SelectionAction.AddQuestion(question)) })
            .padding(16.dp)
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

        println("Checked: $checked")
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
