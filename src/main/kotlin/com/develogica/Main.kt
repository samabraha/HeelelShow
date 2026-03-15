package com.develogica

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.develogica.data.QuizDao
import com.develogica.data.QuizRepository
import com.develogica.ui.QuizView
import com.develogica.ui.SelectionScreen
import com.develogica.util.Command
import com.develogica.util.ConfigProcessor
import com.develogica.vm.HomeViewModel
import com.develogica.vm.Mode
import com.develogica.vm.QuizViewModel

@Composable
fun HeelelUI(
    homeViewModel: HomeViewModel,
    quizViewModel: QuizViewModel,
    exitApplication: () -> Unit,
) {
    val homeState = homeViewModel.uiState
    val quizUIState = quizViewModel.uiState
    Window(
        onCloseRequest = exitApplication, title = "Heelel", state = rememberWindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            width = homeState.width.dp,
            height = homeState.height.dp
        ), resizable = false
    ) {
        MaterialTheme {
            Scaffold {
                when (quizViewModel.mode) {
                    Mode.Playing -> QuizView(homeUIState = homeState, quizViewModel = quizViewModel)
                    Mode.Selection -> SelectionScreen(quizViewModel = quizViewModel)
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    println("Starting application...")

    val processor = ConfigProcessor(args)
    val commands = processor.commands
    val config = processor.config

    commands.forEach { command ->
        when (command) {
            Command.ShowHelp -> showHelp()
            Command.ShowVersion -> showVersion()
        }
    }

    if (commands.isNotEmpty()) {
        println("Exiting application...")
        return
    }

    val homeViewModel = HomeViewModel(config = config)
    val dao = QuizDao(config = config)
    val repo = QuizRepository(config = config, quizDao = dao)
    val quizViewModel = QuizViewModel(repository = repo)

    application {
        HeelelUI(homeViewModel = homeViewModel, quizViewModel = quizViewModel, exitApplication = ::exitApplication)
    }
}

fun showHelp() {
    println("Usage: heelelshow [options]")
    println("Options:")
    println("  -p, --portrait     Launch in portrait mode")
    println("  -l, --landscape    Launch in landscape mode")
    println("  -e, --english      Use English questions")
    println("  -s, --source FILE  Path to the quiz database file")
    println("  -i, --images DIR   Path to the image directory")
    println("  -x, --width W      Set the window width")
    println("  -y, --height H     Set the window height")
    println("  -h, --help         Show this help message")
    println("  -v, --version      Show version information")
}

fun showVersion() {
    println("HeelelShow version 1.0.0")
}
