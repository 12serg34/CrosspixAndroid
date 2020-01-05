package com.home.crosspixandroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyRecycleViewAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private List<String> dataSet;

    MyRecycleViewAdapter(List<String> dataSet) {
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Button textView = ((Button) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer gameId = (Integer) v.getTag(R.id.game_id);
                System.out.println("clicked on text view with game id = " + gameId);
            }
        });
        return new MyViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TextView textView = holder.getTextView();
        textView.setText(dataSet.get(position));
        textView.setTag(R.id.game_id, position);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
