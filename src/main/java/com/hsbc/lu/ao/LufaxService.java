package com.hsbc.lu.ao;

import java.util.HashMap;
import java.util.Map;

import com.lufax.crypto2.LufaxSignException;
import com.lufax.crypto2.LufaxSigner;

public class LufaxService {
	
	//Common parameters for REST API
	private String app_id = "10002";
	private String sign;  //generated based on other parameter
	private String request_time = "1000"; //million seconds
	private String sign_type; // optional parameter
	private String sign_version; // optional parameter

	private String ver = "1";
	private String source = "";
	private String merchantId = "";

	public static String PARTY_NO = "partyNo";
	public static String REACHED_THREE_AUM = "hasReachedThreeAum";
	public static String RES_CODE = "res_code";
	public static String RES_MSG = "res_msg";

	private static String privateKey;
	private static String publicKey;
	static {
		//TODO: get privateKey and publicKey from properties file
	}

	public Map<String, Object> getCustomerInfor(String token){
		
		return this.getCustomerInfor(ver, token, source, merchantId);
	}

	/*
	 * Function specific parameter:
	 * token // user token
	 * call Lufax API:
	 * {API_HOST}/api/v1/alliance/hsbcbank/get-user-info-by-token
	 * method: post
	 * format: x-www-form-urlencoded
	 * response: application/json
	 * response sample:
	 * {
	 *   "res_code": "0",
	 *   "res_msg": "处理正常",
	 *   "data": {
	 *   "partyNo": "ABCDEFGH123456",
	 *        "hasReachedThreeAum": "Y"
	 *   }
	 * } 
	 */
	
	public Map<String, Object> getCustomerInfor(String ver, String token, String source, String merchantId){
		//TODO: call REST API, parse result and return AUM
		Map<String, String> params = new HashMap<String, String>();
		
		params.put("app_id", app_id);
		params.put("request_time", request_time);
		params.put("ver", ver);
		params.put("token", token);
		params.put("source", source);
		params.put("merchantId", merchantId);

		String sign = "";
		sign = genetateSign(params);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(PARTY_NO, "the value from REST response"); // put the real data of REST API response
		data.put(REACHED_THREE_AUM, " "); // put the real data of REST API response
		
		return data;
	}
	/*
	 * String partyNo; //get from getAUMStatus
	 * String applyAccpetStatus; //Y/N
	 * String openStatus; //Y/N
	 * String isZyAccount;//Y/N
	 * 
	 * method: post
	 * format: x-www-form-urlencoded
	 * response: application/json
	 * {API_HOST}/api/v1/alliance/hsbcbank/sync-open-result
	 * 
	 */
	public Map<String, Object> synchAOResult(String partyNo, String applyAccpetStatus, String openStatus, String isZyAccount){
		Map<String, String> params = new HashMap<String, String>();
		
		params.put("app_id", app_id);
		params.put("request_time", request_time);
		params.put("ver", ver);
		params.put("partyNo", partyNo);
		params.put("source", source);
		params.put("merchantId", merchantId);
		params.put("applyAccpetStatus", applyAccpetStatus);
		params.put("openStatus", openStatus);
		params.put("isZyAccount", isZyAccount);

		String sign = "";
		sign = genetateSign(params);

		//TODO: call REST API 
		
		Map synchData = new HashMap();
		synchData.put(RES_CODE, "");
		synchData.put(RES_MSG, "");
		return synchData;
	}
	
	private String genetateSign(Map<String, String> params){
		LufaxSigner encrypto;
		//LufaxSigner decrypto;
		try {
			encrypto = new LufaxSigner(privateKey, null);
			//decrypto = new LufaxSigner(null, publicKey);

			// 请求参数（公共及私有）置于map内
			/*Map<String, String> params = new HashMap<String, String>();
			params.put("app_id", "10002");
			params.put("sign_version", "1");
			params.put("request_time", "1000");
			params.put("param1", "");*/
			// 调用LufaxCrypto库，进行签名计算
			String sign = encrypto.sign(params);
			System.out.println(sign);
			
			//System.out.println("validateSign: "+decrypto.validateSign(params, sign));
		} catch (LufaxSignException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sign;
	}

	

}
