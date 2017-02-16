package com.juicesoft.redpocket;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SinginActivity extends AppCompatActivity {

    private EditText mEditTxtEmail;
    private EditText mEditTxtNickname;
    private EditText mEditTxtPassword;
    private EditText mEditTxtConfirm;
    private FirebaseAuth mfirebaseAuth;
    private Button btnDone;
    private Button btnCancel;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        mfirebaseAuth = FirebaseAuth.getInstance();
        Toolbar toolbar ;
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btnDone = (Button)findViewById(R.id.btnDone);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        mEditTxtEmail = (EditText)findViewById(R.id.editTxtEmail);
        mEditTxtNickname =(EditText)findViewById(R.id.editTxtNickName);
        mEditTxtPassword = (EditText)findViewById(R.id.editTxtPassword);
        mEditTxtConfirm = (EditText)findViewById(R.id.editTxtConfirmPassword);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creatUser();
            }
        });


    }
    private void creatUser() {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        String email = mEditTxtEmail.getText().toString();
        final String nickname = mEditTxtNickname.getText().toString();
        String password = mEditTxtPassword.getText().toString();
        String passwordConfrim = mEditTxtConfirm.getText().toString();

        if (TextUtils.isEmpty(email) ) {
            mEditTxtEmail.setError("請輸入Email");
            mEditTxtEmail.requestFocus();
        }else if( TextUtils.isEmpty(nickname)){
            mEditTxtNickname.setError("請輸入暱稱");
            mEditTxtNickname.requestFocus();
        }else if(TextUtils.isEmpty(password) ){
            mEditTxtPassword.setError("請輸入密碼");
            mEditTxtPassword.requestFocus();
        }else if(TextUtils.isEmpty(passwordConfrim)) {
            mEditTxtConfirm.setError("請確認密碼");
            mEditTxtConfirm.requestFocus();
        }else if(!password.equals(passwordConfrim) ){
            mEditTxtPassword.setError("確認密碼不一致");
            mEditTxtPassword.requestFocus();
        } else {
            mfirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (!task.isSuccessful()) {
                        try {
                            throw task.getException();
                        } catch(FirebaseAuthWeakPasswordException e) {
                            //Toast.makeText(SinginActivity.this, "密碼至少需6位", Toast.LENGTH_SHORT).show();
//                            mEditTxtPassword.setError(getString(R.string.error_weak_password));
                            mEditTxtPassword.setError(getString(R.string.PasswordAuthenticationRowPlaceholderNewPassword));
                            mEditTxtPassword.requestFocus();
                        } catch(FirebaseAuthInvalidCredentialsException e) {
                            Log.e("TAG", e.getMessage());
                        } catch(FirebaseAuthUserCollisionException e) {
                            mEditTxtEmail.setError(getString(R.string.AlertMessageEmailAlreadyInUse));
                            mEditTxtEmail.requestFocus();
                        } catch(Exception e) {
                            Log.e("TAG", e.getMessage());
                        }

                    }else{
                        Log.d("TAG", "signInWithEmail:onComplete:" + task.isSuccessful());
                        setUserDisplayName();
//                        reAuthFirebase();
                    }
                }


            });
//            mAuthListener = new FirebaseAuth.AuthStateListener() {
//                @Override
//                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                    FirebaseUser user = firebaseAuth.getCurrentUser();
//                    if(user!=null){
//                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                                .setDisplayName("try a name").build();
//                        user.updateProfile(profileUpdates);
//
//                        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
//                        Log.w("user888"," "+u.getUid());
//                        if (user != null) {
//                            // Name, email address etc
//                            String name = u.getDisplayName();
//                            String email = u.getEmail();
//                            Log.w("user888"," "+name);
//                            Log.w("user888"," "+email);
//                        }
//
//                    }
//                }
//            };


        }


    }


    private void reAuthFirebase(){
        String email = mEditTxtEmail.getText().toString();
        final String nickname = mEditTxtNickname.getText().toString();
        String password = mEditTxtPassword.getText().toString();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(firebaseUser.getEmail(), password);

        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("999", firebaseUser.getDisplayName());
                        goMainScreen();

                    }
                });
    }



    private void setUserDisplayName(){


        String email = mEditTxtEmail.getText().toString();
        final String nickname = mEditTxtNickname.getText().toString();
        String password = mEditTxtPassword.getText().toString();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nickname)
//                .setPhotoUri(Uri.parse("http://serviceapi.skholingua.com/logo.png"))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("user888", "User profile updated.");
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            Log.w("user888"," "+user.getUid());
                            reAuthFirebase();//firebase bug... that re-authenticating the user at the time of their registration may fix the problem
                        }
                    }
                });


    }




    private void goMainScreen() {
        Intent intent = new Intent(SinginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putString("srcClass", "SinginActivity");
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
