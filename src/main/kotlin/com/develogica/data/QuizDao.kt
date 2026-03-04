package com.develogica.data

import com.develogica.util.Config
import kotlinx.serialization.json.Json
import com.develogica.model.QuestionDTO
import com.develogica.model.QuestionType
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import kotlin.io.path.Path

private const val tableName = "Questions"

class QuizDao(config: Config) {
    private val connection: Connection

    init {
        println("Initializing QuizDao")

        try {
            Class.forName("org.sqlite.JDBC")
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }

        connection = connectToDBFile(config.sourceDir)
    }

    private fun connectToDBFile(location: String): Connection {
        val path = Path(location)
        val dbPath = if (path.isAbsolute) {
            path
        } else {
            Path(System.getProperty("user.home"), "Develogica", location)
        }

        if (dbPath.toFile().exists()) {
            val connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")
            if (connection != null) {
                return connection
            }
        } else {
            println("Database file does not exist: $dbPath")
        }
        
        throw Exception("Could not connect to database at: $location")
    }

    /** Loads n questions from database.
     * Loads all questions if n is -1. */
    fun getQuestions(howMany: Int = -1): List<QuestionDTO> {
        val baseSql = "SELECT * FROM $tableName"
        val sql = if (howMany != -1) "$baseSql LIMIT $howMany" else baseSql
        val result = connection.prepareStatement(sql).executeQuery()

        return buildList {
            while (result.next()) {
                add(getQuestion(result))
            }
        }
    }

    private fun getQuestion(resultRow: ResultSet): QuestionDTO {
        val questionType = resultRow.getString(QuizTable.QuestionType.header)
        val text = resultRow.getString(QuizTable.Text.header)
        val image = resultRow.getString(QuizTable.Image.header)
        val answer = resultRow.getString(QuizTable.Answer.header)
        val options = resultRow.getString(QuizTable.Options.header)
        val moreInfo = resultRow.getString(QuizTable.MoreInfo.header)
        val tags = resultRow.getString(QuizTable.Tags.header)
        return QuestionDTO(
            questionType = QuestionType.valueOf(questionType),
            text = text,
            image = image,
            answer = answer,
            options = Json.decodeFromString(options),
            moreInfo = moreInfo,
            tags = Json.decodeFromString(tags)
        )
    }

    fun closeDatabase() {
        connection.close()
    }
}

enum class QuizTable(val header: String) {
    QuestionType("question_type"),
    Text("question_text"),
    Image("image"),
    Answer("answer"),
    Options("choices"),
    MoreInfo("more_info"),
    Tags("tags");
}
