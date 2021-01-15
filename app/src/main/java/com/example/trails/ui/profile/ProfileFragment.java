package com.example.trails.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.trails.R;
import com.example.trails.login.LoginActivity;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;

    private String userId;
    private ImageButton logout;
    private TextView username;

    private LinearLayout barraEditPerfil;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.profile_fragment, container, false);

        userId  = FirebaseAuth.getInstance().getCurrentUser().getUid();

        username = root.findViewById(R.id.usernameText);
        username.setText("teste");

        barraEditPerfil = root.findViewById(R.id.barraPerfil);

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
}