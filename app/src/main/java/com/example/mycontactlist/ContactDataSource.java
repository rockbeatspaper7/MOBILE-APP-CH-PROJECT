package com.example.mycontactlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class ContactDataSource {
    private SQLiteDatabase database;
    private ContactDBHelper dbHelper;

    public ContactDataSource(Context context) {
        dbHelper = new ContactDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean insertContact(Contact c) {
        boolean didSucceed = false;
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put("contactname", c.getContactName());
            initialValues.put("streetaddress", c.getStreetAddress());
            initialValues.put("city", c.getCity());
            initialValues.put("state", c.getState());
            initialValues.put("zipcode", c.getZipCode());
            initialValues.put("phonenumber", c.getHomePhoneNumber());
            initialValues.put("cellnumber", c.getCellNumber());
            initialValues.put("email", c.getEmail());
            initialValues.put("birthday", c.getBirthday());

            long result = database.insert("contact", null, initialValues);

            if (result > 0) {
                didSucceed = true;
            } else {
                Log.e("ContactDataSource", "Error inserting contact into database");
            }
        } catch (Exception e) {
            Log.e("ContactDataSource", "Error during contact insertion", e);
        }
        return didSucceed;
    }
    public boolean updateContact(Contact c) {
        boolean didSucceed = false;
        try {
            Long rowId = c.getId();
            ContentValues updateValues = new ContentValues();
            updateValues.put("contactname", c.getContactName());
            updateValues.put("streetaddress", c.getStreetAddress());
            updateValues.put("city", c.getCity());
            updateValues.put("state", c.getState());
            updateValues.put("zipcode", c.getZipCode());
            updateValues.put("phonenumber", c.getHomePhoneNumber());
            updateValues.put("cellnumber", c.getCellNumber());
            updateValues.put("email", c.getEmail());
            updateValues.put("birthday", c.getBirthday());

            int rowsUpdated = database.update("contact", updateValues, "_id=" + rowId, null);

            if (rowsUpdated > 0) {
                didSucceed = true;
                Log.d("ContactDataSource", "Contact updated successfully, ID: " + rowId);
            } else {
                Log.e("ContactDataSource", "Error updating contact with ID: " + rowId);
            }
        } catch (Exception e) {
            Log.e("ContactDataSource", "Error during contact update", e);
        }
        return didSucceed;
    }

    public int getLastMarketID() {
        int lastId;
        try {
            String query = "Select MAX (_id) from contact";
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            lastId = cursor.getInt(0);
            cursor.close();
        }
        catch (Exception e) {
            lastId = -1;
        }
        return lastId;
    }

    public ArrayList<String> getContactName() {
        ArrayList<String> contactNames = new ArrayList<>();
        try {
            String query = "Select contactname from contact";
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                contactNames.add(cursor.getString(0));
                cursor.moveToNext();
            }
            cursor.close();
        }
        catch (Exception e) {
            contactNames = new ArrayList<String>();
        }
        return contactNames;
    }

}
