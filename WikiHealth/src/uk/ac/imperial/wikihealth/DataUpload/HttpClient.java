package uk.ac.imperial.wikihealth.DataUpload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Log;

/**
 * Http client for interaction with the remote database
 * @author Panayiotis Kritiotis (pkritiotis@gmail.com)
 *
 */
public class HttpClient {
	private static final String TAG = "HttpClient";
	
	/**
	 * Sends the json object to the requested URL
	 * @param URL
	 * @param jsonObjSend
	 * @return
	 */
	public static JSONObject SendHttpJSONPost(String URL,JSONObject jsonObjSend) {

		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPostRequest = new HttpPost(URL);

			StringEntity se;
			se = new StringEntity(jsonObjSend.toString());

			// Set HTTP parameters
			httpPostRequest.setEntity(se);
			httpPostRequest.setHeader("Accept", "application/json");
			httpPostRequest.setHeader("Content-type", "application/json");

			HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);

			// Get hold of the response entity (-> the data):
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				// Read the content stream
				InputStream instream = entity.getContent();
				// convert content stream to a String
				String resultString= convertStreamToString(instream);
				instream.close();
				// Transform the String into a JSONObject
				JSONObject jsonObjRecv = new JSONObject(resultString);
				// Raw DEBUG output of our received JSON object:
				Log.i(TAG,"<JSONObject>\n"+jsonObjRecv.toString()+"\n</JSONObject>");
				return jsonObjRecv;
			} 
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * converts stream to string
	 * @param is
	 * @return
	 */
	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine()
		 * method. We iterate until the BufferedReader return null which means
		 * there's no more data to read. Each line will appended to a StringBuilder
		 * and returned as String.
		 * 
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * sends a get request
	 * @param URL
	 * @return
	 */
	public static JSONObject SendHttpGet(String URL) {

		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpGet httpGetRequest = new HttpGet(URL);
			long t = System.currentTimeMillis();
			HttpResponse response = (HttpResponse) httpclient.execute(httpGetRequest);
			Log.i(TAG, "HTTPResponse received in [" + (System.currentTimeMillis()-t) + "ms]");
			// Get hold of the response entity (-> the data):
			HttpEntity entity = response.getEntity();
			System.out.println(response.toString());
			if (entity != null) {
				// Read the content stream
				String resultString = EntityUtils.toString(entity);
				
				//resultString.replaceAll("\"", "\\\"");
				JSONObject jsonObjRecv = new JSONObject(resultString);
				// Raw DEBUG output of our received JSON object:
				Log.i(TAG,"<JSONObject>\n"+jsonObjRecv.toString()+"\n</JSONObject>");

				return jsonObjRecv;
			}
		}
		catch (Exception e)
		{
			// More about HTTP exception handling in another tutorial.
			// For now we just print the stack trace.
			e.printStackTrace();
			return null;
		}
		return null;
	}

}