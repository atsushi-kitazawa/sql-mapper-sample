package sample.hibernate.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sample.hibernate.model.User
import sample.hibernate.repository.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    @Transactional
    fun createUserWithFailure(user: User): User {
        val created = userRepository.create(user)
        throw RuntimeException("Simulated error for transaction rollback")
    }
}
