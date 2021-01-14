package com.example.trails.controller;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.trails.model.Trail;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

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

        Glide.with(context)
                .load(imageUrl)
                .into(imageView);
    }
}
