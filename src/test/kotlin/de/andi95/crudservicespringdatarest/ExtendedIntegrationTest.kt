package de.andi95.crudservicespringdatarest

import com.fasterxml.jackson.databind.JsonNode
import org.assertj.core.api.Assertions
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
    fun `follow hal links`() {
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

    @Test
    internal fun `read all elements via get from the first page`() {
        val result = client.get().uri("/conferences")
                .exchange()
                .expectStatus()
                .isOk
                .returnResult(JsonNode::class.java)
                .responseBody
                .blockFirst()

        Assertions.assertThat(result?.get("_embedded")?.get("conferences")?.size()).isEqualTo(20)
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

    @Test
    internal fun `validator avoids to store invalid conference`() {
        val conference = dbBootstrap.createConference(100, 100)
        conference.address = Address("street", "98765", "Berlin")
        client.post().uri("/conferences")
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody(conference)
                .exchange()
                .expectStatus()
                .is4xxClientError
                .expectBody()
                .jsonPath("errors")
                .exists()
    }

    @Test
    internal fun `validator avoids to update invalid conference`() {
        val conferenceUri = client.post().uri("/conferences")
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody(dbBootstrap.createConference(100, 100))
                .exchange()
                .returnResult(Conference::class.java)
                .responseHeaders
                .location
                .toString()

        val conference = dbBootstrap.createConference(100, 100)
        conference.address = Address("street", "98765", "Berlin")

        client.put().uri(conferenceUri)
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody(conference)
                .exchange()
                .expectStatus()
                .is4xxClientError
                .expectBody()
                .jsonPath("errors")
                .exists()
    }

}