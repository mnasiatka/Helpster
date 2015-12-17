package com.example.helpster;

import com.google.android.gms.maps.model.LatLng;

public class MapItem {

	private double latitude, longitude;
	private LatLng location;
	private String name, address;

	public MapItem() {

	}

	public MapItem(double lat, double lng, String name, String address) {
		this.latitude = lat;
		this.latitude = lng;
		this.name = name;
		this.address = address;
		this.location = new LatLng(lat, lng);
	}

	public MapItem(String lat, String lng, String name, String address) {
		this.latitude = Double.parseDouble(lat);
		this.longitude = Double.parseDouble(lng);
		this.name = name;
		this.address = address;
		this.location = new LatLng(this.latitude, this.longitude);
	}

	public String getAddress() {
		return address;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getName() {
		return name;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LatLng getLocation() {
		return location;
	}

	public void setLocation(LatLng location) {
		this.location = location;
	}
}
