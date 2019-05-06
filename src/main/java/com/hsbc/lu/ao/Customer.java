package com.hsbc.lu.ao;

import java.util.Date;

// PO for customer 
public class Customer {
	
	// mandatory fields
	private String partyNo; // Lufax customer ID
	private String name, phone; 
	private boolean gender; // male - Y; female - N
	private boolean isHSBCCNCustomer; // mandatory
	private Date applyDate; 
	// optional fields
	private String city; 
	private boolean interestedMarketActivity;
	private boolean hasReachedThreeAum; // request from Lufax, Y/N
	private boolean applyAccpetStatus; //apply accept status 
	private boolean openStatus;
	private boolean isZyAccount; //卓越用户

	public Customer(String partyNo, String name, boolean gender, String phone, boolean isHSBCCNCustomer, Date applyDate){
		this.partyNo = partyNo;
		this.name = name;
		this.gender = gender;
		this.phone = phone;
		this.isHSBCCNCustomer = isHSBCCNCustomer;
		this.applyDate = applyDate;
	}
	
	//TODO: add all set and get method
	
}
