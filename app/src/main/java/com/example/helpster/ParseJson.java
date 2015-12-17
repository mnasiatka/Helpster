package com.example.helpster;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;
import android.os.StrictMode;
import android.util.Log;

public class ParseJson {
	private static String address = "http://ythogh.com/helpster/scripts/json_request.php";

	private MapItem mi;

	public MapItem doQuery(double lat, double lng, String place) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		int radius = 5000;
		String location = String.format("%f,%f", lat, lng);
		String query = String.format("radius=%d&location=%s&place=%s", radius, location, place);
		// System.out.println(query);
		HttpURLConnection conn = null;
		URL url = null;
		String str = "";
		try {
			url = new URL(address);
			String agent = "Applet";
			String type = "application/x-www-form-urlencoded";
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("User-Agent", agent);
			conn.setRequestProperty("Content-Type", type);
			conn.setRequestProperty("Content-Length", "" + query.length());
			OutputStream out = conn.getOutputStream();
			out.write(query.getBytes());
			Log.d("Progress", "Connected to host server");
			String inputLine = "";
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			int j = 0;
			while ((inputLine = in.readLine()) != null) {
				Log.d("Output", inputLine);
				str = str + inputLine;
				j++;
			}
			Log.e("Finished one", "finally: " + j);

			JSONObject obj = new JSONObject(str);
			JSONArray arr = obj.getJSONArray("results");
			int i = 0;
			String nme = arr.getJSONObject(i).getString("name");
			String adrs = arr.getJSONObject(i).getString("vicinity");
			JSONObject loc = arr.getJSONObject(i).getJSONObject("geometry")
					.getJSONObject("location");
			String lat2 = loc.getString("lat");
			String lng2 = loc.getString("lng");
			mi = new MapItem(lat2, lng2, nme, adrs);
			return mi;
		} catch (Exception e) {
			// e.printStackTrace();
			Log.e("Error", "Place " + place + " could not be found.");
		}
		return null;

	}
}
