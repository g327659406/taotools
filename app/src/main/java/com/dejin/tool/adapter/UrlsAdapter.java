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


/**
 * 专业分类适配器
 */
public class UrlsAdapter extends RecyclerView.Adapter<UrlsAdapter.MyViewHolder> {
    private List<Urls> data;

    public UrlsAdapter(Context context, List<Urls> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_urls, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Urls urls = data.get(position);
        holder.tv_url.setText(urls.url);
        holder.progressBar.setVisibility(urls.isRunning ? View.VISIBLE : View.INVISIBLE);
        holder.cb_status.setVisibility(urls.isAvaliable ? View.VISIBLE : View.INVISIBLE);
        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static interface OnItemClickListener {
        void onItemClick(int i);
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

}
