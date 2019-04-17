package de.andi95.crudservicespringdatarest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.config.EnableMongoAuditing

@SpringBootApplication
@EnableMongoAuditing
class CrudServiceSpringDataRestApplication

fun main(args: Array<String>) {
	runApplication<CrudServiceSpringDataRestApplication>(*args)
}

