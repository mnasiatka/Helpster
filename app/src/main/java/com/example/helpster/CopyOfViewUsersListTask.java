package com.example.helpster;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CopyOfViewUsersListTask extends AsyncTask<String, Boolean, User> {

	private Bitmap bmp;
	private String username, name;
	private User object;
	private Context mContext;
	private ListView lvUsers;

	public CopyOfViewUsersListTask(Context mContext, ListView lvUsers) {
		this.mContext = mContext;
		this.lvUsers = lvUsers;
	}

	@Override
	protected User doInBackground(String... usernames) {
		this.username = usernames[0];
		String photo_url = String.format("http://www.ythogh.com/helpster/photos/%s.jpg", username);
		try {
			InputStream in = new java.net.URL(photo_url).openStream();
			this.bmp = BitmapFactory.decodeStream(in);
			Log.e("Image Size", username + ": " + (bmp.getRowBytes() * bmp.getHeight()));

		} catch (Exception e) {
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		}
		HttpURLConnection conn = null;
		URL url = null;
		String query = String.format("username=%s", this.username);
		try {
			url = new URL("http://ythogh.com/helpster/scripts/get_user_info.php");
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
			this.name = in.readLine();
			return new User(bmp, name, "temp username");
		} catch (Exception e) {
			Log.d("ERROR", "couldn't get user info");
		}
		Log.e("Task", "Probably couldn't get picture");
		return null;
	}

	@Override
	protected void onPostExecute(User result) {
		super.onPostExecute(result);
		if (result == null) {
			Log.d("Null", "Result is null");
		}
		ViewUsersList.allobjlist.add(result);
		ArrayAdapter<User> adapter = new ViewUsersListAdapter(mContext, ViewUsersList.allobjlist);
		lvUsers.setAdapter(adapter);
		for (User o : ViewUsersList.allobjlist) {
			Log.e("List", o.toString());
		}
		ViewUsersList.objlist = ViewUsersList.allobjlist;
		Log.d("List", "------------------");
	}

}
