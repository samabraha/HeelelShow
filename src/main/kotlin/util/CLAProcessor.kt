package util

class CLAProcessor(private val args: Array<String>) {
    val commands: List<Command> = mutableListOf()

    init {
        parseArgs()
    }

    private fun parseArgs() {
        args.forEachIndexed { index, arg ->
            if (arg.startsWith("-")) {
                when (arg) {
                    "-y", "--height" -> readHeight(index)
                    "-x", "--width" -> readWidth(index)
                    "-t", "--tags" -> readTags(index)
                    "-h", "--help" -> setHelp()
                    "-v", "--version" -> setVersion()
                    else -> {}
                }
            }
        }
    }

    private fun readWidth(index: Int) {
        if (index + 1 < args.size) {

        }
    }

    private fun readHeight(index: Int) {
        val nextIndex = index + 1
        if (nextIndex < args.size) {
            val x = args[nextIndex]
            if (x.toFloatOrNull() != null) {
                setHeight(x.toFloat())
            } else {
                if (x.startsWith("-")) {
                    Log.error("No height provided")
                } else {
                    Log.error("Invalid height provided")
                }
            }
        }
    }
    private fun setHeight(height: Float) {

    }


    private fun readTags(index: Int) {
        if (index + 1 < args.size) {

        }
    }

    private fun setHelp() {

    }

    private fun setVersion() {

    }
}

class Command {}