package com.example.serviceprovider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.serviceprovider.Adapters.AcceptedRequestsAdapter;
import com.example.serviceprovider.Adapters.RequestsAdapter;
import com.example.serviceprovider.Models.Requests;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProviderHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    AcceptedRequestsAdapter adapter;
    ArrayList<Requests> mList;
    FirebaseAuth auth;
    String user;
    ImageView backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_history);
        recyclerView = findViewById(R.id.recyclerview4);
        backBtn = findViewById(R.id.backBtn);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser().getUid().toString();
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ServiceProviderMainActivity.class));
            }
        });
        mList = new ArrayList<>();
        adapter = new AcceptedRequestsAdapter(mList, getApplicationContext());
        recyclerView.setAdapter(adapter);
        Query reference = FirebaseDatabase.getInstance().getReference("Requests")
                .orderByChild("status")
                .equalTo("accepted");
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
}