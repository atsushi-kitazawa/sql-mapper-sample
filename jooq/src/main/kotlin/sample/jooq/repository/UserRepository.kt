package sample.jooq.repository

import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.select
import sample.jooq.generated.tables.records.UsersRecord
import sample.jooq.generated.tables.references.ORDERS
import sample.jooq.generated.tables.references.USERS
import sample.jooq.model.Order
import sample.jooq.model.OrderStatus
import sample.jooq.model.User
import sample.jooq.model.UserWithOrders

class UserRepository(
    private val dsl: DSLContext,
) {

    fun create(user: User): User {
        val record = dsl.insertInto(USERS)
            .set(USERS.NAME, user.name)
            .set(USERS.EMAIL, user.email)
            .set(USERS.CREATED_AT, user.createdAt)
            .returning(USERS.ID)
            .fetchOne() ?: error("Failed to insert user")

        return user.copy(id = record.id)
    }

    fun findById(id: Long): User? {
        val record = dsl.selectFrom(USERS)
            .where(USERS.ID.eq(id))
            .fetchOne() ?: return null

        return toModel(record)
    }

    fun update(user: User): Boolean {
        requireNotNull(user.id) { "User id must not be null for update" }
        val updatedRows = dsl.update(USERS)
            .set(USERS.NAME, user.name)
            .set(USERS.EMAIL, user.email)
            .where(USERS.ID.eq(user.id))
            .execute()
        return updatedRows > 0
    }

    fun deleteById(id: Long): Boolean {
        val deletedRows = dsl.deleteFrom(USERS)
            .where(USERS.ID.eq(id))
            .execute()
        return deletedRows > 0
    }

    fun findWithOrders(userId: Long): UserWithOrders? {
        return dsl.select(
            USERS.ID,
            USERS.NAME,
            USERS.EMAIL,
            USERS.CREATED_AT,
            multiset(
                select(
                    ORDERS.ID,
                    ORDERS.USER_ID,
                    ORDERS.ORDER_CODE,
                    ORDERS.AMOUNT,
                    ORDERS.STATUS,
                    ORDERS.ORDERED_AT,
                )
                    .from(ORDERS)
                    .where(ORDERS.USER_ID.eq(USERS.ID))
                    .orderBy(ORDERS.ID)
            ).`as`("orders").convertFrom { result ->
                result.map { r ->
                    Order(
                        id = r.value1(),
                        userId = r.value2()!!,
                        orderCode = r.value3()!!,
                        amount = r.value4()!!,
                        status = OrderStatus.valueOf(r.value5()!!),
                        orderedAt = r.value6()!!,
                    )
                }
            }
        )
            .from(USERS)
            .where(USERS.ID.eq(userId))
            .fetchOne { r ->
                UserWithOrders(
                    id = r.value1()!!,
                    name = r.value2()!!,
                    email = r.value3()!!,
                    createdAt = r.value4()!!,
                    orders = r.value5(),
                )
            }
    }

    fun search(name: String? = null, email: String? = null): List<User> {
        val conditions = mutableListOf<Condition>()
        if (!name.isNullOrBlank()) {
            conditions.add(USERS.NAME.contains(name))
        }
        if (!email.isNullOrBlank()) {
            conditions.add(USERS.EMAIL.contains(email))
        }

        return dsl.selectFrom(USERS)
            .where(conditions)
            .orderBy(USERS.ID)
            .fetch { record -> toModel(record) }
    }

    private fun toModel(record: UsersRecord): User {
        return User(
            id = record.id,
            name = record.name!!,
            email = record.email!!,
            createdAt = record.createdAt!!,
        )
    }
}
