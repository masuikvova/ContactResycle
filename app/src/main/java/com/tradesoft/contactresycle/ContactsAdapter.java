package com.tradesoft.contactresycle;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {
    private Context context;
    private ArrayList<Contact> dataSet = new ArrayList<>();

    public ContactsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ContactViewHolder(item, context);
    }

    public void setData(ArrayList<Contact> data) {
        dataSet = data;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Contact contact = dataSet.get(position);
        Glide.with(context)
                .load(contact.getProfilePicture())
                .placeholder(R.mipmap.ic_launcher)
                .bitmapTransform(new CropCircleTransformation(context))
                .into(holder.ivUserPhoto);
        holder.tvUserName.setText(contact.getUserName());
        holder.tvPhoneNumber.setText(contact.getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        if (dataSet == null)
            return 0;
        return dataSet.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {
        @BindView(R.id.ivActive)
        ImageView ivActive;
        @BindView(R.id.ivUserPhoto)
        ImageView ivUserPhoto;
        @BindView(R.id.tvUserName)
        TextView tvUserName;
        @BindView(R.id.tvPhoneNumber)
        TextView tvPhoneNumber;


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
