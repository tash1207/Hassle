package co.tashawych.hassle.datatypes;

import android.content.ContentValues;
import android.database.Cursor;

public class Contact {
	
	public int id = -1;
	public String name;
	public String picture;
	public String phone;
	public String email;
	public String twitter;
	
	public Contact(String name) {
		this.name = name;
	}
	
	public Contact(String name, String picture, String phone, String email, String twitter) {
		this.name = name;
		this.picture = picture;
		this.phone = phone;
		this.email = email;
		this.twitter = twitter;
	}
	
	public Contact(Cursor c) {
		this.id = c.getInt(c.getColumnIndex("id"));
		this.name = c.getString(c.getColumnIndex("name"));
		this.picture = c.getString(c.getColumnIndex("picture"));
		this.phone = c.getString(c.getColumnIndex("phone"));
		this.email = c.getString(c.getColumnIndex("email"));
		this.twitter = c.getString(c.getColumnIndex("twitter"));
	}
	
	public ContentValues toContentValues() {
		ContentValues cv = new ContentValues();
		if (id > -1) cv.put("id", id);
		cv.put("name", name);
		cv.put("picture", picture);
		cv.put("phone", phone);
		cv.put("email", email);
		cv.put("twitter", twitter);
		return cv;
	}

}
