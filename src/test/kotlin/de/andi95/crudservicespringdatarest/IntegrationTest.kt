package de.andi95.crudservicespringdatarest

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
class IntegrationTest(@Autowired val client: WebTestClient,
                      @Autowired val dbBootstrap: DbBootstrap) {

    @Test
    fun `check if controller is running`() {
        client.get().uri("/")
                .exchange()
                .expectStatus().isOk
    }

    @Test
    internal fun `create element via post`() {
        client.post().uri("/conferences")
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody(dbBootstrap.createConference(100, 100))
                .exchange()
                .expectStatus()
                .isCreated
                .expectBody()
                .jsonPath("name").isEqualTo("conference 100")
    }

    @Test
    internal fun `read element via get`() {
        val conferenceNumber = 200
        val locationHeader = `create conference and return location header`(conferenceNumber, 200)
        client.get().uri(locationHeader)
                .exchange()
                .expectStatus()
                .isOk
                .expectBody()
                .jsonPath("name").isEqualTo("conference $conferenceNumber")
    }

    @Test
    internal fun `update element via put`() {
        val conferenceNumber = 300
        val locationHeader = `create conference and return location header`(conferenceNumber, 300)
        val numberOfParticipants = 350
        val updatedConference = dbBootstrap.createConference(conferenceNumber, numberOfParticipants)
        client.put().uri(locationHeader)
                .syncBody(updatedConference)
                .exchange()
                .expectStatus()
                .isOk
                .expectBody()
                .jsonPath("participants").isEqualTo(numberOfParticipants)
    }

    @Test
    internal fun `delete element via delete`() {
        val locationHeader = `create conference and return location header`(500, 500)
        client.delete().uri(locationHeader)
                .exchange()
                .expectStatus()
                .isNoContent
    }

    @Test
    internal fun `get 404 when try to read non-existing element`() {
        client.get().uri("/conferences/i-do-not-exist")
                .exchange()
                .expectStatus()
                .isNotFound
    }

    @Test
    internal fun `get 201 when try to update non-existing element`() {
        val conferenceToUpdate = dbBootstrap.createConference(123, 456)
        client.put().uri("/conferences/i-do-not-exist-right-now")
                .syncBody(conferenceToUpdate)
                .exchange()
                .expectStatus()
                .isCreated
    }

    @Test
    internal fun `get 404 when try to delete non-existing element`() {
        client.delete().uri("/conferences/i-do-not-exist")
                .exchange()
                .expectStatus()
                .isNotFound
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