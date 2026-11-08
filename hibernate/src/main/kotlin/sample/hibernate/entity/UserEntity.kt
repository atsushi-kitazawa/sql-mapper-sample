package sample.hibernate.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "users")
open class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @Column(nullable = false, length = 100)
    open var name: String = "",

    @Column(nullable = false, unique = true, length = 255)
    open var email: String = "",

    @Column(name = "created_at", nullable = false)
    open var createdAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    open var orders: MutableList<OrderEntity> = mutableListOf(),
) {
    fun addOrder(order: OrderEntity) {
        orders.add(order)
        order.user = this
    }

    fun removeOrder(order: OrderEntity) {
        orders.remove(order)
        order.user = null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserEntity) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String {
        return "UserEntity(id=$id, name='$name', email='$email', createdAt=$createdAt)"
    }
}
