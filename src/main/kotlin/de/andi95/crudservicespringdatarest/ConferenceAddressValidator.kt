package de.andi95.crudservicespringdatarest

import org.springframework.validation.Errors
import org.springframework.validation.Validator

class ConferenceAddressValidator : Validator {

    override fun supports(clazz: Class<*>): Boolean {
        return Conference::class.java.isAssignableFrom(clazz)
    }

    override fun validate(target: Any, errors: Errors) {
        val conference: Conference = target as Conference
        if (conference.address.city == "Berlin" && !conference.address.zip.matches(Regex("1[0-9]{4}")))
        {
            errors.rejectValue("address", "wrong.zipcode.for.city", "ZIP code does not match city")
        }
    }
}
