package sample.jdbi.repository

import org.jdbi.v3.core.Jdbi
import sample.jdbi.dao.UserDao
import sample.jdbi.model.Order
import sample.jdbi.model.OrderStatus
import sample.jdbi.model.User
import sample.jdbi.model.UserWithOrders

class UserRepository(private val jdbi: Jdbi) {

    fun create(user: User): User {
        return jdbi.withExtension<User, UserDao, Exception>(UserDao::class.java) { dao ->
            val id = dao.insert(user)
            user.copy(id = id)
        }
    }

    fun findById(id: Long): User? {
        return jdbi.withExtension<User?, UserDao, Exception>(UserDao::class.java) { dao ->
            dao.findById(id)
        }
    }

    fun update(user: User): Boolean {
        return jdbi.withExtension<Boolean, UserDao, Exception>(UserDao::class.java) { dao ->
            dao.update(user) > 0
        }
    }

    fun deleteById(id: Long): Boolean {
        return jdbi.withExtension<Boolean, UserDao, Exception>(UserDao::class.java) { dao ->
            dao.deleteById(id) > 0
        }
    }

    fun findWithOrders(userId: Long): UserWithOrders? {
        val sql = """
            SELECT
                u.id AS u_id,
                u.name AS u_name,
                u.email AS u_email,
                u.created_at AS u_created_at,
                o.id AS o_id,
                o.user_id AS o_user_id,
                o.order_code AS o_order_code,
                o.amount AS o_amount,
                o.status AS o_status,
                o.ordered_at AS o_ordered_at
            FROM users u
            LEFT JOIN orders o ON u.id = o.user_id
            WHERE u.id = :userId
            ORDER BY o.id
        """.trimIndent()

        return jdbi.withHandle<UserWithOrders?, Exception> { handle ->
            handle.createQuery(sql)
                .bind("userId", userId)
                .scanResultSet { supplier, _ ->
                    val rs = supplier.get()
                    var baseUser: UserWithOrders? = null
                    val orders = mutableListOf<Order>()

                    while (rs.next()) {
                        if (baseUser == null) {
                            baseUser = UserWithOrders(
                                id = rs.getLong("u_id"),
                                name = rs.getString("u_name"),
                                email = rs.getString("u_email"),
                                createdAt = rs.getTimestamp("u_created_at").toLocalDateTime(),
                            )
                        }

                        val orderId = rs.getObject("o_id")
                        if (orderId != null) {
                            orders.add(
                                Order(
                                    id = (orderId as Number).toLong(),
                                    userId = rs.getLong("o_user_id"),
                                    orderCode = rs.getString("o_order_code"),
                                    amount = rs.getInt("o_amount"),
                                    status = OrderStatus.valueOf(rs.getString("o_status")),
                                    orderedAt = rs.getTimestamp("o_ordered_at").toLocalDateTime(),
                                )
                            )
                        }
                    }
                    baseUser?.copy(orders = orders)
                }
        }
    }

    fun search(name: String? = null, email: String? = null): List<User> {
        return jdbi.withHandle<List<User>, Exception> { handle ->
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

            val where = if (conditions.isNotEmpty()) "WHERE " + conditions.joinToString(" AND ") else ""
            val query = handle.createQuery("SELECT id, name, email, created_at FROM users $where ORDER BY id")
            params.forEach { (k, v) -> query.bind(k, v) }
            query.mapTo(User::class.java).list()
        }
    }

    fun executeInTransaction(action: (UserDao) -> Unit) {
        jdbi.useTransaction<Exception> { handle ->
            val dao = handle.attach(UserDao::class.java)
            action(dao)
        }
    }
}
