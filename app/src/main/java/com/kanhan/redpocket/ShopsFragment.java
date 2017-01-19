package com.kanhan.redpocket;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kanhan.redpocket.Data.User;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShopsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShopsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShopsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView mListViewCoins;
    private ListView mListViewTools;
    private int mCoins, mIronFirst, mMindControl, mGoldenHand, mLife, mDice, mVictory;
    private User mUser;

    private OnFragmentInteractionListener mListener;

    public ShopsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mListViewCoins = (ListView) getView().findViewById(R.id.listViewCoins);
        mListViewCoins.setAdapter(new CoinsAdapter());
        mListViewTools = (ListView) getView().findViewById(R.id.listViewTools);
        mListViewTools.setAdapter(new ShopsAdapter(getActivity().getApplicationContext(), ShopsFragment.this));
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShopsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShopsFragment newInstance(String param1, String param2) {
        ShopsFragment fragment = new ShopsFragment();
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
        return inflater.inflate(R.layout.fragment_shops, container, false);
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

    public void buySomething(final String toolName, final String toolPrice) {
        Log.d("☆Firebase", "readUser->");
        DatabaseReference mReadDatabase = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());

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
                        Log.w("ShopFragFirebase", key + " : " + map.get(key) + map.get(key).getClass());
                    }
                    //==============================================================================

                    mUser = u;
                    mCoins = u.getCoins().intValue();

                    mIronFirst = u.getIronFirst().intValue();
                    mMindControl = u.getMindControl().intValue();
                    mGoldenHand = u.getGoldenHand().intValue();
                    mLife = u.getLives().intValue();
                    mDice = u.getDice().intValue();
                    mVictory = u.getVictory().intValue();

                    if(mCoins < Integer.valueOf(toolPrice)){
                        Toast.makeText(getActivity(), "Coins 不足！",
                                Toast.LENGTH_SHORT).show();
                    }else{
                        buyTool(toolName,toolPrice);
                    }



                }else{
                    buyTool(toolName,toolPrice);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("☆firebase failed: ", databaseError.getMessage());
            }

        });
    }

    public void buyTool(final String toolName, final String toolPrice){
        Log.w("buyTool","test");
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
// Add the buttons
        builder.setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                updateUser(toolName,toolPrice);
                //updateUser after buy something
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
// Set other dialog properties

//        builder.setTitle("Title");

        builder.setMessage("購買道具 " + toolName + " ?");

// Create the AlertDialog
        AlertDialog dialog = builder.create();

        dialog.show();

    }

    private void updateUser(final String toolName, final String toolPrice) {
        final DatabaseReference mWriteDatabase = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());
        mWriteDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Map newUserData = new HashMap();

                newUserData.put("coins", Long.valueOf(mCoins) - Long.valueOf(toolPrice));
                int type= 0 ,product=0 ,transaction=0, total=0;
                switch (toolName){
                    case "Iron First":
                        mIronFirst +=1;
                        newUserData.put("ironFirst", Long.valueOf(mIronFirst));
                        product = PlayFragment.ProductType.IronFirst.ProductType();
                        total = mIronFirst;
                        break;
                    case "Mind Control":
                        mMindControl +=1;
                        newUserData.put("mindControl", Long.valueOf(mMindControl));
                        product = PlayFragment.ProductType.MindControl.ProductType();
                        total = mMindControl;
                        break;
                    case "Golden Hand":
                        mGoldenHand +=1;
                        newUserData.put("goldenHand", Long.valueOf(mGoldenHand));
                        product = PlayFragment.ProductType.GoldenHand.ProductType();
                        total = mGoldenHand;
                        break;
                    case "Life":
                        mLife +=1;
                        newUserData.put("lives", Long.valueOf(mLife));
                        product = PlayFragment.ProductType.Life.ProductType();
                        total = mLife;
                        break;
                    case "Dice":
                        mDice +=1;
                        newUserData.put("dice", Long.valueOf(mDice));
                        product = PlayFragment.ProductType.Dice.ProductType();
                        total = mDice;
                        break;
                    case "Victory":
                        mVictory +=1;
                        newUserData.put("victory", Long.valueOf(mVictory));
                        product = PlayFragment.ProductType.Victory.ProductType();
                        total = mVictory;
                        break;
                }
                mWriteDatabase.updateChildren(newUserData);

                type = PlayFragment.ToolTransactionType.Purchase.ToolTransactionType();
                transaction = 1;

                Long rightNow = GetRightNow();
                DatabaseReference drLog0 = FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/transactionLogTool/" + rightNow);
                Map transactionLogTool = new HashMap();
                transactionLogTool.put("type", type);
                transactionLogTool.put("product", product);
                transactionLogTool.put("transaction", transaction);
                transactionLogTool.put("total", total);
                drLog0.setValue(transactionLogTool);

                mCoins = mCoins-Integer.valueOf(toolPrice);
                DatabaseReference drLog1 = FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/transactionLogCoin/" + rightNow);
                Map transactionLogCoin = new HashMap();
                transactionLogCoin.put( "type", PlayFragment.CoinTransactionType.BuyTool.CoinTransactionType());
                transactionLogCoin.put( "transaction", Long.valueOf(toolPrice)*-1);
                transactionLogCoin.put( "totalCoins",mCoins );
                drLog1.setValue(transactionLogCoin);
                Toast.makeText(getActivity(), "花費 " + toolPrice +" 得到 " + toolName + " " + transaction +" 個",
                        Toast.LENGTH_SHORT).show();
        }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("☆firebase failed: ", databaseError.getMessage());
            }
        });
    }

    private Long GetRightNow(){
        Long tsLong = System.currentTimeMillis()/1000;
//        String ts = tsLong.toString();
        return tsLong;
    }

}
