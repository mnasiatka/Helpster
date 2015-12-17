package com.example.helpster;

import android.graphics.Bitmap;

public class User {

	private String username, name, email, phone, password, id;
	private Bitmap photo, initialPhoto;

	public User(String username, String password, String name, String email, String phone) {
		this.username = username;
		this.password = password;
		this.name = name;
		this.email = email;
		this.phone = phone;
	}

	public User(Bitmap photo, String name, String username) {
		this.username = username;
		this.name = name;
		this.photo = photo;

	}

	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public User(String username) {
		this.username = username;
	}

	public User() {
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setPicture(Bitmap img) {
		this.photo = img;
	}

	public Bitmap getPicture() {
		return this.photo;
	}

	public void setInitialPicture(Bitmap img) {
		this.initialPhoto = img;
	}

	public Bitmap getInitialPicture() {
		return this.initialPhoto;
	}

	public String[] getAttributes() {
		String str[] = new String[] { this.name, this.username };
		return str;
	}
}
