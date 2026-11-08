package sample.jdbi

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin

object JdbiDatabase {
    fun create(jdbcUrl: String = "jdbc:h2:mem:jdbidb;MODE=PostgreSQL;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1"): Jdbi {
        val jdbi = Jdbi.create(jdbcUrl, "sa", "")
        jdbi.installPlugin(SqlObjectPlugin())
        jdbi.installPlugin(KotlinPlugin())
        jdbi.installPlugin(KotlinSqlObjectPlugin())
        return jdbi
    }

    fun initSchema(jdbi: Jdbi) {
        val schemaSql = JdbiDatabase::class.java.getResource("/schema.sql")?.readText()
            ?: error("schema.sql not found")
        val dataSql = JdbiDatabase::class.java.getResource("/data.sql")?.readText()
            ?: error("data.sql not found")

        jdbi.useHandle<Exception> { handle ->
            handle.createScript(schemaSql).execute()
            handle.createScript(dataSql).execute()
        }
    }
}
