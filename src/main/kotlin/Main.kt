import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import data.QuizDao
import data.QuizRepository
import ui.QuizView
import ui.SelectionScreen
import vm.HomeViewModel
import vm.Mode
import vm.QuizViewModel
import kotlin.io.path.Path
import kotlin.io.path.pathString

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
            position = WindowPosition.Aligned(Alignment.Center), width = homeState.width, height = homeState.height
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

class CLArgs(args: Array<String>) {
    val imageRoot = "D:/Develogica/Zara_Images/"
    var launchInPortrait = false
    var sourceDir = Path(ConfigInfo.develogicaPath, "tig_qz.db").pathString

    init {
        args.forEachIndexed { index, arg ->
            when (arg) {
                "-p", "--portrait" -> launchInPortrait = true
                "-l", "--landscape" -> launchInPortrait = false
                "-e", "--english" -> sourceDir = Path(ConfigInfo.develogicaPath, "eng_qz.db").pathString
                "-s", "--source" -> handleSource(args, index)
                else -> if (index == 0) println("Unknown argument: '$arg'")
            }
        }
    }

    private fun handleSource(args: Array<String>, index: Int) {
        if (isValidOption(args, index + 1)) {
            val path = Path(args[index + 1])

            if (path.toFile().exists()) {
                sourceDir = path.pathString
            } else {
                println("Source directory does not exist: '${path.pathString}'")
            }
        } else {
            println("No source directory specified")
        }
    }

    private fun isValidOption(args: Array<String>, index: Int): Boolean {
        return index < args.size && !args[index].startsWith("-")
    }
}

fun main(args: Array<String>) = application {
    println("Starting application...")

    val clArgs = CLArgs(args)

    val homeViewModel = HomeViewModel(launchInPortrait = clArgs.launchInPortrait)
    val dao = QuizDao(clArgs = clArgs)
    val repo = QuizRepository(quizDao = dao, imagesRoot = clArgs.imageRoot, clArgs = clArgs)
    val quizViewModel = QuizViewModel(repo)

    HeelelUI(homeViewModel, quizViewModel, ::exitApplication)
}
