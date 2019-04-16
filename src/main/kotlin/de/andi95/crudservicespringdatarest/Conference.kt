package de.andi95.crudservicespringdatarest

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Conference(
        @Id val id:String? = null,
        val name: String,
        val participants: Int,
        val address: Address
)

data class Address(
        val street: String,
        val zip: String,
        val city: String
)