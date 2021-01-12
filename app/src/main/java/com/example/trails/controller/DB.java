package com.example.trails.controller;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.trails.model.Comment;
import com.example.trails.model.Trail;
import com.example.trails.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.Instant;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class DB {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public DB() {

    }
/*
    public void insertData(String collection, Object object) {

        db.collection(collection)
                .add(object)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });


        User u = new User("joao","email");
        Comment cm = new Comment(u, "adorei", 5);

        db.collection(collection)
                .document("gtSQJ1q1BZhMBtIeNxgN")
                .collection("comments").add(cm)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }*/

    public static void loadWithGlide(Context context, String imageUrl, ImageView imageView) {

        // Download directly from StorageReference using Glide
        // (See MyAppGlideModule for Loader registration)

        Glide.with(context /* context */)
                .load(imageUrl)
                .into(imageView);
        // [END storage_load_with_glide]
    }
    
    public Trail retrieveData(String str) {
        DocumentReference dc = db.collection("trails").document("nZPmn3x9wj9VjBH9d4Og");

        final Trail[] t = new Trail[1];
        dc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    t[0] = document.toObject(Trail.class);
                    return;
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
        return t[0];
    }
}
