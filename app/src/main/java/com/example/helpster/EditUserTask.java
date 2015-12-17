package com.example.helpster;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.http.HttpStatus;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

public class EditUserTask extends AsyncTask<String, Boolean, Boolean> {

	private Bitmap bmp;
	private String username, password;
	private User user;
	private Context mContext;

	public EditUserTask(Context mContext, User user) {
		this.mContext = mContext;
		this.user = user;
	}

	@Override
	protected Boolean doInBackground(String... pass) {
		HttpURLConnection conn = null;
		URL url = null;
		String query = String.format("username=%sname=%semail=%s", user.getUsername(),
				user.getName(), user.getEmail());
		if (pass != null) {
			query = String.format("%s&password=%s$verify=%s", query, pass[0], user.getPassword());
		}
		try {
			url = new URL("http://ythogh.com/helpster/scripts/edit_user.php");
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
			int response = conn.getResponseCode();
			if (response == HttpStatus.SC_ACCEPTED) {
				return true;
			}

			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

	}

}
