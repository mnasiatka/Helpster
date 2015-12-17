package com.example.helpster;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;

public class Register extends AppCompatActivity implements View.OnClickListener {

	private EditText etFULLNAME;
	private EditText etUSERNAME;
	private EditText etEMAIL;
	private EditText etPASSWORD;
	private Button btADDPICTURE, btREGISTER;
	private ImageView ivPROFILEPICTURE;
	private TextView tvPASSWORDHELP, tvUSERNAMEDISPLAY;
	private static final String[] VALID_DOMAINS = { "com", "org", "net", "biz", "gov", "edu",
		"info", "co.uk", "me" };
	public static String fullname = "", username = "", email = "", password;
	private boolean isValid;
	private Connection conn;
	private String sql;
	private static boolean first = true;
	private PreparedStatement pst;
	private HashMap<String, String> hm = new HashMap<String, String>();
	Intent i;
	static int dp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		String title = String.format("<font color='#ffffff'>%s</font>",getSupportActionBar()
				.getTitle());
		getSupportActionBar().setTitle(Html.fromHtml(title));

		initialize();
		if (first) {
			username = Login.username;
			first = false;
		}

		float scale = getResources().getDisplayMetrics().density;
		dp = (int) (10*scale + 0.5f);

		etFULLNAME.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (etFULLNAME.getText().toString().length() > 0) {
					etFULLNAME.setBackground(getResources().getDrawable(R.drawable.edittext_good));
				} else {
					etFULLNAME.setBackground(getResources().getDrawable(R.drawable.edittext_bad));
				}
				etFULLNAME.setPadding(dp, dp, dp, dp);
				etFULLNAME.clearFocus();
				etUSERNAME.requestFocus();
				etUSERNAME.setCursorVisible(true);
				return true;
			}

		});
		etFULLNAME.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus && (etFULLNAME.getText().toString().length() > 0)) {
					etFULLNAME.setBackground(getResources().getDrawable(R.drawable.edittext_good));
				} else {
					etFULLNAME.setBackground(getResources().getDrawable(R.drawable.edittext_bad));
				}
				etFULLNAME.setPadding(dp, dp, dp, dp);
			}
		});
		etUSERNAME.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					username = etUSERNAME.getText().toString();
					new checkUsernameTask(username).execute();
				}
			}
		});
		etUSERNAME.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
				username = etUSERNAME.getText().toString();
				new checkUsernameTask(username).execute();
				etUSERNAME.clearFocus();
				etEMAIL.requestFocus();
				etEMAIL.setCursorVisible(true);
				return true;

			}

		});
		etEMAIL.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
				email = etEMAIL.getText().toString();
				isValid = email.matches("[A-z](\\w|[-]|[.]){0,}@[A-z]\\w{0,}[.](com|org|net|edu)");
				if (isValid) {
					etEMAIL.setBackground(getResources().getDrawable(R.drawable.edittext_good));
				} else {
					etEMAIL.setBackground(getResources().getDrawable(R.drawable.edittext_bad));
				}
				etEMAIL.setPadding(dp, dp, dp, dp);
				etEMAIL.clearFocus();
				etPASSWORD.requestFocus();
				etPASSWORD.setCursorVisible(true);
				return true;
			}

		});
		etEMAIL.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					email = etEMAIL.getText().toString();
					isValid = email
							.matches("[A-z](\\w|[-]|[.]){0,}@[A-z]\\w{0,}[.](com|org|net|edu)");
					if (isValid) {
						etEMAIL.setBackground(getResources().getDrawable(R.drawable.edittext_good));
					} else {
						etEMAIL.setBackground(getResources().getDrawable(R.drawable.edittext_bad));
					}
					etEMAIL.setPadding(dp, dp, dp, dp);
				}
			}

		});
		etPASSWORD.setTransformationMethod(new PasswordTransformationMethod());
		etPASSWORD.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
				btREGISTER.performClick();
				return true;

			}

		});
		etPASSWORD.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				password = etPASSWORD.getText().toString();
				isValid = password.length() > 6;
				if (isValid) {
					etPASSWORD.setBackground(getResources().getDrawable(R.drawable.edittext_good));
					tvPASSWORDHELP.setVisibility(View.GONE);
				} else {
					etPASSWORD.setBackground(getResources().getDrawable(R.drawable.edittext_bad));
					tvPASSWORDHELP.setVisibility(View.VISIBLE);
				}
				etPASSWORD.setPadding(dp, dp, dp, dp);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		btREGISTER.setOnClickListener(this);

		etFULLNAME.setBackground(getResources().getDrawable(R.drawable.edittext));
		etFULLNAME.setPadding(dp, dp, dp, dp);
	}

	private class checkUsernameTask extends AsyncTask<String, Void, Boolean> {

		String username, query;

		public checkUsernameTask(String username) {
			this.username = username;
		}

		@Override
		protected Boolean doInBackground(String... params) {
			HttpURLConnection conn = null;
			URL url = null;
			query = String.format("username=%s", username);
			Log.e("Location", "Background");
			try {
				url = new URL("http://ythogh.com/helpster/scripts/check_username.php");
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
		protected void onPostExecute(Boolean success) {
			if (success && etUSERNAME.getText().toString().trim().length() > 0) {
				etUSERNAME.setBackground(getResources().getDrawable(R.drawable.edittext_good));
				tvUSERNAMEDISPLAY.setVisibility(View.GONE);
			} else {
				etUSERNAME.setBackground(getResources().getDrawable(R.drawable.edittext_bad));
				tvUSERNAMEDISPLAY.setVisibility(View.VISIBLE);
			}
			etUSERNAME.setPadding(dp,dp,dp,dp);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.REGISTER_BUTTON_REGISTER) {
			fullname = etFULLNAME.getText().toString();
			username = etUSERNAME.getText().toString();
			password = etPASSWORD.getText().toString();
			email = etEMAIL.getText().toString();
			new addUserTask(fullname, username, password, email).execute();
		}
	}

	@Override
	protected void onResume() {
		if (first) {
			username = Login.username;
			first = false;
		}
		etFULLNAME.setText(fullname);
		etUSERNAME.setText(username);
		etEMAIL.setText(email);
		super.onResume();
	}

	@Override
	protected void onPause() {
		fullname = etFULLNAME.getText().toString();
		username = etUSERNAME.getText().toString();
		email = etEMAIL.getText().toString();
		super.onPause();
	}

	private void initialize() {
		etFULLNAME = (EditText) findViewById(R.id.REGISTER_EDITTEXT_FULLNAME);
		etUSERNAME = (EditText) findViewById(R.id.REGISTER_EDITTEXT_USERNAME);
		etPASSWORD = (EditText) findViewById(R.id.REGISTER_EDITTEXT_PASSWORD);
		etEMAIL = (EditText) findViewById(R.id.REGISTER_EDITTEXT_EMAIL);
		btADDPICTURE = (Button) findViewById(R.id.REGISTER_BUTTON_ADDPICTURE);
		System.out.println(findViewById(R.id.REGISTER_BUTTON_REGISTER) instanceof View);
		System.out.println(findViewById(R.id.REGISTER_BUTTON_REGISTER) instanceof ImageView);
		System.out.println(findViewById(R.id.REGISTER_BUTTON_REGISTER) instanceof Button);
		btREGISTER = (Button) findViewById(R.id.REGISTER_BUTTON_REGISTER);
		ivPROFILEPICTURE = (ImageView) findViewById(R.id.REGISTER_IMAGEVIEW_PROFILEPICTURE);
		tvUSERNAMEDISPLAY = (TextView) findViewById(R.id.REGISTER_TEXTVIEW_USERNAMEDISPLAY);
		tvUSERNAMEDISPLAY.setVisibility(View.GONE);
		tvPASSWORDHELP = (TextView) findViewById(R.id.REGISTER_TEXTVIEW_PASSWORDHELP);
		tvPASSWORDHELP.setVisibility(View.GONE);

	}


	private class addUserTask extends AsyncTask<String, Void, Boolean> {

		String fullname,username,password,email, query;

		public addUserTask(String fullname,String username,String password,String email) {
			this.fullname = fullname;
			this.username = username;
			this.password = password;
			this.email = email;
		}

		@Override
		protected Boolean doInBackground(String... params) {
			HttpURLConnection conn = null;
			URL url = null;
			query = String.format("username=%s&password=%s&fullname=%s&email=%s", username,
					password,fullname,email);
			Log.e("Location", "Background");
			try {
				url = new URL("http://ythogh.com/helpster/scripts/add_user.php");
				String agent = "Applet";
				String type = "application/x-www-form-urlencoded";
				System.out.println(url + "?" + query);
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
		protected void onPostExecute(Boolean success) {
			if (success) {
				User user = new User(etUSERNAME.getText().toString(), etPASSWORD.getText().toString());
				Login.createUser(user);
				Login.username = etUSERNAME.getText().toString();
				Login.password = etPASSWORD.getText().toString();
				i = new Intent(getApplicationContext(), Main.class);
				i.putExtra("username", etUSERNAME.getText().toString());
				startActivity(i);
			} else {
				Toast.makeText(getApplicationContext(),"Something bad happened. Couldn't make a " +
						"new user", Toast
						.LENGTH_SHORT).show();
			}
		}
	}


}
