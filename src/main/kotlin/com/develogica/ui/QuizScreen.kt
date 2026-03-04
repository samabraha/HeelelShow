package com.develogica.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.develogica.model.QuestionMode
import com.develogica.model.QuestionType
import com.develogica.ui.question.ImageView
import com.develogica.util.FontUtil
import com.develogica.vm.HomeUIState
import com.develogica.vm.QuizAction
import com.develogica.vm.QuizMode
import com.develogica.vm.QuizViewModel
import com.develogica.vm.UIState
import kotlinx.coroutines.delay


@Composable
fun QuizView(
    homeUIState: HomeUIState, quizViewModel: QuizViewModel, modifier: Modifier = Modifier
) {
    val uiState = quizViewModel.uiState

    val backColorA by animateColorAsState(targetValue = uiState.backgroundColorA, animationSpec = tween(30_000))
    val backColorB by animateColorAsState(targetValue = uiState.backgroundColorB, animationSpec = tween(15_000))

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    0.05f to backColorA,
                    0.95f to backColorB,
                    startY = 0f,
                    endY = homeUIState.height.toFloat(),
                )
            )
            .clickable {
                when (quizViewModel.quizMode) {
                    QuizMode.TriggeredTimedReveal -> {
                        if (!uiState.isTimerRunning && !uiState.showAnswer) {
                            quizViewModel.handleQuizAction(QuizAction.StartTimer)
                        } else if (uiState.showAnswer) {
                            quizViewModel.handleQuizAction(QuizAction.NextQuestion)
                        }
                    }

                    QuizMode.ManualReveal -> {
                        if (!uiState.showAnswer) {
                            quizViewModel.handleQuizAction(QuizAction.ShowAnswer)
                        } else {
                            quizViewModel.handleQuizAction(QuizAction.NextQuestion)
                        }
                    }

                    QuizMode.TimedReveal -> {
                        if (uiState.showAnswer) {
                            quizViewModel.handleQuizAction(QuizAction.NextQuestion)
                        }
                    }
                }
            }
    ) {
        if (homeUIState.launchInPortrait) {
            PortraitPane(quizViewModel = quizViewModel, uiState = uiState, modifier = modifier)
        } else {
            LandscapePane(quizViewModel = quizViewModel, uiState = uiState, modifier = modifier)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LandscapePane(
    uiState: UIState, quizViewModel: QuizViewModel, modifier: Modifier = Modifier
) {
    val fs = if ((uiState.question.text?.length ?: 0) + (uiState.question.moreInfo?.length ?: 0) > 30) 36.sp else 46.sp
    val textStyle = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold, fontSize = fs)
    val optionStyle = MaterialTheme.typography.displayMedium

    var visibleItemCount by remember { mutableStateOf(0) }

    LaunchedEffect(key1 = uiState.questionId, uiState.showAnswer) {
        visibleItemCount = 0
        if (uiState.question.questionMode == QuestionMode.MCQ || uiState.showAnswer) {
            for (i in 1..uiState.question.options.size) {
                delay(150)
                visibleItemCount = i
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize().padding(20.dp),
    ) {
        AnimatedVisibility(visible = uiState.isTimerRunning) {
            ProgressBar(
                key = uiState.questionId,
                duration = quizViewModel.questionDuration,
                onFinished = {
                    quizViewModel.handleQuizAction(QuizAction.ShowAnswer)
                },
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            if (uiState.question.questionType == QuestionType.IS_TEXT) {
                CardView(
                    text = uiState.question.text ?: "",
                    backColor = uiState.textBackColor,
                    foreColor = uiState.textColor,
                    textStyle = textStyle,
                    modifier = Modifier.weight(1f).heightIn(min = 150.dp, max = 320.dp).animateContentSize(),
                )
            } else {
                ImageView(
                    uiState.question.image,
                    modifier = Modifier.weight(1f).animateContentSize(),
                    orientation = Orientation.Horizontal
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxSize().padding(horizontal = 32.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                itemsIndexed(
                    items = uiState.question.options, key = { _, option -> option.text }) { index, option ->

                    val isVisible = when (uiState.question.questionMode) {
                        QuestionMode.MCQ -> index < visibleItemCount
                        QuestionMode.QA -> uiState.showAnswer && option.isCorrect && index < visibleItemCount
                    }

                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                        modifier = Modifier.animateItem()
                    ) {
                        val backColor = uiState.optionBackColors[index]

                        val animatedTextColor by animateColorAsState(
                            targetValue = if (uiState.showAnswer && option.isCorrect) backColor else Color.White,
                            animationSpec = tween(durationMillis = 500)
                        )

                        val animatedBackColor by animateColorAsState(
                            targetValue = if (uiState.showAnswer && option.isCorrect) Color.White else backColor,
                            animationSpec = tween(durationMillis = 500)
                        )

                        CardView(
                            text = option.text,
                            backColor = animatedBackColor,
                            foreColor = animatedTextColor,
                            textStyle = optionStyle,
                            modifier = Modifier.border(1.dp, animatedTextColor, RoundedCornerShape(24.dp))
//                            .padding(borderWidth)
                                .heightIn(min = 80.dp, max = 150.dp)
                        )
                    }
                }

                val textFont = FontUtil.ethioFont
                uiState.question.moreInfo?.let { moreInfo ->
                    item {
                        AnimatedVisibility(
                            visible = uiState.showAnswer,
                            enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                            modifier = Modifier.animateItem()
                        ) {
                            Text(
                                textAlign = TextAlign.Center,
                                style = textStyle,
                                fontSize = 24.sp,
                                fontFamily = textFont,
                                text = moreInfo,
                                modifier = Modifier.background(color = Color.White.copy(alpha = .5f))
                                    .padding(horizontal = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PortraitPane(
    uiState: UIState, quizViewModel: QuizViewModel, modifier: Modifier = Modifier
) {
    val fs = if ((uiState.question.text?.length ?: 0) + (uiState.question.moreInfo?.length ?: 0) > 32) 32.sp else 44.sp
    val textStyle = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold, fontSize = fs)
    val optionStyle = MaterialTheme.typography.displaySmall

    var visibleItemCount by remember { mutableStateOf(0) }

    LaunchedEffect(key1 = uiState.questionId, uiState.showAnswer) {
        visibleItemCount = 0
        if (uiState.question.questionMode == QuestionMode.MCQ || uiState.showAnswer) {
            for (i in 1..uiState.question.options.size) {
                delay(150)
                visibleItemCount = i
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(visible = uiState.isTimerRunning) {
            ProgressBar(
                key = uiState.questionId,
                duration = quizViewModel.questionDuration,
                onFinished = {
                    quizViewModel.handleQuizAction(QuizAction.ShowAnswer)
                },
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        AnimatedVisibility(
            visible = true, enter = fadeIn(), modifier = Modifier.animateContentSize().padding(10.dp)
        ) {
            if (uiState.question.questionType == QuestionType.IS_TEXT) {
                CardView(
                    text = uiState.question.text ?: "",
                    backColor = uiState.textBackColor,
                    foreColor = uiState.textColor,
                    textStyle = textStyle,
                    modifier = Modifier.heightIn(min = 150.dp, max = 320.dp),
                )
            } else {
                ImageView(imagePath = uiState.question.image, orientation = Orientation.Vertical)
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 32.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(
                items = uiState.question.options, key = { _, option -> option.text }) { index, option ->

                val isVisible = when (uiState.question.questionMode) {
                    QuestionMode.MCQ -> index < visibleItemCount
                    QuestionMode.QA -> uiState.showAnswer && option.isCorrect && index < visibleItemCount
                }

                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                    modifier = Modifier.animateItem()
                ) {
                    val backColor = uiState.optionBackColors[index]

                    val animatedTextColor by animateColorAsState(
                        targetValue = if (uiState.showAnswer && option.isCorrect) backColor else Color.White,
                        animationSpec = tween(durationMillis = 500)
                    )

                    val animatedBackColor by animateColorAsState(
                        targetValue = if (uiState.showAnswer && option.isCorrect) Color.White else backColor,
                        animationSpec = tween(durationMillis = 500)
                    )

                    CardView(
                        text = option.text,
                        backColor = animatedBackColor,
                        foreColor = animatedTextColor,
                        textStyle = optionStyle,
                        modifier = Modifier.border(1.dp, animatedTextColor, RoundedCornerShape(24.dp))
//                            .padding(borderWidth)
                            .heightIn(min = 80.dp)
                    )
                }
            }

            val textFont = FontUtil.ethioFont
            uiState.question.moreInfo?.let { moreInfo ->
                item {
                    AnimatedVisibility(
                        visible = uiState.showAnswer,
                        enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                        modifier = Modifier.animateItem()
                    ) {
                        Text(
                            textAlign = TextAlign.Center,
                            style = textStyle,
                            fontSize = 24.sp,
                            fontFamily = textFont,
                            text = moreInfo,
                            modifier = Modifier.background(color = Color.White.copy(alpha = .5f))
                                .padding(horizontal = 12.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun CardView(
    text: String,
    modifier: Modifier = Modifier,
    backColor: Color,
    foreColor: Color = Color.White,
    textStyle: TextStyle,
) {
    val textFont = FontUtil.ethioFont

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.shadow(elevation = 6.dp, shape = RoundedCornerShape(25.dp)).background(backColor)
            .fillMaxWidth().padding(20.dp)
    ) {
        Text(
            text = text,
            style = textStyle,
            textAlign = TextAlign.Center,
            fontFamily = textFont,
            color = foreColor,
        )
    }
}
