package com.example.trails.ui.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.trails.MainActivity;
import com.example.trails.R;
import com.example.trails.controller.DB;
import com.example.trails.model.Address;
import com.example.trails.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

public class EditProfileActivity extends AppCompatActivity {

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    final Calendar myCalendar = Calendar.getInstance();

    private ImageView userPhoto;
    private TextInputLayout editProfileNameContainer;
    private TextInputLayout editProfileBirthdayContainer;
    private TextInputEditText editProfileBirthdayText;
    private TextInputLayout editProfileEmailContainer;
    private TextInputLayout editProfileCityContainer;
    private TextInputLayout editProfileOldPasswordContainer;
    private TextInputLayout editProfileNewPasswordContainer;
    private TextInputLayout editProfileNewPasswordConfContainer;

    private Button buttonEditUser;


    private DatePickerDialog.OnDateSetListener dataPickerListener;

    private FirebaseFirestore fireStore;

    private DocumentReference df;

    private Context context;

    private FirebaseUser user;
    private Uri pickedImgUri;

    private static int PReqCode = 1;
    private static int REQUESCODE = 1;

    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;

    private boolean photoIsChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_baseline_arrow);
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Editar Perfil");

        context = this;
        fireStore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        df = fireStore.collection("users").document(userId);

        initializeUI();

        loadUserData();

        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestForPermission();
                } else {
                    openGallery();
                }
            }
        });

        editProfileNameContainer.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editProfileNameContainer.getEditText().getText().toString().isEmpty()) {
                    editProfileNameContainer.setError("Este campo é obrigatório.");
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editProfileEmailContainer.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isValidEmail(editProfileEmailContainer.getEditText().getText().toString())) {
                    editProfileEmailContainer.setError("O email inserido não é válido.");
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        editProfileBirthdayText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Check if date is valid
                SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
                sdf.setLenient(false);
                try {
                    sdf.parse(editProfileBirthdayContainer.getEditText().getText().toString());
                    editProfileBirthdayContainer.setError(null);
                } catch (ParseException e) {
                    if (editProfileBirthdayContainer.getEditText().getText().toString().equals("")) {
                        editProfileBirthdayContainer.setError(null);
                    } else {
                        editProfileBirthdayContainer.setError("A data inserida é inválida.");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        buttonEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Confirm Edit

                AlertDialog.Builder confirmationDialogBuilder = new AlertDialog.Builder(context);
                confirmationDialogBuilder.setTitle("Confirmação - Editar Perfil");
                confirmationDialogBuilder.setMessage("Tem a certeza que pretende alterar o seu perfil?");
                confirmationDialogBuilder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editUser();
                    }
                });
                confirmationDialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog confirmationDialog = confirmationDialogBuilder.create();
                confirmationDialog.show();
            }
        });

    }

    private void initializeUI() {
        userPhoto = findViewById(R.id.userPhoto);
        editProfileNameContainer = findViewById(R.id.editProfileNome);
        editProfileEmailContainer = findViewById(R.id.editProfileEmail);
        editProfileBirthdayContainer = findViewById(R.id.editProfileBirthday);
        editProfileBirthdayText = findViewById(R.id.editProfileBirthdayText);
        editProfileBirthdayText.setInputType(InputType.TYPE_NULL);
        editProfileBirthdayText.setKeyListener(null);
        editProfileCityContainer = findViewById(R.id.editProfileCity);
        editProfileOldPasswordContainer = findViewById(R.id.editProfileOldPassword);
        editProfileNewPasswordContainer = findViewById(R.id.editProfileNewPassword);
        editProfileNewPasswordConfContainer = findViewById(R.id.editProfileNewPasswordConf);
        buttonEditUser = findViewById(R.id.editButton);

        dataPickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "/" + String.format("%01d", month) + "/" + year;
                editProfileBirthdayText.setText(date);
            }
        };
    }


    private void updateLabelBirthday(final Date date) {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editProfileBirthdayText.setText(sdf.format(date));

        editProfileBirthdayText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(date);
            }
        });
    }

    private void openDatePicker(Date date) {
        myCalendar.setTime(date);
        DatePickerDialog dialog = new DatePickerDialog(EditProfileActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                dataPickerListener, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {

        backMainActivityProfile();

        return super.onSupportNavigateUp();
    }

    public void loadUserData() {
        initializeUI();
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                final User userObject = documentSnapshot.toObject(User.class);

                if (userObject.getPhoto() != null) {
                    pickedImgUri = Uri.parse(userObject.getPhoto());
                    DB.loadWithGlide(context, userObject.getPhoto(), userPhoto);
                } else {
                    userPhoto.setImageResource(R.drawable.ic_baseline_account_circle_24);
                }

                editProfileNameContainer.getEditText().setText(userObject.getName());
                editProfileEmailContainer.getEditText().setText(userObject.getEmail());

                try {
                    updateLabelBirthday(userObject.getDateOfBirth());

                } catch (Exception e) {
                    editProfileBirthdayContainer.getEditText().setText("");
                }

                editProfileCityContainer.getEditText().setText(userObject.getCity());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {
            pickedImgUri = data.getData();
            userPhoto.setImageURI(pickedImgUri);
            photoIsChanged = true;
        }
    }


    public void editUser() {
        String email = editProfileEmailContainer.getEditText().getText().toString().trim();
        String oldPassword = editProfileOldPasswordContainer.getEditText().getText().toString().trim();
        String newPassword = editProfileNewPasswordContainer.getEditText().getText().toString().trim();
        String newPasswordConf = editProfileNewPasswordConfContainer.getEditText().getText().toString().trim();
        String name = editProfileNameContainer.getEditText().getText().toString();
        Date birthday = convertStrToDate(editProfileBirthdayContainer.getEditText().getText().toString().trim());
        Address address = convertCityToAddress(editProfileCityContainer.getEditText().getText().toString());

        if (!verificationOfInputs(name, email, oldPassword, birthday.toString(), address.getAddress(), newPassword, newPasswordConf)) {
            Toast.makeText(getApplicationContext(), R.string.msgError_fields, Toast.LENGTH_LONG).show();
        } else {
            if (pickedImgUri != null) {
                if (photoIsChanged) {
                    updateUserInfo(pickedImgUri, user, name, email, birthday, address);
                } else {
                    User userUpdate = new User(name, email, birthday, address, user.getUid(), pickedImgUri.toString());
                    DB.updateUser(userUpdate);
                }
            } else {
                User userUpdate = new User(name, email, birthday, address, user.getUid(), null);
                DB.updateUser(userUpdate);
            }

            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
            if (user.getEmail() != email) {
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String email = editProfileEmailContainer.getEditText().getText().toString().trim();
                                user.updateEmail(email)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getApplicationContext(), "User email updated.", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "User email failed.", Toast.LENGTH_LONG).show();
                        return;
                    }
                });
            }

            if (newPassword != null) {
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String newPassword = editProfileNewPasswordContainer.getEditText().getText().toString().trim();
                                user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Password updated.", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Password failed.", Toast.LENGTH_LONG).show();
                        return;
                    }
                });
            }

            backMainActivityProfile();
        }
    }


    public void backMainActivityProfile() {
        Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
        intent.putExtra("profile", R.id.nav_profile);
        startActivityForResult(intent, 1);
        finish();
    }


    static boolean isValidEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    private Address convertCityToAddress(String userCity) {
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

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(EditProfileActivity.this, "Por favor aceita a permissão pedida", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
            }
        } else
            openGallery();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESCODE);
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
                    }
                });
            }
        });
    }

    private boolean verificationOfInputs(String name, String email, String password, String birthday, String city, String newPassword, String newPasswordConf) {
        boolean verifyName = validateUserName(name);
        boolean verifyEmail = validateEmail(email);
        boolean verifyPassword = validatePassword(password);
        boolean verifyBirthday = validateBirthday(birthday);
        boolean verifyCity = validateCity(city);
        boolean verifyNewPassword = validateNewPassword(password, newPassword, newPasswordConf);

        return (verifyName && verifyEmail && verifyPassword && verifyBirthday && verifyCity && verifyNewPassword);
    }

    private boolean validateUserName(String name) {
        if (name.length() == 0 || name.isEmpty()) {
            editProfileNameContainer.setError(getString(R.string.errorUserName));
            editProfileNameContainer.setErrorEnabled(true);
            return false;
        } else {
            return true;
        }
    }

    private boolean validateEmail(String email) {
        matcher = pattern.matcher(email);
        if (email.length() == 0 || email.isEmpty()) {
            editProfileEmailContainer.setError(getString(R.string.errorUserEmail));
            editProfileEmailContainer.setErrorEnabled(true);
            return false;
        } else if (!matcher.matches()) {
            editProfileEmailContainer.setError(getString(R.string.errorUserEmail1));
            editProfileEmailContainer.setErrorEnabled(true);
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePassword(String password) {
        if (password.length() == 0 || password.isEmpty()) {
            editProfileOldPasswordContainer.setError(getString(R.string.errorUserPassword));
            editProfileOldPasswordContainer.setErrorEnabled(true);
            return false;
        } else if (!(password.length() > 5)) {
            editProfileOldPasswordContainer.setError(getString(R.string.errorUserPassword1));
            editProfileOldPasswordContainer.setErrorEnabled(true);
            return false;
        } else {
            return true;
        }
    }

    private boolean validateNewPassword(String oldPassword, String newPassword, String newPasswordConf) {
        if (newPassword.isEmpty() && !newPasswordConf.isEmpty()) {
            editProfileNewPasswordContainer.setError(getString(R.string.errorUserPassword));
            editProfileNewPasswordContainer.setErrorEnabled(true);
            return false;
        }

        if (!newPassword.isEmpty() && newPasswordConf.isEmpty()) {
            editProfileNewPasswordConfContainer.setError(getString(R.string.errorUserPassword));
            editProfileNewPasswordConfContainer.setErrorEnabled(true);
            return false;
        }

        if (newPassword.equals(oldPassword)) {
            editProfileNewPasswordContainer.setError(getString(R.string.txtErorPass));
            editProfileNewPasswordContainer.setErrorEnabled(true);
            return false;
        } else if (!newPassword.equals(newPasswordConf)) {
            editProfileNewPasswordContainer.setError(getString(R.string.txtErorPassConf));
            editProfileNewPasswordContainer.setErrorEnabled(true);
            return false;
        }
        return true;
    }


    private boolean validateBirthday(String dateBirthday) {
        Date currentTime = Calendar.getInstance().getTime();
        Date dtBirthday = convertStrToDate(dateBirthday);
        if (dateBirthday.isEmpty()) {
            editProfileBirthdayContainer.setError(getString(R.string.errorBirthday));
            editProfileBirthdayContainer.setErrorEnabled(true);
            return false;
        } else if (dtBirthday.getTime() >= currentTime.getTime()) {
            editProfileBirthdayContainer.setError(getString(R.string.errorBirthday1));
            editProfileBirthdayContainer.setErrorEnabled(true);
            return false;
        } else {
            return true;
        }
    }

    private Date convertStrToDate(String userBirthday) {
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
            editProfileCityContainer.setError(getString(R.string.errorCity));
            editProfileCityContainer.setErrorEnabled(true);
            return false;
        } else {
            if (checkCity(uCity)) {
                return true;
            } else {
                editProfileCityContainer.setError(getString(R.string.errorCity1));
                editProfileCityContainer.setErrorEnabled(true);
                return false;
            }
        }
    }


    private boolean checkCity(String userCity) {
        Geocoder geocoder = new Geocoder(getApplicationContext());
        try {
            List<android.location.Address> addresses = geocoder.getFromLocationName(userCity, 1);
            if (addresses.isEmpty()) {
                return false;
            } else {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


}