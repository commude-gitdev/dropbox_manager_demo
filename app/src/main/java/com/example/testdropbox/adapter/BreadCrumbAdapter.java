package com.example.testdropbox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testdropbox.R;
import com.example.testdropbox.callback.StringCallback;

import java.util.List;

public class BreadCrumbAdapter extends RecyclerView.Adapter<BreadCrumbAdapter.ViewHolder> {

    List<String> stringList;
    StringCallback stringCallback;

    public BreadCrumbAdapter(List<String> stringList, StringCallback stringCallback) {
        this.stringList = stringList;
        this.stringCallback = stringCallback;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_breadcrumb, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setSelected(true);
        if (position == 0) {
            holder.textView.setText("Root");
            holder.textView.setTextColor(ContextCompat.getColor(holder.textView.getContext(), R.color.purple_500));
        } else {
            holder.textView.setText(stringList.get(position));
            holder.textView.setTextColor(ContextCompat.getColor(holder.textView.getContext(), R.color.black));
        }
        if (position == stringList.size() - 1) {
            holder.imageView.setVisibility(View.GONE);
        } else {
            holder.imageView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return stringList != null ? stringList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgArrow);
            textView = itemView.findViewById(R.id.folderName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition() == 0) {
                        stringCallback.listener("");
                    } else {
                        String path = "";
                        for (int i = 1; i <= getAdapterPosition(); i++) {
                            path = path + "/" + stringList.get(i);
                        }
                        stringCallback.listener(path);
                    }
                }
            });
        }
    }
}
