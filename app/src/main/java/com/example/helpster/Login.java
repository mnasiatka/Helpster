package com.example.helpster;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class Login extends AppCompatActivity implements View.OnClickListener, GoogleApiClient
		.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<People.LoadPeopleResult> {

	private static final String TAG = "Google Plus";
	private static final int RC_PERM_GET_ACCOUNTS = 2;

	public static String username = "", password = "", personName = "", accountName = "";
	private static User loggedUser;
	private EditText etPASSWORD, etUSERNAME;
	private Button btLogin, btRegister;
	private Context mContext;

	private TextView tvINVALIDLOGIN;
	private SignInButton mSignInButton;
	private LoginButton mLoginButton;
	private HashMap<String, String> hm;
	private Intent i;

	private static final int STATE_DEFAULT = 0;
	private static final int STATE_SIGN_IN = 1;
	private static final int STATE_IN_PROGRESS = 2;
	private static final int RC_SIGN_IN = 0;
	private static final String SAVED_PROGRESS = "sign_in_progress";
	private static final String WEB_CLIENT_ID = "WEB_CLIENT_ID";
	private GoogleApiClient mGoogleApiClient;
	private int mSignInProgress;
	private PendingIntent mSignInIntent;
	private int mSignInError;
	private CallbackManager callbackManager;



	private boolean mRequestServerAuthCode = false;
	private boolean mServerHasToken = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getApplicationContext();
		System.out.println("Null context = " + (getApplicationContext() == null));
		System.out.println("Null context = " + (mContext == null));
		FacebookSdk.sdkInitialize(mContext);

		setupFields();
		setupFacebookSignin();
		setupGoogleSignin();
		setupActionBar();
	}

	private void setupFields() {
		setContentView(R.layout.activity_login);
		etPASSWORD = (EditText) findViewById(R.id.LOGIN_EDITTEXT_PASSWORD);
		etPASSWORD.setTransformationMethod(new PasswordTransformationMethod());
		etUSERNAME = (EditText) findViewById(R.id.LOGIN_EDITTEXT_USERNAME);

		etUSERNAME.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				etUSERNAME.clearFocus();
				etPASSWORD.requestFocus();
				etPASSWORD.setCursorVisible(true);
				return true;
			}
		});

		etPASSWORD.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				btLogin.performClick();
				return true;
			}
		});

		btLogin = (Button) findViewById(R.id.LOGIN_BUTTON_LOGIN);
		btRegister = (Button) findViewById(R.id.LOGIN_BUTTON_REGISTER);
		btLogin.setOnClickListener(this);
		btRegister.setOnClickListener(this);

		tvINVALIDLOGIN = (TextView) findViewById(R.id.LOGIN_TEXTVIEW_INVALIDLOGIN);
		tvINVALIDLOGIN.setVisibility(View.GONE);

		mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
		mSignInButton.setSize(SignInButton.SIZE_WIDE);
		mSignInButton.setOnClickListener(this);

		mLoginButton = (LoginButton) findViewById(R.id.login_button);
	}

	private void setupActionBar() {
		String title = String.format("<font color='#ffffff'>%s</font>",getSupportActionBar()
				.getTitle());
		getSupportActionBar().setTitle(Html.fromHtml(title));
	}

	private void setupGoogleSignin() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Plus.API)
				.addScope(new Scope(Scopes.PLUS_LOGIN))
						.addScope(new Scope(Scopes.PLUS_ME))
						.addScope(new Scope(Scopes.PROFILE))
						.addScope(new Scope(Scopes.EMAIL))
						.build();

	}

	private void setupFacebookSignin() {

		mLoginButton.setReadPermissions("user_friends");
		callbackManager = CallbackManager.Factory.create();
		mLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {
				Log.e(TAG, "On Success reached");
				AccessToken at = loginResult.getAccessToken();
				System.out.println("****************************************************");
				Log.e("Application ID", at.getApplicationId());
				Log.e("Token", at.getToken());
				Log.e("User ID", at.getUserId());
				Log.e("Access Token", at.toString());
				Log.e("Expires", at.getExpires().toString());
				for (String s : at.getPermissions()) {
					Log.e("Permissions", s);
				}
				for (String s : at.getDeclinedPermissions()) {
					Log.e("Declined Permissions", s);
				}
				System.out.println("****************************************************");



			}

			@Override
			public void onCancel() {
				Log.e(TAG, "On Cancel reached");
			}

			@Override
			public void onError(FacebookException exception) {
				Log.e(TAG, "On Error reached");
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.LOGIN_BUTTON_LOGIN) {
			new doLoginTask(etUSERNAME.getText().toString(), etPASSWORD.getText().toString()).execute();
		} else if (v.getId() == R.id.LOGIN_BUTTON_REGISTER) {
			username = etUSERNAME.getText().toString();
			startActivity(new Intent(getApplicationContext(), Register.class));
		} else if (v.getId() == R.id.sign_in_button) {
			if (!mGoogleApiClient.isConnecting()) {
				tvINVALIDLOGIN.setText("Sign in clicked");
				mSignInProgress = STATE_SIGN_IN;
				mGoogleApiClient.connect();
				tvINVALIDLOGIN.setText("Screen opened");
			} else {
				tvINVALIDLOGIN.setText("Signing in");
			}
		} else if (v.getId() == R.id.login_button) {

		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (etUSERNAME.getText() != null) {
			outState.putString("username", etUSERNAME.getText().toString());
		}
		if (etPASSWORD.getText() != null) {
			outState.putString("password", etPASSWORD.getText().toString());
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState == null) {
			return;
		}
		if (savedInstanceState.containsKey("username")) {
			etUSERNAME.setText(savedInstanceState.getString("username"));
		}
		if (savedInstanceState.containsKey("password")) {
			etPASSWORD.setText(savedInstanceState.getString("password"));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		etUSERNAME.setText(username);
	}

	@Override
	protected void onPause() {
		username = etUSERNAME.getText().toString();
		Log.d(TAG, "Session ID: " + mGoogleApiClient.getSessionId());
		Log.d(TAG, "Is Connected: " + mGoogleApiClient.isConnected());
		mGoogleApiClient.disconnect();
		Log.d(TAG, "Is Connected: " + mGoogleApiClient.isConnected());
		Log.e(TAG, "Disconnected on pause");
		super.onPause();
	}

	public static User getLoggedUser() {
		return loggedUser;
	}

	public static void createUser(User user) {
		loggedUser = user;
	}

	public static void editUser(User user) {
		loggedUser = user;
	}

	@Override
	public void onConnected(Bundle bundle) {
		tvINVALIDLOGIN.setText("on Connected");
		Log.d(TAG, "onConnected:" + bundle);
		Log.d(TAG, "Session ID: " + mGoogleApiClient.getSessionId());

		if (checkAccountsPermission()) {
			Log.d(TAG, "Account Name: "  + Plus.AccountApi.getAccountName(mGoogleApiClient));
		} else {
			Log.d(TAG, "No account permission");
		}

		Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(this);
		if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
			personName = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getDisplayName();
			accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
			new addUserTask(personName,accountName,personName,accountName).execute();
		} else {
			Toast.makeText(getApplicationContext(), "Couldn't sign in with Google Plus", Toast
					.LENGTH_SHORT).show();
		}

		/*
		Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
		Log.d(TAG, currentPerson.getDisplayName());
		Snackbar.make(getCurrentFocus(), "Snackbar", Snackbar.LENGTH_LONG).show();
		 */

	}


	private boolean checkAccountsPermission() {
		final String perm = Manifest.permission.GET_ACCOUNTS;
		int permissionCheck = ContextCompat.checkSelfPermission(this, perm);
		if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
			return true;
		} else if (ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
			ActivityCompat.requestPermissions(Login.this,
					new String[]{perm},
					RC_PERM_GET_ACCOUNTS);
			return false;
		} else {
			ActivityCompat.requestPermissions(this,
					new String[]{perm},
					RC_PERM_GET_ACCOUNTS);
			return false;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   @NonNull String permissions[],
										   @NonNull int[] grantResults) {
		Log.d(TAG, "onRequestPermissionsResult:" + requestCode);
		if (requestCode == RC_PERM_GET_ACCOUNTS) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Log.d(TAG, "GET_ACCOUNTS Permission Granted");
			} else {
				Log.d(TAG, "GET_ACCOUNTS Permission Denied.");
			}
		}
	}

	@Override
	public void onConnectionSuspended(int i) {
		tvINVALIDLOGIN.setText("on Connection Suspended");
		Log.w(TAG, "onConnectionSuspended:" + i);
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(this, RC_SIGN_IN);
			} catch (IntentSender.SendIntentException e) {
				Log.e(TAG, "Could not resolve ConnectionResult.", e);
				mGoogleApiClient.connect();
			}
		} else {
			showErrorDialog(connectionResult);
		}
	}

	private void showErrorDialog(ConnectionResult connectionResult) {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

		if (resultCode != ConnectionResult.SUCCESS) {
			if (apiAvailability.isUserResolvableError(resultCode)) {
				apiAvailability.getErrorDialog(this, resultCode, RC_SIGN_IN,
						new DialogInterface.OnCancelListener() {
							@Override
							public void onCancel(DialogInterface dialog) {
								Log.d(TAG, "Canceled");
							}
						}).show();
			} else {
				Log.w(TAG, "Google Play Services Error:" + connectionResult);
				String errorString = apiAvailability.getErrorString(resultCode);
				Toast.makeText(this, errorString, Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onResult(People.LoadPeopleResult loadPeopleResult) {
		Log.e(TAG, "" + (loadPeopleResult == null));
	}

	private class doLoginTask extends AsyncTask<String, Void, Boolean> {

		String username,password,query;

		public doLoginTask(String username, String password) {
			this.username = username;
			this.password=  password;
		}

		@Override
		protected Boolean doInBackground(String... params) {

			HttpURLConnection conn = null;
			URL url = null;
			query = String.format("username=%s&password=%s", username, password);
			Log.e("Location", "Background");
			try {
				url = new URL("http://ythogh.com/helpster/scripts/verify_login.php");
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
				inputLine = in.readLine();
				in.close();
				System.out.println(inputLine);
				return true;
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
				Login.username = username;
				Login.password = password;
				Intent i = new Intent(getApplicationContext(), Main.class);
				startActivity(i);
			} else {
				Toast.makeText(getApplicationContext(), "Username/password combination is " +
						"wrong!", Toast.LENGTH_SHORT).show();
			}
			Log.e("Location", "Finished");
		}
	}


	private class addUserTask extends AsyncTask<String, Void, String> {

		String fullname,username,password,email,query;

		public addUserTask(String fullname,String username,String password,String email) {
			this.fullname = fullname;
			this.username = username;
			this.password = password;
			this.email = email;
		}

		@Override
		protected String doInBackground(String... params) {
			HttpURLConnection conn = null;
			URL url = null;
			try {
				query = String.format("username=%s&password=%s&fullname=%s&email=%s", URLEncoder.encode(username, "UTF-8"),
                        URLEncoder.encode(password, "UTF-8"),URLEncoder.encode(fullname, "UTF-8"),
                        email);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
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
				in.close();
				System.out.println(inputLine);
				return inputLine;
			} catch (FileNotFoundException e) {
				System.out.println(url.toString() + " not found on server?");
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println("Cannot connect to server - check network connection");
				e.printStackTrace();
			} finally {
				conn.disconnect();
			}

			return "-1";
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("1")) {
				Login.username = accountName;
				Login.password = personName;
				Intent i = new Intent(getApplicationContext(), Main.class);
				startActivity(i);
			} else if (result.equals("2")) {
				try {
					new doLoginTask(URLEncoder.encode(username, "UTF-8"),
                            URLEncoder.encode(password, "UTF-8")).execute();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(getApplicationContext(),"Something bad happened. Couldn't make a " +
						"new user", Toast
						.LENGTH_SHORT).show();
			}
		}
	}

}
