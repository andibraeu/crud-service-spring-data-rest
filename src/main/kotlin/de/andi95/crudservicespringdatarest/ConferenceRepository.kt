package de.andi95.crudservicespringdatarest

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface ConferenceRepository : PagingAndSortingRepository<Conference, String>