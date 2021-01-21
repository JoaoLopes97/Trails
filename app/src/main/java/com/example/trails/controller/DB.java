package com.example.trails.controller;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.trails.model.Address;
import com.example.trails.model.SingletonCurrentUser;
import com.example.trails.model.Trail;
import com.example.trails.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Date;

import static android.content.ContentValues.TAG;

public class DB {

    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static FirebaseStorage storage = FirebaseStorage.getInstance();

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

    public static void updateTrail(Trail trail) {
        DocumentReference df = db.collection("trails").document(trail.getId());
        df.set(trail);
    }

    public static void loadWithGlide(Context context, String imageUrl, ImageView imageView) {

        Glide.with(context)
                .load(imageUrl)
                .into(imageView);
    }

    public static void insertUser(final FirebaseUser currentUser, String name, String email, Date dateOfBirth, Address address, String photo) {
        DocumentReference df = db.collection("users").document(currentUser.getUid());
        User newUser = new User(name, email, dateOfBirth, address, currentUser.getUid(), photo);
        df.set(newUser);
    }

    public static void getCurrentUserDB(String userId) {
        DocumentReference df = db.collection("users").document(userId);

        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User userObject = documentSnapshot.toObject(User.class);
                SingletonCurrentUser.setCurrentUserInstance(userObject);
            }
        });
    }

    public static void updateUser(User user) {
        DocumentReference df = db.collection("users").document(user.getIdUser());
        df.set(user);
    }

    public static void getUserDB(String userId, final TextView userNameCard, final ImageView reviewPhotoCard, final Context context) {
        DocumentReference df = db.collection("users").document(userId);

        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User userObject = documentSnapshot.toObject(User.class);
                userNameCard.setText(userObject.getName());
                loadWithGlide(context, userObject.getPhoto(), reviewPhotoCard);
            }
        });
    }
}
