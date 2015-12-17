package com.example.helpster;

import android.content.IntentSender.SendIntentException;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class MapActivity extends AppCompatActivity implements ConnectionCallbacks,
OnConnectionFailedListener {

	private static final int REQUEST_RESOLVE_ERROR = 1001;

	private HashSet<mapfilter> filter = new HashSet<mapfilter>(4);
	private static HashMap<String, String> mapping;
	private static HashMap<String, MapItem> hs;
	private static HashMap<MapItem, ArrayList<Item>> results;
	private static final String TAG = "Location";
	private Item[] items;
	private static GoogleMap map;
	private static MapItem mapitem;
	private static HashMap<MapItem, Marker> markers;
	private GoogleApiClient mGoogleApiClient;
	private Location mLastLocation = null;
	private boolean mResolvingError = false;
	private LatLng myLocation;
	private ParseJson pj;
	private Marker me;
	private static ArrayList<Item> alt;
	private Typeface tf;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_map_all:
				break;
			case R.id.action_map_delivery:
				break;
			case R.id.action_map_services:
				break;
			case R.id.action_map_carpool:
				break;
			case R.id.action_map_list:
				finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	public enum mapfilter {
		all, delivery, carpool, services
	}

	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		String title = String.format("<font color='#ffffff'>%s</font>",getSupportActionBar()
				.getTitle());
		getSupportActionBar().setTitle(Html.fromHtml(title));

		items = Main.getItems();
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
		Log.i(TAG, "Created Api Client");
		mGoogleApiClient.connect();

	}

	private void addMarkers() {
		if (results == null) {
			Log.e("Error", "Results mapping is still null at time of marking");
			return;
		}
		if (map != null) {
			me = map.addMarker(new MarkerOptions().position(myLocation).title("Me")
					.snippet("You are here.")
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
			map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
		}

		for (MapItem mi : results.keySet()) {
			Marker nm = map.addMarker(new MarkerOptions().position(mi.getLocation())
					.title(mi.getName()).snippet(mi.getAddress() + "\n" + "More info.."));
			markers.put(mi, nm);
		}
	}

	private static void addMarker(MapItem mi) {
		Marker nm = map.addMarker(new MarkerOptions().position(mi.getLocation())
				.title(mi.getName()).snippet(mi.getAddress()));
		nm.showInfoWindow();
		markers.put(mi, nm);
		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker mark) {
				Log.e("Marker ID", mark.getId() + "," + mark.getTitle());
				MapItem temp_mi = hs.get(mark.getTitle());
				ArrayList<Item> arr_items = results.get(temp_mi);
				Log.e("size results", "" + results.size());
				for (MapItem m : results.keySet()) {
					Log.e("mis", m.getName() + ", " + Arrays.toString(m.getName().getBytes()));
					// System.out.println(results.get(m).size());
					// for (Item it : results.get(m)) {
					// System.out.println(it.name);
					// }
					Log.e("mis", "-----------------------------");
				}
				String n = mapping.get(mark.getTitle());
				MapItem mitemp = hs.get(n);
				if (mitemp != null) {
					ArrayList<Item> ittemps = results.get(mitemp);
					if (ittemps != null) {
						for (Item i : ittemps) {
							Log.d("result", i.name);
						}
					} else {
						Log.e("null", "getting list of items from mapitem was null");
					}
				} else {
					Log.e("null", "getting map item returned null");
				}

				// for (Item item : arr_items) {
				// System.out.println(item.name);
				// }
			}

		});
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		results = new HashMap<MapItem, ArrayList<Item>>();
		hs = new HashMap<String, MapItem>();
		markers = new HashMap<MapItem, Marker>();
		mapping = new HashMap<String, String>();
		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

		if (mLastLocation != null) {
			myLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
			if (map != null) {
				me = map.addMarker(new MarkerOptions().position(myLocation).title("Me")
						.snippet("You are here.")
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
				map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
			}
			pj = new ParseJson();
			for (Item item : items) {
				Log.v("Info", String.format("lat: %s, lng: %s, place: %s", myLocation.latitude,
						myLocation.longitude, item.place));
				if (hs.get(item.place) != null) {
					mapitem = hs.get(item.place);
					alt = results.get(mapitem);
					alt.add(item);
					results.put(mapitem, alt);
				} else {
					new MapInfoTask(myLocation.latitude, myLocation.longitude, item.place, item)
					.execute();
					// mapitem = pj.doQuery(myLocation.latitude,
					// myLocation.longitude, item.place);
					// hs.put(item.place, mapitem);
					// if (mapitem != null) {
					// alt = new ArrayList<Item>();
					// alt.add(item);
					// results.put(mapitem, alt);
					// }
				}
			}
			// addMarkers();
		} else {
			Log.e(TAG, "My location is null");
		}

	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (mResolvingError) {
			return;
		} else if (result.hasResolution()) {
			try {
				mResolvingError = true;
				result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
			} catch (SendIntentException e) {
				mGoogleApiClient.connect();
			}
		} else {
			Log.e(TAG, "Can't connect");
			mResolvingError = true;
		}
	}

	@Override
	public void onConnectionSuspended(int cause) {
		Log.e("OnConnectionSuspended", "How'd we get here!?");
	}

	public static void onQueryReturn(MapItem result, Item item) {
		if (result != null) {
			mapitem = result;
			mapping.put(mapitem.getName(), item.place);
			hs.put(item.place, mapitem);
			alt = new ArrayList<Item>();
			alt.add(item);
			results.put(mapitem, alt);
			Log.e("Result",
					mapitem.getName() + ", " + mapitem.getAddress() + ", " + mapitem.getLocation());
			addMarker(mapitem);
		}

	}

}
