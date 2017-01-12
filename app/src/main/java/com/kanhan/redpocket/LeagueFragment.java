package com.kanhan.redpocket;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kanhan.redpocket.Data.Board;
import com.kanhan.redpocket.Data.ScoreFormat;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LeagueFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LeagueFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeagueFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private DatabaseReference mWriteDatabase, mReadDatabase, mQueryDatabase;
    private List<ScoreFormat> mUserScoreOrderList = new ArrayList();
    private boolean isLoaded = false;
    private ImageView mImgViewNoLeague;
    private TextView mTxtViewSorry, mTxtViewNoRanking, mTxtViewUpdateTime;
    private ListView mListViewLeague;
    private ListAdapter mLeagueAdapter;

    private OnFragmentInteractionListener mListener;

    public LeagueFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LeagueFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LeagueFragment newInstance(String param1, String param2) {
        LeagueFragment fragment = new LeagueFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_league, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        aueryBoard();

        mImgViewNoLeague = (ImageView) getView().findViewById(R.id.imgViewNoLeague) ;
        mTxtViewSorry = (TextView) getView().findViewById(R.id.txtViewSorry) ;
        mTxtViewNoRanking = (TextView) getView().findViewById(R.id.txtViewNoRanking) ;
        mTxtViewUpdateTime = (TextView) getView().findViewById(R.id.txtViewUpdateTime) ;
        /*timestamp to date*/
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH)+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        mTxtViewUpdateTime.setText("Update time:"+year+"/"+month+"/"+day);
        Log.d("updateTimer",year+"-"+month+"-"+day);
        //宣告 ListView 元件
        mListViewLeague = (ListView)getView().findViewById(R.id.listViewLeague);

//        mLeagueAdapter.setOnItemClickListener(listViewOnItemClickListener);
    }

    /*先找到目前的board，再Update*/
    private void aueryBoard() {
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
                        boardOrderby(b.getId());

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

    /*找top100*/
    private void boardOrderby(final String scoreBoardId) {
        final List<ScoreFormat> scoreOrderList = new ArrayList();
        mQueryDatabase = FirebaseDatabase.getInstance().getReference("scores/"+scoreBoardId);
        Query queryRef = mQueryDatabase.orderByChild("score").limitToLast(100);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot boardSnapshot : snapshot.getChildren()) {
                    ScoreFormat s = new ScoreFormat();
                    for (DataSnapshot mSnapshot : boardSnapshot.getChildren()) {
                        if (mSnapshot.getKey().equals("displayName")) {
                            s.setDisplayName((String) mSnapshot.getValue());
                        }
                        if (mSnapshot.getKey().equals("score")) {
                            DecimalFormat mDecimalFormat = new DecimalFormat("###,###,###");
                            String text = mDecimalFormat.format(Double.parseDouble(mSnapshot.getValue().toString()));
                            s.setScore(text);
                        }
                    }

                    scoreOrderList.add(s);

                }
                Collections.reverse(scoreOrderList);
                mUserScoreOrderList = scoreOrderList;
                for(ScoreFormat s:mUserScoreOrderList){
                    Log.w("userScore",s.getDisplayName()+","+s.getScore());
                }
                if(getActivity()!= null) {
                    mLeagueAdapter = new LeagueAdapter(getActivity().getApplicationContext(), mUserScoreOrderList);
                    mListViewLeague.setAdapter(mLeagueAdapter);
                    mImgViewNoLeague.setVisibility(View.INVISIBLE);
                    mTxtViewNoRanking.setVisibility(View.INVISIBLE);
                    mTxtViewSorry.setVisibility(View.INVISIBLE);
                    mTxtViewUpdateTime.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("☆firebase failed: ", databaseError.getMessage());
            }
        });


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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

    private Long GetRightNow(){
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        return tsLong;
    }
}
