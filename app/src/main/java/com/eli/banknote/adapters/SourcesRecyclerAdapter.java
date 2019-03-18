package com.eli.banknote.adapters;

import android.content.Context;
import android.text.Html;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.eli.banknote.utils.ConveterAdapter;
import com.eli.banknote.R;
import com.eli.banknote.utils.SharedPreferences;
import com.eli.banknote.models.newsSource;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;


public class SourcesRecyclerAdapter extends RecyclerView.Adapter<SourcesRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<newsSource> sourceList;
    SharedPreferences sp;
    public SparseBooleanArray itemStateArray = new SparseBooleanArray();
    private ConveterAdapter conveterAdapter = new ConveterAdapter(mContext);

    public SourcesRecyclerAdapter(ArrayList<newsSource> sourceList, Context mContext) {
        this.mContext = mContext;
        this.sourceList = sourceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.news_source_layout, parent, false);
        return new SourcesRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);

    }

    @Override
    public int getItemCount() {
        if (sourceList == null) {
            return 0;
        } else {
            return sourceList.size();
        }

    }

    void loadItems(ArrayList<newsSource> sources) {
        this.sourceList = sources;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.sourceCategory)
        TextView sourceCategoty;
        @BindView(R.id.sourceCountry)
        TextView sourceCountry;
        @BindView(R.id.sourceDescription)
        TextView sourceDescription;
        @BindView(R.id.sourceLanguage)
        TextView sourceLanguage;
        @BindView(R.id.tvSourceId)
        TextView sourceId;
        @BindView(R.id.tvSourceName)
        TextView sourceName;
        @BindView(R.id.sourceUrl)
        TextView sourceUrl;
        @BindView(R.id.sourceCheckBox)
        RadioButton sourceCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }


        void bind(int position) {

            sourceId.setText(sourceList.get(position).getSourceId());
            sourceName.setText("Source: "+sourceList.get(position).getSourceName());
            sourceDescription.setText(sourceList.get(position).getSourceDescription());
            sourceCountry.setText("Country: " + conveterAdapter.getLocaleCountryName(sourceList.get(position).getSourceCountry()));
            sourceLanguage.setText("Language:" + conveterAdapter.convertToLocalLanguage(sourceList.get(position).getSourceLanguage()));
            sourceCategoty.setText("Category: " + sourceList.get(position).getSourceCategory());
            sourceUrl.setText("Url: " + Html.fromHtml(sourceList.get(position).getSourceUrl()));
            if (!itemStateArray.get(position, false)) {
                sourceCheckBox.setChecked(false);
            } else {
                sourceCheckBox.setChecked(true);
            }
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            sp = new SharedPreferences(mContext);
            Toast.makeText(mContext, "Hi" + sourceList.get(adapterPosition).getSourceId(), Toast.LENGTH_SHORT).show();
            sp.put(SharedPreferences.Key.SOURCE_NAME, sourceList.get(adapterPosition).getSourceName());


            if (!itemStateArray.get(adapterPosition, false)) {
                sourceCheckBox.setChecked(true);
                itemStateArray.put(adapterPosition, true);
            } else {
                sourceCheckBox.setChecked(false);
                itemStateArray.put(adapterPosition, false);
            }

        }
    }

}
