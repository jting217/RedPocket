package com.kanhan.redpocket;

import android.content.Intent;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by USER on 2016/12/19.
 */

public class EmailLoginActivity  extends AppCompatActivity {

    private EditText emailText;
    private EditText passWordText;
    private FirebaseAuth mfirebaseAuth;
    private Button buttonDone;
    private Button buttonCancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emaillogin);
        mfirebaseAuth = FirebaseAuth.getInstance();
        emailText = (EditText) findViewById(R.id.editText);
        passWordText = (EditText) findViewById(R.id.editText2);
        Button buttonDone = (Button)findViewById(R.id.button6);
        Button buttonCancel = (Button)findViewById(R.id.button3);
        Toolbar toolbar ;
        toolbar = (Toolbar)findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eMailLogin();

            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    private void eMailLogin() {
        final String email = emailText.getText().toString();
        final String password = passWordText.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "帳密不能為空", Toast.LENGTH_LONG).show();
        } else {
            mfirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d("TAG", email + password);
                    Log.d("TAG", "signInWithEmail:onComplete:" + task.isSuccessful());
                    if (!task.isSuccessful()) {
                        Log.w("TAG", "signInWithEmail:failed", task.getException());
                    }else{
                        goMainScreen();
                    }
                }
            });
        }
    }

    private void goMainScreen() {
        Intent intent = new Intent(EmailLoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}


