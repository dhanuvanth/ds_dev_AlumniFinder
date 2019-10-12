package com.example.aluminifinder;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Nullable;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.NoteHolder> {

    private Context context;
    private List<Note> noteList;
    private List<String> docList = new ArrayList<>();
    Note note;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String currentDate;

    public EventAdapter(Context context, List<Note> noteList) {
        this.context = context;
        this.noteList = noteList;
    }

    @NonNull
    @Override
    public EventAdapter.NoteHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
        return new EventAdapter.NoteHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final NoteHolder noteHolder, int i) {
        note = noteList.get(i);
        blockPeriod();
        if (note.getDate_txt().equals(currentDate)) {
            db.collection("Emails").document(mAuth.getCurrentUser().getEmail())
                    .collection("Events").document(note.getName_txt()).delete();
        } else {
            noteHolder.card_name.setText(note.getName_txt());
            noteHolder.card_college.setText(note.getCollege_txt());
        }
        noteHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View v, int position) {

                db.collection("Emails").document(mAuth.getCurrentUser().getEmail()).collection("Events").document(note.getName_txt())
                        .collection("Accepted").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            docList.clear();
                            for (QueryDocumentSnapshot documentSnapshot :queryDocumentSnapshots) {
                                String name = documentSnapshot.get("name").toString();
                                docList.add(name);
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Invitation Accepted")
                                    .setMessage(""+docList)
                                    .create().show();
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
