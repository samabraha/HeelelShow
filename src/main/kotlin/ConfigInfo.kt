import kotlin.io.path.Path
import kotlin.io.path.pathString

object ConfigInfo {
    val develogicaPath: String = Path(System.getProperty("user.home"), "Develogica").pathString
}