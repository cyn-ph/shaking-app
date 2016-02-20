package com.example.countingshakes.view.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.countingshakes.R;
import com.example.countingshakes.model.RankingItem;
import com.example.countingshakes.utils.Utils;

import java.util.List;

/**
 * Created by cyn on 02/19/2016.
 */
public class RankingItemAdapter extends ArrayAdapter<RankingItem> {
    private Context context;
    private List<RankingItem> rankingItemList;

    public RankingItemAdapter(Context context, List<RankingItem> rankingItems) {
        super(context, -1, rankingItems);
        this.context = context;
        this.rankingItemList = rankingItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Implement view holder pattern
        ViewHolderItem viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.ranking_item, parent, false);
            viewHolder = new ViewHolderItem();
            viewHolder.txtRankingInfo = (TextView) convertView.findViewById(R.id.txt_ranking_info);
            viewHolder.txtRankingDate = (TextView) convertView.findViewById(R.id.txt_ranking_date);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        RankingItem rankingItem = getItem(position);
        viewHolder.txtRankingInfo.setText(context.getResources().getString(R.string.ranking_info_str, rankingItem.getName(), rankingItem.getScore()));
        viewHolder.txtRankingDate.setText(rankingItem.getDate().toString());
        return convertView;
    }

    private static class ViewHolderItem {
        TextView txtRankingInfo;
        TextView txtRankingDate;
    }

    /**
     * This method allow to update the content of the list
     * @param rankingItemList
     */
    public void updateRankingItemList(List<RankingItem> rankingItemList) {
        Log.d(Utils.TAG, "Number of elements: " + rankingItemList.size());
        this.rankingItemList.clear();
        this.rankingItemList.addAll(rankingItemList);
        notifyDataSetChanged();
    }

    @Override
    public boolean isEnabled(int position) {
        //Disable click
        return false;
    }
}
