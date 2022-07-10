package com.example.serviceprovider.Adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.serviceprovider.CustomerLoginRegisterActivity;
import com.example.serviceprovider.CustomersMainActivity;
import com.example.serviceprovider.Models.Requests;
import com.example.serviceprovider.R;
import com.example.serviceprovider.ServiceProviderMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.viewholder> {
    private List<Requests> Items;
    private Context context;
    private ProgressDialog progressDialog;

    public RequestsAdapter(List<Requests> items, Context context) {
        Items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_requests, parent, false);
        progressDialog = new ProgressDialog(context);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, @SuppressLint("RecyclerView") int position)
    {
//        holder.email.setText(Items.get(position).getEmail());
        holder.phone.setText(Items.get(position).getCustPhone());
        holder.name.setText(Items.get(position).getCustName());
        holder.amount.setText(Items.get(position).getAmount());
        holder.request.setText(Items.get(position).getService());
        holder.status.setText(Items.get(position).getStatus());
        holder.address.setText(Items.get(position).getCustAddres());
        holder.referencenumber.setText(Items.get(position).getReferenceNumber());
        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Requests")
                        .child(Items.get(position).getId());
                reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      if(task.isSuccessful())
                      {
                          DatabaseReference customerID = FirebaseDatabase.getInstance().getReference("Users")
                                  .child("ServiceProvider").child(Items.get(position).getUid());

                          customerID.child("CusterServiceID").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                  context.startActivity(new Intent(context, ServiceProviderMainActivity.class));
                              }
                          });
                      }
                    }
                });
            }
        });
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference =  FirebaseDatabase.getInstance().getReference("Users")
                                                .child("ServiceProviders").child(Items.get(position).getUid());

                HashMap driverMap = new HashMap();
                driverMap.put("CusterServiceID", Items.get(position).getId());

                reference.updateChildren(driverMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                       if(task.isSuccessful())
                       {
                           DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Requests").child(Items.get(position).getId());
                           HashMap map = new HashMap();
                           map.put("status", "accepted");
                           reference.updateChildren(map);

                           Toast.makeText(context, "Request accepted", Toast.LENGTH_SHORT).show();

                       }
                       else
                       {
                           Toast.makeText(context, "failed "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
            accept = itemView.findViewById(R.id.accept);
            reject = itemView.findViewById(R.id.reject);
        }
    }


}
