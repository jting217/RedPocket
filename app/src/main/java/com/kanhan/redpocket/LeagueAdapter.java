package com.kanhan.redpocket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kanhan.redpocket.Data.Score;

import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by jting on 2016/12/28.
 */

public class LeagueAdapter extends BaseAdapter {

    private LayoutInflater mLayInf;
    List<Score> mItemList;
    public LeagueAdapter(Context context, List<Score> itemList)
    {
        mLayInf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mItemList = itemList;
    }

    @Override
    public int getCount()
    {
        //取得 ListView 列表 Item 的數量
        return mItemList.size();
    }

    @Override
    public Object getItem(int position)
    {
        //取得 ListView 列表於 position 位置上的 Item
        return position;
    }

    @Override
    public long getItemId(int position)
    {
        //取得 ListView 列表於 position 位置上的 Item 的 ID
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.league_list_adapter, null);
            holder = new LeagueAdapter.Holder();
            holder.mTxtViewOrder = (TextView) convertView.findViewById(R.id.txtViewOrder);
            holder.mImgViewTag = (ImageView) convertView.findViewById(R.id.imgViewTag);
            holder.mTxtViewDisplayName = (TextView) convertView.findViewById(R.id.txtViewDisplayName);
            holder.mTxtViewScore = (TextView) convertView.findViewById(R.id.txtViewScore);


            convertView.setTag(holder);
        } else{
            holder = (Holder) convertView.getTag();
        }
        switch(position) {
            case 0:
                holder.mTxtViewOrder.setText(String.valueOf(position+1));
                holder.mImgViewTag.setImageResource(R.drawable.img_no1);
                holder.mTxtViewDisplayName.setText(mItemList.get(0).getDisplayName());
                holder.mTxtViewScore.setText(String.valueOf(mItemList.get(0).getScore().intValue()));

                break;
            case 1:
                holder.mTxtViewOrder.setText(String.valueOf(position+1));
                holder.mImgViewTag.setImageResource(R.drawable.img_no2);
                holder.mTxtViewDisplayName.setText(mItemList.get(1).getDisplayName());
                holder.mTxtViewScore.setText(String.valueOf(mItemList.get(1).getScore().intValue()));
                break;
            case 2:
                holder.mTxtViewOrder.setText(String.valueOf(position+1));
                holder.mImgViewTag.setImageResource(R.drawable.img_no3);
                holder.mTxtViewDisplayName.setText(mItemList.get(2).getDisplayName());
                holder.mTxtViewScore.setText(String.valueOf(mItemList.get(2).getScore().intValue()));
                break;
            default:
                holder.mTxtViewOrder.setText(String.valueOf(position+1));
                holder.mImgViewTag.setVisibility(View.INVISIBLE);
                holder.mTxtViewDisplayName.setText(mItemList.get(position).getDisplayName());
                holder.mTxtViewScore.setText(String.valueOf(mItemList.get(position).getScore().intValue()));

//            case 2:
//                holder.image.setImageResource(R.drawable.panda);
//                holder.text.setText("panda");
//                break;
        }
        return convertView;
    }
    class Holder{
        TextView mTxtViewOrder;
        ImageView mImgViewTag;
        TextView mTxtViewDisplayName;
        TextView mTxtViewScore;
    }

}
