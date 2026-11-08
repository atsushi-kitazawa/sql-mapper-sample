package sample.mybatis

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import sample.mybatis.mapper.UserMapper
import sample.mybatis.model.OrderStatus
import sample.mybatis.model.User
import sample.mybatis.service.UserService
import java.time.LocalDateTime

@SpringBootTest
class MyBatisUserRepositoryTest {

    @Autowired
    private lateinit var userMapper: UserMapper

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
        userMapper.insert(newUser)

        assertThat(newUser.id).isNotNull()
        val found = userMapper.findById(newUser.id!!)
        assertThat(found).isNotNull
        assertThat(found?.name).isEqualTo("David")
        assertThat(found?.email).isEqualTo("david@example.com")
    }

    @Test
    @DisplayName("CRUD: ユーザーの更新")
    @Transactional
    fun testUpdate() {
        val existing = userMapper.findById(1L)
        assertThat(existing).isNotNull

        existing!!.name = "Alice Updated"
        existing.email = "alice.updated@example.com"
        val rows = userMapper.update(existing)
        assertThat(rows).isEqualTo(1)

        val reloaded = userMapper.findById(1L)
        assertThat(reloaded?.name).isEqualTo("Alice Updated")
        assertThat(reloaded?.email).isEqualTo("alice.updated@example.com")
    }

    @Test
    @DisplayName("CRUD: ユーザーの削除")
    @Transactional
    fun testDelete() {
        val rows = userMapper.deleteById(3L)
        assertThat(rows).isEqualTo(1)

        val found = userMapper.findById(3L)
        assertThat(found).isNull()
    }

    @Test
    @DisplayName("JOIN: 1対多の User と Orders 取得")
    fun testFindWithOrders() {
        val userWithOrders = userMapper.findWithOrders(1L)

        assertThat(userWithOrders).isNotNull
        assertThat(userWithOrders?.name).isEqualTo("Alice")
        assertThat(userWithOrders?.orders).hasSize(2)
        assertThat(userWithOrders?.orders?.map { it.orderCode }).containsExactly("ORD-001", "ORD-002")
        assertThat(userWithOrders?.orders?.first()?.status).isEqualTo(OrderStatus.COMPLETED)
    }

    @Test
    @DisplayName("動的クエリ: 名前とメールでの条件検索")
    fun testSearch() {
        // 全件
        val all = userMapper.search()
        assertThat(all).hasSize(3)

        // 名前検索
        val alice = userMapper.search(name = "Ali")
        assertThat(alice).hasSize(1)
        assertThat(alice.first().name).isEqualTo("Alice")

        // 存在しない条件
        val none = userMapper.search(name = "Unknown")
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

        val searched = userMapper.search(name = "RollbackUser")
        assertThat(searched).isEmpty()
    }
}
