package com.vanguard.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "eventDetails")
public class EventDetails {
	@Id
	 @GeneratedValue(strategy = GenerationType.AUTO)
	 private int eventId;
	 private String buyer_party;
	 private String seller_party;
	 private float premium_amount;
	 private String premium_currency;
}

