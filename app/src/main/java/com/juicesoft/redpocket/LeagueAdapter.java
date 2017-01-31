package com.juicesoft.redpocket;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.juicesoft.redpocket.Data.ScoreFormat;

import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by jting on 2016/12/28.
 */

public class LeagueAdapter extends BaseAdapter {

    private LayoutInflater mLayInf;
    private List<ScoreFormat> mItemList;
    private Context mContext;
    public LeagueAdapter(Context context, List<ScoreFormat> itemList)
    {
        Log.w("LeagueAdapter",String.valueOf(itemList.size()));
        mLayInf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mItemList = itemList;
        mContext = context;
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
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null){
//            Log.w("holer","convertView == null");
            convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.league_list_adapter, null);
            holder = new LeagueAdapter.Holder();
            holder.mTxtViewOrder = (TextView) convertView.findViewById(R.id.txtViewOrder);
            holder.mImgViewTag = (ImageView) convertView.findViewById(R.id.imgViewTag);
            holder.mTxtViewDisplayName = (TextView) convertView.findViewById(R.id.txtViewDisplayName);
            holder.mTxtViewScore = (TextView) convertView.findViewById(R.id.txtViewScore);


            convertView.setTag(holder);
        } else{
//            Log.w("holer","convertView != null");
            holder = (Holder) convertView.getTag();
        }
        switch(position) {
            case 0:
//                Log.w("holer",mItemList.get(position).getDisplayName());
                holder.mTxtViewOrder.setText(String.valueOf(position+1));
                holder.mImgViewTag.setImageResource(R.drawable.img_no1);


                break;
            case 1:
                holder.mTxtViewOrder.setText(String.valueOf(position+1));
                holder.mImgViewTag.setImageResource(R.drawable.img_no2);

                break;
            case 2:
                holder.mTxtViewOrder.setText(String.valueOf(position+1));
                holder.mImgViewTag.setImageResource(R.drawable.img_no3);

                break;
            default:
                holder.mTxtViewOrder.setText(String.valueOf(position+1));
                holder.mImgViewTag.setVisibility(View.INVISIBLE);


//            case 2:
//                holder.image.setImageResource(R.drawable.panda);
//                holder.text.setText("panda");
//                break;
        }

        if(position<3){
            holder.mTxtViewDisplayName.setTextColor(ContextCompat.getColor(mContext,R.color.colorAccent));
            holder.mTxtViewScore.setTextColor(ContextCompat.getColor(mContext,R.color.colorAccent));
//            holder.mTxtViewDisplayName.setTextColor(mContext.getResources().getColor(R.color.colorAccent,null));
//            holder.mTxtViewScore.setTextColor(mContext.getResources().getColor(R.color.colorAccent,null));
        }
//        Log.w("holer",mItemList.get(position).getDisplayName());
        if(mContext != null) {
            holder.mTxtViewDisplayName.setText(mItemList.get(position).getDisplayName());
            holder.mTxtViewScore.setText(String.valueOf(mItemList.get(position).getScore()));
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
