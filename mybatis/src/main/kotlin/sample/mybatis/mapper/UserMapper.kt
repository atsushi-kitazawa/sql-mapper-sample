package sample.mybatis.mapper

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import sample.mybatis.model.User
import sample.mybatis.model.UserWithOrders

@Mapper
interface UserMapper {
    fun insert(user: User): Int
    fun findById(@Param("id") id: Long): User?
    fun update(user: User): Int
    fun deleteById(@Param("id") id: Long): Int
    fun findWithOrders(@Param("userId") userId: Long): UserWithOrders?
    fun search(@Param("name") name: String? = null, @Param("email") email: String? = null): List<User>
}
