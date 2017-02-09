package com.juicesoft.redpocket;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.juicesoft.redpocket.Data.User;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by jting on 2016/12/28.
 */

public class ShopsAdapter extends BaseAdapter {

    private LayoutInflater mLayInf;
    private User mUser;
    private Context mContext;
    private ShopsFragment mFragment;

    public ShopsAdapter(Context context, ShopsFragment fragment)
    {
        //Log.w("ShopsAdapter","ShopsAdapter");
        mLayInf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mFragment = fragment;
    }

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
            holder.txtViewName = (TextView) convertView.findViewById(R.id.txtViewName);
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
        }

        holder.btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View parentRow = (View) v.getParent();
                ListView listView = (ListView) parentRow.getParent();
                final int position = listView.getPositionForView(parentRow);


                View view = (View) v.getParent();
                TextView tv = (TextView) view.findViewById(R.id.txtViewName);
                String s = tv.getText().toString();

                Button bn = (Button) view.findViewById(R.id.btnBuy);
                String a = bn.getText().toString().substring(0,3);


                Log.w("ShopsAdapter",position+","+s+","+a);
                mFragment.buySomething(s,a);
//                switch (v.getId()) {
//                    case R.id.button1:
//                        Toast.makeText(getApplicationContext(),
//                                "按鈕點擊了:" + name[index], Toast.LENGTH_SHORT).show();
//                        break;
//                }
            }
        });
        return convertView;
    }
    class Holder{
        ImageView image;
        TextView txtViewName;
        TextView txtPrice;
        TextView txtInfo;
        Button btnBuy;
    }

}
