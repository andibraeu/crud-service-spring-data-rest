package de.andi95.crudservicespringdatarest

import org.slf4j.LoggerFactory
import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.RepositoryEventHandler

@RepositoryEventHandler
class ConferenceListener {

    private val logger = LoggerFactory.getLogger(javaClass)

    @HandleBeforeCreate
    fun handleBeforeCreate(conference: Conference) {
        logger.info("I'm going to create a conference in {}", conference.address.city)
    }
}