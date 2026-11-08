package sample.mybatis.model

import java.time.LocalDateTime

enum class OrderStatus {
    PENDING,
    COMPLETED,
    CANCELLED
}

data class User(
    var id: Long? = null,
    var name: String = "",
    var email: String = "",
    var createdAt: LocalDateTime = LocalDateTime.now(),
)

data class Order(
    var id: Long? = null,
    var userId: Long = 0,
    var orderCode: String = "",
    var amount: Int = 0,
    var status: OrderStatus = OrderStatus.PENDING,
    var orderedAt: LocalDateTime = LocalDateTime.now(),
)

data class UserWithOrders(
    var id: Long = 0,
    var name: String = "",
    var email: String = "",
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var orders: List<Order> = ArrayList(),
)
