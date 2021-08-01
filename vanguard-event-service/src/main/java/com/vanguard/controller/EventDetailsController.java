package com.vanguard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vanguard.models.EventDetails;
import com.vanguard.service.EventBuilderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/v1/events")
public class EventDetailsController {
	
	@Autowired
	private EventBuilderService eventBuilderService;
	
	@GetMapping(path = "/getFilteredData")
	@Operation(description = "${sw.eventservice.operation.getFilteredData}", responses = {
			@ApiResponse(responseCode = "200", description = "Success"),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
	public ResponseEntity<List<EventDetails>> getAllNumbers(){
		List<EventDetails> response = eventBuilderService.getFilteredData();
		return ResponseEntity.ok(response);
		
	}
	
	


}
