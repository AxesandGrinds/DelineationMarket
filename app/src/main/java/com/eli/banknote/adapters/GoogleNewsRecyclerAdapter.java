package com.eli.banknote.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.eli.banknote.R;
import com.eli.banknote.models.Articles;
import com.eli.banknote.utils.ConveterAdapter;

import java.text.ParseException;
import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GoogleNewsRecyclerAdapter extends RecyclerView.Adapter<GoogleNewsRecyclerAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<Articles> newslist;
    private ConveterAdapter conveterAdapter;
    public GoogleNewsRecyclerAdapter(ArrayList<Articles> newslist, Context mContext){
        this.newslist = newslist;
        this.mContext = mContext;
    }
    @Override
    public GoogleNewsRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.news_title_fragment_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GoogleNewsRecyclerAdapter.MyViewHolder holder, int position) {
        conveterAdapter = new ConveterAdapter(mContext);

        holder.tvNewsTitle.setText(newslist.get(position).getNewsTitle());
        holder.tvDescription.setText(newslist.get(position).getDescription());
        holder.tvSource.setText(newslist.get(position).getSources().getSourceName());
        holder.tvAuthor.setText(newslist.get(position).getAuthorName());
        try {
            holder.tvPublisheDate.setText(conveterAdapter.convertToLocaltime(newslist.get(position).getPublishedAt()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Glide.with(mContext)
                .asBitmap()
                .apply(RequestOptions.placeholderOf(R.mipmap.ic_google_logo_round))
                .load(newslist.get(position).getUrlToImage())
                .thumbnail(0.8f)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        if (newslist==null) {
            return 0;
        } else {
            return newslist.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView tvNewsTitle;
        @BindView(R.id.tv_description)
        TextView tvDescription;
        @BindView(R.id.tv_source)
        TextView tvSource;
        @BindView(R.id.tv_author)
        TextView tvAuthor;
        @BindView(R.id.tv_publishedDate)
        TextView tvPublisheDate;
        @BindView(R.id.img_itemImage)
        ImageView imageView;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
