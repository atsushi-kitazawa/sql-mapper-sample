package sample.hibernate

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import sample.hibernate.model.OrderStatus
import sample.hibernate.model.User
import sample.hibernate.repository.UserRepository
import sample.hibernate.service.UserService
import java.time.LocalDateTime

@SpringBootTest
class HibernateUserRepositoryTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userService: UserService

    @Test
    @DisplayName("CRUD: ユーザーの登録と取得")
    @Transactional
    fun testCreateAndFind() {
        val newUser = User(
            name = "David",
            email = "david@example.com",
            createdAt = LocalDateTime.of(2026, 1, 4, 10, 0),
        )
        val created = userRepository.create(newUser)

        assertThat(created.id).isNotNull()
        val found = userRepository.findById(created.id!!)
        assertThat(found).isNotNull
        assertThat(found?.name).isEqualTo("David")
        assertThat(found?.email).isEqualTo("david@example.com")
    }

    @Test
    @DisplayName("CRUD: ユーザーの更新")
    @Transactional
    fun testUpdate() {
        val existing = userRepository.findById(1L)
        assertThat(existing).isNotNull

        val updated = existing!!.copy(name = "Alice Updated", email = "alice.updated@example.com")
        val success = userRepository.update(updated)
        assertThat(success).isTrue()

        val reloaded = userRepository.findById(1L)
        assertThat(reloaded?.name).isEqualTo("Alice Updated")
        assertThat(reloaded?.email).isEqualTo("alice.updated@example.com")
    }

    @Test
    @DisplayName("CRUD: ユーザーの削除")
    @Transactional
    fun testDelete() {
        val success = userRepository.deleteById(3L)
        assertThat(success).isTrue()

        val found = userRepository.findById(3L)
        assertThat(found).isNull()
    }

    @Test
    @DisplayName("JOIN: 1対多の User と Orders 取得")
    @Transactional
    fun testFindWithOrders() {
        val userWithOrders = userRepository.findWithOrders(1L)

        assertThat(userWithOrders).isNotNull
        assertThat(userWithOrders?.name).isEqualTo("Alice")
        assertThat(userWithOrders?.orders).hasSize(2)
        assertThat(userWithOrders?.orders?.map { it.orderCode }).containsExactly("ORD-001", "ORD-002")
        assertThat(userWithOrders?.orders?.first()?.status).isEqualTo(OrderStatus.COMPLETED)
    }

    @Test
    @DisplayName("動的クエリ: 名前とメールでの条件検索")
    @Transactional
    fun testSearch() {
        // 全件
        val all = userRepository.search()
        assertThat(all).hasSize(3)

        // 名前検索
        val alice = userRepository.search(name = "Ali")
        assertThat(alice).hasSize(1)
        assertThat(alice.first().name).isEqualTo("Alice")

        // 存在しない条件
        val none = userRepository.search(name = "Unknown")
        assertThat(none).isEmpty()
    }

    @Test
    @DisplayName("トランザクション: 例外発生時のロールバック検証")
    fun testTransactionRollback() {
        val userToFail = User(
            name = "RollbackUser",
            email = "rollback@example.com",
            createdAt = LocalDateTime.now(),
        )

        assertThatThrownBy {
            userService.createUserWithFailure(userToFail)
        }.isInstanceOf(RuntimeException::class.java)

        val searched = userRepository.search(name = "RollbackUser")
        assertThat(searched).isEmpty()
    }
}
