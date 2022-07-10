package com.example.serviceprovider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.serviceprovider.Adapters.RequestsAdapter;
import com.example.serviceprovider.Models.Requests;
import com.example.serviceprovider.Models.Services;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerRequests extends AppCompatActivity {

    private RecyclerView recyclerView;
    RequestsAdapter adapter;
    ArrayList<Requests> mList;
    FirebaseAuth auth;
    String user;
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_requests);
        recyclerView = findViewById(R.id.customer_requests);
        backBtn = findViewById(R.id.backBtn);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser().getUid().toString();
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mList = new ArrayList<>();
        adapter = new RequestsAdapter(mList, getApplicationContext());
        recyclerView.setAdapter(adapter);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),CustomersMainActivity.class));
            }
        });
        Query reference = FirebaseDatabase.getInstance().getReference("Requests")
                .orderByChild("status")
                .equalTo("requested");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Requests services = dataSnapshot.getValue(Requests.class);
                    mList.add(services);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Navigation");
        alert.setMessage("Do you want to go back to the login page");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(getApplicationContext(),CustomerLoginRegisterActivity.class));
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(getApplicationContext(),CustomersMainActivity.class));
            }
        });
        alert.create().show();
    }
}