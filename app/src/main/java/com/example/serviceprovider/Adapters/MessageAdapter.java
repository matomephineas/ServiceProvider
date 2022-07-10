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

import com.example.serviceprovider.Models.Messages;
import com.example.serviceprovider.Models.Requests;
import com.example.serviceprovider.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.viewholder> {
    private List<Messages> Items;
    private Context context;

    public MessageAdapter(List<Messages> items, Context context) {
        Items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_chat, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
//        holder.email.setText(Items.get(position).getEmail());
        holder.sender.setText(Items.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return Items.size();
    }

    public class viewholder extends RecyclerView.ViewHolder{
       private TextView sender,reciever;
       private TextView reject,accept;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            sender = itemView.findViewById(R.id.sender);
            reciever = itemView.findViewById(R.id.reciever);

        }
    }
}
