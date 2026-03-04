package com.develogica.util

import kotlinx.serialization.json.Json
import kotlin.io.path.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.pathString

class ConfigProcessor(private val args: Array<String>) {
    val config: Config
    val commands: Set<Command>

    private val configPath = Path(System.getProperty("user.home"), "Develogica", "heelelShow.config")
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    init {
        val loadedConfig = loadConfig()
        val (overriddenConfig, parsedCommands) = parseArgs(loadedConfig)
        config = overriddenConfig
        commands = parsedCommands

        saveConfig()
    }

    private fun loadConfig(): Config {
        val configFile = configPath.toFile()
        if (configFile.exists()) {
            try {
                return json.decodeFromString<Config>(configFile.readText())
            } catch (e: Exception) {
                Log.error("Failed to load config: ${e.message}. Using defaults.")
            }
        }

        Log.info("Config file does not exist. Using defaults.")
        return Config()
    }

    private fun saveConfig() {
        val configFile = configPath.toFile()
        try {
            if (!configFile.parentFile.exists()) {
                configFile.parentFile.mkdirs()
            }
            configFile.writeText(json.encodeToString(config))
        } catch (e: Exception) {
            Log.error("Failed to save config: ${e.message}")
        }
    }

    private fun parseArgs(initialConfig: Config): Pair<Config, Set<Command>> {
        var tempConfig = initialConfig.copy()
        val commands = mutableSetOf<Command>()

        args.forEachIndexed { index, arg ->
            when (arg) {
                "-p", "--portrait" -> tempConfig = tempConfig.copy(launchInPortrait = true)
                "-l", "--landscape" -> tempConfig = tempConfig.copy(launchInPortrait = false)
                "-e", "--english" -> tempConfig = tempConfig.copy(sourceDir = "eng_qz.db")
                "-s", "--source" -> handleSource(args, index)?.let { tempConfig = tempConfig.copy(sourceDir = it) }
                "-i", "--images" -> handleImageRoot(args, index)?.let { tempConfig = tempConfig.copy(imageRoot = it) }
                "-x", "--width" -> handleDimension(args, index)?.let { tempConfig = tempConfig.copy(width = it) }
                "-y", "--height" -> handleDimension(args, index)?.let { tempConfig = tempConfig.copy(height = it) }
                "-h", "--help" -> commands.add(Command.ShowHelp)
                "-v", "--version" -> commands.add(Command.ShowVersion)
            }
        }

        return tempConfig to commands
    }

    private fun handleSource(args: Array<String>, index: Int): String? {
        if (isValidOption(args, index + 1).not()) {
            Log.error("No source directory specified")
            return null
        }

        val path = Path(args[index + 1])
        if (path.toFile().exists()) {
            return path.toString()
        } else {
            Log.error("Source directory does not exist: '${path}'")
            return null
        }
    }

    private fun handleImageRoot(args: Array<String>, index: Int): String? {
        if (isValidOption(args, index + 1).not()) {
            Log.error("No image root directory specified")

            return null
        }

        val path = Path(args[index + 1])
        if (path.toFile().exists() && path.isDirectory()) {
            return path.pathString
        } else {
            Log.error("Image root directory does not exist or is not a directory: '${path}'")
            return null
        }
    }

    private fun handleDimension(args: Array<String>, index: Int): Int? {
        if (isValidOption(args, index + 1).not()) {
            Log.error("No dimension value specified")
            return null
        }

        val value = args[index + 1].toIntOrNull()
        if (value != null && value > 0) {
            return value
        } else {
            Log.error("Invalid dimension value: '${args[index + 1]}'")
            return null
        }
    }

    /**
     * Returns true if the next argument exists and is not a flag
     *  */
    private fun isValidOption(args: Array<String>, index: Int): Boolean {
        return index < args.size && !args[index].startsWith("-")
    }
}

sealed class Command {
    object ShowHelp : Command()
    object ShowVersion : Command()
}
