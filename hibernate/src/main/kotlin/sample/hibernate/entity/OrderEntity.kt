package sample.hibernate.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import sample.hibernate.model.OrderStatus
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
open class OrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    open var user: UserEntity? = null,

    @Column(name = "order_code", nullable = false, unique = true, length = 50)
    open var orderCode: String = "",

    @Column(nullable = false)
    open var amount: Int = 0,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    open var status: OrderStatus = OrderStatus.PENDING,

    @Column(name = "ordered_at", nullable = false)
    open var orderedAt: LocalDateTime = LocalDateTime.now(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrderEntity) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String {
        return "OrderEntity(id=$id, orderCode='$orderCode', amount=$amount, status=$status, orderedAt=$orderedAt)"
    }
}
