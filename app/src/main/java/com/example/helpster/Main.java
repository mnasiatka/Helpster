package com.example.helpster;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("NewApi")
public class Main extends ActionBarActivity {

	static Item items[];
	public static Item selectedItem;
	static int NUM_PAGES;
	private static String sortby;

	static Item allItems[];
	Bundle bundle, extras;
	Connection conn;
	EditText etSearch;
	HashMap<String, String> hm = new HashMap<String, String>();
	static ListView lvDashboard;
	static int index;
	String classes[] = new String[] { "View users", "Edit profile", "Preferences" };
	String goclasses[] = new String[] { "EditUser", "ViewUser", "Preferences" };
	static Context mContext;
	Toolbar toolbar;
	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<Item>> listDataChild;
	HashMap<Integer, List<Item>> listDataChildInts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initView();
		initDrawer();
		populateList();
	}

	private void setupList() {
		expListView = (ExpandableListView) findViewById(R.id.elv);
		prepareListData();
		listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
		expListView.setAdapter(listAdapter);

		expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
										int groupPosition, int childPosition, long id) {
				Toast.makeText(
						getApplicationContext(),
						listDataHeader.get(groupPosition)
								+ " : "
								+ listDataChild.get(
								listDataHeader.get(groupPosition)).get(
								childPosition), Toast.LENGTH_SHORT)
						.show();
				return false;
			}
		});
	}

	private void prepareListData() {
		listDataHeader = new ArrayList<>();
		listDataChild = new HashMap<>();
		listDataChildInts = new HashMap<>();

		// Adding child data
		listDataHeader.add("Service");
		listDataHeader.add("Delivery");
		listDataHeader.add("Buy/Sell");

		// Adding child data
		List<Item> service = new ArrayList<>();
		List<Item> delivery = new ArrayList<>();
		List<Item> buysell = new ArrayList<>();

		for (Item item : Main.items) {
			if (item.type.equals("service")) {
				service.add(item);
			} else if (item.type.equals("delivery")) {
				delivery.add(item);
			} else if (item.type.equals("buysell")) {
				buysell.add(item);
			}
		}
		listDataChildInts.put(0, service);
		listDataChildInts.put(1, delivery);
		listDataChildInts.put(2, buysell);

		listDataChild.put(listDataHeader.get(0), service); // Header, Child data
		listDataChild.put(listDataHeader.get(1), delivery);
		listDataChild.put(listDataHeader.get(2), buysell);
	}

	private void initView() {
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		mContext = getApplicationContext();
		String title = String.format("<font color='#ffffff'>%s</font>",getSupportActionBar().getTitle());
		getSupportActionBar().setTitle(Html.fromHtml(title));

		lvDashboard = (ListView) findViewById(R.id.MAIN_LISTVIEW_DASHBOARD);
	}

	private void initDrawer() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, classes);

		final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		// dynamically adjust the layout width so that the drawer extends more
		// or less (code below uses 95%)
		final ListView navList = (ListView) findViewById(R.id.drawer);
		navList.setAdapter(adapter);
		navList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int pos, long id) {
				drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
					@Override
					public void onDrawerClosed(View drawerView) {
						super.onDrawerClosed(drawerView);

					}
				});
				drawer.closeDrawer(navList);
				Intent intent;
				switch (pos) {
					case 0:
						intent = new Intent(getApplicationContext(), ViewUsersList.class);
						startActivity(intent);
						break;
					case 1:
						intent = new Intent(getApplicationContext(), EditUser.class);
						startActivity(intent);
						break;
					case 2:
						break;
					default:
						break;
				}
			}
		});

	}

	private void testDrawer() {
		Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
		Log.e("toolbar is null", "" + (mToolbar == null));
		setSupportActionBar(mToolbar);
		DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
				this,  mDrawerLayout, mToolbar,
				R.string.open_drawer, R.string.close_drawer
		);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		mDrawerToggle.syncState();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.main_action_add) {
			Intent intent = new Intent(getApplicationContext(), AddRequest.class);
			startActivityForResult(intent, 1);

		} else if (itemId == R.id.action_map) {
			if ((items != null) && (items.length > 0)) {
				Intent i = new Intent(getApplicationContext(), MapActivity.class);
				startActivity(i);
			} else {
				Toast.makeText(mContext, "No items to display at this point", Toast.LENGTH_SHORT);
			}
		} else {
			Log.e("Weird", "hit non-registered action button");
		}
		return true;
	}

	private void populateList() {
		hm.put("sortby", sortby != null ? sortby : "place");
		try {
			new GetAllRequestsTask(getApplicationContext(), lvDashboard).execute();
			// populateUIList(allItems);

		} catch (Exception e) {
			// populateUIList(items);
			Toast.makeText(getApplicationContext(), "Populate failture", Toast.LENGTH_LONG).show();
			e.printStackTrace();
			// System.out.println("items.size:" + items.length);
		}
	}

	private void populateUIList(ArrayList<Item> its) {
		items = new Item[its.size()];
		items = its.toArray(items);
		populateUIList(items);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == 0) {
				sortby = data.getExtras().getString("sortby");
			} else if (requestCode == 1) {
				Toast.makeText(getApplicationContext(), "Request added!", Toast.LENGTH_SHORT);
			} else if (requestCode == 2) {
				Toast.makeText(getApplicationContext(), "Request accepted!", Toast.LENGTH_SHORT)
				.show();
				populateList();

			}
		} else if (resultCode == RESULT_CANCELED) {

		} else if (resultCode == 1) {
			if (requestCode == 2) {
				Toast.makeText(getApplicationContext(),
						"Couldn't accept request!\nCheck network connection?", Toast.LENGTH_LONG)
						.show();
			}
		} else {
			Toast.makeText(getApplicationContext(), "Request accepted!", Toast.LENGTH_SHORT)
					.show();
		}

		//populateList();
		items = SearchItems.search(allItems, "");
		//populateUIList(items);
	}

	private void populateUIList(Item[] its) {
		items = its;
		ArrayAdapter<String> adapter = new CustomListAdapter(this.getApplicationContext(), items);
		lvDashboard.setAdapter(adapter);
		lvDashboard.setVisibility(View.VISIBLE);
		lvDashboard.requestLayout();
		lvDashboard.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				System.out.println("hit it: " + position + ", " + id);
				try {
					Intent intent = new Intent(getApplicationContext(), ViewRequest.class);
					selectedItem = items[position];
					index = position;
					startActivityForResult(intent, 2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void onGetRequestsReturn() {
		/*CustomListAdapter adapter = new CustomListAdapter(mContext, Main.items);
		lvDashboard.setAdapter(adapter);
		lvDashboard.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				System.out.println("hit it: " + position + ", " + id);
				try {
					selectedItem = items[position];
					index = position;
					Intent intent = new Intent(mContext, ViewRequest.class);
					startActivityForResult(intent, 2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		*/
		lvDashboard.setVisibility(View.GONE);
		setupList();
		expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				Toast.makeText(getApplicationContext(), listDataChildInts.get(groupPosition).get
						(childPosition).request, Toast.LENGTH_SHORT).show();
				return false;
			}
		});
	}

	public static Item[] getItems() {
		return items;
	}

	public static Item getSelectedItem() {
		return selectedItem;
	}

	private class GetAllRequestsTask extends AsyncTask<Void, Boolean, ArrayList<Item>> {

		private String username, name;
		private Context mContext;
		private Item item;
		private ArrayList<Item> arrlist;
		private ListView lvRequests;

		public GetAllRequestsTask(Context mContext, ListView lvRequests) {
			this.mContext = mContext;
			this.lvRequests = lvRequests;
		}

		@Override
		protected ArrayList<Item> doInBackground(Void... usernames) {
			HttpURLConnection conn = null;
			URL url = null;
			String query = "json=NULL";
			String result = "";

			try {
				url = new URL("http://ythogh.com/helpster/scripts/get_all_requests.php");
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
				arrlist = new ArrayList<Item>(arr.length());
				for (int i = 0; i < arr.length(); i++) {
					item = new Item();
					item.name = arr.getJSONObject(i).getString("name");
					item.request = arr.getJSONObject(i).getString("request");
					item.compensation = arr.getJSONObject(i).getString("compensation");
					item.delivery = arr.getJSONObject(i).getString("delivery");
					item.username = arr.getJSONObject(i).getString("username");
					item.amount = arr.getJSONObject(i).getString("amount");
					item.place = arr.getJSONObject(i).getString("place");
					item.endTime = arr.getJSONObject(i).getString("endtime");
					item.isAvailable = arr.getJSONObject(i).getString("isAvailable").equals("1");
					item.id = arr.getJSONObject(i).getString("id");
					item.type = arr.getJSONObject(i).getString("type");
					arrlist.add(item);
				}
				Log.d("Length", arr.length() + "");
				return arrlist;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return arrlist;
		}

		@Override
		protected void onPostExecute(ArrayList<Item> result) {
			super.onPostExecute(result);
			if (result == null) {
				Log.d("Null", "Result is null");
				return;
			}
			Item[] ti = new Item[result.size()];
			for (int i = 0; i < ti.length; i++) {
				ti[i] = result.get(i);
			}
			items = ti;
			allItems = items;
			onGetRequestsReturn();

		}
	}

}
