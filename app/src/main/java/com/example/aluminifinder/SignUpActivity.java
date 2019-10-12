package com.example.aluminifinder;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {


    private EditText email;
    private EditText phone_no_signUp;
    private EditText password_new;
    private EditText paseord_confirm;
    private ProgressBar progressBar;

    private String phone_no, emails, p_new, p_confirm;
    private String codeSend;


    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //auth instance
        FirebaseApp.initializeApp(this);

        //initialization
        email = findViewById(R.id.email2_et);
        phone_no_signUp = findViewById(R.id.phone_no2_et);
        password_new = findViewById(R.id.password_new_et);
        paseord_confirm = findViewById(R.id.password_confirm_et);
        progressBar = findViewById(R.id.signUp_progress);
        Button b_signUp = findViewById(R.id.btn_signUp);

        //to send location
        mAuth.useAppLanguage();
        //btn_click
        b_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                emails = email.getText().toString().trim();
                phone_no = phone_no_signUp.getText().toString();
                p_new = password_new.getText().toString();
                p_confirm = paseord_confirm.getText().toString();

                if ((emails.equals("")) && (phone_no.equals("")) && (p_new.equals("")) && p_confirm.equals("")) {
                    Toast.makeText(SignUpActivity.this, "Enter All the Fields!", Toast.LENGTH_SHORT).show();
                }else if((p_new.length() <= 7)){
                    Toast.makeText(SignUpActivity.this, "Password should be minimum 8 charactors", Toast.LENGTH_SHORT).show();
                } else if (p_new.equals(p_confirm)){
                    sendVerficationCode();

                    //verification dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    final View view = getLayoutInflater().inflate(R.layout.verfication_dialog, null);
                    builder.setView(view);
                    builder.setPositiveButton(R.string.verify_code, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText verify_et = view.findViewById(R.id.et_verify);
                            String verify = verify_et.getText().toString();
                            if (verify.equals("")) {
                                Toast.makeText(SignUpActivity.this, "Enter the verification code", Toast.LENGTH_SHORT).show();
                            } else {
                                verifySignInCode(verify);
                            }
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setCancelable(false).create().show();
                }else{
                    Toast.makeText(SignUpActivity.this, "Password Incorrect", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void verifySignInCode(String verify) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSend, verify);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //progressBar invisible
                        progressBar.setVisibility(View.INVISIBLE);
                        if (task.isSuccessful()) {

                            //check for existing account
                            mAuth.fetchProvidersForEmail(emails).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<ProviderQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                    try{
                                    boolean check = !task.getResult().getProviders().isEmpty();
                                    if (!check) {

                                        //back to login
                                        Toast.makeText(SignUpActivity.this, "Successfully verfied", Toast.LENGTH_SHORT).show();
                                        if (p_new.contains(p_confirm)) {
                                            mAuth.createUserWithEmailAndPassword(emails, p_confirm)
                                                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if (task.isSuccessful()) {
                                                                Intent i = new Intent(SignUpActivity.this, PersonalInfoActivity.class);
                                                                i.putExtra("email",emails);
                                                                i.putExtra("phone",""+phone_no);
                                                                startActivity(i);
                                                                finish();
                                                            } else {
                                                                Toast.makeText(SignUpActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } else {
                                                Toast.makeText(SignUpActivity.this, "Password Incorrect", Toast.LENGTH_SHORT).show();
                                        }
                                    }else {
                                        Toast.makeText(SignUpActivity.this, "Email Already Exist", Toast.LENGTH_SHORT).show();
                                    }
                                }catch (Exception e){
                                        Toast.makeText(SignUpActivity.this, "Check your Email Address", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(SignUpActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void sendVerficationCode() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91 "+phone_no,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSend = s;
        }
    };

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
        finish();
    }
}

