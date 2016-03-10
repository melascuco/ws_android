package com.melascuco.android.contactsinmobile;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class MyActivity extends Activity {

    private static final String TAG = MyActivity.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;     // contacts unique ID

    //http://stackoverflow.com/questions/17247851/trying-to-write-and-update-contacts
    protected String[] names = {"Anda", "Candy", "Dandy"};
    protected String[] numbers = {"12345678900","14035567890","12392344556"};
    protected int[] ids = {1,2,3};
    TextView txt;
    ListView list;
    ContentValues cv;
    Uri u;

    String DisplayName = "XYZ";
    String MobileNumber = "123456";
    String HomeNumber = "1111";
    String WorkNumber = "2222";
    String emailID = "email@nomail.com";
    String company = "bad";
    String jobTitle = "abcd";


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

//        txt = (TextView)findViewById(R.id.txt);
//        list = (ListView) findViewById(R.id.list);
//        Uri uri = getTheUri();
//        grantUriPermission("ContactsContract.CommonDataKinds.Phone.CONTENT_URI", uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        insertContacts(names, numbers, ids);
//        showContacts();
//        updateContact();
//        showContacts();
    }


    public void onButtonInsertClick(View v) {
        insertContact(getApplicationContext());
    }

    public void onButtonUpdateClick(View v) {
        updateContact(
                Integer.toString(getContactIDFromNumber("123456", getApplicationContext())),
                "nuevoName",
                "88888888888888",
                "contact@ya.com");
    }
//http://stackoverflow.com/questions/33796243/android-update-exicting-contact-list
    private boolean updateContact(String contactID, String contactName, String contactNumber, String contactEmailAdd) {

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation
                .newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE
                        + "=?", new String[]{contactID, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE})
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contactName)
                .build());

        ops.add(ContentProviderOperation
                .newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE
                        + "=? AND " + ContactsContract.CommonDataKinds.Organization.TYPE + "=?"
                        , new String[]{contactID, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                        , String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)})
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contactNumber)
                .build());


        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static int getContactIDFromNumber(String contactNumber, Context context)
    {
        contactNumber = Uri.encode(contactNumber);
        int phoneContactID = new Random().nextInt();
        Cursor contactLookupCursor = context.getContentResolver().query(
                Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, contactNumber),
                new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
        while(contactLookupCursor.moveToNext()){
            phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
        }
        contactLookupCursor.close();

        return phoneContactID;
    }

    //http://stackoverflow.com/questions/4744187/how-to-add-new-contacts-in-android
    private void insertContact(Context myContext) {


        ArrayList <ContentProviderOperation> ops = new ArrayList< ContentProviderOperation >();

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //------------------------------------------------------ Names
        if (DisplayName != null) {
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            DisplayName).build());
        }

        //------------------------------------------------------ Mobile Number
        if (MobileNumber != null) {
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }

        //------------------------------------------------------ Home Numbers
        if (HomeNumber != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, HomeNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                    .build());
        }

        //------------------------------------------------------ Work Numbers
        if (WorkNumber != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, WorkNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                    .build());
        }

        //------------------------------------------------------ Email
        if (emailID != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailID)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }

        //------------------------------------------------------ Organization
        if (!company.equals("") && !jobTitle.equals("")) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, jobTitle)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .build());
        }

        // Asking the Contact provider to create a new contact
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(myContext, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public static Uri getTheUri() {
        //return Uri.parse("content://com.android.contacts/data/phones");
        return Uri.parse("content://com.android.contacts/contacts");
        //return Uri.parse("content://contacts/people");
    }
    protected void insertContacts(String[] names, String[] numbers, int[] ids) {
        ContentValues cv=new ContentValues();
        //this.grantUriPermission("com.melascuco.android.contactsinmobile",Uri.parse("content://com.android.contacts/contacts"), MODE_WORLD_WRITEABLE);
        this.grantUriPermission("com.melascuco.android.contactsinmobile",Uri.parse("content://com.android.contacts/contacts"), Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        for (int i = 0; i < names.length; i++) {
            cv.put(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,names[i]);
            cv.put(ContactsContract.CommonDataKinds.Phone.NUMBER,numbers[i]);
            cv.put(ContactsContract.CommonDataKinds.Phone._ID,ids[i]);
            u = getContentResolver().insert(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, cv);
        }
    }

    protected void updateContact() {
        //update phone number
        getContentResolver().update(u, cv, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "=Anda", new String[]{"18585541315"});
    }

    protected void showContacts() {
        String [] projection=new String[] {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone._ID};
        int[] to = new int[] { R.id.txtName, R.id.txtNumber };
        txt.setText(ContactsContract.PhoneLookup.CONTENT_FILTER_URI.toString());
        Uri contacts = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor managedCursor = managedQuery(contacts,projection,null,null,null);
        //Cursor managedCursor =cr.query(contacts, projection, null, null, null);
        ListAdapter sca=new SimpleCursorAdapter(this,R.layout.list_entry,managedCursor,projection,to);
        list.setAdapter(sca);
    }


    public void onClickSelectContact(View btnSelectContact) {

        // using native contacts selection
        // Intent.ACTION_PICK = Pick an item from the data, returning what was selected.
        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            Log.d(TAG, "Response: " + data.toString());
            uriContact = data.getData();

            retrieveContactName();
            retrieveContactNumber();
            retrieveContactPhoto();

        }
    }

    private void retrieveContactPhoto() {

        Bitmap photo = null;

        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactID)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
                ImageView imageView = (ImageView) findViewById(R.id.img_contact);
                imageView.setImageBitmap(photo);
            }

            assert inputStream != null;
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void retrieveContactNumber() {

        String contactNumber = null;

        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        Log.d(TAG, "Contact ID: " + contactID);

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        cursorPhone.close();

        Log.d(TAG, "Contact Phone Number: " + contactNumber);
    }

    private void retrieveContactName() {

        String contactName = null;

        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();

        Log.d(TAG, "Contact Name: " + contactName);

    }
}