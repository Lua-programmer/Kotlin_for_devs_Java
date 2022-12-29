package io.github.luaprogrammer.integrationtests.controller

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.luaprogrammer.configs.TestConfig
import io.github.luaprogrammer.model.Person
import io.github.luaprogrammer.testcontainers.AbstractIntegrationTest
import io.restassured.RestAssured.given
import io.restassured.builder.RequestSpecBuilder
import io.restassured.common.mapper.TypeRef
import io.restassured.filter.log.LogDetail
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation::class)//pq a gente quer executar os testes em uma ordem especifica
@TestInstance(TestInstance.Lifecycle.PER_CLASS)//um cliclo de vida por classe
class PersonControllerTest : AbstractIntegrationTest() {
    private var specification: RequestSpecification? = null
    private var objectMapper: ObjectMapper? = null
    private var person: Person? = null

    @BeforeAll//antes de subir os testes faça isso
    fun setup() {
        objectMapper = ObjectMapper()
        objectMapper!!.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) //desabilitar falhas quando não tiver propriedades conhecidas
        person = Person()

    }

    @Test
    @Order(1)
    fun postSetup() {
        specification = RequestSpecBuilder()
            .setBasePath("/person")
            .setPort(TestConfig.SERVER_PORT)
                .addFilter(RequestLoggingFilter(LogDetail.ALL)) //passar detalhes para o console
                .addFilter(ResponseLoggingFilter(LogDetail.ALL))
            .build()
    }
    @Test
    @Order(2)
    fun testCreate() {
        mockPerson()

        val content: String = given()
            .spec(specification)
            .contentType(TestConfig.CONTENT_TYPE)
            .body(person)
            .`when`()
                .post()
            .then()
                .statusCode(200)
                .extract()
                    .body()
                        .asString()

        val createdPerson = objectMapper!!.readValue(content, Person::class.java)

        person = createdPerson

        //verifica se as propriedades são nulas
        assertNotNull(createdPerson.id)
        assertNotNull(createdPerson.firstName)
        assertNotNull(createdPerson.lastName)
        assertNotNull(createdPerson.address)
        assertNotNull(createdPerson.gender)
        assertTrue(createdPerson.id!! > 0)  //verifica se id é maior que 0

        //verificar se o nome das pessoas criadas são iguais a que se espera
        assertEquals("Richard", createdPerson.firstName)
        assertEquals("Stallman", createdPerson.lastName)
        assertEquals("New York City, New York, US", createdPerson.address)
        assertEquals("Male", createdPerson.gender)

    }

    @Test
    @Order(3)
    fun testUpdate() {
        person!!.lastName = "Matthew Stallman"

        val content: String = given()
            .spec(specification)
            .contentType(TestConfig.CONTENT_TYPE)
            .body(person)
            .`when`()
                .put()
            .then()
                .statusCode(200)
                .extract()
                    .body()
                        .asString()

        val updatedPerson = objectMapper!!.readValue(content, Person::class.java)

        person = updatedPerson

        //verifica se as propriedades são nulas
        assertNotNull(updatedPerson.id)
        assertNotNull(updatedPerson.firstName)
        assertNotNull(updatedPerson.lastName)
        assertNotNull(updatedPerson.address)
        assertNotNull(updatedPerson.gender)
        assertTrue(updatedPerson.id!! > 0)  //verifica se id é maior que 0

        //verificar se o nome das pessoas criadas são iguais a que se espera
        assertEquals(person!!.id, updatedPerson.id)
        assertEquals("Richard", updatedPerson.firstName)
        assertEquals("Matthew Stallman", updatedPerson.lastName)
        assertEquals("New York City, New York, US", updatedPerson.address)
        assertEquals("Male", updatedPerson.gender)

    }

    @Test
    @Order(4)
    fun testFindById() {
        val content: String = given()
            .spec(specification)
            .contentType(TestConfig.CONTENT_TYPE)
                .pathParam("id", person!!.id)
            .body(person)
            .`when`()
                .get("{id}")
            .then()
                .statusCode(200)
                .extract()
                    .body()
                        .asString()

        val foundPerson = objectMapper!!.readValue(content, Person::class.java)

        person = foundPerson

        //verifica se as propriedades são nulas
        assertNotNull(foundPerson.id)

        assertNotNull(foundPerson.firstName)
        assertNotNull(foundPerson.lastName)
        assertNotNull(foundPerson.address)
        assertNotNull(foundPerson.gender)
        assertTrue(foundPerson.id!! > 0)  //verifica se id é maior que 0

        //verificar se o nome das pessoas criadas são iguais a que se espera
        assertEquals(person!!.id, foundPerson.id)
        assertEquals("Richard", foundPerson.firstName)
        assertEquals("Matthew Stallman", foundPerson.lastName)
        assertEquals("New York City, New York, US", foundPerson.address)
        assertEquals("Male", foundPerson.gender)

    }

    @Test
    @Order(5)
    fun testDelete() {
       given()
            .spec(specification)
            .contentType(TestConfig.CONTENT_TYPE)
                .pathParam("id", person!!.id)
            .body(person)
            .`when`()
                .delete("{id}")
            .then()
                .statusCode(204)

    }

    @Test
    @Order(6)
    fun testFindAll(){
        val content = given().spec(specification)
            .contentType(TestConfig.CONTENT_TYPE)
            .`when`()
            .get()
            .then()
            .statusCode(200)
            .extract()
            .body()
            .`as`(object : TypeRef<java.util.List<Person?>?>(){})

        val foundPersonOne = content?.get(0)

        assertNotNull(foundPersonOne!!.id)
        assertNotNull(foundPersonOne.firstName)
        assertNotNull(foundPersonOne.lastName)
        assertNotNull(foundPersonOne.address)
        assertNotNull(foundPersonOne.gender)
        assertTrue(foundPersonOne.id!! > 0)

        assertEquals("Leandro", foundPersonOne.firstName)
        assertEquals("Costa", foundPersonOne.lastName)
        assertEquals("Uberlândia - Minas Gerais - Brasil", foundPersonOne.address)
        assertEquals("Male", foundPersonOne.gender)

        val foundPersonSix = content?.get(5)

        assertNotNull(foundPersonSix!!.id)
        assertNotNull(foundPersonSix.firstName)
        assertNotNull(foundPersonSix.lastName)
        assertNotNull(foundPersonSix.address)
        assertNotNull(foundPersonSix.gender)
        assertTrue(foundPersonSix.id!! > 0)

        assertEquals("Marcos", foundPersonSix.firstName)
        assertEquals("Paulo", foundPersonSix.lastName)
        assertEquals("Patos de Minas - Minas Gerais - Brasil", foundPersonSix.address)
        assertEquals("Male", foundPersonSix.gender)
    }

    private fun mockPerson() {
        person?.firstName = "Richard"
        person?.lastName = "Stallman"
        person?.address = "New York City, New York, US"
        person?.gender = "Male"
    }
}