package org.scripps.branch.service;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;

public final class GoogleAuthHelper {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GoogleAuthHelper.class);
	private static final String CLIENT_ID = "1032075006754-pb1652ka24fuhbkpla0t0hhvpskm0c19.apps.googleusercontent.com";

	private static final String CLIENT_SECRET = "YLdmgj4KAoEL-1NOnjm_U9p5";

	private static final String CALLBACK_URI = "http://localhost:8080/branch/auth/google";

	String jsonIdentity;

	// start google authentication constants
	private static final Iterable<String> SCOPE = Arrays
			.asList("https://www.googleapis.com/auth/userinfo.profile;https://www.googleapis.com/auth/userinfo.email"
					.split(";"));
	private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	// end google authentication constants

	private String stateToken;

	private final GoogleAuthorizationCodeFlow flow;

	/**
	 * Constructor initializes the Google Authorization Code Flow with CLIENT
	 * ID, SECRET, and SCOPE
	 */
	public GoogleAuthHelper() {
		flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT,
				JSON_FACTORY, CLIENT_ID, CLIENT_SECRET,
				(Collection<String>) SCOPE).build();

		generateStateToken();
		LOGGER.debug("flow in google auth: " + flow);
		LOGGER.debug("HTTP_TRANSPORT: " + HTTP_TRANSPORT);
		LOGGER.debug("JSON_FACTORY: " + JSON_FACTORY);
		LOGGER.debug("SCOPE: " + SCOPE);

	}

	/**
	 * Builds a login URL based on client ID, secret, callback URI, and scope
	 */
	public String buildLoginUrl() {

		final GoogleAuthorizationCodeRequestUrl url = flow
				.newAuthorizationUrl();
		LOGGER.debug("url google auth: " + url);
		return url.setRedirectUri(CALLBACK_URI).setState(stateToken).build();

	}

	/**
	 * Generates a secure state token
	 */
	private void generateStateToken() {

		SecureRandom sr1 = new SecureRandom();

		stateToken = "google;" + sr1.nextInt();

		LOGGER.debug("state toke google auth: " + stateToken);

	}

	/**
	 * Accessor for state token
	 */
	public String getStateToken() {
		return stateToken;
	}

	/**
	 * Expects an Authentication Code, and makes an authenticated request for
	 * the user's profile information
	 * 
	 * @return JSON formatted user profile information
	 * @param authCode
	 *            authentication code provided by google
	 */
	public String getUserInfoJson(final String authCode) throws IOException {

		final GoogleTokenResponse response = flow.newTokenRequest(authCode)
				.setRedirectUri(CALLBACK_URI).execute();
		final Credential credential = flow.createAndStoreCredential(response,
				null);
		final HttpRequestFactory requestFactory = HTTP_TRANSPORT
				.createRequestFactory(credential);
		// Make an authenticated request
		final GenericUrl url = new GenericUrl(USER_INFO_URL);
		final HttpRequest request = requestFactory.buildGetRequest(url);
		request.getHeaders().setContentType("application/json");
		ObjectMapper mapper = new ObjectMapper();
		jsonIdentity = request.execute().parseAsString();
		JsonNode obj = mapper.readTree(jsonIdentity);
		LOGGER.debug("response: " + response);
		LOGGER.debug("credential: " + credential);
		LOGGER.debug("requestFactory: " + requestFactory);
		LOGGER.debug("url: " + url);
		LOGGER.debug("request: " + request);
		LOGGER.debug("jsonIdentity: " + obj.get("id"));

		return jsonIdentity;

	}

}
