package sample.springjdbc.repository

import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import sample.springjdbc.model.Order
import sample.springjdbc.model.OrderStatus
import sample.springjdbc.model.User
import sample.springjdbc.model.UserWithOrders
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.LocalDateTime

@Repository
class UserRepository(
    private val jdbcClient: JdbcClient,
) {

    fun create(user: User): User {
        val keyHolder = GeneratedKeyHolder()
        jdbcClient.sql(
            """
            INSERT INTO users (name, email, created_at)
            VALUES (:name, :email, :createdAt)
            """.trimIndent()
        )
            .param("name", user.name)
            .param("email", user.email)
            .param("createdAt", Timestamp.valueOf(user.createdAt))
            .update(keyHolder, "id")

        val generatedId = keyHolder.key?.toLong() ?: error("Failed to retrieve generated id")
        return user.copy(id = generatedId)
    }

    fun findById(id: Long): User? {
        return jdbcClient.sql("SELECT id, name, email, created_at FROM users WHERE id = :id")
            .param("id", id)
            .query { rs, _ -> mapUser(rs) }
            .optional()
            .orElse(null)
    }

    fun update(user: User): Boolean {
        val updatedRows = jdbcClient.sql(
            """
            UPDATE users
            SET name = :name, email = :email
            WHERE id = :id
            """.trimIndent()
        )
            .param("id", user.id)
            .param("name", user.name)
            .param("email", user.email)
            .update()
        return updatedRows > 0
    }

    fun deleteById(id: Long): Boolean {
        val deletedRows = jdbcClient.sql("DELETE FROM users WHERE id = :id")
            .param("id", id)
            .update()
        return deletedRows > 0
    }

    fun findWithOrders(userId: Long): UserWithOrders? {
        val sql = """
            SELECT
                u.id AS user_id,
                u.name AS user_name,
                u.email AS user_email,
                u.created_at AS user_created_at,
                o.id AS order_id,
                o.order_code AS order_code,
                o.amount AS order_amount,
                o.status AS order_status,
                o.ordered_at AS order_ordered_at
            FROM users u
            LEFT JOIN orders o ON u.id = o.user_id
            WHERE u.id = :userId
            ORDER BY o.id
        """.trimIndent()

        return jdbcClient.sql(sql)
            .param("userId", userId)
            .query(ResultSetExtractor { rs ->
                var baseUser: UserWithOrders? = null
                val orders = mutableListOf<Order>()

                while (rs.next()) {
                    if (baseUser == null) {
                        baseUser = UserWithOrders(
                            id = rs.getLong("user_id"),
                            name = rs.getString("user_name"),
                            email = rs.getString("user_email"),
                            createdAt = rs.getTimestamp("user_created_at").toLocalDateTime(),
                        )
                    }

                    val orderId = rs.getObject("order_id")
                    if (orderId != null) {
                        orders.add(
                            Order(
                                id = (orderId as Number).toLong(),
                                userId = rs.getLong("user_id"),
                                orderCode = rs.getString("order_code"),
                                amount = rs.getInt("order_amount"),
                                status = OrderStatus.valueOf(rs.getString("order_status")),
                                orderedAt = rs.getTimestamp("order_ordered_at").toLocalDateTime(),
                            )
                        )
                    }
                }
                baseUser?.copy(orders = orders)
            })
    }

    fun search(name: String? = null, email: String? = null): List<User> {
        val conditions = mutableListOf<String>()
        val params = mutableMapOf<String, Any>()

        if (!name.isNullOrBlank()) {
            conditions.add("name LIKE :name")
            params["name"] = "%$name%"
        }
        if (!email.isNullOrBlank()) {
            conditions.add("email LIKE :email")
            params["email"] = "%$email%"
        }

        val whereClause = if (conditions.isNotEmpty()) {
            "WHERE " + conditions.joinToString(" AND ")
        } else {
            ""
        }

        val query = jdbcClient.sql("SELECT id, name, email, created_at FROM users $whereClause ORDER BY id")
        params.forEach { (k, v) -> query.param(k, v) }

        return query.query { rs, _ -> mapUser(rs) }.list()
    }

    private fun mapUser(rs: ResultSet): User {
        return User(
            id = rs.getLong("id"),
            name = rs.getString("name"),
            email = rs.getString("email"),
            createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
        )
    }
}
