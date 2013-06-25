package co.tashawych.hassle.db;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import co.tashawych.hassle.datatypes.Contact;

public class HassleContactDB {
	
	private static final String TABLE_NAME = "contacts";
    
    public static void createTable(SQLiteDatabase db) {
        String CREATE_TABLE = 
        		"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + 
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "picture TEXT, " +
                "phone TEXT, " +
                "email TEXT, " +
                "twitter TEXT)";
        db.execSQL(CREATE_TABLE);
    }
    
    public static void dropTable(SQLiteDatabase db) {
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	}
	
	public static void update(SQLiteDatabase db, Contact contact) {
        db.replace(TABLE_NAME, null, contact.toContentValues());
    }
	
	public static Contact getContactById(SQLiteDatabase db, int id) {
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id='" + id + "';", null);
        Contact contact = null;
        
        if ( cursor.moveToFirst() ) {
                contact = new Contact(cursor);
        }
        cursor.close();
        
        return contact;
	}
	
	public static boolean checkIfExists(SQLiteDatabase db, Contact contact) {
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + 
				" WHERE name='" + contact.name + "'" +
				" OR phone='" + contact.phone + "'" +
				" OR email='" + contact.email + "'" +
				" OR twitter='" + contact.twitter + "';", null);
        
        if ( cursor.getCount() > 0 ) return true;
        else return false;
	}
	
	public static ArrayList<Contact> getAllContacts(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY name ASC;", null);
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        
        for (boolean hasItem = cursor.moveToFirst(); hasItem; hasItem = cursor.moveToNext()) {
                contacts.add(new Contact(cursor));
        }
        cursor.close();
        
        Log.d("contact", contacts.get(0).toContentValues().toString());

        return contacts;
	}
	
	public static Cursor getCursor(SQLiteDatabase db) {
		return db.rawQuery("SELECT id AS _id, name, picture, phone, email, twitter " +
                "FROM contacts ORDER BY name COLLATE NOCASE ASC", null);
	}

}
