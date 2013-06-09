package co.tashawych.hassle.db;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import co.tashawych.hassle.datatypes.Contact;

public class DatabaseHelper extends SQLiteOpenHelper {
	
    private static final String DATABASE_NAME = "hassle";
    private static final int DATABASE_VERSION = 1;
    
    // Make sure there is only one helper and one database
    protected static DatabaseHelper helper = null;
    protected static SQLiteDatabase db = null;
    
    public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create all tables
        HassleContactDB.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
    
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
    
    public static synchronized DatabaseHelper getHelper(Context context) {
    	if (helper == null) {
    		helper = new DatabaseHelper(context);
    		db = helper.getWritableDatabase();
    	}
    	return helper;
    }
    
    
    // CONTACTS METHODS
    public void updateContact(Contact contact) {
    	HassleContactDB.update(db, contact);
    }
    
    public Contact getContact(int id) {
    	return HassleContactDB.getContactById(db, id);
    }
    
    public ArrayList<Contact> getAllContacts() {
    	return HassleContactDB.getAllContacts(db);
    }
    
    public boolean checkIfExists(Contact contact) {
    	return HassleContactDB.checkIfExists(db, contact);
    }

}
