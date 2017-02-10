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

public class ToolsAdapter extends BaseAdapter {

    private LayoutInflater mLayInf;
    private User mUser;
    private Context mContext;
    private PlayFragment mFragment;
    public ToolsAdapter(Context context, User user, PlayFragment fragment)
    {
//        Log.w("LeagueAdapter",user.toString());
        mLayInf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mUser = user;
        mContext = context;
        mFragment = fragment;
    }

    @Override
    public int getCount() {
        return 5;
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
            convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.tools_list_adapter, null);
            holder = new ToolsAdapter.Holder();
            holder.mImageItem = (ImageView) convertView.findViewById(R.id.imageItem);
            holder.mTxtViewName = (TextView) convertView.findViewById(R.id.txtViewName);
            holder.mTxtViewInfo = (TextView) convertView.findViewById(R.id.txtViewInfo);
            holder.mBtnAmount = (Button) convertView.findViewById(R.id.btnAmount);

            convertView.setTag(holder);
        } else{
            holder = (Holder) convertView.getTag();
        }
        switch(position) {
            case 0:
                holder.mImageItem.setImageResource(R.drawable.icon_ironfist);
                holder.mTxtViewName.setText("Iron First");
                holder.mTxtViewInfo.setText("Tool Information");
                holder.mBtnAmount.setText(String.valueOf(mUser.getIronFirst()));
//                Log.w("holer0",String.valueOf(mUser.getIronFirst()));
                break;
            case 1:
                holder.mImageItem.setImageResource(R.drawable.icon_mindcontrol);
                holder.mTxtViewName.setText("Mind Control");
                holder.mTxtViewInfo.setText("Tool Information");
                holder.mBtnAmount.setText(String.valueOf(mUser.getMindControl()));
//                Log.w("holer0",String.valueOf(mUser.getMindControl()));
                break;
            case 2:
                holder.mImageItem.setImageResource(R.drawable.icon_goldenhand);
                holder.mTxtViewName.setText("Golden Hand");
                holder.mTxtViewInfo.setText("Tool Information");
                holder.mBtnAmount.setText(String.valueOf(mUser.getGoldenHand()));
//                Log.w("holer0",String.valueOf(mUser.getGoldenHand()));
                break;
            case 3:
                holder.mImageItem.setImageResource(R.drawable.dice_icon);
                holder.mTxtViewName.setText("Dice");
                holder.mTxtViewInfo.setText("Tool Information");
                holder.mBtnAmount.setText(String.valueOf(mUser.getDice()));
//                Log.w("holer0",String.valueOf(mUser.getDice()));
                break;
            case 4:
                holder.mImageItem.setImageResource(R.drawable.victory_icon);
                holder.mTxtViewName.setText("Victory");
                holder.mTxtViewInfo.setText("Tool Information");
                holder.mBtnAmount.setText(String.valueOf(mUser.getVictory()));
//                Log.w("holer0",String.valueOf(mUser.getVictory()));
                break;
//            case 2:
//                holder.image.setImageResource(R.drawable.panda);
//                holder.text.setText("panda");
//                break;
        }


        holder.mBtnAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View parentRow = (View) v.getParent();
                ListView listView = (ListView) parentRow.getParent();
                final int position = listView.getPositionForView(parentRow);


                View view = (View) v.getParent();
                TextView tv = (TextView) view.findViewById(R.id.txtViewName);
                String s = tv.getText().toString();

                Button bn = (Button) view.findViewById(R.id.btnAmount);
                String a = bn.getText().toString();


                Log.w("mBtnAmount",position+","+s+","+a);
                mFragment.useTool(s,a);
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
        ImageView mImageItem;
        TextView mTxtViewName;
        TextView mTxtViewInfo;
        Button mBtnAmount;
    }

}
