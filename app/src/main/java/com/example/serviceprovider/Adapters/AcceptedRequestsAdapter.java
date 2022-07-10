package com.example.serviceprovider.Adapters;

import android.app.ProgressDialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class AcceptedRequestsAdapter extends RecyclerView.Adapter<AcceptedRequestsAdapter.viewholder> {
    private List<Requests> Items;
    private Context context;
    private ProgressDialog progressDialog;

    public AcceptedRequestsAdapter(List<Requests> items, Context context) {
        Items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_accepted_requests, parent, false);
        progressDialog = new ProgressDialog(context);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position)
    {
//        holder.email.setText(Items.get(position).getEmail());
        holder.phone.setText(Items.get(position).getCustName());
        holder.name.setText(Items.get(position).getCustName());
        holder.amount.setText(Items.get(position).getAmount());
        holder.request.setText(Items.get(position).getService());
        holder.status.setText(Items.get(position).getStatus());
        holder.address.setText(Items.get(position).getCustAddres());
        holder.referencenumber.setText(Items.get(position).getReferenceNumber());

    }

    @Override
    public int getItemCount() {
        return Items.size();
    }

    public class viewholder extends RecyclerView.ViewHolder{
       private TextView email,phone,amount,name,request,status,address,referencenumber;
       private TextView reject,accept;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            referencenumber = itemView.findViewById(R.id.referencenumber);
            email = itemView.findViewById(R.id.rAmount);
            phone = itemView.findViewById(R.id.rPhone);
            amount = itemView.findViewById(R.id.rAmount);
            name = itemView.findViewById(R.id.rName);
            request = itemView.findViewById(R.id.rRequest);
            status = itemView.findViewById(R.id.rStatus);
            address = itemView.findViewById(R.id.rAddress);

        }
    }
}
