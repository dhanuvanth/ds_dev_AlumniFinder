package com.example.aluminifinder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText et_email;
    private EditText et_password;
    private Button b_login;
    private TextView tv_signUp;
    private ProgressBar progressBar;

    private String email, password;
    private boolean connected;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginscreen);

        //database
        FirebaseApp.initializeApp(this);

        et_email = findViewById(R.id.email_et);
        et_password = findViewById(R.id.password_et);
        b_login = findViewById(R.id.btn_login);
        tv_signUp = findViewById(R.id.tv_sign_up);
        progressBar = findViewById(R.id.progress_login);

        tv_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connected = false;
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                    Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Snackbar.make(v, "No Internet Connection...", Snackbar.LENGTH_LONG).show();
                    connected = false;
                }
            }
        });

        b_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = et_email.getText().toString().trim();
                password = et_password.getText().toString();

                //progressBar
                progressBar.setVisibility(View.VISIBLE);
                //check if email exist
                //check not email field is empty
                connected = false;
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;

                    if (!email.equals("")) {
                        mAuth.fetchProvidersForEmail(email).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<ProviderQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                try {
                                    boolean check = !task.getResult().getProviders().isEmpty();

                                    if (!check) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(LoginActivity.this, "Email does not Exists", Toast.LENGTH_SHORT).show();
                                    } else {
                                        //sign in
                                        //check not email and password is empty
                                        if (!email.equals("") && !password.equals("")) {
                                            mAuth.signInWithEmailAndPassword(email, password)
                                                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if (task.isSuccessful()) {

                                                                String token_id = FirebaseInstanceId.getInstance().getToken();
                                                                Map<String,Object> tokenMap = new HashMap<>();
                                                                tokenMap.put("token_id",token_id);

                                                                db.collection("Emails").document(mAuth.getCurrentUser().getEmail())
                                                                        .update(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Intent i = new Intent(getApplicationContext(),AlumniSearchActivity.class);
                                                                        startActivity(i);
                                                                        finish();
                                                                    }
                                                                });

                                                            } else {
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                Toast.makeText(LoginActivity.this, "Password Incorrect", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(LoginActivity.this, "Enter All Fields", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } catch (Exception e) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(LoginActivity.this, "Check you Email Address!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, "Enter the Email Address", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Snackbar.make(v, "No Internet Connection...", Snackbar.LENGTH_LONG).show();
                    connected = false;
                }
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Intent i = new Intent(LoginActivity.this, AlumniSearchActivity.class);
            i.putExtra("emails", email);
            startActivity(i);
            finish();
        } else {
            // No user is signed in

        }

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Exit").setMessage("Do you want to exit?").setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }
}
