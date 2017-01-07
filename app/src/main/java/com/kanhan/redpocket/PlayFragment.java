package com.kanhan.redpocket;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.kanhan.redpocket.Data.Board;
import com.kanhan.redpocket.Data.SystemPreferences;
import com.kanhan.redpocket.Data.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


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
    private static final String ARG_PALY_SOUND = "arg_play_sound";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mPlaySound;
    private static int tsec=0;
    private int csec=0,cmin=0,setTsec=50;
    private static Timer timer01;
    private TimerTask timerTask;
    private boolean startflag=false;
    private static long tmpTimer;
    private Intent countDownSound;

    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private User iniUser;
    private DatabaseReference mWriteDatabase, mReadDatabase, mQueryDatabase;

    private ImageView mImgViewScissors, mImgViewRock, mImgViewPaper, mImgViewPlayer, mImgViewNpc;
    private TextView mTxtViewResult, mTxtViewCounter, mTxtViewCoins, mTxtViewScore, mTxtViewLives, mTxtViewPlayCounter;
    private int mCoins;
    private int mScore;
    private int mLives;
    private int ptlogMultiple, ptlogMatchResult, ptlogScore, ptlogUserInput, ptlogComputerInput, ptlogTotalScore;
    private int ltlogType, ltlogTransaction;
    private boolean updatePlayResult = false, updateGetLife = false;
    private static SystemPreferences mSystemPreferences;
    private long mLifeCounter;
    private long mStartDateInterval, mEndDateInterval;
    private static String mBoardNode = "";

    private static PlayFragment instance;


    private int mColor;

    private boolean chkReaded = false;
    private static boolean isFirstCreatTimer = true;

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
    public static PlayFragment newInstance(String param1, int param2, String playSound) {
        Log.d("FragPlay","newInstance");
//        if(instance == null){
            instance = new PlayFragment();
            Bundle args = new Bundle();
            args.putString(ARG_PARAM1, param1);
            args.putInt(ARG_COLOR, param2);
            args.putString(ARG_PALY_SOUND, playSound);
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
            mPlaySound = getArguments().getString(ARG_PALY_SOUND);
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
        mTxtViewPlayCounter = (TextView) getView().findViewById(R.id.txtViewPlayCounter);
        mTxtViewResult = (TextView) getView().findViewById(R.id.txtViewResult);
        mTxtViewCoins  = (TextView) getView().findViewById(R.id.txtViewCoins);
        mTxtViewScore = (TextView) getView().findViewById(R.id.txtViewScore);
        mTxtViewLives = (TextView) getView().findViewById(R.id.txtViewLives);
        iniUser = new User();
        readUser(FragmentState.OnIni.value);
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
        ptlogMultiple = 1;

        ((MainActivity)getActivity()).removeBottomNavListener();
        mImgViewScissors.setOnClickListener(null);
        mImgViewRock.setOnClickListener(null);
        mImgViewPaper.setOnClickListener(null);

        mCoins = Integer.valueOf(mTxtViewCoins.getText().toString());
        mScore = Integer.valueOf(mTxtViewScore.getText().toString());
        mLives = Integer.valueOf(mTxtViewLives.getText().toString());

        Log.d("mCoins,S,L",mCoins+","+mScore+","+mLives);
        ptlogMatchResult = 0;
        mTxtViewPlayCounter.setVisibility(View.VISIBLE);

//        countDownSound = new Intent(getActivity(),CountDownSoundService.class);
//        if(mPlaySound.equals("1")) {
//            getActivity().startService(countDownSound);
//        }
        if(mPlaySound.equals("1")) {
            MediaPlayer mpLose = MediaPlayer.create(getActivity(), R.raw.game_start_countdown);
            mpLose.start();
            mpLose.seekTo(0);
        }
        new CountDownTimer(6000, 1000) {
            //mTxtViewCounter.setVisibility(v.VISIBLE );

            @Override
            public void onTick(long millisUntilFinished) {
                //倒數秒數中要做的事

                mTxtViewPlayCounter.setText(new SimpleDateFormat("s").format(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                //倒數完成後要做的事
//                getActivity().stopService(countDownSound);
                if(mPlaySound.equals("1")) {
                    mTxtViewPlayCounter.setVisibility(View.INVISIBLE);
                }
                int result=0;
                //Player
                int iComPlay = (int) (Math.random() * 3 + 1);
                if(iComPlay == 1){
                    ptlogComputerInput = Input.Scissor.value;
                }else if(iComPlay == 2){
                    ptlogComputerInput = Input.Rock.value;
                }else{
                    ptlogComputerInput = Input.Paper.value;
                }
                switch (v.getId()) {
                    case R.id.imgViewScissors:
                        // do something
                        ptlogUserInput = Input.Scissor.value;
                        Log.d("tag", "剪刀");
                        mImgViewPlayer.setImageResource(R.drawable.img_card_scissor_red);
                        if(iComPlay == 1){
                            result = R.string.text_tie;
                            ptlogMatchResult = MatchResult.Tie.value;
                        }else if(iComPlay == 2){
                            result = R.string.text_lose;
                            ptlogMatchResult = MatchResult.Lose.value;
                        }else{
                            result = R.string.text_win;
                            ptlogMatchResult = MatchResult.Win.value;
                        }
                        break;
                    case R.id.imgViewRock:
                        // do something else
                        ptlogUserInput = Input.Rock.value;
                        Log.d("tag", "石頭");
                        mImgViewPlayer.setImageResource(R.drawable.img_card_rock_red);
                        if(iComPlay == 1){
                            result = R.string.text_win;
                            ptlogMatchResult = MatchResult.Win.value;
                        }else if(iComPlay == 2){
                            result = R.string.text_tie;
                            ptlogMatchResult = MatchResult.Tie.value;
                        }else{
                            result = R.string.text_lose;
                            ptlogMatchResult = MatchResult.Lose.value;
                        }
                        break;
                    case R.id.imgViewPaper:
                        ptlogUserInput = Input.Paper.value;
                        // i'm lazy, do nothing
                        Log.d("tag", "布");
                        mImgViewPlayer.setImageResource(R.drawable.img_card_paper_red);
                        if(iComPlay == 1){
                            result = R.string.text_lose;
                            ptlogMatchResult = MatchResult.Lose.value;
                        }else if(iComPlay == 2){
                            result = R.string.text_win;
                            ptlogMatchResult = MatchResult.Win.value;
                        }else{
                            result = R.string.text_tie;
                            ptlogMatchResult = MatchResult.Tie.value;
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
                int mr = ptlogMatchResult;
                int getScore = 0 , getLives = 0;
                switch (ptlogMatchResult){

                    case 1 :
                        mScore+=10;
                        mLives-=1;
                        getScore = 10;
                        getLives = -1;
                        if(mPlaySound.equals("1")) {
                            MediaPlayer mpLose = MediaPlayer.create(getActivity(), R.raw.lose);
                            mpLose.start();
                            mpLose.seekTo(0);
                        }
//                        mpLose.release();
                        break;
                    case 2 :
                        mScore+=100;
                        mLives-=1;
                        getScore = 100;
                        getLives = -1;
                        if(mPlaySound.equals("1")) {
                            MediaPlayer mpWin = MediaPlayer.create(getActivity(), R.raw.win);
                            mpWin.start();
                            mpWin.seekTo(0);
                        }
//                        mpWin.release();
                        break;
                    default:
                        if(mPlaySound.equals("1")) {
                            MediaPlayer mpTie = MediaPlayer.create(getActivity(), R.raw.excitement);
                            mpTie.start();
                            mpTie.seekTo(0);
                        }
                        break;

                }

                ptlogScore = getScore;
                ltlogTransaction = getLives;
                ltlogType = LifeTransactionLife.PlayGame.value;

                Log.d("Result:", String.valueOf(result)+","+mScore+","+mLives);
                ptlogTotalScore = mScore;
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

                updateUser(user.getUid(),UpdateUserTimer.PlayGame.value);
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
        Log.d("FragPlay","onResume->"+GetRightNow());
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("FragPlay","onStop->");
        updateTimer(user.getUid());

    }

    @Override
    public void onStart() {
        Log.d("FragPlay","onStart->"+GetRightNow()+","+tmpTimer+","+tsec);
        super.onStart();
    }

    /*目前的board*/
    private void updateBoard(final String userId) {
        final Long rightNow = GetRightNow();
        mQueryDatabase = FirebaseDatabase.getInstance().getReference("score-boards");
        Query queryRef = mQueryDatabase.orderByChild("endDateInterval").startAt(rightNow);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Long targetTime = 0L;
                Log.d("board",rightNow.toString());
                for (DataSnapshot boardSnapshot: snapshot.getChildren()) {
                    Board b = boardSnapshot.getValue(Board.class);
                    if(rightNow >= b.getStartDateInterval()) {
                        Log.e("Get Data", boardSnapshot.getKey() + "," + b.getStartDateInterval() + "," + b.getEndDateInterval());

                        DatabaseReference wRef = FirebaseDatabase.getInstance().getReference("score-boards/" + boardSnapshot.getKey() + "/scores/" + userId);
                        Map board = new HashMap();
                        board.put("displayName", "test");
                        board.put("score", mScore);
                        wRef.setValue(board);
                        break;//新加的，怕有錯註記一下
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("☆firebase failed: ", databaseError.getMessage());
            }

        });

    }

    private void boardNode(Long sTime) {
        Log.e("Get Data2","--------------"+sTime);

        mQueryDatabase = FirebaseDatabase.getInstance().getReference("score-boards");
        Query queryRef = mQueryDatabase.orderByChild("startDateInterval").equalTo(sTime);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot boardSnapshot: snapshot.getChildren()) {
                    Board b = boardSnapshot.getValue(Board.class);
                    Log.e("Get Data2", boardSnapshot.getKey()+","+b.getStartDateInterval()+","+b.getEndDateInterval());
                    mBoardNode = boardSnapshot.getKey();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("☆firebase failed: ", databaseError.getMessage());
            }
        });

    }

    private void readSystemPreferences(final Long lifeCounter) {
        Log.d("☆Firebase(playFrag)","readSystemPreferences");
        mReadDatabase = FirebaseDatabase.getInstance().getReference("systemPreferences");

        mReadDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                SystemPreferences sp = snapshot.getValue(SystemPreferences.class);
                //以下這段也可以用！
//                Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
//                //Adding it to a string
//                for (Object key : map.keySet()) {
//                    Log.w("firebase",key + " : " + map.get(key) +  map.get(key).getClass());
//                }
                mSystemPreferences = sp;
                setTsec = mSystemPreferences.getCounterSec().intValue();
                Log.w("☆firebase(playFrag)",String.valueOf(sp.getCounterSec())+","+String.valueOf(mSystemPreferences.getCounterSec()));
                CreateTimer(lifeCounter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("☆firebase failed(pF) " , databaseError.getMessage());
            }

        });

    }


    /*找top100*/
    private void boardOrderby() {
        Log.d("☆Firebase(playFrag)","boardOrderby");

        mQueryDatabase = FirebaseDatabase.getInstance().getReference("score-boards/0/scores");
        Query queryRef = mQueryDatabase.orderByChild("score").limitToLast(3);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot boardSnapshot: snapshot.getChildren()) {
                    Log.w("firebase+",boardSnapshot.toString());
//                    Map<String, Object> map = (HashMap<String, Object>) boardSnapshot.getValue();
//
//                    //Adding it to a string
//                    for (Object key : map.keySet()) {
//                        Log.w("firebase+", key + " : " + map.get(key) + map.get(key).getClass());
//                    }
//                    Board b = boardSnapshot.getValue(Board.class);
//                    Log.e("Get Data2", boardSnapshot.getKey()+","+b.getStartDateInterval()+","+b.getEndDateInterval());
//                    mBoardNode = boardSnapshot.getKey();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("☆firebase failed: ", databaseError.getMessage());
            }
        });


    }

    private void readUser(final int when) {
        Log.d("☆Firebase", "readUser->"+String.valueOf(FragmentState.values()[when-1]));
        mReadDatabase = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());

        mReadDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                User u = snapshot.getValue(User.class);
                if(u != null){
                    //以下這段也可以用！
                    Long rightNow = GetRightNow();
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

                    if(when == FragmentState.OnIni.value) {
//                        mLifeCounter = u.getLifeCounter();
                        readSystemPreferences(u.getLifeCounter()/1000);
                        //CreateTimer(u.getLifeCounter());
                    }
//                    if( !(rightNow>=u.getStartDateInterval() && rightNow <= u.getEndDateInterval()) ){
//                        board();
//                    }

                }else{
                    readUser(FragmentState.OnIni.value);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("☆firebase failed: ", databaseError.getMessage());
            }

        });
    }

    private void updateUser(final String userId, final int when) {
        Log.d("☆Firebase",String.valueOf(UpdateUserTimer.values()[when-1]));
        mWriteDatabase = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());

        mWriteDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                User user = new User();

                user.setCoins(Long.valueOf(mCoins));
                user.setScore(Long.valueOf(mScore));
//                Map<String, Object> userValues = user.toMap();
//                Map<String, Object> childUpdates = new HashMap<>();
//                childUpdates.put("/posts/" + key, postValues);
//                childUpdates.put("/user-posts/" + userId + "/" + key, postValues);
//                mWriteDatabase.updateChildren(userValues);
//                mWriteDatabase.setValue(user);

                Map newUserData = new HashMap();
                if(when == UpdateUserTimer.PlayGame.value)
                {
                    newUserData.put("lives", Long.valueOf(mLives));
                    newUserData.put("score", Long.valueOf(mScore));
                }else if(when == UpdateUserTimer.FiveMinutesTimer.value) {
                    newUserData.put("lives", Long.valueOf(mLives));
                }else if(when == UpdateUserTimer.GetNewIntervalDate.value){
                    newUserData.put("startDateInterval", Long.valueOf(mStartDateInterval));
                    newUserData.put("endDateInterval", Long.valueOf(mEndDateInterval));
                    iniUser.setStartDateInterval(mStartDateInterval);
                }

               // mBoardNode
                mWriteDatabase.updateChildren(newUserData);

                if(when != UpdateUserTimer.GetNewIntervalDate.value) {//需要寫log
                    Long rightNow = GetRightNow();
                    if (when == UpdateUserTimer.PlayGame.value) {
                        mWriteDatabase = FirebaseDatabase.getInstance().getReference("users/" + userId + "/transactionLogPlay/" + rightNow);
                        Map transactionLogPlay = new HashMap();
                        transactionLogPlay.put("multiple", ptlogMultiple);
                        transactionLogPlay.put("matchResult", ptlogMatchResult);
                        transactionLogPlay.put("score", ptlogScore);
                        transactionLogPlay.put("userInput", ptlogUserInput);
                        transactionLogPlay.put("computerInput", ptlogComputerInput);
                        transactionLogPlay.put("totalScore", ptlogTotalScore);
                        mWriteDatabase.setValue(transactionLogPlay);
                    }
                    mWriteDatabase = FirebaseDatabase.getInstance().getReference("users/" + userId + "/transactionLogLife/" + rightNow);
                    Map transactionLogLife = new HashMap();
                    transactionLogLife.put("type", ltlogType);
                    transactionLogLife.put("transaction", ltlogTransaction);
                    mWriteDatabase.setValue(transactionLogLife);

                    if (when == UpdateUserTimer.PlayGame.value) {
                        mTxtViewScore.setText(String.valueOf(mScore));
                    }
                    mTxtViewLives.setText(String.valueOf(mLives));
                }

                if (when == UpdateUserTimer.PlayGame.value) {
                    updateBoard(userId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("☆firebase failed: ", databaseError.getMessage());
            }
        });
    }

    private void updateTimer(final String userId) {
        Log.d("updateTimer",userId);
        mWriteDatabase = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());
        Map newUserData = new HashMap();
        newUserData.put("lifeCounter", ServerValue.TIMESTAMP);
        mWriteDatabase.updateChildren(newUserData);
        mWriteDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                    User u = snapshot.getValue(User.class);
                    Log.d("updateTimer",u.getLifeCounter().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("☆firebase failed: ", databaseError.getMessage());
            }
        });
    }

    public enum FragmentState {
        OnIni(1),
        OnStart(2);

        private int value;

        private FragmentState(int value) {
            this.value = value;
        }
        public int FragmentState() {
            return this.value;
        }
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

    public enum Input {
        Paper(1),
        Rock(2),
        Scissor(3);

        private int value;

        private Input(int value) {
            this.value = value;
        }
        public int Input() {
            return this.value;
        }
    }


    public enum UpdateUserTimer {
        SignUpReward(1),
        PlayGame(2),
        Purchase(3),//購買
        FiveMinutesTimer(4),
        GetNewIntervalDate(5);

        private int value;

        private UpdateUserTimer(int value) {
            this.value = value;
        }
        public int UpdateUserTimer() {
            return this.value;
        }
    }

    public enum LifeTransactionLife {
        SignUpReward(1),
        PlayGame(2),
        Purchase(3),
        FiveMinutesTimer(4);

        private int value;

        private LifeTransactionLife(int value) {
            this.value = value;
        }
        public int LifeTransactionLife() {
            return this.value;
        }
    }

    private Long GetRightNow(){
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        return tsLong;
    }

    //TimerTask無法直接改變元件因此要透過Handler來當橋樑
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:
                    if(tsec != 0) {
                        csec = tsec % 60;
                        cmin = tsec / 60;
                        String s = "";
                        if (cmin < 10) {
                            s = "0" + cmin;
                        } else {
                            s = "" + cmin;
                        }
                        if (csec < 10) {
                            s = s + ":0" + csec;
                        } else {
                            s = s + ":" + csec;
                        }
                        //s字串為00:00格式
                        mTxtViewCounter.setText(s);
                        break;
                    }else{
                        mLives = Integer.valueOf(mTxtViewLives.getText().toString());
                        tsec = mSystemPreferences.getCounterSec().intValue();
                        mLives+=1;
                        ltlogTransaction = 1;
                        ltlogType = LifeTransactionLife.FiveMinutesTimer.value;
                        updateUser(user.getUid(),UpdateUserTimer.FiveMinutesTimer.value);
                    }
            }
        }
    };

    private void CreateTimer(Long lifeCounter){
//        Log.d("CreateTimer",String.valueOf(lifeCounter));
        //(GetRightNow()-lifeCounter)/300=
        //宣告Timer
        if(timer01 != null){
            timer01.purge();
            timer01.cancel();
            timer01 = null;
        }
        timer01 =new Timer();
        CreateTimerTask();;
        //設定Timer(task為執行內容，0代表立刻開始,間格1秒執行一次)
        if(isFirstCreatTimer) {
            if(lifeCounter == 0){
                tsec = setTsec;
            }else {
                long rightNow = GetRightNow();
                long countSec = mSystemPreferences.getCounterSec() - ((rightNow - lifeCounter) % mSystemPreferences.getCounterSec());
                long getLife = ((rightNow - lifeCounter) / mSystemPreferences.getCounterSec());
                if(getLife>0){
                    mLives = Integer.valueOf(mTxtViewLives.getText().toString());
//                    tsec = mSystemPreferences.getCounterSec().intValue();
                    mLives+=getLife;
                    ltlogTransaction = (int) getLife;
                    ltlogType = LifeTransactionLife.FiveMinutesTimer.value;
                    updateUser(user.getUid(),UpdateUserTimer.FiveMinutesTimer.value);
                }
                tsec = (int)countSec;
                Log.d("firstCreatTimer",rightNow+","+lifeCounter+","+countSec+","+getLife+","+mSystemPreferences.getCounterSec());
            }
            isFirstCreatTimer = false;
        }
        timer01.schedule(timerTask, 0,1000);

        startflag=true;
    }
    private void CreateTimerTask(){
        timerTask = new TimerTask(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
//            Log.w("☆task",String.valueOf(startflag));
                if (startflag){
                    //如果startflag是true則每秒tsec+1
                    tsec--;
                    Message message = new Message();
                    //傳送訊息1
                    message.what =1;
                    handler.sendMessage(message);
                }
            }
        };
    }

}//程式結尾
