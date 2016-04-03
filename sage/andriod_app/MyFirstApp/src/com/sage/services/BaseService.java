package com.sage.services;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.sage.constants.ServicesConstants;

import android.app.Activity;
import android.net.Uri;
import android.text.TextUtils;

public abstract class BaseService {

	private Activity activity;

	public BaseService(Activity activity) {
		this.activity = activity;
	}

	public JsonElement service() throws Exception {
		InputStream is = null;
		InputStream caInput = null;
		OutputStream os  = null;
		try {

			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };
			// Install the all-trusting trust manager
			final SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

			int len = 1000;
			URL url = new URL(getUrl());

			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			addHeaderProperties(conn);

			conn.setReadTimeout(ServicesConstants.READ_TIMEOUT);
			conn.setConnectTimeout(ServicesConstants.CONNECTION_TIMEOUT);
			setHttpMethod(conn);

			if (!conn.getRequestMethod().equals(ServicesConstants.GET_REQUEST_TYPE)) {
				conn.setDoInput(true);
				conn.setDoOutput(true);

				Uri.Builder builder = new Uri.Builder();
				addBodyParameters(builder);
				String query = builder.build().getEncodedQuery();
				if (!TextUtils.isEmpty(query)) {
					 os = conn.getOutputStream();
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, ServicesConstants.UTF_8));
					writer.write(query);
					writer.flush();
					writer.close();
					os.close();
				}

			}

			conn.connect();
			is = conn.getInputStream();

			String contentAsString = readIt(is, len);
			contentAsString.replaceAll("\\\\", "");
			JsonElement loginResponse = new Gson().fromJson(contentAsString, JsonElement.class);
			return loginResponse;
		} finally {
			if (caInput != null) {
				caInput.close();
			}
			if (is != null) {
				is.close();
			}
			if(os != null) {
				os.close();
			}
		}
	}

	protected abstract void addBodyParameters(Uri.Builder builder);

	protected abstract String getUrl();

	protected abstract void addHeaderProperties(HttpURLConnection conn);

	protected abstract void setHttpMethod(HttpURLConnection conn) throws ProtocolException;

	private String readIt(InputStream inputStream, int len) throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(inputStream, writer, ServicesConstants.UTF_8);
		String response = writer.toString();
		return response;
	}

}
