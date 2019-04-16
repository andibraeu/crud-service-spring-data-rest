package de.andi95.crudservicespringdatarest

import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import kotlin.math.roundToInt

@Component
class DbBootstrap(val conferenceRepository: ConferenceRepository) : ApplicationListener<ContextRefreshedEvent>{

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun onApplicationEvent(p0: ContextRefreshedEvent) {
        for (count in 1..25) {
            logger.info("import dataset {}", count)
            val randomNumber = Math.random() * 100 + count
            conferenceRepository.save(createConference(count, randomNumber.roundToInt()))
        }
    }

    fun createConference(number: Int, numberOfParticipants: Int) =
            Conference(null, "conference $number", numberOfParticipants,
                    Address("street $number", "zip $number", "city $number"))

}