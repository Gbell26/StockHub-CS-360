package com.example.gregorybellinventoryapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InventoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        recyclerView = findViewById(R.id.recyclerView);
        Button buttonAddItem = findViewById(R.id.buttonAddItem);
        dbHelper = new DatabaseHelper(this);


        items = dbHelper.getAllItems();
        adapter = new ItemAdapter(items, position -> {

            dbHelper.deleteItem(items.get(position).getId());
            items.remove(position);
            adapter.notifyItemRemoved(position);
        }, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editName = findViewById(R.id.editTextItemName);
                EditText editQuantity = findViewById(R.id.editTextItemCount);

                String name = editName.getText().toString();
                int quantity = Integer.parseInt(editQuantity.getText().toString());

                dbHelper.addItem(name, quantity);
                items.clear();
                items.addAll(dbHelper.getAllItems());
                adapter.notifyDataSetChanged();
            }
        });
    }
}