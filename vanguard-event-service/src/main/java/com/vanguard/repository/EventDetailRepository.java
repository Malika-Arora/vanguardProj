package com.vanguard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.vanguard.models.EventDetails;

@Repository
public interface EventDetailRepository extends CrudRepository<EventDetails, Integer> {
	@Query("SELECT ed FROM EventDetails ed WHERE (ed.seller_party ='EMU_BANK' AND ed.premium_currency='AUD')"+ 
	" OR (ed.seller_party ='BISON_BANK' AND ed.premium_currency='USD')")
	List<EventDetails> findFilteredData();
}