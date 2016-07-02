package com.sage.services;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.sage.constants.ImageType;
import com.sage.constants.ServicesConstants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.MessageFormat;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

public class PostRecipeImage {

	private final String url = ServicesConstants.PICTURE_URL_POST;
	private Bitmap image;
	private String token;
	String username;
	private String recipeId;
	private String path;
	private ImageType imageType;

	public PostRecipeImage(Bitmap image, String token,String username, String recipeId, String path, ImageType imageType) {
		this.image = image;
		this.token = token;
		this.username = username;
		this.recipeId = recipeId;
		this.path = path;
		this.imageType = imageType;
	}

	public JsonElement sendImage() {
		FileOutputStream fos = null;
		File f = null;
		ByteArrayOutputStream bos = null;
		try {
			HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

			DefaultHttpClient client = new DefaultHttpClient();

			SchemeRegistry registry = new SchemeRegistry();
			SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
			socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
			registry.register(new Scheme("https", socketFactory, 443));
			SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
			
			HttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());
			// Set verifier     
			HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

			HttpPost httpPost = new HttpPost(MessageFormat.format(url, recipeId, imageType.getValue(), token,username));
			f = new File(path);
			f.createNewFile();

			// Convert bitmap to byte array
			bos = new ByteArrayOutputStream();
			image.compress(CompressFormat.JPEG, 80, bos);
			byte[] bitmapMetadata = bos.toByteArray();

			// write the bytes in file
			fos = new FileOutputStream(f);
			fos.write(bitmapMetadata);
			fos.flush();
			long byteCount =f.length();
			Log.i("fileSizeSavedAtServer", "file size that was saved in Server " + byteCount);
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addBinaryBody("file", f);
			HttpEntity entity = builder.build();
			httpPost.setEntity(entity);
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity respEntity = response.getEntity();

			if (respEntity != null) {
				String content = EntityUtils.toString(respEntity);
				return new Gson().fromJson(content, JsonElement.class);
			}
		} catch (Exception ex) {
			Log.e("error saving picture", "error saving picture", ex);
		} finally {
			cleanResources(fos, f, bos);
		}
		return null;

	}

	private void cleanResources(FileOutputStream fos, File f, ByteArrayOutputStream bos) {
		try {
            if(fos != null) {
                fos.close();
            }
            if(f != null && f.exists()) {
                f.delete();
            }
			if(bos != null) {
				bos.close();
			}
        } catch (Exception ex) {
            Log.e("finally failed ", "finally failed", ex);
        }
	}
}
