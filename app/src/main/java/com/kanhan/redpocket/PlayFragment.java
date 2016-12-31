package com.kanhan.redpocket;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kanhan.redpocket.Data.User;


import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private User iniUser;
    private DatabaseReference mWriteDatabase, mReadDatabase;

    private ImageView mImgViewScissors, mImgViewRock, mImgViewPaper, mImgViewPlayer, mImgViewNpc;
    private TextView mTxtViewResult, mTxtViewCounter, mTxtViewCoins, mTxtViewScore, mTxtViewLives;
    private int mCoins;
    private int mScore;
    private int mLives;
    private int mMatchResult;
    private int mLifeTransationType;

    private static PlayFragment instance;


    private int mColor;

    private boolean chkReaded = false;

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
        mTxtViewScore = (TextView) getView().findViewById(R.id.txtViewScore);
        mTxtViewLives = (TextView) getView().findViewById(R.id.txtViewLives);
        iniUser = new User();
        readUser();



    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d("FragPlay","onViewCreated");
        super.onViewCreated(view, savedInstanceState);


    }

    private View.OnClickListener imgViewPlayOnClick = new View.OnClickListener() {
        public void onClick(final View v) {
            Log.d("FragPlay","OnClickListener");
            if(chkReaded) {
                PlayGame(v);
            }
        }
    };

// TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

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
        ((MainActivity)getActivity()).removeBottomNavListener();
        mImgViewScissors.setOnClickListener(null);
        mImgViewRock.setOnClickListener(null);
        mImgViewPaper.setOnClickListener(null);

        mCoins = Integer.valueOf(mTxtViewCoins.getText().toString());
        mScore = Integer.valueOf(mTxtViewScore.getText().toString());
        mLives = Integer.valueOf(mTxtViewLives.getText().toString());

        Log.d("mCoins,S,L",mCoins+","+mScore+","+mLives);
        mMatchResult = 0;
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
                            mMatchResult = MatchResult.Tie.value;
                        }else if(iComPlay == 2){
                            result = R.string.text_lose;
                            mMatchResult = MatchResult.Lose.value;
                        }else{
                            result = R.string.text_win;
                            mMatchResult = MatchResult.Win.value;
                        }
                        break;
                    case R.id.imgViewRock:
                        // do something else
                        Log.d("tag", "石頭");
                        mImgViewPlayer.setImageResource(R.drawable.img_card_rock_red);
                        if(iComPlay == 1){
                            result = R.string.text_win;
                            mMatchResult = MatchResult.Win.value;
                        }else if(iComPlay == 2){
                            result = R.string.text_tie;
                            mMatchResult = MatchResult.Tie.value;
                        }else{
                            result = R.string.text_lose;
                            mMatchResult = MatchResult.Lose.value;
                        }
                        break;
                    case R.id.imgViewPaper:
                        // i'm lazy, do nothing
                        Log.d("tag", "布");
                        mImgViewPlayer.setImageResource(R.drawable.img_card_paper_red);
                        if(iComPlay == 1){
                            result = R.string.text_lose;
                            mMatchResult = MatchResult.Lose.value;
                        }else if(iComPlay == 2){
                            result = R.string.text_win;
                            mMatchResult = MatchResult.Win.value;
                        }else{
                            result = R.string.text_tie;
                            mMatchResult = MatchResult.Tie.value;
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
                int mr = mMatchResult;
                int getScore = 0 , getLives = 0;
                switch (mMatchResult){
                    case 1 :
                        mScore+=10;
                        mLives-=1;
                        getScore = 10;
                        getLives = -1;
                        break;
                    case 2 :
                        mScore+=100;
                        mLives-=1;
                        getScore = 100;
                        getLives = -1;
                        break;

                }


                Log.d("Result:", String.valueOf(result)+","+mScore+","+mLives);

//                    mTxtViewResult.setVisibility(View.VISIBLE);
//                    mTxtViewResult.setText(result);

                CustomDialog dialog = new  CustomDialog(v.getContext(),getString(result),String.valueOf(getLives),String.valueOf(getScore),new CustomDialog.ICustomDialogEventListener() {
                    @Override
                    public void customDialogEvent(int id) {
                    }
                },R.style.dialog);
                dialog.show();
                dialog.getWindow().setLayout(1200, 650);

//                mTxtViewCoins.setText(String.valueOf(Integer.valueOf(mTxtViewCoins.getText().toString())+10));
                mTxtViewScore.setText(String.valueOf(mScore));
                mTxtViewLives.setText(String.valueOf(mLives));
                updateUser(user.getUid());
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

                        ((MainActivity)getActivity()).setBottomNavListener();
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

    private void readUser() {
        Log.d("☆Firebase", "readUser");
        mReadDatabase = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());

        mReadDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                User u = snapshot.getValue(User.class);
                if(u != null){
                    //以下這段也可以用！
                    Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();

                    //Adding it to a string
                    for (Object key : map.keySet()) {
                        Log.w("firebase", key + " : " + map.get(key) + map.get(key).getClass());
                    }
                    iniUser = u;
                    Log.d("firebase",String.valueOf(u.getLives()));

                    mTxtViewCoins.setText(String.valueOf(u.getCoins()));
                    mTxtViewScore.setText(String.valueOf(u.getScore()));
                    mTxtViewLives.setText(String.valueOf(u.getLives()));

                    chkReaded = true;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("☆firebase failed: ", databaseError.getMessage());
            }

        });
    }

    private void updateUser(final String userId) {
        Log.d("☆Firebase", "updateUser");
        mWriteDatabase = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());

        mWriteDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                User user = new User();

                user.setCoins(Long.valueOf(mCoins));
                user.setScore(Long.valueOf(mScore));
                Map<String, Object> userValues = user.toMap();
//                Map<String, Object> childUpdates = new HashMap<>();
//                childUpdates.put("/posts/" + key, postValues);
//                childUpdates.put("/user-posts/" + userId + "/" + key, postValues);
//                mWriteDatabase.updateChildren(userValues);
//                mWriteDatabase.setValue(user);

                Map newUserData = new HashMap();
                    newUserData.put("lives", Long.valueOf(mLives));
                    newUserData.put("score",Long.valueOf(mScore));
                mWriteDatabase.updateChildren(newUserData);

                mWriteDatabase = FirebaseDatabase.getInstance().getReference("users/" + userId +"/transatinLogPlay/"+GetRightNow());
                Map transatinLogPlay = new HashMap();
                transatinLogPlay.put("test",1);
                transatinLogPlay.put("ss",2);
                mWriteDatabase.setValue(transatinLogPlay);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("☆firebase failed: ", databaseError.getMessage());
            }
        });
    }

    public enum MatchResult {
        Lose(1),
        Win(2),
        Tie(3);

        private int value;

        private MatchResult(int value) {
            this.value = value;
        }

        public int MatchResult() {
            return this.value;
        }
    }

    private Long GetRightNow(){
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        return tsLong;
    }

}//程式結尾
