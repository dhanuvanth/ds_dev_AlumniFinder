package com.example.aluminifinder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

import javax.annotation.Nullable;

public class NotificationActivity extends AppCompatActivity {

    private ArrayList<Note> doc_list;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String currentEmail;
    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        currentEmail = mAuth.getCurrentUser().getEmail();

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        doc_list = new ArrayList<>();
        db.collection("Emails").document(currentEmail).collection("Notification").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                doc_list.clear();
                if (queryDocumentSnapshots != null) {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Note note = documentSnapshot.toObject(Note.class);
                        note.setDocumentId(documentSnapshot.getId());

                        String name = note.getName_txt();
                        String college = note.getCollege_txt();
                        String date = note.getDate_txt();
                        id = note.getDocumentId();
                        doc_list.add(new Note(name,college,date,id));
                    }
                }
                recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
                adapter = new NoteAdapter(NotificationActivity.this,doc_list);
                Collections.reverse(doc_list);
                recyclerView.setAdapter(adapter);
            }
        });
    }
}
