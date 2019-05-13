package com.hsbc.lu.ao;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.lufax.crypto2.LufaxSignException;
import com.lufax.crypto2.LufaxSigner;
@Component
public class LufaxService {
	
	//Common parameters for REST API
	private String app_id = "?";
	private String sign;  //generated based on other parameter
	private String request_time = "10000"; //10 seconds
	private String sign_type = ""; // optional parameter
	private String sign_version = "1"; // optional parameter

	private String ver = "1";
	private String source = "?";
	private String merchantId = "10002";

	public static String PARTY_NO = "partyNo";
	public static String REACHED_THREE_AUM = "hasReachedThreeAum";
	public static String RES_CODE = "res_code";
	public static String RES_MSG = "res_msg";
	public static String DEFAULT_CHARSET = "utf-8";

	private LufaxSigner encrypto;
	private LufaxSigner decrypto;
	
	@Value("${privateKey}")
	private String privateKey;
	@Value("${publicKey}")
	private String publicKey;

	/*public static void main(String[] arg) {
		LufaxService service = new LufaxService();
		System.out.println(testProp);
		
	}*/
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

		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, String> params = new HashMap<String, String>();
		
		params.put("app_id", app_id);
		params.put("request_time", request_time);
		params.put("ver", ver);
		params.put("token", token);
		params.put("source", source);
		params.put("merchantId", merchantId);

		String sign = "";
		sign = genetateSign(params);

		Map<String, String> paramsSigned = new HashMap<String, String>();
		paramsSigned.putAll(params);
		paramsSigned.put("sign", sign);
		
		/*
		// if Lufax request URL parameter we need to append the parameter to url.
		StringBuffer strPara = new StringBuffer();
		for (Iterator<String> it = paramsSigned.keySet().iterator(); it.hasNext();) {
			String para = it.next();
			strPara.append("&").append(para).append("=").append(paramsSigned.get(para));
		}*/    
		
		try {
            String url = "/api/v1/alliance/hsbcbank/get-user-info-by-token";
            
            Map<String, String> paramHeader = new HashMap<>(); 
            paramHeader.put("Content-Type", "x-www-form-urlencoded"); 
            paramHeader.put("Accept", "application/json"); 
            
            String result = null; 
            HttpPost httpPost = new HttpPost(url); 
            setHeader(httpPost, paramHeader); 
            try {
            	setBody(httpPost, paramsSigned, DEFAULT_CHARSET);
            } catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(httpPost); 
            
            String lusign = response.getFirstHeader("X-LUFAX-API-SIGN").getValue();

			String content = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);

			Boolean isValid;
			try {
				isValid = decrypto.validateSign(content, lusign);
				if (isValid){
					// TODO: put the real data of REST API response
					data.put(PARTY_NO, "the value from REST response");
					// TODO: put the real data of REST API response
					data.put(REACHED_THREE_AUM, " ");
				}
			} catch (LufaxSignException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		return data;
	}
	
	private void setHeader(HttpRequestBase request, Map<String, String> paramHeader) {
		// set Header
		if (paramHeader != null) {
			Set<String> keySet = paramHeader.keySet();
			for (String key : keySet) {
				request.addHeader(key, paramHeader.get(key));
			}
		}
	}

	private void setBody(HttpPost httpPost, Map<String, String> paramBody, String charset) throws Exception {
		// set parameters
		if (paramBody != null) {
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			Set<String> keySet = paramBody.keySet();
			for (String key : keySet) {
				list.add(new BasicNameValuePair(key, paramBody.get(key)));
			}

			if (list.size() > 0) {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, DEFAULT_CHARSET);
				httpPost.setEntity(entity);
			}
		}
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
		try {
			encrypto = new LufaxSigner(privateKey, null);
			decrypto = new LufaxSigner(null, publicKey);
			String sign = encrypto.sign(params);
			System.out.println(sign);
			
			// System.out.println("validateSign: "+decrypto.validateSign(params, sign));
		} catch (LufaxSignException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sign;
	}

}
