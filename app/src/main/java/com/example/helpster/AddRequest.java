package com.example.helpster;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;

public class AddRequest extends AppCompatActivity implements OnClickListener {

	String comptype[] = { "Venmo", "Paypal", "Cash", "Other" };
	Bundle extras;
	static EditText etREQUEST, etDELIVERY, etAMOUNT, etPLACE;
	Spinner comptypeSpinner;
	TimePicker tpEndtime;
	Button btContinue;
	HashMap<String, String> hm = new HashMap<String, String>();

	@Override
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.request);

		String title = String.format("<font color='#ffffff'>%s</font>",getSupportActionBar()
				.getTitle());
		getSupportActionBar().setTitle(Html.fromHtml(title));

		extras = getIntent().getExtras();
		etPLACE = (EditText) findViewById(R.id.REQUEST_EDITTEXT_PLACE);
		etPLACE.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
				etPLACE.clearFocus();
				etDELIVERY.requestFocus();
				etDELIVERY.setCursorVisible(true);
				return true;

			}

		});
		etDELIVERY = (EditText) findViewById(R.id.REQUEST_EDITTEXT_DELIVERY);
		etDELIVERY.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
				etDELIVERY.clearFocus();
				etREQUEST.requestFocus();
				etREQUEST.setCursorVisible(true);
				return true;

			}

		});
		etREQUEST = (EditText) findViewById(R.id.REQUEST_EDITTEXT_REQUEST);
		etREQUEST.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
				etREQUEST.clearFocus();
				etAMOUNT.requestFocus();
				return true;
			}
		});


		etAMOUNT = (EditText) findViewById(R.id.REQUEST_EDITTEXT_AMOUNT);
		etAMOUNT.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
				InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				return true;

			}

		});
		tpEndtime = (TimePicker) findViewById(R.id.REQUESTONE_TIMEPICKER_ENDTIME);
		final Calendar c = Calendar.getInstance();
		int cur_hour = c.get(Calendar.HOUR_OF_DAY);
		int cur_minute = c.get(Calendar.MINUTE);
		tpEndtime.setCurrentHour(cur_hour + 1);
		tpEndtime.setCurrentMinute(cur_minute);
		ArrayAdapter<String> comptypeAdapter = new ArrayAdapter<String>(this,
				R.layout.my_spinner_textview);
		comptypeAdapter.addAll(comptype);
		comptypeSpinner = (Spinner) findViewById(R.id.REQUEST_SPINNER_COMPENSATION);
		comptypeSpinner.setAdapter(comptypeAdapter);
		btContinue = (Button) findViewById(R.id.REQUEST_BUTTON_CONTINUE);
		btContinue.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		if (id == R.id.REQUEST_BUTTON_CONTINUE) {
			String a = etAMOUNT.getText().toString();
			a = a.trim().length() == 0 ? "0.00" : a;
			String d = etDELIVERY.getText().toString();
			d = d.trim().length() == 0 ? etREQUEST.getText().toString() : d;
			TimeFormatter tf = new TimeFormatter(tpEndtime);
			String time = tf.format();
			String username = Login.username;
			String request = etREQUEST.getText().toString();
			String paymentMethod = comptypeSpinner.getSelectedItem().toString();
			String place = etPLACE.getText().toString();
			new AddRequestTask(username, request, a, paymentMethod, place, time, d).execute();
		}
	}

	private class AddRequestTask extends AsyncTask<String, Void, Boolean> {

		String username, request, amount, compensation, place, endtime, delivery, query;

		public AddRequestTask(String username, String request, String amount, String
				compensation, String place, String endtime, String delivery) {
			this.username = username;
			this.request = request;
			this.amount = amount;
			this.compensation = compensation;
			this.place = place;
			this.endtime = endtime;
			this.delivery = delivery;
		}

		@Override
		protected Boolean doInBackground(String... params) {

			HttpURLConnection conn = null;
			URL url = null;
			query = String.format("username=%s&request=%s&amount=%s&compensation=%s&place=%s" +
					"&endtime=%s&delivery=%s",username,request,amount,compensation,place,endtime,
					delivery);
			try {
				url = new URL("http://ythogh.com/helpster/scripts/add_request.php");
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
				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String inputLine = in.readLine();
				System.out.println(inputLine);
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
		protected void onPostExecute(Boolean s) {
			if (s) {
				Toast toast = Toast.makeText(getApplicationContext(), "Request Added!",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				System.out.println("request add successful");
				setResult(RESULT_OK);
				finish();
			} else {
				Toast toast = Toast.makeText(getApplicationContext(), "Request coulnd't be added " +
								"for some reason.",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent returnIntent) {
		setResult(resultCode);
		finish();
	}

	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}

}
