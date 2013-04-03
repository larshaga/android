/**
 * Copyright (C) Posten Norge AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.digipost.android.authentication;

import java.math.BigInteger;
import java.security.SecureRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.MultivaluedMap;

import no.digipost.android.R;
import no.digipost.android.api.ApiConstants;
import no.digipost.android.api.DigipostApiException;
import no.digipost.android.api.DigipostAuthenticationException;
import no.digipost.android.api.DigipostClientException;
import no.digipost.android.api.JSONConverter;
import no.digipost.android.gui.NetworkConnection;
import no.digipost.android.model.Access;
import no.digipost.android.model.TokenValue;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class OAuth2 {

	private static String state = "";
	private static String nonce = "";

	private static SecureRandom random = new SecureRandom();

	public static String getAuthorizeURL() {
		state = getSecureRandom(20);
		return ApiConstants.URL_API_OAUTH_AUTHORIZE_NEW + "?" + ApiConstants.RESPONSE_TYPE + "=" + ApiConstants.CODE + "&"
				+ ApiConstants.CLIENT_ID + "=" + Secret.CLIENT_ID + "&" + ApiConstants.REDIRECT_URI + "=" + Secret.REDIRECT_URI + "&"
				+ ApiConstants.STATE + "=" + state;
	}

	public static void retriveAccessToken(final String url_state, final String url_code, final Context context)
			throws DigipostApiException, DigipostClientException, DigipostAuthenticationException {
		nonce = getSecureRandom(20);

		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		params.add(ApiConstants.GRANT_TYPE, ApiConstants.CODE);
		params.add(ApiConstants.CODE, url_code);
		params.add(ApiConstants.REDIRECT_URI, Secret.REDIRECT_URI);
		params.add(ApiConstants.NONCE, nonce);

		Access data = getAccessData(params, context);

		if (!stateVerified(url_state)) {
			throw new DigipostApiException(context.getString(R.string.error_digipost_api));
		} else if (!authenticationVerified(data.getId_token())) {
			throw new DigipostApiException(context.getString(R.string.error_digipost_api));
		}

		encryptAndStoreRefreshToken(data, context);
	}

	public static void retriveAccessToken(final String refresh_token, final Context context) throws DigipostApiException,
			DigipostClientException, DigipostAuthenticationException {
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		params.add(ApiConstants.GRANT_TYPE, ApiConstants.REFRESH_TOKEN);
		params.add(ApiConstants.REFRESH_TOKEN, refresh_token);
		Secret.ACCESS_TOKEN = getAccessData(params, context).getAccess_token();
	}

	public static void encryptAndStoreRefreshToken(final Access data, final Context context) {
		Secret.ACCESS_TOKEN = data.getAccess_token();
		String refresh_token = data.getRefresh_token();
		KeyStoreAdapter ksa = new KeyStoreAdapter();
		String cipher = ksa.encrypt(refresh_token);
		refresh_token = null;
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = settings.edit();
		editor.putString(ApiConstants.REFRESH_TOKEN, cipher);
		editor.commit();
	}

	public static void updateAccessToken(final Context context) throws DigipostApiException, DigipostClientException,
			DigipostAuthenticationException {
		String encrypted_refresh_token = getEncryptedRefreshToken(context);

		KeyStoreAdapter ksa = new KeyStoreAdapter();
		String refresh_token = ksa.decrypt(encrypted_refresh_token);
		retriveAccessToken(refresh_token, context);
	}

	public static String getEncryptedRefreshToken(final Context context) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		return settings.getString(ApiConstants.REFRESH_TOKEN, "");
	}

	public static Access getAccessData(final MultivaluedMap<String, String> params, final Context context) throws DigipostApiException,
			DigipostClientException, DigipostAuthenticationException {
		Client c = Client.create();
		WebResource r = c.resource(ApiConstants.URL_API_OAUTH_ACCESSTOKEN);
		ClientResponse cr = null;
		try {
			cr = r.queryParams(params)
					.header(ApiConstants.POST, ApiConstants.POST_API_ACCESSTOKEN_HTTP)
					.header(ApiConstants.CONTENT_TYPE, ApiConstants.APPLICATION_FORM_URLENCODED)
					.header(ApiConstants.AUTHORIZATION, getB64Auth())
					.post(ClientResponse.class);
		} catch (Exception e) {
			throw new DigipostClientException(context.getString(R.string.error_your_network));
		}

		int statusCode = cr.getStatus();

		if (statusCode == 400) {
			throw new DigipostAuthenticationException(context.getString(R.string.error_invalid_token));
		}

		NetworkConnection networkConnection = new NetworkConnection(context);
		networkConnection.checkHttpStatusCode(statusCode);

		return (Access) JSONConverter.processJackson(Access.class, cr.getEntityInputStream());
	}

	private static boolean authenticationVerified(final String id_token) {
		String split_by = ".";
		int splitindex = id_token.indexOf(split_by);

		String signature_enc = id_token.substring(0, splitindex);
		String token_value_enc = id_token.substring(splitindex + split_by.length(), id_token.length());
		String signature_dec = new String(Base64.decode(signature_enc.getBytes(), Base64.DEFAULT));

		if (!encryptHmacSHA256(token_value_enc).equals(signature_dec)) {
			return false;
		}

		TokenValue data = (TokenValue) JSONConverter.processJackson(TokenValue.class,
				new String(Base64.decode(token_value_enc.getBytes(), Base64.DEFAULT)));
		String aud = data.getAud();

		if (!aud.equals(Secret.CLIENT_ID)) {
			return false;
		}
		return true;
	}

	private static boolean stateVerified(final String received_state) {
		return state.equals(received_state);
	}

	private static String getSecureRandom(final int num_bytes) {
		return new BigInteger(130, random).toString(32);
	}

	private static String encryptHmacSHA256(final String data) {
		SecretKeySpec secretKey = new SecretKeySpec(Secret.CLIENT_SECRET.getBytes(), ApiConstants.HMACSHA256);
		Mac mac = null;
		try {
			mac = Mac.getInstance(ApiConstants.HMACSHA256);
			mac.init(secretKey);
		} catch (Exception e) {
			// Ignore
		}

		byte[] hmacData = mac.doFinal(data.getBytes());

		return new String(hmacData);
	}

	private static String getB64Auth() {
		String source = Secret.CLIENT_ID + ":" + Secret.CLIENT_SECRET;
		return ApiConstants.BASIC + Base64.encodeToString(source.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
	}
}
