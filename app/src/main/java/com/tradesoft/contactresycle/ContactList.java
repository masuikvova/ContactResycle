package com.tradesoft.contactresycle;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

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
    private static final int REQUEST_CODE_PERMISSIONS = 0;
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
    private ArrayList<Contact> checkedContacts = new ArrayList<>();

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
                        ImageView ivActive = (ImageView) view.findViewById(R.id.ivActive);
                        if (((boolean) ivActive.getTag())) {
                            checkedContacts.remove(searchContacts.get(position));
                            adapter.setSelectedData(checkedContacts);
                            adapter.notifyDataSetChanged();
                        } else {
                            if (!checkedContacts.contains(searchContacts.get(position)))
                                checkedContacts.add(searchContacts.get(position));
                            adapter.setSelectedData(checkedContacts);
                            adapter.notifyDataSetChanged();
                        }
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
        int readContacts = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS);
        if (readContacts != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            loadContacts();
        }
    }

    private void loadContacts() {
        getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                return new CursorLoader(getActivity(), ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, selection, selectionArgs, "display_name ASC");
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
                adapter.setSelectedData(checkedContacts);
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

    @TargetApi(23)
    private void requestPermission() {
        int hasPermission = getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS);

        List<String> permissionList = new ArrayList<>();
        if (hasPermission != PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.READ_CONTACTS);

        if (permissionList.contains(Manifest.permission.READ_CONTACTS))
            requestPermissions(permissionList.toArray(new String[permissionList.size()]), REQUEST_CODE_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int readContacts = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS);
        boolean grantedPermission = readContacts == PackageManager.PERMISSION_GRANTED;
        boolean requestedPermission = false;
        for (int i = 0; i < permissions.length; i++) {
            if (permissions[i].equals(Manifest.permission.READ_CONTACTS) &&
                    grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                grantedPermission = true;
                requestedPermission = true;
            }
        }
        if (requestedPermission && grantedPermission) {
            loadContacts();
        }
    }
}
