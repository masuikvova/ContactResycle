package com.tradesoft.contactresycle;


import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.util.ArrayList;
import java.util.List;

public class ContactLoader {

    interface OnContactLoadedCallback {
        void onLoadFinished(List<Contact> contacts);
    }

    private static final String TAG = "ContactLoader";
    private static final String[] PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.RawContacts.ACCOUNT_TYPE,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.PHOTO_URI
    };
    private static final String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " > ?";
    private static final String[] selectionArgs = new String[]{"0"};

    //String selection = ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?";
    //String[] selectionArgs = new String[]{"com.whatsapp"};  // com.whatsapp  org.telegram.messenger.account

    public static void load(final Activity activity, Fragment fragment, final String countryCode, final OnContactLoadedCallback callback) {
        fragment.getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                return new CursorLoader(activity, ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, selection, selectionArgs, "display_name ASC");
                //return new CursorLoader(getActivity(), ContactsContract.RawContacts.CONTENT_URI, null, selection, selectionArgs, "display_name ASC");
            }

            @Override
            public void onLoadFinished(Loader<Cursor> objectLoader, Cursor c) {
                List<Contact> contactsList = new ArrayList<Contact>();
                Log.i(TAG, "onLoadFinished");
                if (c.getCount() > 0) {
                    while (c.moveToNext()) {
                        Contact contact = new Contact();
                        contact.setUserName(c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)));
                        contact.setProfilePicture(c.getString(c.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)));
                        //contact.addPhoneNumber(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                        Cursor pCur = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{ id }, null);
                        while (pCur.moveToNext())
                        {
                            String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            contact.addPhoneNumber(contactNumber);
                            //break;
                        }
                        pCur.close();
                        if (!contactsList.contains(contact))
                            contactsList.add(contact);

                    }
                }
                if (callback != null)
                    callback.onLoadFinished(contactsList);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> cursorLoader) {

            }
        });
    }
}
