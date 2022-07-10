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

import com.example.serviceprovider.Adapters.HistoryAdapter;
import com.example.serviceprovider.Adapters.RequestsAdapter;
import com.example.serviceprovider.Models.Requests;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    HistoryAdapter adapter;
    ArrayList<Requests> mList;
    FirebaseAuth auth;
    String user;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        recyclerView = findViewById(R.id.recyclerview2);
        backBtn = findViewById(R.id.backBtn);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser().getUid().toString();
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mList = new ArrayList<>();
        adapter = new HistoryAdapter(mList, getApplicationContext());
        recyclerView.setAdapter(adapter);
        Query reference = FirebaseDatabase.getInstance().getReference("Requests")
                .orderByChild("id")
                .equalTo(user);
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
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),CustomersMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

}