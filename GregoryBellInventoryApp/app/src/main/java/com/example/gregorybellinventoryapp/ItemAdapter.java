package com.example.gregorybellinventoryapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<Item> items;
    private OnItemClickListener listener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public ItemAdapter(List<Item> items, OnItemClickListener listener,Context context) {
        this.items = items;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Item item = items.get(position);
        holder.textViewName.setText(item.getName());
        holder.textViewQuantity.setText(String.valueOf(item.getQuantity()));

        holder.buttonAdd.setOnClickListener(v -> updateItemQuantity(position, 1));
        holder.buttonDecrease.setOnClickListener(v -> updateItemQuantity(position, -1));

        holder.buttonDelete.setOnClickListener(v -> {

            DatabaseHelper dbHelper = new DatabaseHelper(context);
            dbHelper.deleteItem(item.getId());
            items.remove(position);
            notifyItemRemoved(position);
        });

        holder.itemView.setOnClickListener(v -> listener.onItemClick(position));
    }

    private void updateItemQuantity(int position, int increment) {
        Item item = items.get(position);
        int newQuantity = item.getQuantity() + increment;

        if (newQuantity >= 0) {
            item.setQuantity(newQuantity);


            DatabaseHelper dbHelper = new DatabaseHelper(context);
            dbHelper.updateItem(item);

            notifyItemChanged(position);
        }
        if (newQuantity == 0) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                String message = item.getName() + " has run out of stock.";
                String phoneNumber = "";
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            }
        }
    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewQuantity;
        Button buttonAdd, buttonDecrease, buttonDelete;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.item_name);
            textViewQuantity = itemView.findViewById(R.id.item_quantity);
            buttonAdd = itemView.findViewById(R.id.buttonAdd);
            buttonDecrease = itemView.findViewById(R.id.buttonDecrease);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}