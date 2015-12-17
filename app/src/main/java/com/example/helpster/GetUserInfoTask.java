package com.example.helpster;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import android.os.AsyncTask;

class GetUserInfoTask extends AsyncTask<User, Void, User> {
	String username, password;
	private User user;

	public GetUserInfoTask(String username) {
		this.username = username;
	}

	public GetUserInfoTask(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	protected User doInBackground(User... usr) {
		// if our user is viewing his own page
		this.user = usr[0];
		// if the user is viewing someone else's page
		HashMap<String, String> hm = new HashMap<String, String>();
		String query = String.format("username=", username);
		if (password != null) {
			query = query + "&password=" + password;
		}
		HttpURLConnection conn = null;
		URL url = null;
		try {
			url = new URL(String.format("http://ythogh.com/helpster/scripts/get_user_info.php"));
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
			String inputLine = "";
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((inputLine = in.readLine()) != null) {
				user.setEmail(inputLine);
			}
		} catch (Exception e) {

		}
		return user;
	}

	@Override
	protected void onPostExecute(User result) {
		ViewUser.onInfoTaskReturn(result);
	}

}
