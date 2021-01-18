package com.example.trails.ui.login;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.trails.MainActivity;
import com.example.trails.R;
import com.example.trails.controller.DB;
import com.example.trails.model.Address;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    private static int REQUESCODE = 1;
    private static int PReqCode = 1;
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    final Calendar myCalendar = Calendar.getInstance();
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

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
        if (!verificationOfInputs(userName.getEditText().getText().toString(),
                userEmail.getEditText().getText().toString().trim(),
                userPassword.getEditText().getText().toString().trim(),
                userBirthdayText.getText().toString().trim(),
                userCity.getEditText().getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.msgError_fields, Toast.LENGTH_LONG).show();
        } else {
            String email = userEmail.getEditText().getText().toString().trim();
            String password = userPassword.getEditText().getText().toString().trim();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String name = userName.getEditText().getText().toString();
                            String email = userEmail.getEditText().getText().toString().trim();
                            Date birthday = convertStrToDate(userBirthdayText.getText().toString().trim());
                            Address address = convertCityToAddress(userCity.getEditText().getText().toString());

                            if(pickedImgUri != null){
                                updateUserInfo(pickedImgUri, user, name, email, birthday, address);
                            }else{
                                DB.insertUser(user, name, email, birthday, address, null);
                                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            Toast.makeText(getApplicationContext(), R.string.seccessRegister, Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    msgError.setText(R.string.msgError);
                }
            });
        }
    }

    private void updateUserInfo(Uri pickedImgUri, final FirebaseUser currentUser, final String name, final String email, final Date dateOfBirth, final Address address) {
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DB.insertUser(currentUser, name, email, dateOfBirth, address, uri.toString());
                        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
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
        userBirthdayText.setInputType(InputType.TYPE_NULL);
        userBirthdayText.setKeyListener(null);
        userBirthdayContainer = findViewById(R.id.userBirthday);
        userCity = findViewById(R.id.userCity);
        regBtn = findViewById(R.id.btnRegister);
        msgError = findViewById(R.id.msgError);
        loginRes = findViewById(R.id.btnLogin);

        dataPickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel();
            }
        };

        userBirthdayText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        userBirthdayText.setText(sdf.format(myCalendar.getTime()));
    }

    private void openDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(RegistrationActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                dataPickerListener, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void openGallery() {
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
            pickedImgUri = data.getData();
            ImgUserPhoto.setImageURI(pickedImgUri);
        }
    }

    private boolean verificationOfInputs(String name, String email, String password, String birthday, String city){
        boolean verifyName = validateUserName(name);
        boolean verifyEmail = validateEmail(email);
        boolean verifyPassword = validatePassword(password);
        boolean verifyBirthday = validateBirthday(birthday);
        boolean verifyCity = validateCity(city);

        return (verifyName && verifyEmail && verifyPassword && verifyBirthday && verifyCity);
    }

    private boolean validateUserName(String name) {
        if (name.length() == 0 || name.isEmpty()) {
            userName.setError(getString(R.string.errorUserName));
            userName.setErrorEnabled(true);
            return false;
        } else {
            userName.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEmail(String email) {
        matcher = pattern.matcher(email);
        if (email.length() == 0 || email.isEmpty()) {
            userEmail.setError(getString(R.string.errorUserEmail));
            userEmail.setErrorEnabled(true);
            return false;
        }else if(!matcher.matches()) {
            userEmail.setError(getString(R.string.errorUserEmail1));
            userEmail.setErrorEnabled(true);
            return false;
        }else{
            userEmail.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword(String password) {
        if (password.length() == 0 || password.isEmpty()) {
            userPassword.setError(getString(R.string.errorUserPassword));
            userPassword.setErrorEnabled(true);
            return false;
        }else if(!(password.length() > 5)) {
            userPassword.setError(getString(R.string.errorUserPassword1));
            userPassword.setErrorEnabled(true);
            return false;
        }else{
            userPassword.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateBirthday(String dateBirthday) {
        Date currentTime = Calendar.getInstance().getTime();
        Date dtBirthday = convertStrToDate(dateBirthday);
        if (dateBirthday.isEmpty()) {
            userBirthdayContainer.setError(getString(R.string.errorBirthday));
            userBirthdayContainer.setErrorEnabled(true);
            return false;
        } else if(dtBirthday.getTime() >= currentTime.getTime()){
            userBirthdayContainer.setError(getString(R.string.errorBirthday1));
            userBirthdayContainer.setErrorEnabled(true);
            return false;
        }else{
            userBirthdayContainer.setErrorEnabled(false);
            return true;
        }
    }

    private Date convertStrToDate(String userBirthday){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date = format.parse(userBirthday);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean validateCity(String uCity) {
        if (uCity.length() == 0 || uCity.isEmpty()) {
            userCity.setError(getString(R.string.errorCity));
            userCity.setErrorEnabled(true);
            return false;
        } else {
            if(checkCity(uCity)){
                userCity.setErrorEnabled(false);
                return true;
            }else{
                userCity.setError(getString(R.string.errorCity1));
                userCity.setErrorEnabled(true);
                return false;
            }
        }
    }


    private boolean checkCity(String userCity){
        Geocoder geocoder = new Geocoder(getApplicationContext());
        try {
            List<android.location.Address> addresses = geocoder.getFromLocationName(userCity, 1);
            if(addresses.isEmpty()){
                return false;
            }else{
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Address convertCityToAddress(String userCity){
        Geocoder geocoder = new Geocoder(getApplicationContext());
        try {
            List<android.location.Address> addresses = geocoder.getFromLocationName(userCity, 1);
            android.location.Address address = addresses.get(0);
            Address userAddress = new Address(address.getLocality(), address.getLatitude(), address.getLongitude());
            return userAddress;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}