package com.example.aluminifinder;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;

public class MeetUpActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private EditText m_name, m_loc, m_remarks;
    private TextView m_date, m_time;
    private Button b_send;

    private String date_txt;
    private String email_txt;
    private String time_txt;
    private String name_txt;
    private String loc_txt;
    private String remarks_txt;

    private NotificationManagerCompat notificationManagerCompat;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet_up);

        Initialization();
        date();
        time();

        notification_channal();
    }

    private void notification_channal() {
        b_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name_txt = m_name.getText().toString();
                loc_txt = m_loc.getText().toString();
                remarks_txt = m_remarks.getText().toString();
                //notificationManagerCompat = NotificationManagerCompat.from(this);

            /*  Intent i = new Intent(MeetUpActivity.this,LoginActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(MeetUpActivity.this,0,i,0);

                Notification notification = new NotificationCompat.Builder(MeetUpActivity.this,"channrl1")
                        .setContentTitle(auth.getCurrentUser().getEmail())
                        .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                        .setContentText("Name : " + name_txt + "\n" +
                                "Time : " + time_txt + "\n" +
                                "Date : " + date_txt + "\n" +
                                "loc : " + loc_txt + "\n" +
                                "Remarks : " + remarks_txt + "\n")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setContentIntent(pendingIntent)
                        .build();
                notificationManagerCompat.notify(1 ,notification);*/

                if ((name_txt != null) && (loc_txt != null) && (!TextUtils.isEmpty(date_txt)) && (!TextUtils.isEmpty(time_txt))) {
                    Toast.makeText(MeetUpActivity.this, "Notification send...", Toast.LENGTH_SHORT).show();

                    String email = getIntent().getStringExtra("email_id");
                    String college = getIntent().getStringExtra("college");
                    email_txt = auth.getCurrentUser().getEmail();

                    Note note = new Note(college,date_txt,email_txt,time_txt,name_txt,loc_txt,remarks_txt);

                    db.collection("Emails").document(email).collection("Notification")
                            .add(note);
                    db.collection("Emails").document(auth.getCurrentUser().getEmail()).collection("Events")
                            .document(name_txt)
                            .set(note);
                    finish();
                }else {
                    Toast.makeText(MeetUpActivity.this, "Check all fields and retry!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void Initialization() {
        m_name = findViewById(R.id.et_m_name);
        m_loc = findViewById(R.id.et_m_loc);
        m_time = findViewById(R.id.tv_m_time);
        m_date = findViewById(R.id.tv_m_date);
        m_remarks = findViewById(R.id.et_m_remarks);
        b_send = findViewById(R.id.btn_send);
    }

    private void date() {
        m_date.setOnClickListener(new View.OnClickListener() {
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
        m_date.setText(currentDate);
        date_txt = m_date.getText().toString();
    }

    private void time() {
        m_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new DialogTimeFragment();
                dialogFragment.show(getSupportFragmentManager(), "Time Picker");
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        m_time.setText("" + hourOfDay + ":" + "" + minute);
        time_txt = m_time.getText().toString();
    }
}
