package com.example.helpster;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class ViewRequest extends AppCompatActivity implements OnClickListener {

	String request, name, amount, place, endtime, contact, delivery, username;
	Bundle extras;
	Button baccept, breturn;
	static int NUM_PAGES;
	TextView tvname;
	TextView tvrequest;
	TextView tvwhere;
	TextView tvprice;
	TextView tvduration;
	TextView tvcontact;
	TextView tvdelivery;
	Item item;
	HashMap<String, String> hm = new HashMap<String, String>();
	ViewPager vfPager;
	static String id;

	@Override
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.viewrequest);

		String title = String.format("<font color='#ffffff'>%s</font>",getSupportActionBar()
				.getTitle());
		getSupportActionBar().setTitle(Html.fromHtml(title));

		item = Main.getSelectedItem();
		extras = getIntent().getExtras();
		id = item.id;

		tvname = (TextView) findViewById(R.id.viewRequestName);
		tvrequest = (TextView) findViewById(R.id.viewRequestRequest);
		tvwhere = (TextView) findViewById(R.id.viewRequestWhere);
		tvprice = (TextView) findViewById(R.id.viewRequestPrice);
		tvduration = (TextView) findViewById(R.id.viewRequestDuration);
		tvcontact = (TextView) findViewById(R.id.viewRequestContact);
		tvdelivery = (TextView) findViewById(R.id.viewRequestDelivery);
		tvrequest.setText(item.request);
		tvname.setText(item.name);
		tvwhere.setText(item.place);
		tvprice.setText(item.amount);
		tvduration.setText(item.endTime);
		tvcontact.setText(item.contact);
		tvdelivery.setText(item.delivery);


		baccept = (Button) findViewById(R.id.baccept);
		breturn = (Button) findViewById(R.id.breturn);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.view_request, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.e("HIT", "hit it");
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		final int ItemId = v.getId();
		if (ItemId == R.id.baccept) {
			new AcceptTask(item.username, id).execute();
		} else if (ItemId == R.id.breturn) {
			finish();
		}

	}



	private class AcceptTask extends AsyncTask<String, Void, Boolean> {

		String username, id, query;

		public AcceptTask(String username, String id) {
			this.username = username;
			this.id = id;
			Log.e("Location", "Constructor");
		}

		@Override
		protected Boolean doInBackground(String... params) {

			HttpURLConnection conn = null;
			URL url = null;
			query = String.format("username=%s&id=%s", username, id);
			Log.e("Location", "Background");
			try {
				url = new URL("http://ythogh.com/helpster/scripts/accept_request.php");
				String agent = "Applet";
				String type = "application/x-www-form-urlencoded";
				System.out.println(url + "&" + query);
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
				inputLine = in.readLine();
				return inputLine.equals("1");
			} catch (FileNotFoundException e) {
				System.out.println(url.toString() + " not found on server?");
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println("Cannot connect to server - check network connection");
				e.printStackTrace();
			} finally {
				conn.disconnect();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean s){
			if (s) {
				setResult(RESULT_OK);
				finish();
			} else {

				Toast.makeText(getApplicationContext(),"Couldn't accept this request for some " +
						"reason",Toast.LENGTH_SHORT).show();
			}

		}
	}

}
