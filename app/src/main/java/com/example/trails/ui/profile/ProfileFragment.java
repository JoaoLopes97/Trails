package com.example.trails.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.trails.R;
import com.example.trails.controller.DB;
import com.example.trails.ui.login.LoginActivity;
import com.example.trails.model.User;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;

    private String userId;
    private ImageButton logout;
    private TextView username;
    private TextView verPerfil;
    private ImageView userPhoto;

    private FirebaseFirestore fireStore;

    private LinearLayout barraEditPerfil;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.profile_fragment, container, false);

        barraEditPerfil = root.findViewById(R.id.barraPerfil);
        username = root.findViewById(R.id.usernameText);
        verPerfil = root.findViewById(R.id.viewProfileText);
        userPhoto = root.findViewById(R.id.imageViewUserPhoto);

        loadData(root);


        barraEditPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        logout = root.findViewById(R.id.sign_out_menu);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                //FirebaseUser user = mAuth.getCurrentUser();


                /*AccessToken.setCurrentAccessToken(null);
                if (LoginManager.getInstance() != null) {
                    LoginManager.getInstance().logOut();
                }*/


                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    public void loadData(final View root) {
        fireStore = FirebaseFirestore.getInstance();

        userId  = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference df = fireStore.collection("users").document(userId);

        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User userObject = documentSnapshot.toObject(User.class);
                username.setText(userObject.getName());

                if(userObject.getPhoto() == null) {
                    userPhoto.setImageResource(R.drawable.ic_baseline_account_circle_24);
                } else {
                    DB.loadWithGlide(root.getContext(), userObject.getPhoto(), userPhoto);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        username.setText("Username");
                    }
                });
    }
}