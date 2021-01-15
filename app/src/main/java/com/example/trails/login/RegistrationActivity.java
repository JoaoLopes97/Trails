package com.example.trails.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.trails.R;
import com.example.trails.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegistrationActivity extends AppCompatActivity {

    private static int REQUESCODE = 1;
    private static int PReqCode = 1;
    private ImageView ImgUserPhoto;
    private Uri pickedImgUri;
    private TextInputLayout email, password, passwordConf, username;
    private Button regBtn;
    private TextView msgError;

    private Button loginRes;

    private FirebaseAuth mAuth;
    private FirebaseFirestore fireStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        initializeUI();

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });

        ImgUserPhoto = findViewById(R.id.regUserPhoto);

        ImgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestForPermission();
                } else {
                    openGallery();
                }
            }
        });

        loginRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void registerNewUser() {
        String email, password, passwordConf;
        email = this.email.getEditText().getText().toString();
        password = this.password.getEditText().getText().toString();
        passwordConf = this.passwordConf.getEditText().getText().toString();

        //Verificar campos

        if(email.isEmpty()) {
            this.email.setError("Por favor insira um email");
            if(!isValidEmail(email)) {
                this.email.setError("Por favor insira um email válido.");
            }
        }



        if (email.isEmpty() || password.isEmpty()) {
            msgError.setText(R.string.msgError_emailPass);
            return;
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)

                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), R.string.seccessRegister, Toast.LENGTH_LONG).show();
                            // after we created user account we need to update his profile picture and name
                            updateUserInfo(pickedImgUri, user);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    msgError.setText(R.string.msgError);
                }
            });
        }
    }

    // update user photo and name
    private void updateUserInfo(Uri pickedImgUri, final FirebaseUser currentUser) {
        // first we need to upload user photo to firebase storage and get url
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DocumentReference df = fireStore.collection("users").document(currentUser.getUid());
                        User newUser = new User(username.getEditText().getText().toString(), email.getEditText().getText().toString(), currentUser.getUid(), uri.toString());
                        df.set(newUser);
                        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });

    }

    private void initializeUI() {
        email = findViewById(R.id.emailLogin);
        password = findViewById(R.id.passwordLogin);
        passwordConf = findViewById(R.id.passwordLoginConf);
        regBtn = findViewById(R.id.registerLogin);
        msgError = findViewById(R.id.msgError);
        username = findViewById(R.id.username);
        loginRes = findViewById(R.id.registerLoginBtn);
    }

    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image !
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESCODE);
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegistrationActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(RegistrationActivity.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(RegistrationActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
            }
        } else
            openGallery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {
            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData();
            ImgUserPhoto.setImageURI(pickedImgUri);
        }
    }

    static boolean isValidEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

}