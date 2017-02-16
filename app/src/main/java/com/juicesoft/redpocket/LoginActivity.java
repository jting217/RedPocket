package com.juicesoft.redpocket;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LoginActivity extends AppCompatActivity {
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    //Firebase的連結
    private FirebaseAuth mfirebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    FirebaseDatabase database;
    DatabaseReference myRef;

    private Button getLoginButton2;
    private Button getSignin;
    //private EditText emailText;
   // private EditText passWordText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        FirebaseAuth auth = FirebaseAuth.getInstance();

       // emailText = (EditText) findViewById(R.id.editText);
       //passWordText = (EditText) findViewById(R.id.editText);
        getLoginButton2 = (Button) findViewById(R.id.loginbtn);
        getSignin = (Button) findViewById(R.id.signbtn);

      //EMAIL登入
        getLoginButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEmail = new Intent();
                intentEmail.setClass(LoginActivity.this,EmailLoginActivity.class);
                startActivity(intentEmail);

            }
        });

        getSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSignup = new Intent();
                intentSignup.setClass(LoginActivity.this,SinginActivity.class);

                startActivity(intentSignup);

            }
        });


        //Fb登入
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.loginButton);
        loginButton.setReadPermissions("email", "public_profile");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                Log.d("", "facebookSuccess" + loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        //確認使用者是否有登入Firebase
        mfirebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser muser = firebaseAuth.getCurrentUser();

                if (muser != null) {

                    Log.d("TAG", "onAuthStateChanged:signed_in:" + muser.getUid());
                    goMainScreen();
                } else {
                    Log.d("TAG", "onAuthStateChanged:signed_out");

                }
            }
        };


    }

    //獲取FB的使用者權杖並註冊為Firebase的用戶
    private void handleFacebookAccessToken(AccessToken accessToken) {

        Log.d("TAG", "handleFacebookAccessToken:" + accessToken);
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        Log.d("TAG", "" + credential);
        //註冊Firebase用戶
        mfirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("TAG", "signInWithCredential:onComplete:" + task.isSuccessful());

                database = FirebaseDatabase.getInstance();
                myRef = database.getReference("User");
                FirebaseUser testUser = FirebaseAuth.getInstance().getCurrentUser();
                //取得Firebase設置的使用者ID
                String testUid = testUser.getUid();

                // myRef.setValue(testUid);
                Log.d("tag", testUid);


                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Id", testUid);

                bundle.putString("srcClass", "LoginActivity");

                intent.putExtras(bundle);

                startActivity(intent);


                if (!task.isSuccessful()) {
                    Log.w("TAG", "signInWithCredential", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    private void goMainScreen() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mfirebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            mfirebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }

    }
}
