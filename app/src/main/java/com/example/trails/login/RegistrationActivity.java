package com.example.trails.login;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.example.trails.ui.profile.EditProfileActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    private static int REQUESCODE = 1;
    private static int PReqCode = 1;
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;
    private ImageView ImgUserPhoto;
    private Uri pickedImgUri;
    private TextInputLayout userEmail, userPassword, userName,userBirthdayContainer, userCity;
    private TextInputEditText userBirthdayText;
    private DatePickerDialog.OnDateSetListener dataPickerListener;
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
        String name, email, password, birthday, city;
        name = userName.getEditText().getText().toString().trim();
        email = userEmail.getEditText().getText().toString().trim();
        password = userPassword.getEditText().getText().toString().trim();
        birthday = userBirthdayText.getText().toString().trim();
        city = userCity.getEditText().getText().toString().trim();

        verificationOfInputs(name);

        if (email.isEmpty() || password.isEmpty()) {
            msgError.setText(R.string.msgError_fields);
            return;
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), R.string.seccessRegister, Toast.LENGTH_LONG).show();
                            if(pickedImgUri != null){
                                updateUserInfo(pickedImgUri, user);
                            }else{
                                DocumentReference df = fireStore.collection("users").document(user.getUid());
                                User newUser = new User(userName.getEditText().getText().toString().trim(), userEmail.getEditText().getText().toString().trim(), null, null , user.getUid(), null);
                                df.set(newUser);
                                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
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
                        User newUser = new User(userName.getEditText().getText().toString().trim(), userEmail.getEditText().getText().toString().trim(), convertStrToDate(userBirthdayText.getText().toString().trim()), userCity.getEditText().getText().toString().trim() , currentUser.getUid(), uri.toString());
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
        ImgUserPhoto = findViewById(R.id.regUserPhoto);
        userName = findViewById(R.id.username);
        userEmail = findViewById(R.id.emailLogin);
        userPassword = findViewById(R.id.passwordLogin);
        userBirthdayText = findViewById(R.id.txtBirthday);
        userBirthdayContainer = findViewById(R.id.userBirthday);
        userCity = findViewById(R.id.userCity);
        regBtn = findViewById(R.id.btnRegister);
        msgError = findViewById(R.id.msgError);
        loginRes = findViewById(R.id.btnLogin);

        userBirthdayContainer.setStartIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        RegistrationActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dataPickerListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dataPickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                String date = day + "/" + String.format("%01d", month) + "/" + year;
                userBirthdayText.setText(date);
            }
        };

        userBirthdayText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Check if data is valid
                SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
                sdf.setLenient(false);
                try {
                    sdf.parse(userBirthdayContainer.getEditText().getText().toString());
                    userBirthdayContainer.setError(null);
                } catch (ParseException e) {
                    userBirthdayContainer.setError("A data inserida é inválida.");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


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

    private Date convertStrToDate(String userBirthday){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(userBirthday);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void verificationOfInputs(String name){
        if (name.length() == 0 || name.isEmpty()) {
            userName.setError(getString(R.string.errorUserName));
            userName.setErrorEnabled(true);
        } else {
            userName.setErrorEnabled(false);
        }

        userEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {

                //text.trim()
                if (text.length() == 0 ) {
                    userEmail.setError(getString(R.string.errorUserEmail));
                    userEmail.setErrorEnabled(true);
                }else if(!validateEmail(text.toString())) {
                    userEmail.setError(getString(R.string.errorUserEmail1));
                    userEmail.setErrorEnabled(true);
                }else{
                    userEmail.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        userPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() == 0 ) {
                    userPassword.setError(getString(R.string.errorUserPassword));
                    userPassword.setErrorEnabled(true);
                }else if(!validatePassword(text.toString())) {
                    userPassword.setError(getString(R.string.errorUserPassword1));
                    userPassword.setErrorEnabled(true);
                }else{
                    userPassword.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private boolean validateEmail(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean validatePassword(String password) {
        return password.length() > 5;
    }
}