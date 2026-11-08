package sample.springjdbc.model

import java.time.LocalDateTime

enum class OrderStatus {
    PENDING,
    COMPLETED,
    CANCELLED
}

data class User(
    val id: Long? = null,
    val name: String,
    val email: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)

data class Order(
    val id: Long? = null,
    val userId: Long,
    val orderCode: String,
    val amount: Int,
    val status: OrderStatus,
    val orderedAt: LocalDateTime = LocalDateTime.now(),
)

data class UserWithOrders(
    val id: Long,
    val name: String,
    val email: String,
    val createdAt: LocalDateTime,
    val orders: List<Order> = emptyList(),
)
