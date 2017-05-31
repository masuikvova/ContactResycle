package com.tradesoft.contactresycle;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ContactList extends Fragment {

    private static final String[] PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.PHOTO_URI
    };
    String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " > ?";
    String[] selectionArgs = new String[]{"0"};

    private Unbinder unbinder;
    @BindView(R.id.rvContacts)
    RecyclerView recyclerView;
    @BindView(R.id.editText)
    EditText etSearch;
    private String searchKey = "";
    private ContactsAdapter adapter;
    private ArrayList<Contact> loadedContacts = new ArrayList<>();
    private ArrayList<Contact> searchContacts = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact_list, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initView() {
        etSearch.requestFocus();
        etSearch.clearFocus();
        etSearch.setFocusableInTouchMode(true);
        etSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etSearch.addTextChangedListener(new MyTextWatcher());
        ((MainActivity) getActivity()).hideKeyBoard();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        Toast.makeText(getActivity(), "pos = " + position, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );
        adapter = new ContactsAdapter(getActivity());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        etSearch.setText("");
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).hideKeyBoard();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                return new CursorLoader(getActivity(), ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, selection, selectionArgs, null);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> objectLoader, Cursor c) {
                if (c.getCount() > 0) {
                    while (c.moveToNext()) {
                        Contact contact = new Contact();
                        contact.setUserName(c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)));
                        contact.setPhoneNumber(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        contact.setProfilePicture(c.getString(c.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)));
                        loadedContacts.add(contact);
                    }
                }
                searchContacts.addAll(loadedContacts);
                adapter.setData(searchContacts);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoaderReset(Loader<Cursor> cursorLoader) {

            }
        });
    }

    private class MyTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            searchKey = s.toString().toUpperCase();
            searchContacts.clear();
            for (Contact item : loadedContacts) {
                try {
                    if (item.getUserName().toUpperCase().contains(searchKey)) {
                        searchContacts.add(item);
                    }
                } catch (IllegalStateException ignore) {

                }
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.setData(searchContacts);
                    adapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void afterTextChanged(Editable s) {
            searchKey = s.toString().toUpperCase();

        }
    }
}
