package sample.hibernate.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import sample.hibernate.entity.UserEntity

interface SpringDataUserRepository : JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    @Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.orders WHERE u.id = :id")
    fun findByIdWithOrders(@Param("id") id: Long): UserEntity?
}
