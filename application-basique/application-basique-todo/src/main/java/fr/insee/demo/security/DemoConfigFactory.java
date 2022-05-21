package fr.insee.demo.security;

public class DemoConfigFactory  {
	
	
	public String clientId = "localhost-java";
	public String clientSecret = "";
	private String configurationEndpoint =
			  "http://localhost:8180/auth/realms/test/" +
						 ".well-known/openid-configuration";
	
	
}
