package com.example.trails.ui.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.trails.R;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        userImage = this.findViewById(R.id.UserImage);

        userImage.setImageResource(R.mipmap.portrait_background);
    }
}