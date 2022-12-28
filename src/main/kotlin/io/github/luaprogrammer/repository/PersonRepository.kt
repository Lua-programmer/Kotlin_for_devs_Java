package io.github.luaprogrammer.repository

import io.github.luaprogrammer.model.Person
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PersonRepository : JpaRepository<Person, Long?>
//fun mstr(value: String?) = if (value != null) value.lenght else null