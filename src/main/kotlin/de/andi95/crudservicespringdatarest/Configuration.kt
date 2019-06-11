package de.andi95.crudservicespringdatarest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer

@Configuration
class Configuration : RepositoryRestConfigurer {

    override fun configureValidatingRepositoryEventListener(validatingListener: ValidatingRepositoryEventListener?) {
        validatingListener?.addValidator("beforeCreate", conferenceAddressValidator())
        validatingListener?.addValidator("beforeSave", conferenceAddressValidator())
    }

    @Bean
    fun conferenceAddressValidator() : ConferenceAddressValidator {
        return ConferenceAddressValidator()
    }
}