package de.andi95.crudservicespringdatarest

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Conference(
        @Id var id:String? = null,
        var name: String,
        var participants: Int,
        var address: Address
)

data class Address(
        var street: String,
        var zip: String,
        var city: String
)