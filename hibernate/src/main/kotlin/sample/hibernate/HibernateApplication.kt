package sample.hibernate

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HibernateApplication

fun main(args: Array<String>) {
    runApplication<HibernateApplication>(*args)
}
