package sample.mybatis.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sample.mybatis.mapper.UserMapper
import sample.mybatis.model.User

@Service
class UserService(
    private val userMapper: UserMapper,
) {

    @Transactional
    fun createUserWithFailure(user: User): User {
        userMapper.insert(user)
        throw RuntimeException("Simulated error for transaction rollback")
    }
}
