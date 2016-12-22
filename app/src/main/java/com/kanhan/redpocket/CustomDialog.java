package com.kanhan.redpocket;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by jting on 2016/12/19.
 */

public class CustomDialog extends Dialog implements View.OnClickListener{

    //增加一個回調函數,用以從外部接收返回值
    public interface ICustomDialogEventListener {
        public void customDialogEvent(int id);
    }

    private ICustomDialogEventListener mCustomDialogEventListener;
    private Context mContext;
    private String mStrResult, mStrLives, mStrScores;

    public CustomDialog(Context context) {
        super(context);
        mContext = context;
    }

    public CustomDialog(Context context, String strResult, String strLives, String strScores,ICustomDialogEventListener listener,int theme) {
        super(context, theme);
        Log.d("=========",strResult+","+strLives+","+strScores);
        mContext = context;
        mStrResult = strResult;
        mStrLives = strLives;
        mStrScores = strScores;
        mCustomDialogEventListener = listener;
    }
    private void bindImageClickEvent(View layout){
        TextView mTxtViewClose = (TextView) layout.findViewById(R.id.txtViewClose);
        mTxtViewClose.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_dialog, null);

        TextView tvResult = (TextView)layout.findViewById(R.id.txtViewGetResult);
        tvResult.setText(mStrResult);//mStrResult);
        TextView tvLives = (TextView)layout.findViewById(R.id.txtViewGetLives);
        tvLives.setText("Get " + mStrLives + " Lives");
        TextView tvScores = (TextView)layout.findViewById(R.id.txtViewGetScores);
        tvScores.setText("Get " + mStrScores + " Scores");


        bindImageClickEvent(layout);

        this.setContentView(layout);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        int drawableID = -1;
        switch (id){
            case R.id.txtViewClose:
                Log.d("=========","txtViewClose");

                break;
                //drawableID = R.drawable.animal1;
//                Fragment frag = PlayFragment.newInstance("Play", R.color.colorWhite);
//                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                ft.add(R.id.container, frag, frag.getTag());
//                ft.commit();


        }
        if (drawableID != -1) {
            mCustomDialogEventListener.customDialogEvent(drawableID);
        }
        dismiss();
    }



}
