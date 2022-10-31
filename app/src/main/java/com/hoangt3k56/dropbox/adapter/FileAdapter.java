package com.hoangt3k56.dropbox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hoangt3k56.dropbox.listener.ListenerMetadata;
import com.hoangt3k56.dropbox.R;
import com.hoangt3k56.dropbox.model.Entrie;

import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.viewHodel> {

    private List<Entrie> entrieList;
    ListenerMetadata listenerEntrie;

    public FileAdapter(ListenerMetadata listenerEntrie) {
        this.listenerEntrie = listenerEntrie;
    }

    public void setEntrieList(List<Entrie> list) {
        this.entrieList = list;
        notifyDataSetChanged();
     }

    @NonNull
    @Override
    public viewHodel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.layout_item_file, parent, false);
        return new viewHodel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHodel holder, int position) {
        Entrie entrie = entrieList.get(position);

        if (entrie != null) {
            holder.tvName_file.setText(entrie.getName());
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listenerEntrie.listener(entrie, 0);
                }
            });

            holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listenerEntrie.listener(entrie, 1);
                    return false;
                }
            });

            if (entrie.getTag().equals("file")) {
                holder.img_file.setImageResource(R.drawable.icons8_file);
            }
            else if (entrie.getTag().equals("folder")) {
                holder.img_file.setImageResource(R.drawable.icons8_folder_48);
            }
        }
    }

    @Override
    public int getItemCount() {

        if (entrieList != null) {
            return entrieList.size();
        }
        return 0;
    }


    public class viewHodel extends RecyclerView.ViewHolder {

        private TextView tvName_file;
        private ImageView img_file;
        private LinearLayout layout;

        public viewHodel(@NonNull View itemView) {
            super(itemView);
            tvName_file = (TextView) itemView.findViewById(R.id.tvName_file);
            img_file = (ImageView) itemView.findViewById(R.id.img_item_file);
            layout = (LinearLayout) itemView.findViewById(R.id.item_file);
        }
    }
}
