package com.vanguard.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.vanguard.application.VanguardEventServiceApplication;
import com.vanguard.models.EventDetails;
import com.vanguard.repository.EventDetailRepository;

@SpringBootTest(classes = {VanguardEventServiceApplication.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
public class EventBuilderServiceTest {
	private EventBuilderService eventBuilderService;
	@Mock
	private EventDetailRepository eventDetailRepository;
	@BeforeEach 
	public void init() { 
		MockitoAnnotations.initMocks(this);
		eventBuilderService = new EventBuilderService(eventDetailRepository); 
	  }
	
	@Test
	public void testGetFilterdData() throws Exception {
		when(eventDetailRepository.findFilteredData()).thenReturn(filteredData());
		List<EventDetails> eventList = eventBuilderService.getFilteredData();
		EventDetails ed = eventList.get(0);
		assertEquals(1, eventList.size());
		assertEquals("ANZ", ed.getBuyer_party());
		assertEquals("EMU_BANK", ed.getSeller_party());
		assertEquals(200.00F, ed.getPremium_amount());
		assertEquals("AUD", ed.getPremium_currency());
	}
	@Test
	public void testAnagram() throws Exception {
		EventDetails eventDetails = new EventDetails();
		eventDetails.setSeller_party("EMU_BANK");
		eventDetails.setBuyer_party("MUN_BAKE");
		eventDetails.setPremium_currency("AUD");
		eventDetails.setPremium_amount(100.00F);
		assertFalse(eventBuilderService.checkNotAnagram(eventDetails));
	}
	
	@Test
	public void testNotAnagram() throws Exception {
		EventDetails eventDetails = new EventDetails();
		eventDetails.setSeller_party("EMUD_BANK");
		eventDetails.setBuyer_party("MUN_BAKE");
		eventDetails.setPremium_currency("AUD");
		eventDetails.setPremium_amount(100.00F);
		assertTrue(eventBuilderService.checkNotAnagram(eventDetails));
	}
	@Test
	public void testSaveEventDetails() throws Exception{
		when(eventDetailRepository.save(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
		EventDetails eventDetails = new EventDetails();
		eventDetails.setSeller_party("EMUD_BANK");
		eventDetails.setBuyer_party("MUN_BAKE");
		eventDetails.setPremium_currency("AUD");
		eventDetails.setPremium_amount(100.00F);
		EventDetails result = eventBuilderService.saveEventDetails(eventDetails);
		assertEquals(eventDetails.getBuyer_party(),result.getBuyer_party() );
		assertEquals(eventDetails.getSeller_party(), result.getSeller_party());
		assertEquals(eventDetails.getPremium_amount(), result.getPremium_amount());
		assertEquals(eventDetails.getPremium_currency(), result.getPremium_currency());
	}
	
	public List<EventDetails> filteredData() {
		List<EventDetails> eventList = new ArrayList<>();
		EventDetails eventDetails = new EventDetails();
		eventDetails.setSeller_party("EMU_BANK");
		eventDetails.setBuyer_party("MUN_BAKE");
		eventDetails.setPremium_currency("AUD");
		eventDetails.setPremium_amount(100.00F);
		eventList.add(eventDetails);
		EventDetails eventDetails1 = new EventDetails();
		eventDetails1.setSeller_party("EMU_BANK");
		eventDetails1.setBuyer_party("ANZ");
		eventDetails1.setPremium_currency("AUD");
		eventDetails1.setPremium_amount(200.00F);
		eventList.add(eventDetails1);
		return eventList;
	}

	
}
