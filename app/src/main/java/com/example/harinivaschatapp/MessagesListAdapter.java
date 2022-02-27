package com.example.harinivaschatapp;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harinivaschatapp.databinding.MessageBinding;

public class MessagesListAdapter extends RecyclerView.Adapter<MessagesListAdapter.ViewHolder> {
    public MessagesListAdapter(Context context, String email, List<Message> messages) {
        this.context = context;
        this.messages = messages;
        this.email = email;
    }

    @NonNull
    @Override
    public MessagesListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final MessageBinding binding = MessageBinding.inflate(LayoutInflater.from(context), parent, false);

        Log.d("CustomLog", "Creating a new message");

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesListAdapter.ViewHolder holder, int position) {
        final Message message = messages.get(position);

        holder.binding.contentTextView.setText(message.content);
        holder.binding.fromTextView.setText(message.from);
        holder.binding.timeTextView.setText(message.timestamp.toDate().toString());

        holder.binding.getRoot().setGravity((message.from.equals(email)) ? Gravity.END : Gravity.START);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    public List<Message> messages;
    private final Context context;
    private final String email;



    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(MessageBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }


        public final MessageBinding binding;
    }
}
