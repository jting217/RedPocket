package com.juicesoft.redpocket;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
        Holder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.shops_list_adapter, null);
            holder = new Holder();
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
                holder.image.setImageResource(R.drawable.icon_coin);
                holder.txtPrice.setText("100 Coins");
                holder.txtInfo.setText("Coins Information");
                holder.btnBuy.setText("$0.99");
                break;
            case 1:
                holder.image.setImageResource(R.drawable.icon_coin);
                holder.txtPrice.setText("220 Coins");
                holder.txtInfo.setText("Coins Information");
                holder.btnBuy.setText("$1.99");
                break;
//            case 2:
//                holder.image.setImageResource(R.drawable.panda);
//                holder.text.setText("panda");
//                break;
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
                String a = bn.getText().toString();


                Log.w("CoinAdapter",position+","+s+","+a);
//                mFragment.useTool(s,a);
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
        TextView txtPrice;
        TextView txtInfo;
        Button btnBuy;
    }
}
