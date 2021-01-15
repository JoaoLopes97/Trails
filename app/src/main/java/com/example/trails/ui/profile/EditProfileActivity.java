package com.example.trails.ui.profile;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.trails.MainActivity;
import com.example.trails.R;
import com.example.trails.controller.DB;
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
import com.google.firebase.internal.InternalTokenProvider;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView userPhoto;
    private TextInputLayout editProfileNomeContainer;
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
        editProfileNomeContainer = findViewById(R.id.editProfileNome);
        editProfileEmailContainer = findViewById(R.id.editProfileEmail);
        editProfileBirthdayContainer = findViewById(R.id.editProfileBirthday);
        editProfileBirthdayText = findViewById(R.id.editProfileBirthdayText);
        editProfileCityContainer = findViewById(R.id.editProfileCity);
        editProfileOldPasswordContainer = findViewById(R.id.editProfileOldPassword);
        editProfileNewPasswordContainer = findViewById(R.id.editProfileNewPassword);
        editProfileNewPasswordConfContainer = findViewById(R.id.editProfileNewPasswordConf);

        buttonEditUser = findViewById(R.id.editButton);

        loadUserData();


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

        editProfileBirthdayText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Check if data is valid
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
                editUser();
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

                editProfileNomeContainer.getEditText().setText(userObject.getName());
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



    public void editUser() {

        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User userObject = documentSnapshot.toObject(User.class);

                String userName = editProfileNomeContainer.getEditText().getText().toString();
                String email = editProfileEmailContainer.getEditText().getText().toString();
                String dateBirthString = editProfileBirthdayContainer.getEditText().getText().toString();
                Date dateBirth = null;
                try {
                    dateBirth = new SimpleDateFormat("dd/MM/yyyy").parse(dateBirthString);
                } catch (Exception e) {
                    return;
                }

                String city = editProfileCityContainer.getEditText().getText().toString();

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
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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

                final User editedUser = new User(userName, dateBirth, userObject.getLocation(), email, userObject.getFavoriteRoutes(), userObject.getDownloadRoutes(), city);

                AlertDialog.Builder confirmationDialogBuilder = new AlertDialog.Builder(context);

                confirmationDialogBuilder.setTitle("Confirmação - Editar Perfil");
                confirmationDialogBuilder.setMessage("Tem a certeza que pretende alterar o seu perfil?");

                confirmationDialogBuilder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        df.set(editedUser);

                        backMainActivityProfile();
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
    }

}