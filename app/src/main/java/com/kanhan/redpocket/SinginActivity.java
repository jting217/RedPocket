package com.kanhan.redpocket;

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


public class SinginActivity extends AppCompatActivity {

    private EditText emailText;
    private EditText passWordText;
    private EditText passWordText2;
    private FirebaseAuth mfirebaseAuth;
    private Button buttonDone;
    private Button buttonCancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        mfirebaseAuth = FirebaseAuth.getInstance();
        Toolbar toolbar ;
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Button buttonDone = (Button)findViewById(R.id.button5);
        Button buttonCancel = (Button)findViewById(R.id.button4);
        emailText =(EditText)findViewById(R.id.editText);
        passWordText = (EditText)findViewById(R.id.editText2);
        passWordText2 = (EditText)findViewById(R.id.editText3);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creatUser();
            }
        });


    }
    private void creatUser() {
        String email = emailText.getText().toString();
        String password = passWordText.getText().toString();
        String password2 = passWordText2.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)||TextUtils.isEmpty(password2)) {
            Toast.makeText(this, "帳密不能為空", Toast.LENGTH_LONG).show();
        } else {
            mfirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d("TAG", "signInWithEmail:onComplete:" + task.isSuccessful());
                    if (!task.isSuccessful()) {
                        Log.w("TAG", "signInWithEmail:failed", task.getException());
                    }
                }
            });
        }

    }

}
