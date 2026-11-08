package sample.hibernate

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import sample.hibernate.entity.UserEntity
import sample.hibernate.model.OrderStatus
import sample.hibernate.repository.SpringDataUserRepository
import java.time.LocalDateTime

@SpringBootTest
class SpringDataUserRepositoryTest {

    @Autowired
    private lateinit var springDataUserRepository: SpringDataUserRepository

    @Test
    @DisplayName("Spring Data JPA: CRUD操作（保存・取得・更新・削除）")
    @Transactional
    fun testSpringDataCrud() {
        // 保存
        val newUser = UserEntity(
            name = "Eve",
            email = "eve@example.com",
            createdAt = LocalDateTime.now(),
        )
        val saved = springDataUserRepository.save(newUser)
        assertThat(saved.id).isNotNull()

        // 取得
        val found = springDataUserRepository.findById(saved.id!!).orElse(null)
        assertThat(found).isNotNull
        assertThat(found?.name).isEqualTo("Eve")

        // 更新
        found!!.name = "Eve Updated"
        val updated = springDataUserRepository.save(found)
        assertThat(updated.name).isEqualTo("Eve Updated")

        // 削除
        springDataUserRepository.delete(updated)
        assertThat(springDataUserRepository.findById(saved.id!!)).isEmpty
    }

    @Test
    @DisplayName("Spring Data JPA: JPQL JOIN FETCH による 1対多 結合取得")
    @Transactional
    fun testFindByIdWithOrders() {
        val user = springDataUserRepository.findByIdWithOrders(1L)

        assertThat(user).isNotNull
        assertThat(user?.name).isEqualTo("Alice")
        assertThat(user?.orders).hasSize(2)
        assertThat(user?.orders?.map { it.orderCode }).containsExactly("ORD-001", "ORD-002")
        assertThat(user?.orders?.first()?.status).isEqualTo(OrderStatus.COMPLETED)
    }
}
