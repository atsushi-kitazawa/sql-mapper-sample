package sample.jdbi.dao

import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.kotlin.BindKotlin
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMappers
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import sample.jdbi.model.User

@RegisterKotlinMappers
interface UserDao {

    @SqlUpdate("INSERT INTO users (name, email, created_at) VALUES (:name, :email, :createdAt)")
    @GetGeneratedKeys("id")
    fun insert(@BindKotlin user: User): Long

    @SqlQuery("SELECT id, name, email, created_at FROM users WHERE id = :id")
    fun findById(@Bind("id") id: Long): User?

    @SqlUpdate("UPDATE users SET name = :name, email = :email WHERE id = :id")
    fun update(@BindKotlin user: User): Int

    @SqlUpdate("DELETE FROM users WHERE id = :id")
    fun deleteById(@Bind("id") id: Long): Int
}
