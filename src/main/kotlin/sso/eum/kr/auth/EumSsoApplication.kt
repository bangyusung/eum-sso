package sso.eum.kr.auth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EumSsoApplication

fun main(args: Array<String>) {
    runApplication<EumSsoApplication>(*args)
}
