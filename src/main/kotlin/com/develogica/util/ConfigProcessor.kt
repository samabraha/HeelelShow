package com.develogica.util

import kotlinx.serialization.json.Json
import java.io.File
import kotlin.io.path.Path

class ConfigProcessor(private val args: Array<String>) {

    val config: Config
    val commands: Set<Command>

    private val configFile = File(Path(System.getProperty("user.home"), "Develogica").toString(), "heelelShow.config")
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    init {
        val loadedConfig = loadConfig()
        val (overriddenConfig, parsedCommands) = parseArgs(loadedConfig)
        config = overriddenConfig
        commands = parsedCommands
        saveConfig()
    }

    private fun loadConfig(): Config {
        if (configFile.exists()) {
            try {
                return json.decodeFromString<Config>(configFile.readText())
            } catch (e: Exception) {
                println("Failed to load config: ${e.message}. Using defaults.")
            }
        }
        return Config()
    }

    private fun saveConfig() {
        try {
            if (!configFile.parentFile.exists()) {
                configFile.parentFile.mkdirs()
            }
            configFile.writeText(json.encodeToString(config))
        } catch (e: Exception) {
            println("Failed to save config: ${e.message}")
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
        if (isValidOption(args, index + 1)) {
            val path = Path(args[index + 1])
            if (path.toFile().exists()) {
                return path.toString()
            } else {
                println("Source directory does not exist: '${path}'")
            }
        } else {
            println("No source directory specified")
        }
        return null
    }

    private fun handleImageRoot(args: Array<String>, index: Int): String? {
        if (isValidOption(args, index + 1)) {
            val path = Path(args[index + 1])
            if (path.toFile().exists() && path.toFile().isDirectory) {
                return path.toString()
            } else {
                println("Image root directory does not exist or is not a directory: '${path}'")
            }
        } else {
            println("No image root directory specified")
        }
        return null
    }

    private fun handleDimension(args: Array<String>, index: Int): Float? {
        if (isValidOption(args, index + 1)) {
            val value = args[index + 1].toFloatOrNull()
            if (value != null) {
                return value
            } else {
                println("Invalid dimension value: '${args[index + 1]}'")
            }
        } else {
            println("No dimension value specified")
        }
        return null
    }

    private fun isValidOption(args: Array<String>, index: Int): Boolean {
        return index < args.size && !args[index].startsWith("-")
    }
}

sealed class Command {
    object ShowHelp : Command()
    object ShowVersion : Command()
}
