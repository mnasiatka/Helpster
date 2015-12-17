package com.example.helpster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.StrictMode;
import android.util.Log;

public class ViewRequestsOnMapTask {

	public ViewRequestsOnMapTask(double lat, double lon) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
		.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		String input = readBugzilla(lat, lon);
		try {
			JSONObject json = new JSONObject(input);
			Log.i("info", json.toString());
			writeJSON();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String readBugzilla(double lat, double lon) {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(
				String.format(
						"https://maps.googleapis.com/maps/api/place/radarsearch/json?key=%s&location=%f,%f&radius=%d",
						"AIzaSyB8LbFm5oKbkvKoWdqD8pud0ZDFc6y3Wgg", lat, lon,
						5000));
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null)
					builder.append(line);
			} else
				Log.e("wtfd", "Failed to download file");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

	public void writeJSON() {
		JSONObject object = new JSONObject();
		try {
			object.put("name", "Jack Hack");
			object.put("score", new Integer(200));
			object.put("current", new Double(152.32));
			object.put("nickname", "Hacker");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println(object);
	}
}
