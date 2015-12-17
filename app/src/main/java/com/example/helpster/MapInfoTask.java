package com.example.helpster;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.util.Log;

public class MapInfoTask extends AsyncTask<Void, Void, MapItem> {
	private static String address = "http://ythogh.com/helpster/scripts/json_request.php";

	private MapItem mi;
	private double lat, lng;
	private String place;
	private Item item;

	public MapInfoTask(double lat, double lng, String place, Item item) {
		this.lat = lat;
		this.lng = lng;
		this.place = place;
		this.item = item;
	}

	@Override
	protected MapItem doInBackground(Void... params) {
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
			while ((inputLine = in.readLine()) != null) {
				Log.d("Output", inputLine);
				str = str + inputLine;
			}

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
			// Log.d("DEBUG", str);
		}
		return null;
	}

	@Override
	protected void onPostExecute(MapItem result) {
		super.onPostExecute(result);
		MapActivity.onQueryReturn(result, this.item);
	}

}
