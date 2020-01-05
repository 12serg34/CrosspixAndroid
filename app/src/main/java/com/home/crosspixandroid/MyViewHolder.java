package com.home.crosspixandroid;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class MyViewHolder extends RecyclerView.ViewHolder {
    private TextView textView;

    MyViewHolder(@NonNull TextView itemView) {
        super(itemView);
        this.textView = itemView;
    }

    public TextView getTextView() {
        return textView;
    }
}
