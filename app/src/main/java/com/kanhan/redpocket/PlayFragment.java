package com.kanhan.redpocket;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_COLOR = "arg_color";
    private static Activity mAct;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView mImgViewScissors, mImgViewRock, mImgViewPaper, mImgViewPlayer, mImgViewNpc;
    private TextView mTxtViewResult, mTxtViewCounter, mTxtViewCoins;

    private static PlayFragment instance;


    private int mColor;

    private OnFragmentInteractionListener mListener;



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayFragment newInstance(String param1, int param2) {
        Log.d("FragPlay","newInstance");
//        if(instance == null){
            instance = new PlayFragment();
            Bundle args = new Bundle();
            args.putString(ARG_PARAM1, param1);
            args.putInt(ARG_COLOR, param2);

            instance.setArguments(args);
//        }
//        PlayFragment fragment = new PlayFragment();

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("FragPlay","onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mColor = getArguments().getInt(ARG_COLOR);
        }else {
//            mText = savedInstanceState.getString(ARG_TEXT);
            mColor = savedInstanceState.getInt(ARG_COLOR);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("FragPlay","onCreateView");
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_play, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d("FragPlay","onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        mImgViewScissors = (ImageView) getView().findViewById(R.id.imgViewScissors);
        mImgViewRock = (ImageView) getView().findViewById(R.id.imgViewRock);
        mImgViewPaper = (ImageView) getView().findViewById(R.id.imgViewPaper);

        mImgViewPlayer = (ImageView) getView().findViewById(R.id.imgViewPlayer);
        mImgViewNpc  = (ImageView) getView().findViewById(R.id.imgViewNpc);
        mImgViewScissors.setOnClickListener(imgViewPlayOnClick);
        mImgViewRock.setOnClickListener(imgViewPlayOnClick);
        mImgViewPaper.setOnClickListener(imgViewPlayOnClick);

        mTxtViewCounter = (TextView) getView().findViewById(R.id.txtViewCounter);
        mTxtViewResult = (TextView) getView().findViewById(R.id.txtViewResult);
        mTxtViewCoins  = (TextView) getView().findViewById(R.id.txtViewCoins);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d("FragPlay","onViewCreated");
        super.onViewCreated(view, savedInstanceState);


    }

    private View.OnClickListener imgViewPlayOnClick = new View.OnClickListener() {
        public void onClick(final View v) {
            Log.d("FragPlay","OnClickListener");

            PlayGame(v);

        }
    };

// TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        Log.d("FragPlay","onDetach");
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

    }



    public void PlayGame(final View v){
        Log.d("FragPlay","PlayGame");
        mImgViewScissors.setOnClickListener(null);
        mImgViewRock.setOnClickListener(null);
        mImgViewPaper.setOnClickListener(null);

        mTxtViewCounter.setVisibility(View.VISIBLE);

        new CountDownTimer(4000, 1000) {
            //mTxtViewCounter.setVisibility(v.VISIBLE );
            @Override
            public void onTick(long millisUntilFinished) {
                //倒數秒數中要做的事
                mTxtViewCounter.setText(new SimpleDateFormat("s").format(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                //倒數完成後要做的事
                mTxtViewCounter.setVisibility(View.INVISIBLE);
                int result=0;
                //Player
                int iComPlay = (int) (Math.random() * 3 + 1);

                switch (v.getId()) {
                    case R.id.imgViewScissors:
                        // do something
                        Log.d("tag", "剪刀");
                        mImgViewPlayer.setImageResource(R.drawable.img_card_scissor_red);
                        if(iComPlay == 1){
                            result = R.string.text_tie;
                        }else if(iComPlay == 2){
                            result = R.string.text_lose;
                        }else{
                            result = R.string.text_win;
                        }
                        break;
                    case R.id.imgViewRock:
                        // do something else
                        Log.d("tag", "石頭");
                        mImgViewPlayer.setImageResource(R.drawable.img_card_rock_red);
                        if(iComPlay == 1){
                            result = R.string.text_win;
                        }else if(iComPlay == 2){
                            result = R.string.text_tie;
                        }else{
                            result = R.string.text_lose;
                        }
                        break;
                    case R.id.imgViewPaper:
                        // i'm lazy, do nothing
                        Log.d("tag", "布");
                        mImgViewPlayer.setImageResource(R.drawable.img_card_paper_red);
                        if(iComPlay == 1){
                            result = R.string.text_lose;
                        }else if(iComPlay == 2){
                            result = R.string.text_win;
                        }else{
                            result = R.string.text_tie;
                        }
                        break;
                }
                if(iComPlay == 1){
                    mImgViewNpc.setImageResource(R.drawable.img_card_scissor_black);
                }else if(iComPlay == 2) {
                    mImgViewNpc.setImageResource(R.drawable.img_card_rock_black);
                }else{
                    mImgViewNpc.setImageResource(R.drawable.img_card_paper_black);
                }
                Log.d("Result:", String.valueOf(result));
//                    mTxtViewResult.setVisibility(View.VISIBLE);
//                    mTxtViewResult.setText(result);

                CustomDialog dialog = new  CustomDialog(v.getContext(),getString(result),"10","10",new CustomDialog.ICustomDialogEventListener() {
                    @Override
                    public void customDialogEvent(int id) {
                    }
                },R.style.dialog);
                dialog.show();
                dialog.getWindow().setLayout(1200, 650);

                mTxtViewCoins.setText(String.valueOf(Integer.valueOf(mTxtViewCoins.getText().toString())+10));

                new CountDownTimer(2000, 1000) {
                    //mTxtViewCounter.setVisibility(v.VISIBLE );
                    @Override
                    public void onTick(long millisUntilFinished) {
                        //倒數秒數中要做的事

                    }
                    @Override
                    public void onFinish() {
//                            ((MainActivity)getActivity()).recallPlayFragment();
                        mImgViewNpc.setImageResource(R.drawable.img_cardback);
                        mImgViewScissors.setOnClickListener(imgViewPlayOnClick);
                        mImgViewRock.setOnClickListener(imgViewPlayOnClick);
                        mImgViewPaper.setOnClickListener(imgViewPlayOnClick);
                        mImgViewPlayer.setImageResource(R.drawable.img_cardback);
                    }
                }.start();
            }
        }.start();



    }

    @Override
    public void onPause() {
        Log.d("FragPlay","onPause");
        super.onPause();


    }

    @Override
    public void onResume() {
        Log.d("FragPlay","onResume");
        super.onResume();

    }

    @Override
    public void onStop() {
        Log.d("FragPlay","onStop");
        super.onStop();
    }

}
