package sample.jooq

import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.conf.RenderNameCase
import org.jooq.conf.Settings
import org.jooq.impl.DSL
import java.sql.Connection
import java.sql.DriverManager

object JooqDatabase {
    fun createConnection(jdbcUrl: String = "jdbc:h2:mem:jooqdb;MODE=PostgreSQL;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1"): Connection {
        return DriverManager.getConnection(jdbcUrl, "sa", "")
    }

    fun createDsl(connection: Connection): DSLContext {
        val settings = Settings().withRenderNameCase(RenderNameCase.LOWER)
        return DSL.using(connection, SQLDialect.H2, settings)
    }

    fun initSchema(connection: Connection) {
        val schemaSql = JooqDatabase::class.java.getResource("/schema.sql")?.readText()
            ?: error("schema.sql not found")
        val dataSql = JooqDatabase::class.java.getResource("/data.sql")?.readText()
            ?: error("data.sql not found")

        connection.createStatement().use { stmt ->
            // H2 allows multiple statements separated by semicolon
            stmt.execute(schemaSql)
            stmt.execute(dataSql)
        }
    }
}
