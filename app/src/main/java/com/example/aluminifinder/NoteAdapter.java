package com.example.aluminifinder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Nullable;

class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {

    private Context context;
    private List<Note> noteList;
    private String docid;
    private String date_txt;
    private String email_txt;
    private String college_txt;
    private String time_txt;
    private String name_txt;
    private String loc_txt;
    private String remarks_txt;
    private String id;
    Note note;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String currentDate;
    private String name1;

    public NoteAdapter(Context context, List<Note> noteList) {
        this.context = context;
        this.noteList = noteList;
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
        return new NoteHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final NoteHolder noteHolder, int i) {
        note = noteList.get(i);
        blockPeriod();
        if (note.getDate_txt().equals(currentDate)){
            db.collection("Emails").document(mAuth.getCurrentUser().getEmail())
                    .collection("Notification").document(note.getDocumentId()).delete();
        }else {
            noteHolder.card_name.setText(note.getName_txt());
            noteHolder.card_college.setText(note.getCollege_txt());
        }
        noteHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                final String name = noteHolder.card_name.getText().toString();

                db.collection("Emails").document(mAuth.getCurrentUser().getEmail()).collection("Notification").get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (queryDocumentSnapshots != null) {
                                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                        Note noted = snapshot.toObject(Note.class);

                                        docid = noted.getName_txt();
                                        if (docid.equals(name)) {
                                            noted.setDocumentId(snapshot.getId());
                                            id = noted.getDocumentId();
                                            name_txt = noted.getName_txt();
                                            college_txt = noted.getCollege_txt();
                                            email_txt = noted.getEmail_txt();
                                            loc_txt = noted.getLoc_txt();
                                            date_txt = noted.getDate_txt();
                                            time_txt = noted.getTime_txt();
                                            remarks_txt = noted.getRemarks_txt();
                                        }
                                    }

                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Invitation")
                                            .setMessage("Name : " + name_txt + "\n\n" +
                                                    "College : " + college_txt + "\n\n" +
                                                    "Email : " + email_txt + "\n\n" +
                                                    "Location : " + loc_txt + "\n\n" +
                                                    "Date : " + date_txt + "\n\n" +
                                                    "Time : " + time_txt + "\n\n" +
                                                    "Remarks : " + remarks_txt + "\n\n")
                                            .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    db.collection("Emails").document(mAuth.getCurrentUser().getEmail())
                                                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                                                    name1 = documentSnapshot.get("name1").toString();
                                                                    Note note = new Note(name1);
                                                                    db.collection("Emails").document(email_txt)
                                                                            .collection("Events").document(name_txt)
                                                                            .collection("Accepted").add(note);
                                                                    //Toast.makeText(context, ""+name1, Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            })
                                            .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    db.collection("Emails").document(mAuth.getCurrentUser().getEmail())
                                                            .collection("Notification").document(id).delete();
                                                }
                                            }).create().show();
                                }
                            }
                        });

            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    private void blockPeriod() {
        Calendar c = Calendar.getInstance();
        c.get(Calendar.YEAR);
        c.get(Calendar.MONTH);
        c.get(Calendar.DAY_OF_MONTH);
        currentDate = DateFormat.getDateInstance(DateFormat.LONG).format(c.getTime());
    }

    class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
         TextView card_name;
         TextView card_college;

        private ItemClickListener itemClickListener;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            card_name = itemView.findViewById(R.id.card_name);
            card_college = itemView.findViewById(R.id.card_college);

            itemView.setOnClickListener(this);
        }

        void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition());
        }
    }
}
