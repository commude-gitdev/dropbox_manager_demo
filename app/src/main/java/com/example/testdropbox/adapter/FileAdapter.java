package com.example.testdropbox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.Metadata;
import com.example.testdropbox.R;
import com.example.testdropbox.callback.ListenerMetadata;
import com.example.testdropbox.callback.ListenerView;

import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    List<Metadata> metadataList;
    ListenerMetadata listenerMetadata;
    ListenerView listenerView;
    Metadata metadata;

    public FileAdapter(ListenerMetadata listenerMetadata, ListenerView listenerView) {
        this.metadata = null;
        this.listenerMetadata = listenerMetadata;
        this.listenerView = listenerView;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
        notifyDataSetChanged();
    }

    public void setMetadataList(List<Metadata> metadataList) {
        this.metadata = null;
        this.metadataList = metadataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(metadataList.get(position).getName());
        if (metadataList.get(position) instanceof FolderMetadata) {
            holder.imageView.setImageResource(R.drawable.ic_baseline_folder_24);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_baseline_insert_drive_file_24);
        }

        if (metadata != null && metadata.equals(metadataList.get(position))) {
            holder.layout.setBackgroundResource(R.color.selected_color);
        } else {
            holder.layout.setBackgroundResource(R.color.white);
        }
    }

    @Override
    public int getItemCount() {
        return metadataList != null ? metadataList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgCustom);
            textView = itemView.findViewById(R.id.tvCustom);
            layout = itemView.findViewById(R.id.layoutCustom);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listenerMetadata.listener(metadataList.get(getAdapterPosition()));
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listenerView.listener(view, metadataList.get(getAdapterPosition()));
                    return true;
                }
            });

        }
    }
}
