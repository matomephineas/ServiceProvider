package com.example.serviceprovider.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.serviceprovider.Models.Requests;
import com.example.serviceprovider.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.viewholder> {
    private List<Requests> Items;
    private Context context;

    public HistoryAdapter(List<Requests> items, Context context) {
        Items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, @SuppressLint("RecyclerView") int position)
    {
//        holder.email.setText(Items.get(position).getEmail());
        holder.phone.setText(Items.get(position).getPhone());
        holder.name.setText(Items.get(position).getName());
        holder.amount.setText(Items.get(position).getAmount());
        holder.request.setText(Items.get(position).getService());
        holder.status.setText(Items.get(position).getStatus());
        holder.address.setText(Items.get(position).getCustAddres());
        holder.pname.setText(Items.get(position).getCustName());
        holder.pphone.setText(Items.get(position).getCustPhone());
        holder.paddress.setText(Items.get(position).getCustAddres());
        holder.referencenumber.setText(Items.get(position).getReferenceNumber());
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Requests").child(Items.get(position).getId());
                reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                     if(task.isSuccessful())
                     {
                         Toast.makeText(context, "Request cancelled successfully", Toast.LENGTH_SHORT).show();

                     }
                     else
                     {
                         Toast.makeText(context, "Error: "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                     }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return Items.size();
    }

    public class viewholder extends RecyclerView.ViewHolder{
       private TextView pname,pphone,referencenumber,phone,amount,name,request,status,address,paddress;
       private TextView cancel,accept;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            referencenumber = itemView.findViewById(R.id.referencenumber);
            pname = itemView.findViewById(R.id.pName);
            pphone = itemView.findViewById(R.id.pPhone);
            phone = itemView.findViewById(R.id.hPhone);
            amount = itemView.findViewById(R.id.hAmount);
            name = itemView.findViewById(R.id.hName);
            request = itemView.findViewById(R.id.hRequest);
            status = itemView.findViewById(R.id.hStatus);
            address = itemView.findViewById(R.id.hAddress);
            paddress= itemView.findViewById(R.id.pAddress);
           cancel = itemView.findViewById(R.id.cancel);
        }
    }
}
