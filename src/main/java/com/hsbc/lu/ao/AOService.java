package com.hsbc.lu.ao;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@CrossOrigin
@RestController
@RequestMapping(value="/api/lu/ao")
public class AOService {
    
	@Autowired
	private LufaxService lufaxService;
	private CustomerDAO customerDAO;
	private EmailUtil emailUtil;
	private ObjectMapper objectMapper = new ObjectMapper();
	
    /* register customer
     * a. send email to CCSS team about customer information
     * b. store customer information into DB
     */
    @RequestMapping(value="/customers", method=RequestMethod.POST)
    @ResponseBody
    public String registerCustomer(WebRequest request) throws JsonProcessingException {
    	System.out.println("partyNo: "+request.getParameter("partyNo"));
    	System.out.println("hasReachedThreeAum: "+request.getParameter("hasReachedThreeAum"));
    	
    	System.out.println("name: "+request.getParameter("name"));
    	System.out.println("gender: "+request.getParameter("gender"));
    	// other parameters ...
        //TODO: construct customer object
    	Customer cus = null;
        sendEmail2AO(cus); // construct the customer object
        addCustomer(cus); // construct the customer object
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("customer", cus);
    	return objectMapper.writeValueAsString(result);
    }

    /*
     * query customer AUM by token
     */
    @RequestMapping(value="/aum", method=RequestMethod.POST)
    @ResponseBody
    public String queryAUM(WebRequest request) throws JsonProcessingException {
    	
    	String token = request.getParameter("token");
    	System.out.println("token: " + token);
    	
    	Map <String, Object> customerAUM = getAUMByToken(token); 
    	String aum = (String)customerAUM.get(LufaxService.REACHED_THREE_AUM);
    	String partyNo = (String)customerAUM.get(LufaxService.PARTY_NO);

        Map<String, String> result = new HashMap<String, String>();
        result.put(LufaxService.PARTY_NO, partyNo);
        result.put(LufaxService.REACHED_THREE_AUM, aum);
//        result.put(LufaxService.PARTY_NO, "C0123456789");
//        result.put(LufaxService.REACHED_THREE_AUM, "Y");
    	return objectMapper.writeValueAsString(result);
    }
    // insert customer information to DB
    private void addCustomer(Customer customer){
    	customerDAO.insert(customer);
    }
    
    // send email to CCSS team about the customer information
    private void sendEmail2AO(Customer customer){
    	emailUtil.sendEmail();
    }
    
    //Invoke LufaxService.getAUMstatus()
    //return Y/N
    private Map<String, Object> getAUMByToken(String token){
    	Map<String, Object> data = lufaxService.getCustomerInfor(token);
    	return data;
    }
    
    //we will synchronize data with Lufax about the customer open account status.
    //Invoke LufaxService.synchAOResult()	
    private String synDataWithLufax(String partyNo){
    	//TODO: get the release data from the our DB.
    	String applyAccpetStatus = "N",  openStatus = "N",  isZyAccount="N";
    	Map synResult = lufaxService.synchAOResult(partyNo, applyAccpetStatus, openStatus, isZyAccount);
    	return (String)synResult.get(LufaxService.RES_CODE);
    }
}
