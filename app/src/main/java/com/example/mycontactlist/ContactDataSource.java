package com.example.mycontactlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

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
            initialValues.put("birthday", String.valueOf(c.getBirthday().getTimeInMillis()));

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
            updateValues.put("birthday",
                    String.valueOf(c.getBirthday().getTimeInMillis()));

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

    public ArrayList<Contact> getContacts(String sortField, String sortOrder) {
        ArrayList<Contact> contacts = new ArrayList<>();
        try {
            String query = "SELECT * FROM contact ORDER BY " + sortField + " " + sortOrder;

            Log.d("Test!", "Fetching contacts from database...");
            Cursor cursor = database.rawQuery(query, null);
            Log.d("Test!", "Rows fetched: " + cursor.getCount());

            Contact newContact;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                newContact = new Contact();
                newContact.setId(cursor.getInt(0));
                newContact.setContactName(cursor.getString(1));
                newContact.setStreetAddress(cursor.getString(2));
                newContact.setCity(cursor.getString(3));
                newContact.setState(cursor.getString(4));
                newContact.setZipCode(cursor.getString(5));
                newContact.setHomePhoneNumber(cursor.getString(6));
                newContact.setCellNumber(cursor.getString(7));
                newContact.setEmail(cursor.getString(8));

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.valueOf(cursor.getString(9)));
                newContact.setBirthday(calendar);
                contacts.add(newContact);
                cursor.moveToNext();
            }
            cursor.close();
        }
        catch (Exception e) {
            Log.e("ContactDataSource", "ERROR GETTING CONTACTS");
        }
        return contacts;
    }

    public Contact getSpecificContacts(long contactId) {
        Contact contact = new Contact();
        String query = "SELECT * FROM contact WHERE _id =" + contactId;

            Log.d("Test!", "Fetching contacts from database...");
            Cursor cursor = database.rawQuery(query, null);
            Log.d("Test!", "Rows fetched: " + cursor.getCount());

            if (cursor.moveToFirst()) {
                contact.setId(cursor.getInt(0));
                contact.setContactName(cursor.getString(1));
                contact.setStreetAddress(cursor.getString(2));
                contact.setCity(cursor.getString(3));
                contact.setState(cursor.getString(4));
                contact.setZipCode(cursor.getString(5));
                contact.setHomePhoneNumber(cursor.getString(6));
                contact.setCellNumber(cursor.getString(7));
                contact.setEmail(cursor.getString(8));

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.valueOf(cursor.getString(9)));
                contact.setBirthday(calendar);

                cursor.close();
            }
            return contact;
    }

    public boolean deleteContact(long contactId) {
        boolean didDelete = false;
        try {
            didDelete = database.delete("contact", "_id=" + contactId, null) > 0;
        }
        catch (Exception e) {
            //Do nothing -return value already set to false
        }
        return didDelete;
    }
}

