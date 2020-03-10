package com.dejin.tool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dejin.tool.R;
import com.dejin.tool.bean.Urls;

import java.util.List;

public class UrlsEncodeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Urls> data;

    public UrlsEncodeAdapter(Context context, List<Urls> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return viewType == 1 ? new AddViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_add, parent, false)) : new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_urls, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            Urls urls = data.get(position);
            myViewHolder.tv_url.setText(urls.url);
            myViewHolder.progressBar.setVisibility(urls.isRunning ? View.VISIBLE : View.INVISIBLE);
            myViewHolder.cb_status.setVisibility(urls.isAvaliable ? View.VISIBLE : View.INVISIBLE);
            myViewHolder.itemView.setOnClickListener(view -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            });
            myViewHolder.itemView.setOnLongClickListener(v -> {
                if (onItemLongClickListener != null) {
                    onItemLongClickListener.onItemLongClick(position);
                }
                return false;
            });
        } else {
            AddViewHolder myViewHolder = (AddViewHolder) holder;
            myViewHolder.itemView.setOnClickListener(view -> {
                if (onAddClickListener != null) {
                    onAddClickListener.onAddClick();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return data.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == data.size()) {
            return 1;
        } else {
            return 0;
        }
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static interface OnItemClickListener {
        void onItemClick(int i);
    }


    private OnItemLongClickListener onItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public static interface OnItemLongClickListener {
        void onItemLongClick(int i);
    }

    private OnAddClickListener onAddClickListener;

    public void setOnAddClickListener(OnAddClickListener onAddClickListener) {
        this.onAddClickListener = onAddClickListener;
    }

    public static interface OnAddClickListener {
        void onAddClick();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_url;
        private ProgressBar progressBar;
        private CheckBox cb_status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_url = (TextView) itemView.findViewById(R.id.tv_url);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            cb_status = (CheckBox) itemView.findViewById(R.id.cb_status);
        }
    }

    class AddViewHolder extends RecyclerView.ViewHolder {

        public AddViewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }

}
