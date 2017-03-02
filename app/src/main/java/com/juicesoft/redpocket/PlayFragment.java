package com.juicesoft.redpocket;

import android.content.Context;
import android.content.DialogInterface;
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
import com.juicesoft.redpocket.Data.Tool;
import com.juicesoft.redpocket.Data.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


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

    private ListView mListViewTools;
    private ListAdapter mToolsAdapter;
    private static int mUseTool = 0, mMultiple = 1 ;

    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private User iniUser;
    private DatabaseReference mWriteDatabase, mReadDatabase, mQueryDatabase;

    private ImageView mImgViewScissors, mImgViewRock, mImgViewPaper, mImgViewAuto, mImgViewPlayer, mImgViewNpc, mImgViewTools;//, mAnimationNpc, mAnimationPlayer;
    private ImageView mImgViewToolUsed_00, mImgViewToolUsed_01, mImgViewToolUsed_02, mImgViewToolUsed_03, mImgViewToolUsed_04;
    private TextView mTxtViewResult, mTxtViewCounter, mTxtViewCoins, mTxtViewScore, mTxtViewLives, mTxtViewPlayCounter, mTxtViewUpperLives;
    private RelativeLayout mCoorContentRegion, mCenterRegion, mCoorContentRegionAmin;
    private int mCoins, mDice, mLives, mWinWithPaper, mWinWithRock, mWinWithScissor, mDailyPlayTimes, mDailyWinTimes;
    private int mDiceTmp;
    private boolean mWinWithPaperFlag = false, mWinWithRockFlag = false, mWinWithScissorFlag = false, mDailyWinTimesFlag = false;
    private static int mScore;
    private int ptlogMultiple, ptlogMatchResult, ptlogScore, ptlogUserInput, ptlogComputerInput, ptlogTotalScore;
    private int ltlogType, ltlogTransaction;
    private static SystemPreferences mSystemPreferences;
    private static long mStartDateInterval, mEndDateInterval;
    private static String mScoreBoardId ;
    private GifView mGifView, mGifViewPlayer, mGifViewNpc;

    private static Tool[] tools ;
    private static PlayFragment instance;

    private int mColor;

    private boolean chkReaded = false, autoPlay = false, isPlaying = false;
    private static boolean isFirstCreatTimer = true;

    private OnFragmentInteractionListener mListener;


    public static PlayFragment newInstance(String param1, int param2, String playSound) {

        instance = new PlayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_COLOR, param2);
        args.putString(ARG_PALY_SOUND, playSound);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e("tool", "In frag's on save instance state ");
        //outState.putParcelableArray("tools", tools);
        //Save the fragment's state here
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mColor = getArguments().getInt(ARG_COLOR);
            mPlaySound = getArguments().getString(ARG_PALY_SOUND);
        }else {
            mColor = savedInstanceState.getInt(ARG_COLOR);
        }

//        if (savedInstanceState != null) {
//            tools = (Tool[])savedInstanceState.getSerializable("tools");
//            for (Tool ob : tools) {
//                if(ob != null) {
//                    Log.e("tool","--"+ob.getProduct()+","+ob.getTimes());
//                }
//            }
//        }else{
//            Log.e("tool2","--nothing...");
//            for (Tool ob : tools) {
//                if(ob != null) {
//                    Log.e("tool","--"+ob.getProduct()+","+ob.getTimes());
//                }
//            }
//        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mSystemPreferences != null && iniUser !=null){
            if((GetRightNow()- iniUser.getLifeCounter()) >  mSystemPreferences.getCounterSec()) {
                CreateTimer(iniUser.getLifeCounter());
                Log.e("Timer1","CreateTimer->"+iniUser.getLifeCounter());
            }
        }
        return inflater.inflate(R.layout.fragment_play, container, false);
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
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
        mTxtViewUpperLives = (TextView) getView().findViewById(R.id.txtViewUpperLives);

        mCenterRegion= (RelativeLayout) getView().findViewById(R.id.centerRegion);

        mCoorContentRegion = (RelativeLayout) getView().findViewById(R.id.coorContentRegion);
        mCoorContentRegion.setOnClickListener(layoutViewToolsOnClick);

        mCoorContentRegionAmin = (RelativeLayout) getView().findViewById(R.id.coorContentRegionAnim);

        mImgViewTools = (ImageView) getView().findViewById(R.id.imgViewTools);
        mListViewTools = (ListView) getView().findViewById(R.id.listViewTools);


        mGifView = (GifView) getView().findViewById(R.id.gifView);
        mGifView.setImageResource(R.raw.firework3);

        mGifViewPlayer = (GifView) getView().findViewById(R.id.gifViewPlayer);
        mGifViewPlayer.setImageResource(R.raw.player);

        mGifViewNpc = (GifView) getView().findViewById(R.id.gifViewNpc);
        mGifViewNpc.setImageResource(R.raw.npc);

        mImgViewToolUsed_00 = (ImageView) getView().findViewById(R.id.imgViewToolUsed_00);
        mImgViewToolUsed_01 = (ImageView) getView().findViewById(R.id.imgViewToolUsed_01);
        mImgViewToolUsed_02 = (ImageView) getView().findViewById(R.id.imgViewToolUsed_02);
        mImgViewToolUsed_03 = (ImageView) getView().findViewById(R.id.imgViewToolUsed_03);
        mImgViewToolUsed_04 = (ImageView) getView().findViewById(R.id.imgViewToolUsed_04);


        iniUser = new User();
        readUser(UpdateUserTimer.OnIni.value);

        updateBoard(UpdateUserTimer.OnIni.value);

        tools =((MainActivity)getActivity()).getToolsArray();

        if(tools!=null) {
            int tooCount = 0;
            for (Tool ob : tools) {
                if (ob != null) {
                    Log.e("tool", "--" + ob.getProduct() + "," + ob.getTimes());
                    if(ob.getProduct() == ProductType.Dice.value) {
                        setToolIconShow(tooCount, R.drawable.tool_dice);
                    }
                }
                tooCount++;
            }

        }else{
            tools = new Tool[5];
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private View.OnClickListener imgViewToolsOnClick = new View.OnClickListener() {
        public void onClick(final View v) {
            readUser(UpdateUserTimer.OnIni.value);
            mCoorContentRegion.setVisibility(View.VISIBLE);
//            Log.e("test", String.valueOf(mCoorContentRegion.isShown()));
        }
    };
    private View.OnClickListener layoutViewToolsOnClick = new View.OnClickListener() {
        public void onClick(final View v) {
            mCoorContentRegion.setVisibility(View.INVISIBLE);
//            Log.e("test", String.valueOf(mCoorContentRegion.isShown()));
        }
    };

    private View.OnClickListener imgViewPlayOnClick = new View.OnClickListener() {
        public void onClick(final View v) {
            if(chkReaded) {
                PlayGame(v);
            }
        }
    };

    private View.OnClickListener imgViewAutoPlayOnClick = new View.OnClickListener() {
        public void onClick(final View v) {
            if(chkReaded) {
                if(autoPlay == false){
                    autoPlay = true;
                    mImgViewAuto.setImageResource(R.drawable.button_auto_active);
                }else{
                    autoPlay = false;
                    mImgViewAuto.setImageResource(R.drawable.button_auto);
                }

                if(autoPlay == true && isPlaying == false) {
                    isPlaying = true;
                    PlayGame(v);
                }
            }
        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public void PlayGame(final View v){

        if(mLives>0) {
            if(((MainActivity) getActivity()) != null) {
                ((MainActivity) getActivity()).disalbeBottomNavListener();
            }

            if(isTheDateBeforeToday(iniUser.getDailyResetDate())){
                mCoins = mCoins + mSystemPreferences.getDailyReward().intValue();
                mDailyPlayTimes = 0;
                mDailyWinTimes = 0;
                updateUser(user,UpdateUserTimer.DailyReset.value);
            }

            if(isTheDateBeforeToday(iniUser.getSpecialTimeRewardGetDate())){
                if(isSpecialTime()) {
                    mCoins = mCoins + mSystemPreferences.getSpecialTimeReward().intValue();
                    updateUser(user, UpdateUserTimer.SpecialTimeReward.value);
                }
            }


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

            mScore = Integer.valueOf(mTxtViewScore.getText().toString());
            mLives = Integer.valueOf(mTxtViewLives.getText().toString());

            ptlogMatchResult = 0;
            //倒數5秒拿掉
            //mTxtViewPlayCounter.setVisibility(View.VISIBLE);
            mImgViewNpc.setVisibility(View.INVISIBLE);
            //mAnimationNpc.setVisibility(View.VISIBLE);
            mGifViewNpc.setVisibility(View.VISIBLE);

            mImgViewPlayer.setVisibility(View.INVISIBLE);
            mGifViewPlayer.setVisibility(View.VISIBLE);
            //mAnimationPlayer.setVisibility(View.VISIBLE);

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

                    if (Integer.valueOf(new SimpleDateFormat("s").format(millisUntilFinished)) <= 5) {
                        mTxtViewPlayCounter.setText(new SimpleDateFormat("s").format(millisUntilFinished));
                    }
                }

                @Override
                public void onFinish() {
                    //倒數完成後要做的事
//                if(mPlaySound.equals("1")) {
//                    getActivity().stopService(countDownSound);
//                }
                    //倒數5秒拿掉
                    //mTxtViewPlayCounter.setVisibility(View.INVISIBLE);
                    mImgViewNpc.setVisibility(View.VISIBLE);
                    //mAnimationNpc.setVisibility(View.INVISIBLE);
                    mGifViewNpc.setVisibility(View.INVISIBLE);
                    mImgViewPlayer.setVisibility(View.VISIBLE);
                    mGifViewPlayer.setVisibility(View.VISIBLE);
                    //mAnimationPlayer.setVisibility(View.INVISIBLE);
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
//                    if(mUseTool == ProductType.Dice.value){
//                        mMultiple = (int) (Math.random() * 6 + 1);
//                    }
                    for (Tool ob : tools) {
                        if(ob != null){
                            int product = ob.getProduct();
                            if(product == ProductType.Dice.value){
                                mMultiple = (int) (Math.random() * 6 + 1);
                                Log.e("TOOL","倍數 "+ mMultiple);
                            }
                        }
                    }

                    /*
                    boolean empty = false;
                    for (Tool ob : tools) {
                        empty = false;
                        if (ob == null) {
                            empty = true;
                            break;
                        }
                        i++;
                    }
*/

                    switch (ptlogMatchResult) {

                        case 1: //Lose
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
                        case 2: //Win
                            mScore += (100 * mMultiple);
                            mLives -= 1;
                            getScore = (100 * mMultiple);
                            getLives = -1;
                            if (mPlaySound.equals("1")) {
                                MediaPlayer mpWin = MediaPlayer.create(getActivity(), R.raw.win);
                                mpWin.start();
                                mpWin.seekTo(0);
                            }
                            mGifView.setVisibility(View.VISIBLE);
                            break;
                        default: //Tie
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
//                    if(mUseTool == ProductType.Dice.value) {
//                        mMultiple = 1;
//                        mDice -= 1;
//                    }
                    int toolCount = 0;
                    for (Tool ob : tools) {
                        if(ob != null){
                            int product = ob.getProduct();
                            int times = ob.getTimes();
                            if(product == ProductType.Dice.value){
                                mMultiple = 1;
                                if(ptlogMatchResult != MatchResult.Tie.value) {//不是平手就要減次數
                                    times--;
                                    ob.setTimes(times);
                                    if(times != 0){
                                       tools[toolCount] = ob;
                                        Log.e("TOOL","DICE 剩 "+ times);
                                    }else{
                                        tools[toolCount] = null;
                                        Log.e("TOOL","DICE 用完 "+ times);
                                        setToolIconHide(toolCount,ProductType.Dice.value);
                                        ((MainActivity)getActivity()).saveToolsArray(tools);
                                    }
                                }
                            }
                        }
                        toolCount++;
                    }
                    mTxtViewResult.setVisibility(View.VISIBLE);
                    mTxtViewResult.setText(result);
                    /*
                    double width = mCenterRegion.getWidth() * 0.9;
                    double height = mCenterRegion.getHeight() * 0.5;

                    final CustomDialog dialog = new CustomDialog(v.getContext(), getString(result), String.valueOf(getLives), String.valueOf(getScore), new CustomDialog.ICustomDialogEventListener() {
                        @Override
                        public void customDialogEvent(int id) {
                        }
                    }, R.style.dialog);
                    dialog.show();
                    dialog.getWindow().setLayout((int)width, (int)height);
                                    */

//                mTxtViewCoins.setText(String.valueOf(Integer.valueOf(mTxtViewCoins.getText().toString())+10));

                    updateUser(user, UpdateUserTimer.PlayGame.value);
                    new CountDownTimer(3000, 1000) {
                        //mTxtViewCounter.setVisibility(v.VISIBLE );
                        @Override
                        public void onTick(long millisUntilFinished) {
                            //倒數秒數中要做的事
                            //if (new SimpleDateFormat("s").format(millisUntilFinished).equals("1") && (autoPlay == true || isPlaying == true)) {
                            //    dialog.cancel();
                            //}
                        }

                        @Override
                        public void onFinish() {
//                            ((MainActivity)getActivity()).recallPlayFragment();

                            mImgViewNpc.setImageResource(R.drawable.img_cardback);
                            mImgViewPlayer.setImageResource(R.drawable.img_cardback);
                            mTxtViewResult.setVisibility(View.INVISIBLE);
                            mGifView.setVisibility(View.INVISIBLE);
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
                                ((MainActivity) getActivity()).enableBottomNavListener();
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
                ((MainActivity) getActivity()).enableBottomNavListener();
                mImgViewAuto.setImageResource(R.drawable.button_auto);
            }
        }
    }

    @Override
    public void onPause() {
        //Log.d("FragPlay","onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        //Log.d("FragPlay","onResume->"+GetRightNow());
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        //Log.d("FragPlay","onStop->");
        if(iniUser != null && mSystemPreferences != null)
            updateTimer(user.getUid());

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /*先找到目前的board，再Update*/
    private void updateBoard(final int when) {
        final Long rightNow = GetRightNow();
        if(rightNow> mStartDateInterval && rightNow < mEndDateInterval && mScoreBoardId.length() != 0 ){
            updateUserScore(mScoreBoardId,when);
        }else{
            mQueryDatabase = FirebaseDatabase.getInstance().getReference("score-boards");
            Query queryRef = mQueryDatabase.orderByChild("endDateInterval").startAt(rightNow);
            queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot boardSnapshot : snapshot.getChildren()) {
                        Board b = boardSnapshot.getValue(Board.class);
                        if (rightNow >= b.getStartDateInterval()) {
                            mStartDateInterval = b.getStartDateInterval();
                            mEndDateInterval = b.getEndDateInterval();
                            mScoreBoardId = b.getId();
                            updateUserScore(b.getId(), when);
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("☆firebase failed: ", databaseError.getMessage());
                }

            });
        }

    }

    private void updateUserScore(final String boardId, final int when) {
        final DatabaseReference updateUserScoreDBref = FirebaseDatabase.getInstance().getReference("scores/" + boardId+ "/" + user.getUid());

        updateUserScoreDBref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
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


    private void readSystemPreferences(final Long lifeCounter, final Long userDailyResetDate) {

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
                //Log.w("☆firebase(playFrag)",String.valueOf(sp.getCounterSec())+","+String.valueOf(mSystemPreferences.getCounterSec()));
                CreateTimer(lifeCounter);
                mTxtViewUpperLives.setText("/"+mSystemPreferences.getLivesUpperLimit());

                if(isTheDateBeforeToday(userDailyResetDate)){
                    //Log.w("check00-0",String.valueOf(mCoins));
                    mCoins = mCoins + mSystemPreferences.getDailyReward().intValue();
                    mDailyPlayTimes = 0;
                    mDailyWinTimes = 0;
                    //Log.w("check00",String.valueOf(mCoins));
                    updateUser(user,UpdateUserTimer.DailyReset.value);
                }

                if(isTheDateBeforeToday(iniUser.getSpecialTimeRewardGetDate())){
                    if(isSpecialTime()) {
                        mCoins = mCoins + mSystemPreferences.getSpecialTimeReward().intValue();
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
        mReadDatabase = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());

        mReadDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User u = snapshot.getValue(User.class);
                if(u != null){
                    //==============================================================================
                    //Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                    //for (Object key : map.keySet()) {
                    //    Log.w("firebase", key + " : " + map.get(key) + map.get(key).getClass());
                    //}
                    //==============================================================================

                    iniUser = u;
                    mTxtViewCoins.setText(String.valueOf(u.getCoins()));
                    mTxtViewLives.setText(String.valueOf(u.getLives()));
                    if(u.getLives()!=null) {
                        mLives = u.getLives().intValue();
                    }else{mLives = 0 ;}
                    if(u.getWinWithScissor()!=null) {
                        mWinWithScissor = u.getWinWithScissor().intValue();
                    }else{mWinWithScissor = 0 ;}
                    if(u.getWinWithRock()!=null) {
                        mWinWithRock = u.getWinWithRock().intValue();
                    }else{mWinWithRock = 0 ;}
                    if(u.getWinWithPaper()!=null) {
                        mWinWithPaper = u.getWinWithPaper().intValue();
                    }else{mWinWithPaper = 0 ;}
                    if(u.getDailyPlayTimes()!=null) {
                        mDailyPlayTimes = u.getDailyPlayTimes().intValue();
                    }else{mDailyPlayTimes = 0 ;}
                    if(u.getDailyWinTimes()!=null) {
                        mDailyWinTimes = u.getDailyWinTimes().intValue();
                    }else{mDailyWinTimes = 0 ;}
                    if(u.getCoins()!=null) {
                        mCoins = u.getCoins().intValue();
                    }else{mCoins = 0 ;}
                    if(u.getDice()!=null) {
                        mDice = u.getDice().intValue();
                        mDiceTmp = u.getDice().intValue();
                    }else{mDice = 0 ;}

                    if(getActivity() != null) {
                        mToolsAdapter = new ToolsAdapter(getActivity().getApplicationContext(), iniUser, PlayFragment.this);
                        mListViewTools.setAdapter(mToolsAdapter);
                        mImgViewTools.setOnClickListener(imgViewToolsOnClick);
                    }

                    if(when == UpdateUserTimer.OnIni.value) {
                        Long d = u.getDailyResetDate();
                        if(d==null){
                            d = 0L;
                        }
                        readSystemPreferences(u.getLifeCounter()/1000,d);
//                        updateBoard(UpdateUserTimer.OnIni.value);
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
        //Log.d("☆Firebase",String.valueOf(UpdateUserTimer.values()[when-1]));
        mWriteDatabase = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());

        mWriteDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                User user = new User();

                user.setCoins(Long.valueOf(mCoins));

                long[] mTotalDice= {0L,0L,0L,0L,0L,0L};

                Map newUserData = new HashMap();
                if(when == UpdateUserTimer.PlayGame.value)
                {
                    if(mDailyPlayTimes/mSystemPreferences.getPlayTimesPerDice() == 1 &&
                            mDailyPlayTimes%mSystemPreferences.getPlayTimesPerDice() == 0){
                        mDice+=1;
                        mTotalDice[0] = mDice;
                        //Log.w("totalDice0",String.valueOf(mDice));
                    }
                    if(mDailyWinTimes/mSystemPreferences.getWinTimesGetOneDiceFirst() == 1 &&
                            mDailyWinTimes%mSystemPreferences.getWinTimesGetOneDiceFirst() == 0 && mDailyWinTimesFlag){
                        mDice+=1;
                        mTotalDice[1] = mDice;
                        //Log.w("totalDice1",String.valueOf(mDice));
                    }
                    if(mDailyWinTimes/mSystemPreferences.getWinTimesGetOneDiceSecond() == 1 &&
                            mDailyWinTimes%mSystemPreferences.getWinTimesGetOneDiceSecond() == 0 && mDailyWinTimesFlag){
                        mDice+=1;
                        mTotalDice[2] = mDice;
                        //Log.w("totalDice2",String.valueOf(mDice));
                    }
                    if(mWinWithScissor/mSystemPreferences.getWinWithMatchResult() == 1 &&
                            mWinWithScissor%mSystemPreferences.getWinWithMatchResult() == 0 && mWinWithScissorFlag){
                        mDice+=1;
                        mTotalDice[3] = mDice;
                        //Log.w("totalDice3",String.valueOf(mDice));
                    }
                    if(mWinWithRock/mSystemPreferences.getWinWithMatchResult() == 1 &&
                            mWinWithRock%mSystemPreferences.getWinWithMatchResult() == 0 && mWinWithRockFlag){
                        mDice+=1;
                        mTotalDice[4] = mDice;

                    }
                    if(mWinWithPaper/mSystemPreferences.getWinWithMatchResult() == 1 &&
                            mWinWithPaper%mSystemPreferences.getWinWithMatchResult() == 0 && mWinWithPaperFlag){
                        mDice+=1;
                        mTotalDice[5] = mDice;

                    }
                    //Log.w("totalDice",mTotalDice[0]+","+mTotalDice[1]+","+mTotalDice[2]+","+mTotalDice[3]+","+mTotalDice[4]+","+mTotalDice[5]);

                    newUserData.put("lives", Long.valueOf(mLives));
                    newUserData.put("dailyPlayTimes",Long.valueOf(mDailyPlayTimes));
                    newUserData.put("dailyWinTimes",Long.valueOf(mDailyWinTimes));
                    newUserData.put("winWithScissor",Long.valueOf(mWinWithScissor));
                    newUserData.put("winWithRock",Long.valueOf(mWinWithRock));
                    newUserData.put("winWithPaper",Long.valueOf(mWinWithPaper));
                    newUserData.put("dice", Long.valueOf(mDice));

                }else if(when == UpdateUserTimer.FiveMinutesTimer.value) {
                    newUserData.put("lives", Long.valueOf(mLives));
                    updateTimer(fUser.getUid());

                }else if(when == UpdateUserTimer.DailyReset.value){
                    newUserData.put("dailyPlayTimes", Long.valueOf(mDailyPlayTimes));
                    newUserData.put("dailyWinTimes", Long.valueOf(mDailyWinTimes));
                    newUserData.put("dailyResetDate", ServerValue.TIMESTAMP);
                    newUserData.put("coins", Long.valueOf(mCoins));
                    mTxtViewCoins.setText(String.valueOf(mCoins));

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
                        /*
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
                                                */

                        if(mDice != mDiceTmp){
                            DatabaseReference drLog0 = FirebaseDatabase.getInstance().getReference("users/" + fUser.getUid() + "/transactionLogTool/" + rightNow);
                            Map transactionLogTool = new HashMap();
                            transactionLogTool.put("type", ToolTransactionType.PlayGame.value);
                            transactionLogTool.put("product", ProductType.Dice.value);
                            transactionLogTool.put("transaction", -1);
                            transactionLogTool.put("total", mDice);
                            drLog0.setValue(transactionLogTool);
                            Log.e("TOOL","write log to firebase : "+mDiceTmp+","+mDice);
                            mDiceTmp = mDice;
                            Log.e("TOOL","write log to firebase : "+mDiceTmp+","+mDice);

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
        mWriteDatabase = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());
        Map newUserData = new HashMap();
        newUserData.put("lifeCounter", ServerValue.TIMESTAMP);
        mWriteDatabase.updateChildren(newUserData);
        mWriteDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User u = snapshot.getValue(User.class);
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
        if(userDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(userDate);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);

            String strUserDate = String.valueOf(year) + String.valueOf(String.format("%02d", month)) + String.valueOf(String.format("%02d", day));

            Calendar calR = Calendar.getInstance();
            calR.setTimeInMillis(System.currentTimeMillis());
            int yearR = calR.get(Calendar.YEAR);
            int monthR = calR.get(Calendar.MONTH) + 1;
            int dayR = calR.get(Calendar.DAY_OF_MONTH);
            String strRightDate = String.valueOf(yearR) + String.valueOf(String.format("%02d", monthR)) + String.valueOf(String.format("%02d", dayR));

            if (Integer.valueOf(strUserDate) < Integer.valueOf(strRightDate)) {
                isTheDateBeforeToday = true;
            }
        }
        return isTheDateBeforeToday;
    }

    private boolean isSpecialTime(){
        boolean isSpecialTime = false;
        Long mSpecialTimeStartDateInterval = mSystemPreferences.getSpecialTimeStartDateInterval();
        Long mSpecialTimeEndDateInterval = mSystemPreferences.getSpecialTimeEndDateInterval();

        Calendar startCal = Calendar.getInstance();
        startCal.setTimeInMillis( mSpecialTimeStartDateInterval );
        int startHh = startCal.get(Calendar.HOUR_OF_DAY);
        int startSs = startCal.get(Calendar.MINUTE);

        String startHHSS = String.valueOf(String.format("%02d", startHh))+String.valueOf(String.format("%02d", startSs));

        Calendar endCal = Calendar.getInstance();
        endCal.setTimeInMillis( mSpecialTimeEndDateInterval );
        int endHh = endCal.get(Calendar.HOUR_OF_DAY);
        int endSs = endCal.get(Calendar.MINUTE);

        String endHHSS = String.valueOf(String.format("%02d", endHh))+String.valueOf(String.format("%02d", endSs));

        Long rightNow = System.currentTimeMillis();
        Calendar curCal = Calendar.getInstance();
        curCal.setTimeInMillis( rightNow );

        int curHh = curCal.get(Calendar.HOUR_OF_DAY);
        int curSs = curCal.get(Calendar.MINUTE);

        String curHHSS = String.valueOf(String.format("%02d", curHh))+String.valueOf(String.format("%02d", curSs));

        if(Integer.valueOf(curHHSS) >= Integer.valueOf(startHHSS) && Integer.valueOf(curHHSS) <= Integer.valueOf(endHHSS))
        {
            isSpecialTime = true;
        }

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
                        if(mLives > mSystemPreferences.getLivesUpperLimit()){
                            mLives = mSystemPreferences.getLivesUpperLimit().intValue();
                        }
                        ltlogTransaction = 1;
                        ltlogType = LifeTransactionType.FiveMinutesTimer.value;
                        updateUser(user,UpdateUserTimer.FiveMinutesTimer.value);
                    }
            }
        }
    };

    private void CreateTimer(Long lifeCounter){
        if(timer01 != null){
            timer01.purge();
            timer01.cancel();
            timer01 = null;
        }
        timer01 =new Timer();
        CreateTimerTask();
        //設定Timer(task為執行內容，0代表立刻開始,間格1秒執行一次)
        //Log.w("CreateTimer",isFirstCreatTimer+",");
        if(isFirstCreatTimer) {
            if(lifeCounter == 0){
                //tsec = setTsec;
                tsec = mSystemPreferences.getCounterSec().intValue();
            }else {
                long rightNow = GetRightNow();
                long countSec = mSystemPreferences.getCounterSec() - ((rightNow - lifeCounter) % mSystemPreferences.getCounterSec());
                long getLife = ((rightNow - lifeCounter) / mSystemPreferences.getCounterSec());
                Log.e("Timer2","counterSec->"+mSystemPreferences.getCounterSec()+",rightNow->"+rightNow+",lifeCounter->"+lifeCounter+",diff->"+(rightNow - lifeCounter));
                if(getLife>0){
                    mLives = Integer.valueOf(mTxtViewLives.getText().toString());
//                    tsec = mSystemPreferences.getCounterSec().intValue();
                    mLives+=getLife;
                    if(mLives > mSystemPreferences.getLivesUpperLimit()){
                        mLives = mSystemPreferences.getLivesUpperLimit().intValue();
                    }
                    ltlogTransaction = (int) getLife;
                    ltlogType = LifeTransactionType.FiveMinutesTimer.value;
                    updateUser(user,UpdateUserTimer.FiveMinutesTimer.value);
                }
                tsec = (int)countSec;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
// Add the buttons
        builder.setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                if(Integer.valueOf(toolAmount) > 0) {
                    mCoorContentRegion.setVisibility(View.INVISIBLE);
                    int i = findTheEmptyToolArray();
                    if(i != -1) { //共5個道具格，若還有空格則可使用道具！
                        if (toolName.equals("Dice")) {
                            //mUseTool = ProductType.Dice.value;
                            if(!isToolUsing(ProductType.Dice.value)) {//沒有同樣道具使用中
                                mDice--;
                                setToolIconShow(i, R.drawable.tool_dice);//左側顯示道具圖示
                                Tool t = new Tool();
                                t.setProduct(ProductType.Dice.value);
                                t.setTimes(10);
                                tools[i] = t;
                                ((MainActivity)getActivity()).saveToolsArray(tools);
                            }else{
                                Log.e("toolCheck","dice using...");
                            }
                        }
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
        if(!toolName.equals("Dice")){
            builder.setMessage( " Coming soon...");
        }else if(Integer.valueOf(toolAmount) > 0) {
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


    public int findTheEmptyToolArray(){
        int i = 0;
        boolean empty = false;
        if(tools != null) {
            for (Tool ob : tools) {
                empty = false;
                if (ob == null) {
                    empty = true;
                    break;
                }
                i++;
            }
        }
        // return -1 時就是沒有空的可以用
        if(!empty) {
            i=-1;
        }
        return i;
    }


    public boolean isToolUsing(int Product){
        int i = 0;
        boolean isUsing = false;
        if(tools != null) {
            for (Tool ob : tools) {
                if(ob != null) {
                    if (ob.getProduct() == Product) {
                        isUsing = true;
                        break;
                    }
                }
            }
        }
        return isUsing;
    }

    private void setToolIconShow(int i,int resId){
        mImgViewToolUsed_00.setImageResource(R.drawable.tool_dice);

        switch(i){
            case 0:
                mImgViewToolUsed_00.setImageResource(resId);
                mImgViewToolUsed_00.setVisibility(View.VISIBLE);
                break;
            case 1:
                mImgViewToolUsed_01.setImageResource(resId);
                mImgViewToolUsed_01.setVisibility(View.VISIBLE);
                break;
            case 2:
                mImgViewToolUsed_02.setImageResource(resId);
                mImgViewToolUsed_02.setVisibility(View.VISIBLE);
                break;
            case 3:
                mImgViewToolUsed_03.setImageResource(resId);
                mImgViewToolUsed_03.setVisibility(View.VISIBLE);
                break;
            case 4:
                mImgViewToolUsed_04.setImageResource(resId);
                mImgViewToolUsed_04.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setToolIconHide(int i,int resId){
        mImgViewToolUsed_00.setImageResource(R.drawable.tool_dice);

        switch(i){
            case 0:
                mImgViewToolUsed_00.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mImgViewToolUsed_01.setVisibility(View.INVISIBLE);
                break;
            case 2:
                mImgViewToolUsed_02.setVisibility(View.INVISIBLE);
                break;
            case 3:
                mImgViewToolUsed_03.setVisibility(View.INVISIBLE);
                break;
            case 4:
                mImgViewToolUsed_04.setVisibility(View.INVISIBLE);
                break;
        }
    }

}//程式結尾
