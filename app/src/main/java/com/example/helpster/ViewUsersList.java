package com.example.helpster;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ViewUsersList extends Activity {

	private EditText etSearch;
	private ListView lvUsers;
	List<String> list;
	public static ArrayList<User> objlist;
	public static ArrayList<User> allobjlist;
	private User objects[];
	private Bitmap bmp;
	private String name, username;
	private User user = Login.getLoggedUser();
	private static Context mContext;
	public static User selectedItem;
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getApplicationContext();
		username = Login.username;
		objects = new User[3];
		allobjlist = new ArrayList<User>();
		objlist = allobjlist;
		setContentView(R.layout.activity_view_users_list);
		lvUsers = (ListView) findViewById(R.id.VIEWUSERS_LISTVIEW_USERS);
		new ViewUsersListTask(getApplicationContext(), lvUsers).execute();
		etSearch = (EditText) findViewById(R.id.VIEWUSERS_EDITTEXT_SEARCH);
		etSearch.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				return true;
			}

		});

		etSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable search) {
				if (search.toString().trim().length() == 0) {
					objlist = allobjlist;
				} else {
					objlist = SearchItems.msearch(allobjlist, search.toString());
				}
				populateList(objlist);

			}

		});

		lvUsers.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(mContext, position + ": " + objlist.get(position).getUsername(),
						Toast.LENGTH_SHORT).show();
				selectedItem = objlist.get(position);
				System.out.println("there");
				intent = new Intent(ViewUsersList.this.getApplicationContext(), ViewUser.class);
				startActivityForResult(intent, 1);
			}

		});
	}

	private void populateList(ArrayList<User> objs) {
		ArrayAdapter<User> adapter = new ViewUsersListAdapter(getApplicationContext(), objs);
		lvUsers.setAdapter(adapter);
		InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
		lvUsers.requestFocus();
		System.out.println(objs.size());

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		populateList(objlist);
	}

}
