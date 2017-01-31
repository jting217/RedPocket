package com.juicesoft.redpocket;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.juicesoft.redpocket.Data.Board;
import com.juicesoft.redpocket.Data.Score;
import com.juicesoft.redpocket.Data.SystemPreferences;
import com.juicesoft.redpocket.Data.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    private int csec=0,cmin=0,setTsec=300;
    private static Timer timer01;
    private TimerTask timerTask;
    private boolean startflag=false;
    private static long tmpTimer;
    private Intent countDownSound;
    private ListView mListViewTools;
    private ListAdapter mToolsAdapter;
    private static int mUseTool = 0, mMultiple = 1 ;

    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private User iniUser;
    private DatabaseReference mWriteDatabase, mReadDatabase, mQueryDatabase;

    private ImageView mImgViewScissors, mImgViewRock, mImgViewPaper, mImgViewAuto, mImgViewPlayer, mImgViewNpc, mImgViewTools;
    private TextView mTxtViewResult, mTxtViewCounter, mTxtViewCoins, mTxtViewScore, mTxtViewLives, mTxtViewPlayCounter;
    private RelativeLayout mCoorContentRegion;
    private int mCoins, mDice, mLives, mWinWithPaper, mWinWithRock, mWinWithScissor, mDailyPlayTimes, mDailyWinTimes;
    private boolean mWinWithPaperFlag = false, mWinWithRockFlag = false, mWinWithScissorFlag = false, mDailyWinTimesFlag = false;
    private static int mScore;
    private int ptlogMultiple, ptlogMatchResult, ptlogScore, ptlogUserInput, ptlogComputerInput, ptlogTotalScore;
    private int ltlogType, ltlogTransaction;
    private boolean updatePlayResult = false, updateGetLife = false;
    private static SystemPreferences mSystemPreferences;
    private long mLifeCounter;
    private long mStartDateInterval, mEndDateInterval;
    private static String mBoardNode = "";

    private static PlayFragment instance;


    private int mColor;

    private boolean chkReaded = false, autoPlay = false, isPlaying = false;
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
        mImgViewAuto = (ImageView) getView().findViewById(R.id.imgViewAuto);

        mImgViewPlayer = (ImageView) getView().findViewById(R.id.imgViewPlayer);
        mImgViewNpc  = (ImageView) getView().findViewById(R.id.imgViewNpc);
        mImgViewScissors.setOnClickListener(imgViewPlayOnClick);
        mImgViewRock.setOnClickListener(imgViewPlayOnClick);
        mImgViewPaper.setOnClickListener(imgViewPlayOnClick);
        mImgViewAuto.setOnClickListener(imgViewAutoPlayOnClick);

        mTxtViewCounter = (TextView) getView().findViewById(R.id.txtViewCounter);
        mTxtViewPlayCounter = (TextView) getView().findViewById(R.id.txtViewPlayCounter);
        mTxtViewResult = (TextView) getView().findViewById(R.id.txtViewResult);
        mTxtViewCoins  = (TextView) getView().findViewById(R.id.txtViewCoins);
        mTxtViewScore = (TextView) getView().findViewById(R.id.txtViewScores);
        mTxtViewLives = (TextView) getView().findViewById(R.id.txtViewLives);

        mCoorContentRegion = (RelativeLayout) getView().findViewById(R.id.coorContentRegion);
        mCoorContentRegion.setOnClickListener(layoutViewToolsOnClick);

        mImgViewTools = (ImageView) getView().findViewById(R.id.imgViewTools);
//        mImgViewTools.setOnClickListener(imgViewToolsOnClick);

        mListViewTools = (ListView) getView().findViewById(R.id.listViewTools);
//        mToolsAdapter = new ToolsAdapter(getActivity().getApplicationContext(),iniUser);
//        mListViewTools.setAdapter(mToolsAdapter);
        //mLeagueAdapter = new LeagueAdapter(getActivity().getApplicationContext(), mUserScoreOrderList);

        iniUser = new User();
        readUser(UpdateUserTimer.OnIni.value);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d("FragPlay","onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    private View.OnClickListener imgViewToolsOnClick = new View.OnClickListener() {
        public void onClick(final View v) {
            Log.d("FragPlay","OnClickListener");
            readUser(UpdateUserTimer.OnIni.value);
            mCoorContentRegion.setVisibility(View.VISIBLE);
        }
    };
    private View.OnClickListener layoutViewToolsOnClick = new View.OnClickListener() {
        public void onClick(final View v) {
            Log.d("FragPlay","layoutViewToolsOnClick");
            mCoorContentRegion.setVisibility(View.INVISIBLE);
        }
    };

    private View.OnClickListener imgViewPlayOnClick = new View.OnClickListener() {
        public void onClick(final View v) {
            Log.d("FragPlay","imgViewPlayOnClick");
            if(chkReaded) {
                PlayGame(v);
            }
        }
    };

    private View.OnClickListener imgViewAutoPlayOnClick = new View.OnClickListener() {
        public void onClick(final View v) {
            Log.d("FragPlay","OnAutoPlayClickListener");
            if(chkReaded) {
                Log.d("FragPlay","OnAutoPlayClickListener->chkReaded");
                if(autoPlay == false){
                    Log.d("FragPlay","OnAutoPlayClickListener->auto=false--->true");
                    autoPlay = true;
                    mImgViewAuto.setImageResource(R.drawable.button_auto_active);
                }else{
                    Log.d("FragPlay","OnAutoPlayClickListener->auto=true--->false");
                    autoPlay = false;
                    mImgViewAuto.setImageResource(R.drawable.button_auto);
                }

                Log.d("FragPlay","autoPlay->"+autoPlay+",isPlaying->"+isPlaying);
                if(autoPlay == true && isPlaying == false) {
                    Log.d("FragPlay","OnAutoPlayClickListener->playing"+",mLives="+mLives);
                    isPlaying = true;
                    PlayGame(v);
                }
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
        if(mLives>0) {
            Log.d("FragPlay", "PlayGame");
//            ptlogMultiple = 1;
            if(isTheDateBeforeToday(iniUser.getDailyResetDate())){
                Log.w("check00-0",String.valueOf(mCoins));
                mCoins = mCoins + mSystemPreferences.getDailyReward().intValue();
                mDailyPlayTimes = 0;
                mDailyWinTimes = 0;
                Log.w("check00",String.valueOf(mCoins));
                updateUser(user,UpdateUserTimer.DailyReset.value);
            }

            if(isTheDateBeforeToday(iniUser.getSpecialTimeRewardGetDate())){
                if(isSpecialTime()) {
                    Log.w("check01-0", String.valueOf(mCoins));
                    mCoins = mCoins + mSystemPreferences.getSpecialTimeReward().intValue();
                    Log.w("check01", String.valueOf(mCoins));
                    updateUser(user, UpdateUserTimer.SpecialTimeReward.value);
                }
            }

            ((MainActivity) getActivity()).removeBottomNavListener();
            mImgViewScissors.setOnClickListener(null);
            mImgViewRock.setOnClickListener(null);
            mImgViewPaper.setOnClickListener(null);
            if(autoPlay == false){
                mImgViewAuto.setOnClickListener(null);
                if(!(autoPlay == true || isPlaying == true)) {
                    switch (v.getId()) {
                        case R.id.imgViewScissors:
                            mImgViewScissors.setImageResource(R.drawable.button_scissor_active);
                            break;
                        case R.id.imgViewRock:
                            mImgViewRock.setImageResource(R.drawable.button_rock_active);
                            break;
                        case R.id.imgViewPaper:
                            mImgViewPaper.setImageResource(R.drawable.button_paper_active);
                            break;
                    }
                }
            }

//            mCoins = Integer.valueOf(mTxtViewCoins.getText().toString());
            mScore = Integer.valueOf(mTxtViewScore.getText().toString());
            mLives = Integer.valueOf(mTxtViewLives.getText().toString());

            Log.d("mCoins,S,L", mCoins + "," + mScore + "," + mLives);
            ptlogMatchResult = 0;
            mTxtViewPlayCounter.setVisibility(View.VISIBLE);

//        countDownSound = new Intent(getActivity(),CountDownSoundService.class);
//        if(mPlaySound.equals("1")) {
//            getActivity().startService(countDownSound);
//        }

            new CountDownTimer(6000, 1000) {
                //mTxtViewCounter.setVisibility(v.VISIBLE );

                @Override
                public void onTick(long millisUntilFinished) {
                    //倒數秒數中要做的事
                    if (new SimpleDateFormat("s").format(millisUntilFinished).equals("5") && mPlaySound.equals("1")) {
                        MediaPlayer mpLose = MediaPlayer.create(getActivity(), R.raw.game_start_countdown);
                        mpLose.start();
                        mpLose.seekTo(0);
                    }
                    mTxtViewPlayCounter.setText(new SimpleDateFormat("s").format(millisUntilFinished));
                }

                @Override
                public void onFinish() {
                    //倒數完成後要做的事
//                if(mPlaySound.equals("1")) {
//                    getActivity().stopService(countDownSound);
//                }
                    mTxtViewPlayCounter.setVisibility(View.INVISIBLE);
                    int result = 0;
                    //Player
                    int iComPlay = (int) (Math.random() * 3 + 1);
                    if (iComPlay == 1) {
                        ptlogComputerInput = Input.Scissor.value;
                    } else if (iComPlay == 2) {
                        ptlogComputerInput = Input.Rock.value;
                    } else {
                        ptlogComputerInput = Input.Paper.value;
                    }

                    if(autoPlay == true || isPlaying == true) {//自動玩
                        int iUserPlay = (int) (Math.random() * 3 + 1);
                        if (iUserPlay == Input.Scissor.value) {//剪刀
                            ptlogUserInput = Input.Scissor.value;
                            mImgViewPlayer.setImageResource(R.drawable.img_card_scissor_red);
                            if (iComPlay == 1) {
                                result = R.string.text_tie;
                                ptlogMatchResult = MatchResult.Tie.value;
                            } else if (iComPlay == 2) {
                                result = R.string.text_lose;
                                ptlogMatchResult = MatchResult.Lose.value;
                            } else {
                                result = R.string.text_win;
                                ptlogMatchResult = MatchResult.Win.value;
                            }
                        } else if (iUserPlay == Input.Rock.value) {//石頭
                            ptlogUserInput = Input.Rock.value;
                            mImgViewPlayer.setImageResource(R.drawable.img_card_rock_red);
                            if (iComPlay == 1) {
                                result = R.string.text_win;
                                ptlogMatchResult = MatchResult.Win.value;
                            } else if (iComPlay == 2) {
                                result = R.string.text_tie;
                                ptlogMatchResult = MatchResult.Tie.value;
                            } else {
                                result = R.string.text_lose;
                                ptlogMatchResult = MatchResult.Lose.value;
                            }
                        } else {//布
                            ptlogUserInput = Input.Paper.value;
                            mImgViewPlayer.setImageResource(R.drawable.img_card_paper_red);
                            if (iComPlay == 1) {
                                result = R.string.text_lose;
                                ptlogMatchResult = MatchResult.Lose.value;
                            } else if (iComPlay == 2) {
                                result = R.string.text_win;
                                ptlogMatchResult = MatchResult.Win.value;
                            } else {
                                result = R.string.text_tie;
                                ptlogMatchResult = MatchResult.Tie.value;
                            }
                        }

                    }else{//非自動
                            switch (v.getId()) {
                                case R.id.imgViewScissors:
                                    // do something
                                    ptlogUserInput = Input.Scissor.value;
                                    mImgViewPlayer.setImageResource(R.drawable.img_card_scissor_red);
                                    if (iComPlay == 1) {
                                        result = R.string.text_tie;
                                        ptlogMatchResult = MatchResult.Tie.value;
                                    } else if (iComPlay == 2) {
                                        result = R.string.text_lose;
                                        ptlogMatchResult = MatchResult.Lose.value;
                                    } else {
                                        result = R.string.text_win;
                                        ptlogMatchResult = MatchResult.Win.value;
                                    }
                                    break;
                                case R.id.imgViewRock:
                                    ptlogUserInput = Input.Rock.value;
                                    mImgViewPlayer.setImageResource(R.drawable.img_card_rock_red);
                                    if (iComPlay == 1) {
                                        result = R.string.text_win;
                                        ptlogMatchResult = MatchResult.Win.value;
                                    } else if (iComPlay == 2) {
                                        result = R.string.text_tie;
                                        ptlogMatchResult = MatchResult.Tie.value;
                                    } else {
                                        result = R.string.text_lose;
                                        ptlogMatchResult = MatchResult.Lose.value;
                                    }
                                    break;
                                case R.id.imgViewPaper:
                                    ptlogUserInput = Input.Paper.value;
                                    mImgViewPlayer.setImageResource(R.drawable.img_card_paper_red);
                                    if (iComPlay == 1) {
                                        result = R.string.text_lose;
                                        ptlogMatchResult = MatchResult.Lose.value;
                                    } else if (iComPlay == 2) {
                                        result = R.string.text_win;
                                        ptlogMatchResult = MatchResult.Win.value;
                                    } else {
                                        result = R.string.text_tie;
                                        ptlogMatchResult = MatchResult.Tie.value;
                                    }
                                    break;
                            }

                    }
                    if (iComPlay == 1) {
                        mImgViewNpc.setImageResource(R.drawable.img_card_scissor_black);
                    } else if (iComPlay == 2) {
                        mImgViewNpc.setImageResource(R.drawable.img_card_rock_black);
                    } else {
                        mImgViewNpc.setImageResource(R.drawable.img_card_paper_black);
                    }
                    int mr = ptlogMatchResult;
                    int getScore = 0, getLives = 0;
                    if(mUseTool == ProductType.Dice.value){
                        mMultiple = (int) (Math.random() * 6 + 1);
                        Log.w("useTool","Dice->"+mMultiple);
                    }
                    switch (ptlogMatchResult) {

                        case 1: //輸
                            Log.w("useTool","lose->"+mScore+","+(10*mMultiple));
                            mScore += (10 * mMultiple);
                            mLives -= 1;
                            getScore = (10 * mMultiple);
                            getLives = -1;
                            if (mPlaySound.equals("1")) {
                                MediaPlayer mpLose = MediaPlayer.create(getActivity(), R.raw.lose);
                                mpLose.start();
                                mpLose.seekTo(0);
                            }
                            break;
                        case 2: //贏
                            Log.w("useTool","win->"+mScore+","+(100*mMultiple));
                            mScore += (100 * mMultiple);
                            mLives -= 1;
                            getScore = (100 * mMultiple);
                            getLives = -1;
                            if (mPlaySound.equals("1")) {
                                MediaPlayer mpWin = MediaPlayer.create(getActivity(), R.raw.win);
                                mpWin.start();
                                mpWin.seekTo(0);
                            }
                            break;
                        default: //平手
                            if (mPlaySound.equals("1")) {
                                MediaPlayer mpTie = MediaPlayer.create(getActivity(), R.raw.excitement);
                                mpTie.start();
                                mpTie.seekTo(0);
                            }
                            break;
                    }


                    ptlogScore = getScore;
                    ltlogTransaction = getLives;
                    ltlogType = LifeTransactionType.PlayGame.value;

                    Log.d("Result:", String.valueOf(result) + "," + mScore + "," + mLives);
                    ptlogTotalScore = mScore;

                    if(ptlogMatchResult == MatchResult.Win.value)
                    {
                        mDailyWinTimes += 1;
                        mDailyWinTimesFlag = true;
                        if(ptlogUserInput == Input.Scissor.value){
                            mWinWithScissor += 1;
                            mWinWithScissorFlag = true;
                        }else if(ptlogUserInput == Input.Rock.value){
                            mWinWithRock += 1;
                            mWinWithRockFlag = true;
                        }else if(ptlogUserInput == Input.Paper.value){
                            mWinWithPaper += 1;
                            mWinWithPaperFlag = true;
                        }
                    }
                    mDailyPlayTimes += 1;

                    ptlogMultiple = mMultiple;
                    if(mUseTool == ProductType.Dice.value) {
                        mMultiple = 1;
                        //mUseTool = 0;
                        mDice -= 1;
                    }
//                    mTxtViewResult.setVisibility(View.VISIBLE);
//                    mTxtViewResult.setText(result);

                    final CustomDialog dialog = new CustomDialog(v.getContext(), getString(result), String.valueOf(getLives), String.valueOf(getScore), new CustomDialog.ICustomDialogEventListener() {
                        @Override
                        public void customDialogEvent(int id) {
                        }
                    }, R.style.dialog);
                    dialog.show();
                    dialog.getWindow().setLayout(1200, 650);

//                mTxtViewCoins.setText(String.valueOf(Integer.valueOf(mTxtViewCoins.getText().toString())+10));

                    updateUser(user, UpdateUserTimer.PlayGame.value);
                    new CountDownTimer(3000, 1000) {
                        //mTxtViewCounter.setVisibility(v.VISIBLE );
                        @Override
                        public void onTick(long millisUntilFinished) {
                            //倒數秒數中要做的事
                            if (new SimpleDateFormat("s").format(millisUntilFinished).equals("1") && (autoPlay == true || isPlaying == true)) {
                                dialog.cancel();
                            }
                        }

                        @Override
                        public void onFinish() {
//                            ((MainActivity)getActivity()).recallPlayFragment();

                            mImgViewNpc.setImageResource(R.drawable.img_cardback);
                            mImgViewPlayer.setImageResource(R.drawable.img_cardback);
                            if(autoPlay == false) {
                                if(!(autoPlay == true || isPlaying == true)) {
                                    switch (v.getId()) {
                                        case R.id.imgViewScissors:
                                            mImgViewScissors.setImageResource(R.drawable.button_scissor);
                                            break;
                                        case R.id.imgViewRock:
                                            mImgViewRock.setImageResource(R.drawable.button_rock);
                                            break;
                                        case R.id.imgViewPaper:
                                            mImgViewPaper.setImageResource(R.drawable.button_paper);
                                            break;
                                    }
                                }
                                mImgViewScissors.setOnClickListener(imgViewPlayOnClick);
                                mImgViewRock.setOnClickListener(imgViewPlayOnClick);
                                mImgViewPaper.setOnClickListener(imgViewPlayOnClick);
                                mImgViewAuto.setOnClickListener(imgViewAutoPlayOnClick);

                                isPlaying = false;
                                ((MainActivity) getActivity()).setBottomNavListener();
                            }else{
                                PlayGame(v);
                            }

                        }
                    }.start();
                }
            }.start();

        }else{//若無 Life
            if(autoPlay == true){
                autoPlay = false;
                mImgViewScissors.setOnClickListener(imgViewPlayOnClick);
                mImgViewRock.setOnClickListener(imgViewPlayOnClick);
                mImgViewPaper.setOnClickListener(imgViewPlayOnClick);
                ((MainActivity) getActivity()).setBottomNavListener();
                mImgViewAuto.setImageResource(R.drawable.button_auto);
            }
        }
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

    /*先找到目前的board，再Update*/
    private void updateBoard(final int when) {
        final Long rightNow = GetRightNow();
        mQueryDatabase = FirebaseDatabase.getInstance().getReference("score-boards");
        Query queryRef = mQueryDatabase.orderByChild("endDateInterval").startAt(rightNow);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("board",rightNow.toString());
                for (DataSnapshot boardSnapshot: snapshot.getChildren()) {
                    Board b = boardSnapshot.getValue(Board.class);
                    if(rightNow >= b.getStartDateInterval()) {
                        Log.e("Get Data", boardSnapshot.getKey() + "," + b.getStartDateInterval() + "," + b.getEndDateInterval());
                        updateUserScore(b.getId(),when);
//                        DatabaseReference wRef = FirebaseDatabase.getInstance().getReference("score-boards/" + boardSnapshot.getKey() + "/scores/" + fUser.getUid());
//                        Map board = new HashMap();
//                        board.put("displayName", fUser.getDisplayName());
//                        Log.w("displayName",fUser.getDisplayName());
//                        board.put("score", mScore);
//                        wRef.setValue(board);
//                        mTxtViewScore.setText(String.valueOf(u.getScore()));
//                        chkReaded = true;
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

    private void updateUserScore(final String boardId, final int when) {
        Log.d("☆Firebase", "readScores->"+boardId);

        final DatabaseReference updateUserScoreDBref = FirebaseDatabase.getInstance().getReference("scores/" + boardId+ "/" + user.getUid());

        updateUserScoreDBref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                Log.w("updateUserScore",snapshot.toString()+","+snapshot.getChildrenCount());
                Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                if(snapshot.getChildrenCount() == 0){
                    Score s = new Score();
                    s.setDisplayName(user.getDisplayName());
                    s.setScore(0L);
                    updateUserScoreDBref.setValue(s);
                    mTxtViewScore.setText("0");
                }else{
                    Score s = snapshot.getValue(Score.class);
                    Map newScore = new HashMap();
                    if(when == UpdateUserTimer.OnIni.value)
                    {
                        mScore = s.getScore().intValue();
                    }
                    newScore.put("score", Long.valueOf(mScore));
                    updateUserScoreDBref.updateChildren(newScore);
                    mTxtViewScore.setText(String.valueOf(mScore));
                }
                chkReaded = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("☆firebase failed: ", databaseError.getMessage());
            }

        });
    }

    private void readBoard(final FirebaseUser fUser) {
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

    private void readScores(final String boardID) {
        final Long rightNow = GetRightNow();
        mQueryDatabase = FirebaseDatabase.getInstance().getReference("scores/"+boardID+"/"+user.getUid());
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

    private void readSystemPreferences(final Long lifeCounter, final Long userDailyResetDate) {
        Log.d("☆Firebase(playFrag)","readSystemPreferences");
        mReadDatabase = FirebaseDatabase.getInstance().getReference("systemPreferences");

        mReadDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
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

                if(isTheDateBeforeToday(userDailyResetDate)){
                    Log.w("check00-0",String.valueOf(mCoins));
                    mCoins = mCoins + mSystemPreferences.getDailyReward().intValue();
                    mDailyPlayTimes = 0;
                    mDailyWinTimes = 0;
                    Log.w("check00",String.valueOf(mCoins));
                    updateUser(user,UpdateUserTimer.DailyReset.value);
                }

                if(isTheDateBeforeToday(iniUser.getSpecialTimeRewardGetDate())){
                    if(isSpecialTime()) {
                        Log.w("check01-0", String.valueOf(mCoins));
                        mCoins = mCoins + mSystemPreferences.getSpecialTimeReward().intValue();
                        Log.w("check01", String.valueOf(mCoins));
                        updateUser(user, UpdateUserTimer.SpecialTimeReward.value);
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("☆firebase failed(pF) " , databaseError.getMessage());
            }

        });

    }




    private void readUser(final int when) {
        Log.d("☆Firebase", "readUser->"+String.valueOf(UpdateUserTimer.values()[when-1]));
        mReadDatabase = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());

        mReadDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                User u = snapshot.getValue(User.class);
                if(u != null){
                    //以下這段也可以用！
                    //==============================================================================
                    Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();

                    for (Object key : map.keySet()) {
                        Log.w("firebase", key + " : " + map.get(key) + map.get(key).getClass());
                    }
                    //==============================================================================

                    iniUser = u;
                    Log.d("firebase",String.valueOf(u.getLives()));

                    mTxtViewCoins.setText(String.valueOf(u.getCoins()));
//                    mTxtViewScore.setText(String.valueOf(u.getScore()));
                    mTxtViewLives.setText(String.valueOf(u.getLives()));
                    mLives = u.getLives().intValue();
                    mWinWithScissor = u.getWinWithScissor().intValue();
                    mWinWithRock = u.getWinWithRock().intValue();
                    mWinWithPaper = u.getWinWithPaper().intValue();
                    mDailyPlayTimes = u.getDailyPlayTimes().intValue();
                    mDailyWinTimes = u.getDailyWinTimes().intValue();
                    mCoins = u.getCoins().intValue();
                    mDice = u.getDice().intValue();

                    if(getActivity() != null) {
                        mToolsAdapter = new ToolsAdapter(getActivity().getApplicationContext(), iniUser, PlayFragment.this);
                        mListViewTools.setAdapter(mToolsAdapter);
                        mImgViewTools.setOnClickListener(imgViewToolsOnClick);
                    }
//                    updateBoard(user);
//                    chkReaded = true;

                    if(when == UpdateUserTimer.OnIni.value) {
                        Log.w("DailyResetDate",String.valueOf(u.getDailyResetDate()));
                        readSystemPreferences(u.getLifeCounter()/1000,u.getDailyResetDate());
                        Log.w("getLifeCounter",u.getLifeCounter()/1000+",");
                        updateBoard(UpdateUserTimer.OnIni.value);
//                        mTxtViewScore.setText(String.valueOf(0));
                    }

                }else{
                    readUser(UpdateUserTimer.OnIni.value);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("☆firebase failed: ", databaseError.getMessage());
            }

        });
    }

    private void updateUser(final FirebaseUser fUser, final int when) {
        Log.d("☆Firebase",String.valueOf(UpdateUserTimer.values()[when-1]));
        mWriteDatabase = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());

        mWriteDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                User user = new User();

                user.setCoins(Long.valueOf(mCoins));
//                user.setScore(Long.valueOf(mScore));
//                Map<String, Object> userValues = user.toMap();
//                Map<String, Object> childUpdates = new HashMap<>();
//                childUpdates.put("/posts/" + key, postValues);
//                childUpdates.put("/user-posts/" + userId + "/" + key, postValues);
//                mWriteDatabase.updateChildren(userValues);
//                mWriteDatabase.setValue(user);
                long[] mTotalDice= {0L,0L,0L,0L,0L,0L};

                Map newUserData = new HashMap();
                if(when == UpdateUserTimer.PlayGame.value)
                {
                    if(mDailyPlayTimes/mSystemPreferences.getPlayTimesPerDice() == 1 &&
                            mDailyPlayTimes%mSystemPreferences.getPlayTimesPerDice() == 0){
                        mDice+=1;
                        mTotalDice[0] = mDice;
                        Log.w("totalDice0",String.valueOf(mDice));
                    }
                    if(mDailyWinTimes/mSystemPreferences.getWinTimesGetOneDiceFirst() == 1 &&
                            mDailyWinTimes%mSystemPreferences.getWinTimesGetOneDiceFirst() == 0 && mDailyWinTimesFlag){
                        mDice+=1;
                        mTotalDice[1] = mDice;
                        Log.w("totalDice1",String.valueOf(mDice));
                    }
                    if(mDailyWinTimes/mSystemPreferences.getWinTimesGetOneDiceSecond() == 1 &&
                            mDailyWinTimes%mSystemPreferences.getWinTimesGetOneDiceSecond() == 0 && mDailyWinTimesFlag){
                        mDice+=1;
                        mTotalDice[2] = mDice;
                        Log.w("totalDice2",String.valueOf(mDice));
                    }
                    if(mWinWithScissor/mSystemPreferences.getWinWithMatchResult() == 1 &&
                            mWinWithScissor%mSystemPreferences.getWinWithMatchResult() == 0 && mWinWithScissorFlag){
                        mDice+=1;
                        mTotalDice[3] = mDice;
                        Log.w("totalDice3",String.valueOf(mDice));
                    }
                    if(mWinWithRock/mSystemPreferences.getWinWithMatchResult() == 1 &&
                            mWinWithRock%mSystemPreferences.getWinWithMatchResult() == 0 && mWinWithRockFlag){
                        mDice+=1;
                        mTotalDice[4] = mDice;
                        Log.w("totalDice4",String.valueOf(mDice));
                    }
                    if(mWinWithPaper/mSystemPreferences.getWinWithMatchResult() == 1 &&
                            mWinWithPaper%mSystemPreferences.getWinWithMatchResult() == 0 && mWinWithPaperFlag){
                        mDice+=1;
                        mTotalDice[5] = mDice;
                        Log.w("totalDice5",String.valueOf(mDice));
                    }
                    Log.w("totalDice",mTotalDice[0]+","+mTotalDice[1]+","+mTotalDice[2]+","+mTotalDice[3]+","+mTotalDice[4]+","+mTotalDice[5]);

                    newUserData.put("lives", Long.valueOf(mLives));
                    newUserData.put("dailyPlayTimes",Long.valueOf(mDailyPlayTimes));
                    newUserData.put("dailyWinTimes",Long.valueOf(mDailyWinTimes));
                    newUserData.put("winWithScissor",Long.valueOf(mWinWithScissor));
                    newUserData.put("winWithRock",Long.valueOf(mWinWithRock));
                    newUserData.put("winWithPaper",Long.valueOf(mWinWithPaper));
                    newUserData.put("dice", Long.valueOf(mDice));
//                    newUserData.put("score", Long.valueOf(mScore));
                }else if(when == UpdateUserTimer.FiveMinutesTimer.value) {
                    newUserData.put("lives", Long.valueOf(mLives));
                    updateTimer(fUser.getUid());
                    Log.w("LifeCounter",Long.valueOf(mLives)+",");
                }
//                else if(when == UpdateUserTimer.GetNewIntervalDate.value){
//                    newUserData.put("startDateInterval", Long.valueOf(mStartDateInterval));
//                    newUserData.put("endDateInterval", Long.valueOf(mEndDateInterval));
////                    iniUser.setStartDateInterval(mStartDateInterval);
//                }
                else if(when == UpdateUserTimer.DailyReset.value){
//                    mCoins = u.getCoins().intValue() + mSystemPreferences.getDailyReward().intValue();
//                    mDailyPlayTimes = 0;
//                    mDailyWinTimes = 0;
                    newUserData.put("dailyPlayTimes", Long.valueOf(mDailyPlayTimes));
                    newUserData.put("dailyWinTimes", Long.valueOf(mDailyWinTimes));
                    newUserData.put("dailyResetDate", ServerValue.TIMESTAMP);
                    newUserData.put("coins", Long.valueOf(mCoins));
                    Log.w("DailyReset",mCoins+",");
                    mTxtViewCoins.setText(String.valueOf(mCoins));
//                    readUser(UpdateUserTimer.Normal.value);
                }else if(when == UpdateUserTimer.SpecialTimeReward.value) {
                    newUserData.put("coins", Long.valueOf(mCoins));
                    newUserData.put("specialTimeRewardGetDate",ServerValue.TIMESTAMP);
                    mTxtViewCoins.setText(String.valueOf(mCoins));
                }

                mWriteDatabase.updateChildren(newUserData);

                if(when != UpdateUserTimer.GetNewIntervalDate.value) {//需要寫log
                    Long rightNow = GetRightNow();
                    if (when == UpdateUserTimer.PlayGame.value) {
                        DatabaseReference mWriteTranLogDatabase = FirebaseDatabase.getInstance().getReference("users/" + fUser.getUid() + "/transactionLogPlay/" + rightNow);
                        Map transactionLogPlay = new HashMap();
                        transactionLogPlay.put("multiple", ptlogMultiple);
                        transactionLogPlay.put("matchResult", ptlogMatchResult);
                        transactionLogPlay.put("score", ptlogScore);
                        transactionLogPlay.put("userInput", ptlogUserInput);
                        transactionLogPlay.put("computerInput", ptlogComputerInput);
                        transactionLogPlay.put("totalScore", ptlogTotalScore);
                        mWriteTranLogDatabase.setValue(transactionLogPlay);

                        if(mDailyPlayTimes/mSystemPreferences.getPlayTimesPerDice() == 1 &&
                                mDailyPlayTimes%mSystemPreferences.getPlayTimesPerDice() == 0){
                            DatabaseReference drLog0 = FirebaseDatabase.getInstance().getReference("users/" + fUser.getUid() + "/transactionLogTool/" + rightNow);
                            Map transactionLogTool = new HashMap();
                            transactionLogTool.put("type", ToolTransactionType.PlayTimesPerDice.value);
                            transactionLogTool.put("product", ProductType.Dice.value);
                            transactionLogTool.put("transaction", 1);
                            transactionLogTool.put("total", mTotalDice[0]);
                            drLog0.setValue(transactionLogTool);
                        }
                        if(mDailyWinTimes/mSystemPreferences.getWinTimesGetOneDiceFirst() == 1 &&
                                mDailyWinTimes%mSystemPreferences.getWinTimesGetOneDiceFirst() == 0 && mDailyWinTimesFlag){
                            DatabaseReference drLog0 = FirebaseDatabase.getInstance().getReference("users/" + fUser.getUid() + "/transactionLogTool/" + rightNow);
                            Map transactionLogTool = new HashMap();
                            transactionLogTool.put("type", ToolTransactionType.WinTimesGetOneDiceFirst.value);
                            transactionLogTool.put("product", ProductType.Dice.value);
                            transactionLogTool.put("transaction", 1);
                            transactionLogTool.put("total", mTotalDice[1]);
                            drLog0.setValue(transactionLogTool);
                            mDailyWinTimesFlag = false;
                        }
                        if(mDailyWinTimes/mSystemPreferences.getWinTimesGetOneDiceSecond() == 1 &&
                                mDailyWinTimes%mSystemPreferences.getWinTimesGetOneDiceSecond() == 0 && mDailyWinTimesFlag){
                            DatabaseReference drLog0 = FirebaseDatabase.getInstance().getReference("users/" + fUser.getUid() + "/transactionLogTool/" + rightNow);
                            Map transactionLogTool = new HashMap();
                            transactionLogTool.put("type", ToolTransactionType.WinTimesGetOneDiceSecond.value);
                            transactionLogTool.put("product", ProductType.Dice.value);
                            transactionLogTool.put("transaction", 1);
                            transactionLogTool.put("total", mTotalDice[2]);
                            drLog0.setValue(transactionLogTool);
                            mDailyWinTimesFlag = false;
                        }
                        if(mWinWithScissor/mSystemPreferences.getWinWithMatchResult() > 0 &&
                                mWinWithScissor%mSystemPreferences.getWinWithMatchResult() == 0 && mWinWithScissorFlag){
                            DatabaseReference drLog0 = FirebaseDatabase.getInstance().getReference("users/" + fUser.getUid() + "/transactionLogTool/" + rightNow);
                            Map transactionLogTool = new HashMap();
                            transactionLogTool.put("type", ToolTransactionType.WinWithMatchResult.value);
                            transactionLogTool.put("product", ProductType.Dice.value);
                            transactionLogTool.put("transaction", 1);
                            transactionLogTool.put("total", mTotalDice[3]);
                            drLog0.setValue(transactionLogTool);
                            mWinWithScissorFlag = false;
                        }
                        if(mWinWithRock/mSystemPreferences.getWinWithMatchResult() > 0 &&
                                mWinWithRock%mSystemPreferences.getWinWithMatchResult() == 0 && mWinWithRockFlag){
                            DatabaseReference drLog0 = FirebaseDatabase.getInstance().getReference("users/" + fUser.getUid() + "/transactionLogTool/" + rightNow);
                            Map transactionLogTool = new HashMap();
                            transactionLogTool.put("type", ToolTransactionType.WinWithMatchResult.value);
                            transactionLogTool.put("product", ProductType.Dice.value);
                            transactionLogTool.put("transaction", 1);
                            transactionLogTool.put("total", mTotalDice[4]);
                            drLog0.setValue(transactionLogTool);
                            mWinWithRockFlag = false;
                        }
                        if(mWinWithPaper/mSystemPreferences.getWinWithMatchResult() > 0 &&
                                mWinWithPaper%mSystemPreferences.getWinWithMatchResult() == 0 && mWinWithPaperFlag) {
                            DatabaseReference drLog0 = FirebaseDatabase.getInstance().getReference("users/" + fUser.getUid() + "/transactionLogTool/" + rightNow);
                            Map transactionLogTool = new HashMap();
                            transactionLogTool.put("type", ToolTransactionType.WinWithMatchResult.value);
                            transactionLogTool.put("product", ProductType.Dice.value);
                            transactionLogTool.put("transaction", 1);
                            transactionLogTool.put("total", mTotalDice[5]);
                            drLog0.setValue(transactionLogTool);
                            mWinWithPaperFlag = false;
                        }
                        if(mUseTool>0){
                            DatabaseReference drLog0 = FirebaseDatabase.getInstance().getReference("users/" + fUser.getUid() + "/transactionLogTool/" + rightNow);
                            Map transactionLogTool = new HashMap();
                            transactionLogTool.put("type", ToolTransactionType.PlayGame.value);
                            transactionLogTool.put("product", mUseTool);
                            transactionLogTool.put("transaction", -1);
                            transactionLogTool.put("total", mDice);
                            drLog0.setValue(transactionLogTool);
                            mUseTool = 0;
                        }
                    }

                    if (when == UpdateUserTimer.PlayGame.value || when == UpdateUserTimer.FiveMinutesTimer.value) {
                        DatabaseReference drLog0 = FirebaseDatabase.getInstance().getReference("users/" + fUser.getUid() + "/transactionLogLife/" + rightNow);
                        Map transactionLogLife = new HashMap();
                        transactionLogLife.put("type", ltlogType);
                        transactionLogLife.put("transaction", ltlogTransaction);
                        transactionLogLife.put("totalLives", mLives);
                        drLog0.setValue(transactionLogLife);
                    }
                    if (when == UpdateUserTimer.DailyReset.value){
                        DatabaseReference drLog0 = FirebaseDatabase.getInstance().getReference("users/" + fUser.getUid() + "/transactionLogCoin/" + rightNow);
                        Map transactionLogCoin = new HashMap();
                        transactionLogCoin.put( "type", CoinTransactionType.DailyReward.value);
                        transactionLogCoin.put( "transaction", mSystemPreferences.getDailyReward());
                        transactionLogCoin.put( "totalCoins", mCoins);
                        drLog0.setValue(transactionLogCoin);
                    }

                    if (when == UpdateUserTimer.SpecialTimeReward.value){
                        DatabaseReference drLog0 = FirebaseDatabase.getInstance().getReference("users/" + fUser.getUid() + "/transactionLogCoin/" + rightNow);
                        Map transactionLogCoin = new HashMap();
                        transactionLogCoin.put( "type", CoinTransactionType.SpecialTimeReward.value);
                        transactionLogCoin.put( "transaction", mSystemPreferences.getSpecialTimeReward());
                        transactionLogCoin.put( "totalCoins", mCoins);
                        drLog0.setValue(transactionLogCoin);
                    }

                    if (when == UpdateUserTimer.PlayGame.value) {
                        mTxtViewScore.setText(String.valueOf(mScore));
                    }
                    mTxtViewLives.setText(String.valueOf(mLives));
                }

                if (when == UpdateUserTimer.PlayGame.value) {
                    updateBoard(UpdateUserTimer.PlayGame.value);
                    updateTimer(fUser.getUid());
                }

                readUser(UpdateUserTimer.Normal.value);
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


    public enum ProductType {
        Life(1),
        Dice(2),
        IronFirst(3),
        Timer(4),
        MindControl(5),
        GoldenHand(6),
        Victory(7);

        private int value;

        private ProductType(int value) {
            this.value = value;
        }
        public int ProductType() {
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
        GetNewIntervalDate(5),
        OnIni(6),
        DailyReset(7),
        SpecialTimeReward(8),
        Normal(9);

        private int value;

        private UpdateUserTimer(int value) {
            this.value = value;
        }
        public int UpdateUserTimer() {
            return this.value;
        }
    }

    public enum LifeTransactionType {
        SignUpReward(1),
        PlayGame(2),
        Purchase(3),
        FiveMinutesTimer(4);

        private int value;

        private LifeTransactionType(int value) {
            this.value = value;
        }
        public int LifeTransactionLife() {
            return this.value;
        }
    }

    public enum CoinTransactionType {
        DailyReward(1),
        BuyLife(2),
        SpecialTimeReward(3),
        BuyTool(4);

        private int value;

        private CoinTransactionType(int value) {
            this.value = value;
        }
        public int CoinTransactionType() {
            return this.value;
        }
    }

    public enum ToolTransactionType {
        Purchase(1),
        PlayTimesPerDice(2),
        WinTimesGetOneDiceFirst(3),
        WinTimesGetOneDiceSecond(4),
        WinWithMatchResult(5),
        PlayGame(6);

        private int value;

        private ToolTransactionType(int value) {
            this.value = value;
        }
        public int ToolTransactionType() {
            return this.value;
        }
    }

    private Long GetRightNow(){
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        return tsLong;
    }
    private boolean isTheDateBeforeToday(Long userDate){
        boolean isTheDateBeforeToday = false;

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis( userDate );
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH)+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String strUserDate = String.valueOf(year)+String.valueOf(String.format("%02d", month))+String.valueOf(String.format("%02d", day));
//        Log.w("isTheDateBeforeToday",year+","+month+","+day);

        Calendar calR = Calendar.getInstance();
        calR.setTimeInMillis(System.currentTimeMillis());
        int yearR = calR.get(Calendar.YEAR);
        int monthR = calR.get(Calendar.MONTH)+1;
        int dayR = calR.get(Calendar.DAY_OF_MONTH);
        String strRightDate = String.valueOf(yearR)+String.valueOf(String.format("%02d", monthR))+String.valueOf(String.format("%02d", dayR));
//        Log.w("isTheDateBeforeToday",yearR+","+monthR+","+dayR);
        if( Integer.valueOf(strUserDate) < Integer.valueOf(strRightDate) ){
            isTheDateBeforeToday = true;
        }
        Log.w("isTheDateBeforeToday",userDate+","+isTheDateBeforeToday+",rightNow:"+strRightDate+","+strUserDate);
        return isTheDateBeforeToday;
    }

    private boolean isSpecialTime(){
        boolean isSpecialTime = false;
        Long mSpecialTimeStartDateInterval = mSystemPreferences.getSpecialTimeStartDateInterval();
        Long mSpecialTimeEndDateInterval = mSystemPreferences.getSpecialTimeEndDateInterval();

        Calendar startCal = Calendar.getInstance();
        startCal.setTimeInMillis( mSpecialTimeStartDateInterval );
//        int startYear = startCal.get(Calendar.YEAR);
//        int startMonth = startCal.get(Calendar.MONTH)+1;
//        int startDay = startCal.get(Calendar.DAY_OF_MONTH);
        int startHh = startCal.get(Calendar.HOUR_OF_DAY);
        int startSs = startCal.get(Calendar.MINUTE);
//
//        String strStartDate = String.valueOf(startYear)+String.valueOf(String.format("%02d", startMonth))+String.valueOf(String.format("%02d", startDay))
//                +String.valueOf(String.format("%02d", startHh))+String.valueOf(String.format("%02d", startSs));
        String startHHSS = String.valueOf(String.format("%02d", startHh))+String.valueOf(String.format("%02d", startSs));
//        Log.w("isSpecialTime",mSpecialTimeStartDateInterval+","+strStartDate+","+startHHSS);

        Calendar endCal = Calendar.getInstance();
        endCal.setTimeInMillis( mSpecialTimeEndDateInterval );
//        int endYear = endCal.get(Calendar.YEAR);
//        int endMonth = endCal.get(Calendar.MONTH)+1;
//        int endDay = endCal.get(Calendar.DAY_OF_MONTH);
        int endHh = endCal.get(Calendar.HOUR_OF_DAY);
        int endSs = endCal.get(Calendar.MINUTE);

//        String strEndDate = String.valueOf(endYear)+String.valueOf(String.format("%02d", endMonth))+String.valueOf(String.format("%02d", endDay))
//                +String.valueOf(String.format("%02d", endHh))+String.valueOf(String.format("%02d", endSs));
        String endHHSS = String.valueOf(String.format("%02d", endHh))+String.valueOf(String.format("%02d", endSs));
//        Log.w("isSpecialTime",mSpecialTimeEndDateInterval+","+strEndDate+","+endHHSS);

        Long rightNow = System.currentTimeMillis();
        Calendar curCal = Calendar.getInstance();
        curCal.setTimeInMillis( rightNow );
//        int curYear = curCal.get(Calendar.YEAR);
//        int curMonth = curCal.get(Calendar.MONTH)+1;
//        int curDay = curCal.get(Calendar.DAY_OF_MONTH);
        int curHh = curCal.get(Calendar.HOUR_OF_DAY);
        int curSs = curCal.get(Calendar.MINUTE);

//        String strCurDate = String.valueOf(curYear)+String.valueOf(String.format("%02d", curMonth))+String.valueOf(String.format("%02d", curDay))
//                +String.valueOf(String.format("%02d", curHh))+String.valueOf(String.format("%02d", curSs));
        String curHHSS = String.valueOf(String.format("%02d", curHh))+String.valueOf(String.format("%02d", curSs));
//        Log.w("isSpecialTime",rightNow+","+strCurDate+","+curHHSS);

        if(Integer.valueOf(curHHSS) >= Integer.valueOf(startHHSS) && Integer.valueOf(curHHSS) <= Integer.valueOf(endHHSS))
        {
            isSpecialTime = true;
        }
        Log.w("isSpecialTime",isSpecialTime+",");

        return isSpecialTime;
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
                        ltlogType = LifeTransactionType.FiveMinutesTimer.value;
                        updateUser(user,UpdateUserTimer.FiveMinutesTimer.value);
                    }
            }
        }
    };

    private void CreateTimer(Long lifeCounter){
        Log.d("CreateTimer",String.valueOf(lifeCounter));
        //(GetRightNow()-lifeCounter)/300=
        //宣告Timer
        if(timer01 != null){
            timer01.purge();
            timer01.cancel();
            timer01 = null;
        }
        timer01 =new Timer();
        CreateTimerTask();
        //設定Timer(task為執行內容，0代表立刻開始,間格1秒執行一次)
        Log.w("CreateTimer",isFirstCreatTimer+",");
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
                    ltlogType = LifeTransactionType.FiveMinutesTimer.value;
                    updateUser(user,UpdateUserTimer.FiveMinutesTimer.value);
                }
                tsec = (int)countSec;
                Log.d("firstCreatTimer","rightNow:"+rightNow+",lifeCounter:"+lifeCounter+",countSec:"+countSec+",getLife:"+getLife+","+mSystemPreferences.getCounterSec());
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
                    //如果startflag是true則每秒tsec-1
                    tsec--;
                    Message message = new Message();
                    //傳送訊息1
                    message.what =1;
                    handler.sendMessage(message);
                }
            }
        };
    }

    public void useTool(final String toolName, final String toolAmount){
        Log.w("useTool","test");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
// Add the buttons
        builder.setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                if(Integer.valueOf(toolAmount) > 0) {
                    mCoorContentRegion.setVisibility(View.INVISIBLE);
                    if (toolName.equals("Dice")) {
                        mUseTool = ProductType.Dice.value;
                    }
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
// Set other dialog properties

//        builder.setTitle("Title");
        if(Integer.valueOf(toolAmount) > 0) {
            builder.setMessage("使用道具 " + toolName + " ?");
        }else{
            builder.setMessage( toolName + "  不足!");
        }
// Create the AlertDialog
        AlertDialog dialog = builder.create();

        dialog.show();

    }

    public void setIsFirstCreatTimer(){
        isFirstCreatTimer = true;
    }


}//程式結尾
