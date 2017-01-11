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

public class ShopsAdapter extends BaseAdapter {
    @Override
    public int getCount() {
        return 6;
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
        Holder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.shops_list_adapter, null);
            holder = new ShopsAdapter.Holder();
            holder.image = (ImageView) convertView.findViewById(R.id.imageItem);
            holder.txtPrice = (TextView) convertView.findViewById(R.id.txtViewName);
            holder.txtInfo = (TextView) convertView.findViewById(R.id.txtInfo);
            holder.btnBuy = (Button) convertView.findViewById(R.id.btnBuy);

            convertView.setTag(holder);
        } else{
            holder = (Holder) convertView.getTag();
        }
        switch(position) {
            case 0:
                holder.image.setImageResource(R.drawable.icon_ironfist);
                holder.txtPrice.setText("Iron First");
                holder.txtInfo.setText("Tool Information");
                holder.btnBuy.setText("100 Coins");
                break;
            case 1:
                holder.image.setImageResource(R.drawable.icon_mindcontrol);
                holder.txtPrice.setText("Mind Control");
                holder.txtInfo.setText("Tool Information");
                holder.btnBuy.setText("200 Coins");
                break;
            case 2:
                holder.image.setImageResource(R.drawable.icon_goldenhand);
                holder.txtPrice.setText("Golden Hand");
                holder.txtInfo.setText("Tool Information");
                holder.btnBuy.setText("150 Coins");
                break;
            case 3:
                holder.image.setImageResource(R.drawable.life_icon);
                holder.txtPrice.setText("Life");
                holder.txtInfo.setText("Tool Information");
                holder.btnBuy.setText("100 Coins");
                break;
            case 4:
                holder.image.setImageResource(R.drawable.dice_icon);
                holder.txtPrice.setText("Dice");
                holder.txtInfo.setText("Tool Information");
                holder.btnBuy.setText("100 Coins");
                break;
            case 5:
                holder.image.setImageResource(R.drawable.victory_icon);
                holder.txtPrice.setText("Victory");
                holder.txtInfo.setText("Tool Information");
                holder.btnBuy.setText("100 Coins");
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
        Button btnBuy;
    }

}