package com.example.trails.controller;

import android.app.Fragment;
import android.content.Context;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.trails.model.Comment;
import com.example.trails.model.Trail;
import com.example.trails.model.User;
import com.example.trails.ui.details.DetailsTrailFragment;
import com.example.trails.ui.explore.TrailAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.nio.InvalidMarkException;
import java.util.List;
import java.time.Instant;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.example.trails.MainActivity.db;

public class DB {

    public static void insertTrail(final Trail trail) {

        db.collection("trails")
                .add(trail)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        trail.setId(documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }


    public static void loadWithGlide(Context context, String imageUrl, ImageView imageView) {

        // Download directly from StorageReference using Glide
        // (See MyAppGlideModule for Loader registration)

        Glide.with(context /* context */)
                .load(imageUrl)
                .into(imageView);
        // [END storage_load_with_glide]
    }

/*
    public static void retrieveData(String trailId) {
        DocumentReference dc = db.collection("trails").document(trailId);

        dc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    TrailAdapter.selectedTrail =  document.toObject(Trail.class);
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }*/
}
