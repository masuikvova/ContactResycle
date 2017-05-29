package com.tradesoft.contactresycle;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;


class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {
    private Cursor cursor;
    private Context context;
    private int nameColIdx;
    private int idColIdx;
    private int picColIndx;

    public ContactsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ContactViewHolder(item, context);
    }

    public void setCursor(Cursor c) {
        this.cursor = c;
        nameColIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
        idColIdx = cursor.getColumnIndex(ContactsContract.Contacts._ID);
        picColIndx = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        cursor.moveToPosition(position);
        String contactName = cursor.getString(nameColIdx);
        long contactId = cursor.getLong(idColIdx);
        /* Uri my_contact_Uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactId));
        InputStream photo_stream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),my_contact_Uri);
        Bitmap bitmap = BitmapFactory.decodeStream(photo_stream);
        holder.ivUserPhoto.setImageBitmap(bitmap);*/
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), photoUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.ivUserPhoto.setImageBitmap(bitmap);
        holder.tvUserName.setText(contactName + "  " + contactId);

    }

    @Override
    public int getItemCount() {
        if (cursor == null)
            return 0;
        return cursor.getCount();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {
        @BindView(R.id.ivActive)
        ImageView ivActive;
        @BindView(R.id.ivUserPhoto)
        ImageView ivUserPhoto;
        @BindView(R.id.tvUserName)
        TextView tvUserName;

        @SuppressWarnings("UnusedParameters")
        public ContactViewHolder(View itemView, final Context context) {
            super(itemView);
            itemView.setOnTouchListener(this);
            ButterKnife.bind(this, itemView);
            ivActive.setTag(false);
        }


        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.setBackgroundColor(Color.parseColor("#5500a250"));  //highlight item color
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    v.setBackgroundColor(Color.WHITE); //default item color
                    break;
            }
            return true;
        }
    }
}
