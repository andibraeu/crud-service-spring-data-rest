package de.andi95.crudservicespringdatarest

import com.fasterxml.jackson.databind.JsonNode
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebFlux
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebFlux
class ExtendedIntegrationTest(@Autowired val client: WebTestClient,
                              @Autowired val dbBootstrap: DbBootstrap) {

    @Test
    internal fun `update element via patch`() {
        val conferenceNumber = 300
        val locationHeader = `create conference and return location header`(conferenceNumber, 300)
        val numberOfParticipants = 350
        client.patch().uri(locationHeader)
                .syncBody(mapOf("participants" to numberOfParticipants))
                .exchange()
                .expectStatus()
                .isOk
                .expectBody()
                .jsonPath("participants").isEqualTo(numberOfParticipants)
    }

    @Test
    fun followHalLinks() {
        val links = client
                .post().uri("/conferences")
                .syncBody(dbBootstrap.createConference(600, 600))
                .exchange()
                .returnResult(JsonNode::class.java)
                .responseBody
                .blockFirst()
                ?.get("_links")

        // then
        client.get().uri(links?.get("self")?.get("href")?.asText().toString())
                .exchange()
                .expectStatus()
                .isOk
                .expectBody()
                .jsonPath("name")
                .isEqualTo("conference 600")
    }


    private fun `create conference and return location header`(conferenceNumber: Int, numberOfParticipants: Int): String {
        return client.post().uri("/conferences")
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody(dbBootstrap.createConference(conferenceNumber, numberOfParticipants))
                .exchange()
                .returnResult(Conference::class.java)
                .responseHeaders
                .location
                .toString()
    }
}