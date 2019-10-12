package com.example.aluminifinder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class AlumniSearchActivity extends AppCompatActivity {

    private Spinner state_search, district_search, college_search;
    private Button b_search;

    private String sr_state;
    private String sr_district;
    private String sr_college;
    private String email;
    private TextView t_state, t_district, t_college;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    List<String> lst_state, lst_district, lst_college;
    HashSet<String> set_state, set_district, set_college;
    ArrayAdapter<String> array_state;
    ArrayAdapter<String> array_district;
    ArrayAdapter<String> array_college;
    private LocationManager locationManager;
    private boolean connected;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumni_search);

        FirebaseApp.initializeApp(this);

        setTitle("Alumni Search");
        //To get email
        email = mAuth.getCurrentUser().getEmail();

        initialization();
        state();
        district();
        college();
        search();
    }

    @Override
    protected void onStart() {
        super.onStart();
        state();
        district();
        college();
    }

    private void initialization() {
        state_search = findViewById(R.id.search_state);
        district_search = findViewById(R.id.search_district);
        college_search = findViewById(R.id.search_college);
        b_search = findViewById(R.id.btn_search);
        t_state = findViewById(R.id.tv_state_search);
        t_district = findViewById(R.id.tv_district_search);
        t_college = findViewById(R.id.tv_college_search);
    }

    private void state() {
        //list all items
        lst_state = new ArrayList<>();
        array_state = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_layout, lst_state);
        set_state = new HashSet<>();
        final String toolName = t_state.getText().toString();

        db.collection(toolName).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                lst_state.clear();
                set_state.clear();

                if (queryDocumentSnapshots != null) {
                    for (QueryDocumentSnapshot DocumentSnapshot : queryDocumentSnapshots) {
                        Note note = DocumentSnapshot.toObject(Note.class);
                        if (note.getName().length() != 0) {
                            String name = note.getName().substring(0, 1).toUpperCase() + note.getName().substring(1).toLowerCase();
                            set_state.add(name);
                        }
                        lst_state.clear();
                        lst_state.addAll(set_state);
                    }
                }
                array_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                array_state.notifyDataSetChanged();
                state_search.setAdapter(array_state);
                Collections.reverse(lst_state);
            }
        });

        state_search.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sr_state = array_state.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void district() {
        lst_district = new ArrayList<>();
        set_district = new HashSet<>();
        array_district = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_layout, lst_district);
        final String Name = t_district.getText().toString();

        registerForContextMenu(district_search);

        db.collection(Name).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                set_district.clear();
                lst_district.clear();
                if (queryDocumentSnapshots != null) {
                    for (QueryDocumentSnapshot DocumentSnapshot : queryDocumentSnapshots) {
                        Note note = DocumentSnapshot.toObject(Note.class);
                        if (note.getName().length() != 0) {
                            String name = note.getName().substring(0, 1).toUpperCase() + note.getName().substring(1).toLowerCase().trim();
                            set_district.add(name);
                        }
                        lst_district.clear();
                        lst_district.addAll(set_district);
                    }
                }
                array_district.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                array_district.notifyDataSetChanged();
                district_search.setAdapter(array_district);
                Collections.reverse(lst_district);
            }
        });

        district_search.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sr_district = array_district.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void college() {
        lst_college = new ArrayList<>();
        set_college = new HashSet<>();
        array_college = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_layout, lst_college);
        final String Name = t_college.getText().toString();

        registerForContextMenu(college_search);

        db.collection(Name).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                lst_college.clear();
                set_college.clear();
                if (queryDocumentSnapshots != null) {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Note note = documentSnapshot.toObject(Note.class);
                        if (note.getName().length() != 0) {
                            String name = note.getName().substring(0, 1).toUpperCase() + note.getName().substring(1).toLowerCase().trim();
                            set_college.add(name);
                        }
                        lst_college.clear();
                        lst_college.addAll(set_college);
                    }
                }
                array_college.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                array_college.notifyDataSetChanged();
                college_search.setAdapter(array_college);
                Collections.reverse(lst_college);
            }
        });

        college_search.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sr_college = array_college.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void search() {
        b_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connected = false;
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                    Intent i = new Intent(AlumniSearchActivity.this, MapsActivity.class);
                    i.putExtra("state", sr_state);
                    i.putExtra("district", sr_district);
                    i.putExtra("college", sr_college);
                    startActivity(i);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Snackbar.make(v, "No Internet Connection...", Snackbar.LENGTH_LONG).show();
                    connected = false;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_id:
                Intent i = new Intent(AlumniSearchActivity.this, PersonalInfoActivity.class);
                startActivity(i);
                break;

            case R.id.event_id:
                i = new Intent(getApplicationContext(), EventActivity.class);
                startActivity(i);
                break;

            case R.id.notification_id:
                Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
                startActivity(intent);
                break;
            case R.id.about:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Developed By")
                        .setMessage("S.Dhanuvanth\ndhanuvanth@gmail.com\n+91 7010 384 896")
                        .create().show();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AlumniSearchActivity.this);
        builder.setTitle("Logout").setMessage("Do you want to logout?").setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Map<String,Object> tokenRemove = new HashMap<>();
                tokenRemove.put("token_id", FieldValue.delete());

                db.collection("Emails").document(mAuth.getCurrentUser().getEmail()).update(tokenRemove).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mAuth.signOut();
                        Intent i = new Intent(AlumniSearchActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
            }
        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }
}
