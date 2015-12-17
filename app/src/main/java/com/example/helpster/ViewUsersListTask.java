package com.example.helpster;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONArray;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ViewUsersListTask extends AsyncTask<Void, Boolean, ArrayList<User>> {

	private Bitmap bmp;
	private String username, name;
	private User user;
	private Context mContext;
	private ListView lvUsers;
	private ArrayList<User> arrlist;

	public ViewUsersListTask(Context mContext, ListView lvUsers) {
		this.mContext = mContext;
		this.lvUsers = lvUsers;
	}

	@Override
	protected ArrayList<User> doInBackground(Void... usernames) {
		HttpURLConnection conn = null;
		URL url = null;
		String query = "json=NULL";
		String result = "";

		try {
			url = new URL("http://ythogh.com/helpster/scripts/test_get_users.php");
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
				Log.e("Log", inputLine);
				result = result + inputLine;
			}
			in.close();
			conn.disconnect();
			JSONArray arr = new JSONArray(result);
			arrlist = new ArrayList<User>(arr.length());
			for (int i = 0; i < arr.length(); i++) {
				user = new User();
				user.setUsername(arr.getJSONObject(i).getString("username"));
				user.setName(arr.getJSONObject(i).getString("name"));
				String photo_url = String.format("http://www.ythogh.com/helpster/photos/%s.jpg",
						arr.getJSONObject(i).getString("username"));
				try {
					InputStream ins = new java.net.URL(photo_url).openStream();
					user.setPicture(BitmapFactory.decodeStream(ins));
				} catch (Exception e) {
					user.setPicture(BitmapFactory.decodeResource(mContext.getResources(),
							R.drawable.blank_profile));
				}
				arrlist.add(user);
			}
			Log.d("Length", arr.length() + "");
			return arrlist;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arrlist;
	}

	@Override
	protected void onPostExecute(ArrayList<User> result) {
		super.onPostExecute(result);
		if (result == null) {
			Log.d("Null", "Result is null");
			return;
		}
		ViewUsersList.allobjlist = result;
		ArrayAdapter<User> adapter = new ViewUsersListAdapter(mContext, ViewUsersList.allobjlist);
		lvUsers.setAdapter(adapter);
		for (User o : ViewUsersList.allobjlist) {
			Log.e("List", o.toString());
		}
		ViewUsersList.objlist = ViewUsersList.allobjlist;
		Log.d("List", "------------------");
	}

}
