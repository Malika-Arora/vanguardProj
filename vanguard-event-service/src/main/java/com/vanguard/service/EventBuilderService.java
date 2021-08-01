package com.vanguard.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.vanguard.common.NodeElements;
import com.vanguard.exceptions.CustomException;
import com.vanguard.models.EventDetails;
import com.vanguard.repository.EventDetailRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EventBuilderService {
	@Value(value = "${event.buyerSellerNodePath}")
	private String buyerSellerNodePath;
	
	@Value(value = "${event.paymentDetailsNodePath}")
	private String paymentDetailsNodePath;
	
	@Autowired
	private EventDetailRepository eventDetailRepository;
	
	public EventBuilderService(EventDetailRepository eventDetailRepository2) {
		this.eventDetailRepository = eventDetailRepository2;
	}

	public void buildEventDetails(List<InputStream> fileList) {
		fileList.forEach(file -> {
			EventDetails eventDetails = new EventDetails();
			try {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder;
				dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(file);
				doc.getDocumentElement().normalize();
				XPath xPath = XPathFactory.newInstance().newXPath();
				NodeList nodeList;
				nodeList = (NodeList) xPath.compile(buyerSellerNodePath).evaluate(doc, XPathConstants.NODESET);
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node nNode = nodeList.item(i);
					for (int j = 0; j < nNode.getChildNodes().getLength(); j++) {
						Node childNode = nNode.getChildNodes().item(j);
						if (childNode.getNodeType() == Node.ELEMENT_NODE
								&&  Objects.equals(NodeElements.buyerPartyReference.toString(), childNode.getNodeName())) {
							eventDetails.setBuyer_party(childNode.getAttributes().item(0).getNodeValue());
						}
						if (childNode.getNodeType() == Node.ELEMENT_NODE
								&&  Objects.equals(NodeElements.sellerPartyReference.toString(), childNode.getNodeName())) {
							eventDetails.setSeller_party(childNode.getAttributes().item(0).getNodeValue());
						}
					}
				}
				
				nodeList = (NodeList) xPath.compile(paymentDetailsNodePath).evaluate(doc, XPathConstants.NODESET);
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node nNode = nodeList.item(i);
					for (int j = 0; j < nNode.getChildNodes().getLength(); j++) {
						Node childNode = nNode.getChildNodes().item(j);
						if (childNode.getNodeType() == Node.ELEMENT_NODE
								&&  Objects.equals(NodeElements.currency.toString(), childNode.getNodeName())) {
							eventDetails.setPremium_currency(childNode.getTextContent());
						}
						if (childNode.getNodeType() == Node.ELEMENT_NODE
								&&  Objects.equals(NodeElements.amount.toString(), childNode.getNodeName())) {
							eventDetails.setPremium_amount(Float.parseFloat(childNode.getTextContent()));
						}
					}
				}
				saveEventDetails(eventDetails);
			} catch (XPathExpressionException e) {
				throw new CustomException("Error reading xml path");
			} catch (ParserConfigurationException e1) {
				throw new CustomException("Error parsing xml file");
			} catch (SAXException e) {
				throw new CustomException("Error reading xml path");
			} catch (IOException e) {
				throw new CustomException("Error reading xml path");
			}
		});
	}

	public EventDetails saveEventDetails(EventDetails eventDetails) {
		return eventDetailRepository.save(eventDetails);
		
	}

	public List<EventDetails> getFilteredData() {
		List<EventDetails> eventList =  eventDetailRepository.findFilteredData();
		return eventList.stream().filter(el->checkNotAnagram(el)).collect(Collectors.toList());
	}

	public Boolean checkNotAnagram(EventDetails el) {
		List<String> buyerList = new ArrayList<>(Arrays.asList(el.getBuyer_party().split("")));
	    List<String> sellerList = new ArrayList<>(Arrays.asList(el.getSeller_party().split("")));
	    
	    Collections.sort(buyerList);
	    Collections.sort(sellerList);
	    
	    String buyer = String.join("", buyerList);
	    String seller = String.join("", sellerList);
	    
	    return !buyer.equals(seller);
		
	}

	

}
