package com.tradesoft.contactresycle;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ContactList extends Fragment {

    private static final String[] PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.PHOTO_URI
    };

    private Unbinder unbinder;
    @BindView(R.id.rvContacts)
    RecyclerView recyclerView;
    private ContactsAdapter adapter;

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
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ContactsAdapter(getActivity());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                return new CursorLoader(getActivity(), ContactsContract.Contacts.CONTENT_URI, PROJECTION, null, null, null);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> objectLoader, Cursor c) {
                adapter.setCursor(c);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoaderReset(Loader<Cursor> cursorLoader) {
                // TODO do I need to do anything here?
            }
        });
    }
}
