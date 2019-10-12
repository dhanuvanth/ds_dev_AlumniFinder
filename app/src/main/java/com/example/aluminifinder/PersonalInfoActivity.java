package com.example.aluminifinder;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class PersonalInfoActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private EditText et_name_1;
    private TextView et_email;
    private EditText et_number;
    private RadioGroup r_group;
    private RadioButton r_btn;
    private TextView tv_date;
    private Button b_submit;
    private TextView t_state, t_district, t_college;
    private Spinner s_state;
    private Spinner s_district;
    private Spinner s_college;
    private ImageView b_state, b_district, b_college;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    List<String> lst_state, lst_district, lst_college;
    HashSet<String> set_state, set_district, set_college;
    ArrayAdapter<String> array_state;
    ArrayAdapter<String> array_district;
    ArrayAdapter<String> array_college;

    private String name_1, email, number;
    private String state_txt;
    private String district_txt;
    private String college_txt;
    private String date;
    private String radio;
    private String email_title;
    private String phone_txt;
    private String token_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        //firebase instance
        FirebaseApp.initializeApp(this);

        setTitle("Personal Info");

        initialization();
        date();
        radio_btn();
        state();
        district();
        college();

        b_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_data();
            }
        });

        //getintent and convert to string
        email_title = mAuth.getCurrentUser().getEmail();
        phone_txt = getIntent().getStringExtra("phone");
    }

    @Override
    protected void onStart() {
        super.onStart();
        state();
        district();
        college();

        //display email
        et_email.setText(email_title);
    }

    private void initialization() {
        et_name_1 = findViewById(R.id.et_1_name);
        et_email = findViewById(R.id.email_person);
        et_number = findViewById(R.id.et_number);
        r_group = findViewById(R.id.rd_group);
        tv_date = findViewById(R.id.tv_date);
        b_submit = findViewById(R.id.btn_submit);
        t_state = findViewById(R.id.tv_state);
        t_district = findViewById(R.id.tv_district);
        t_college = findViewById(R.id.tv_college);
        s_state = findViewById(R.id.spin_state);
        s_district = findViewById(R.id.spin_district);
        s_college = findViewById(R.id.spin_college);
        b_state = findViewById(R.id.iv_state);
        b_district = findViewById(R.id.iv_district);
        b_college = findViewById(R.id.iv_college);
    }

    private void date() {
        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new DialogDateFragment();
                dialogFragment.show(getSupportFragmentManager(), "Date picker");
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDate = DateFormat.getDateInstance(DateFormat.LONG).format(c.getTime());
        tv_date.setText(currentDate);
        date = tv_date.getText().toString();
    }

    private void radio_btn() {
        r_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectId = group.getCheckedRadioButtonId();
                r_btn = findViewById(selectId);
                r_btn.setChecked(true);
                radio = r_btn.getText().toString();
            }
        });
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
                s_state.setAdapter(array_state);
                Collections.reverse(lst_state);
            }
        });

        b_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogbox(toolName);
            }
        });

        s_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                state_txt = array_state.getItem(position);
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

        registerForContextMenu(s_district);

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
                s_district.setAdapter(array_district);
                Collections.reverse(lst_district);
            }
        });

        b_district.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogbox(Name);
            }
        });

        s_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                district_txt = array_district.getItem(position);
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

        registerForContextMenu(s_college);

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
                s_college.setAdapter(array_college);
                Collections.reverse(lst_college);
            }
        });

        b_college.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogbox(Name);
            }
        });

        s_college.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                college_txt = array_college.getItem(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void dialogbox(final String name) {
        android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(PersonalInfoActivity.this);
        @SuppressLint("InflateParams") final View inflater = getLayoutInflater().inflate(R.layout.login_dialog, null);
        TextView tv_name = inflater.findViewById(R.id.tv_title);
        final EditText itemAdd = inflater.findViewById(R.id.title_txt);
        String title = name.trim();
        tv_name.setText(title);
        itemAdd.setHint("Enter the name...");
        itemAdd.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(inflater).setPositiveButton("add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String item = itemAdd.getText().toString();

                Note note = new Note(item);
                db.collection(name).add(note);

            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(true).create().show();
    }

    private void save_data() {
        name_1 = et_name_1.getText().toString();
        et_email.setText(email_title);
        email = et_email.getText().toString();
        number = et_number.getText().toString();

        if ((name_1 != null) && (email != null) && (number != null) && (radio != null) && (date != null)) {
            Note note = new Note(state_txt, district_txt, college_txt, name_1, email, number, date, radio,0,0);
            db.collection("Emails").document(email).set(note);

            token_id = FirebaseInstanceId.getInstance().getToken();
            Map<String,Object> tokenMap = new HashMap<>();
            tokenMap.put("token_id",token_id);
            db.collection("Emails").document(email).update(tokenMap);
            Intent i = new Intent(PersonalInfoActivity.this,LoginActivity.class);
            startActivity(i);
            finish();
        }
    }
}
