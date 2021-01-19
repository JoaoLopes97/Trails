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
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.example.trails.ui.login.LoginActivity;
import com.example.trails.ui.login.RegistrationActivity;
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
import com.google.firebase.internal.InternalTokenProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

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

        String userId  = user.getUid();

        df = fireStore.collection("users").document(userId);

        userPhoto = findViewById(R.id.userPhoto);
        editProfileNameContainer = findViewById(R.id.editProfileNome);
        editProfileEmailContainer = findViewById(R.id.editProfileEmail);
        editProfileBirthdayContainer = findViewById(R.id.editProfileBirthday);
        editProfileBirthdayText = findViewById(R.id.editProfileBirthdayText);
        editProfileCityContainer = findViewById(R.id.editProfileCity);
        editProfileOldPasswordContainer = findViewById(R.id.editProfileOldPassword);
        editProfileNewPasswordContainer = findViewById(R.id.editProfileNewPassword);
        editProfileNewPasswordConfContainer = findViewById(R.id.editProfileNewPasswordConf);

        buttonEditUser = findViewById(R.id.editButton);

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


        dataPickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "/" + String.format("%01d", month) + "/" + year;
                editProfileBirthdayText.setText(date);
            }
        };

        editProfileBirthdayContainer.setStartIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        EditProfileActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dataPickerListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        editProfileNameContainer.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(editProfileNameContainer.getEditText().getText().toString().isEmpty()) {
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

    @Override
    public boolean onSupportNavigateUp() {

        backMainActivityProfile();

        return super.onSupportNavigateUp();
    }

    public void loadUserData() {
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User userObject = documentSnapshot.toObject(User.class);

                if(userObject.getPhoto() == null) {
                    userPhoto.setImageResource(R.drawable.ic_baseline_account_circle_24);
                } else {
                    DB.loadWithGlide(context, userObject.getPhoto(), userPhoto);
                }

                editProfileNameContainer.getEditText().setText(userObject.getName());
                editProfileEmailContainer.getEditText().setText(userObject.getEmail());

                try {
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String strDate = dateFormat.format(userObject.getDateOfBirth());
                    editProfileBirthdayContainer.getEditText().setText(strDate);

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
        }
    }


    public void editUser() {

        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User userObject = documentSnapshot.toObject(User.class);

                String userName = editProfileNameContainer.getEditText().getText().toString();

                String email = editProfileEmailContainer.getEditText().getText().toString();

                String dateBirthString = editProfileBirthdayContainer.getEditText().getText().toString();
                Date dateBirth = null;
                try {
                    dateBirth = new SimpleDateFormat("dd/MM/yyyy").parse(dateBirthString);
                } catch (Exception e) {
                    return;
                }

                Address address = convertCityToAddress(editProfileCityContainer.getEditText().getText().toString());

                String oldPassword = editProfileOldPasswordContainer.getEditText().getText().toString();
                final String newPassword = editProfileNewPasswordContainer.getEditText().getText().toString();
                String newPasswordConf = editProfileNewPasswordConfContainer.getEditText().getText().toString();

                if (!oldPassword.equals("")) {
                    if (newPassword.equals(newPasswordConf)) {
                        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
                        user.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        user.updatePassword(newPassword);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                editProfileOldPasswordContainer.setError("A password inserida não está correta.");
                                return;
                            }
                        });
                    } else {
                        editProfileNewPasswordContainer.setError("As password inseridas não correspondem.");
                        editProfileNewPasswordConfContainer.setError("As password inseridas não correspondem.");
                        return;
                    }
                } else if (!newPassword.equals("") || !newPasswordConf.equals("")) {
                    editProfileOldPasswordContainer.setError("Necessita de inserir a sua password atual.");
                    editProfileNewPasswordContainer.setError("Necessita de inserir a sua password atual.");
                    editProfileNewPasswordConfContainer.setError("Necessita de inserir a sua password atual.");
                    return;
                }

                if (userObject.getPhoto() != null) {
                    pickedImgUri = Uri.parse(userObject.getPhoto());
                }

                if(pickedImgUri != null){
                    updateUserInfo(pickedImgUri, user, userName, email, dateBirth, address);
                }else{
                    DB.insertUser(user, userName, email, dateBirth, address, null);
                }

                backMainActivityProfile();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
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

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(EditProfileActivity.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
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

}