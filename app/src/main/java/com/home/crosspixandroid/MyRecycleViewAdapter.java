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
    private View selectedView;

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
                if (selectedView != null) {
                    selectedView.setSelected(false);
                }
                v.setSelected(true);
                selectedView = v;
                System.out.println(v.isSelected());
            }
        });
        return new MyViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TextView textView = holder.getTextView();
        textView.setText(dataSet.get(position));
        textView.setTag(R.id.position_in_game_list, position);
    }

    public int getSelectedPosition() {
        return (Integer) selectedView.getTag(R.id.position_in_game_list);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
