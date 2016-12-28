package com.kanhan.redpocket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by jting on 2016/12/28.
 */

public class CoinsAdapter extends BaseAdapter {

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        Holder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.coins_adapter, null);
            holder = new Holder();
            holder.image = (ImageView) convertView.findViewById(R.id.imageItem);
            holder.txtPrice = (TextView) convertView.findViewById(R.id.txtPrice);
            holder.txtInfo = (TextView) convertView.findViewById(R.id.txtInfo);

            convertView.setTag(holder);
        } else{
            holder = (Holder) convertView.getTag();
        }
        switch(position) {
            case 0:
                holder.image.setImageResource(R.drawable.icon_coin);
                holder.txtPrice.setText("100 Coins");
                holder.txtInfo.setText("Coins Information");
                break;
            case 1:
                holder.image.setImageResource(R.drawable.icon_coin);
                holder.txtPrice.setText("220 Coins");
                holder.txtInfo.setText("Coins Information");
                break;
//            case 2:
//                holder.image.setImageResource(R.drawable.panda);
//                holder.text.setText("panda");
//                break;
        }
        return convertView;
    }
    class Holder{
        ImageView image;
        TextView txtPrice;
        TextView txtInfo;
        Button btn;
    }
}
