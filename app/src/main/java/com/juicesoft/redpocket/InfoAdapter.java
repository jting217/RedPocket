package com.juicesoft.redpocket;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by jting on 2016/12/28.
 */

public class InfoAdapter extends BaseAdapter {

    @Override
    public int getCount() {
        return 3;
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
            convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.info_list_adapter, null);
            holder = new Holder();

            holder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
            holder.txtMore = (TextView) convertView.findViewById(R.id.txtMore);
            holder.txtArrow = (TextView) convertView.findViewById(R.id.txtArrow);


            convertView.setTag(holder);
        } else{
            holder = (Holder) convertView.getTag();
        }
        switch(position) {
            case 0:
                holder.txtTitle.setText("Bar Info");
                holder.txtMore.setText("more");
                holder.txtArrow.setText("＞");
                break;
            case 1:
                holder.txtTitle.setText("How to play");
                holder.txtMore.setText(" ");
                holder.txtArrow.setText("＞");

                break;
            case 2:
                holder.txtTitle.setText("Terms");
                holder.txtMore.setText(" ");
                holder.txtArrow.setText("＞");

                break;
        }


//        holder.btnBuy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                View parentRow = (View) v.getParent();
//                ListView listView = (ListView) parentRow.getParent();
//                final int position = listView.getPositionForView(parentRow);
//
//
//                View view = (View) v.getParent();
//                TextView tv = (TextView) view.findViewById(R.id.txtViewName);
//                String s = tv.getText().toString();
//
//                Button bn = (Button) view.findViewById(R.id.btnBuy);
//                String a = bn.getText().toString().substring(0,3);
//
//
//                Log.w("ShopsAdapter",position+","+s+","+a);
//                mFragment.buySomething(s,a);
////                switch (v.getId()) {
////                    case R.id.button1:
////                        Toast.makeText(getApplicationContext(),
////                                "按鈕點擊了:" + name[index], Toast.LENGTH_SHORT).show();
////                        break;
////                }
//            }
//        });

        return convertView;
    }
    class Holder{
        TextView txtTitle;
        TextView txtMore;
        TextView txtArrow;
    }




}
