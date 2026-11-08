package sample.hibernate.repository

import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.Predicate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import sample.hibernate.entity.UserEntity
import sample.hibernate.model.Order
import sample.hibernate.model.User
import sample.hibernate.model.UserWithOrders

@Repository
class UserRepository(
    private val entityManager: EntityManager,
) {

    @Transactional
    fun create(user: User): User {
        val entity = UserEntity(
            name = user.name,
            email = user.email,
            createdAt = user.createdAt,
        )
        entityManager.persist(entity)
        entityManager.flush()
        return user.copy(id = entity.id)
    }

    fun findById(id: Long): User? {
        val entity = entityManager.find(UserEntity::class.java, id) ?: return null
        return toModel(entity)
    }

    @Transactional
    fun update(user: User): Boolean {
        requireNotNull(user.id) { "User id must not be null for update" }
        val entity = entityManager.find(UserEntity::class.java, user.id) ?: return false
        entity.name = user.name
        entity.email = user.email
        entityManager.flush()
        return true
    }

    @Transactional
    fun deleteById(id: Long): Boolean {
        val entity = entityManager.find(UserEntity::class.java, id) ?: return false
        entityManager.remove(entity)
        entityManager.flush()
        return true
    }

    fun findWithOrders(userId: Long): UserWithOrders? {
        val jpql = """
            SELECT DISTINCT u
            FROM UserEntity u
            LEFT JOIN FETCH u.orders
            WHERE u.id = :userId
        """.trimIndent()

        val results = entityManager.createQuery(jpql, UserEntity::class.java)
            .setParameter("userId", userId)
            .resultList

        val entity = results.firstOrNull() ?: return null

        return UserWithOrders(
            id = entity.id!!,
            name = entity.name,
            email = entity.email,
            createdAt = entity.createdAt,
            orders = entity.orders.sortedBy { it.id }.map { orderEntity ->
                Order(
                    id = orderEntity.id,
                    userId = entity.id!!,
                    orderCode = orderEntity.orderCode,
                    amount = orderEntity.amount,
                    status = orderEntity.status,
                    orderedAt = orderEntity.orderedAt,
                )
            },
        )
    }

    fun search(name: String? = null, email: String? = null): List<User> {
        val cb = entityManager.criteriaBuilder
        val cq = cb.createQuery(UserEntity::class.java)
        val root = cq.from(UserEntity::class.java)

        val predicates = mutableListOf<Predicate>()

        if (!name.isNullOrBlank()) {
            predicates.add(cb.like(root.get("name"), "%$name%"))
        }
        if (!email.isNullOrBlank()) {
            predicates.add(cb.like(root.get("email"), "%$email%"))
        }

        if (predicates.isNotEmpty()) {
            cq.where(*predicates.toTypedArray())
        }
        cq.orderBy(cb.asc(root.get<Long>("id")))

        return entityManager.createQuery(cq).resultList.map { toModel(it) }
    }

    private fun toModel(entity: UserEntity): User {
        return User(
            id = entity.id,
            name = entity.name,
            email = entity.email,
            createdAt = entity.createdAt,
        )
    }
}
