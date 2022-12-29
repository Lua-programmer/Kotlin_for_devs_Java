package io.github.luaprogrammer.testcontainers

import org.flywaydb.database.mysql.MySQLConnection
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.MapPropertySource
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.lifecycle.Startables
import java.util.stream.Stream

@ContextConfiguration(initializers = [AbstractIntegrationTest.initializer::class])
open class AbstractIntegrationTest {
    internal class initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            startContainers()
            val environment = applicationContext.environment
            val testcontainers = MapPropertySource("testecontainers", createConnectionConfiguration())
            environment.propertySources.addFirst(testcontainers)  //criando as configurações de conexão a partir de um
        // container que ele subiu e setando isso nos environments do spring
        }

        companion object

            private var mysql: MySQLContainer<*> = MySQLContainer("mysql:8.0.28")

            private fun createConnectionConfiguration(): MutableMap<String, Any> {
                return java.util.Map.of(
                    "spring.datasource.url", mysql.jdbcUrl,
                    "spring.datasource.username", mysql.username,
                    "spring.datasource..password", mysql.password,
                )
            }

            private fun startContainers() {
                Startables.deepStart(Stream.of(mysql)).join()
            }
        }
    }