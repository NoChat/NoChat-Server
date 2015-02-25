package services.sms;
public class SetBase {
	private String api_key = "NCS54ECADC9569BB";
	private String api_secret = "812425307D7D0AA93FF37F30F215E96F";
	private String base_url="https://api.coolsms.co.kr/1/";
	
	public String getApiKey() {
		return api_key;
	}		
	public String getApiSecret() {
		return api_secret;
	}
	public String getBaseUrl() {
		return base_url;
	}
}
