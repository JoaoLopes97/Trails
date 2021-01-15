package com.example.trails.ui.profile;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.trails.R;
import com.example.trails.controller.DB;
import com.example.trails.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView userPhoto;
    private TextInputLayout editProfileNomeContainer;
    private TextInputLayout editProfileBirthdayContainer;
    private TextInputEditText editProfileBirthdayText;
    private TextInputLayout editProfileEmailContainer;
    private TextInputLayout editProfileCityContainer;
    private TextInputLayout editProfileNewPasswordContainer;
    private TextInputLayout editProfileNewPasswordConfContainer;



    private DatePickerDialog.OnDateSetListener dataPickerListener;

    private FirebaseFirestore fireStore;
    private String userId;

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

        loadData(this.getBaseContext());

        userPhoto = findViewById(R.id.userPhoto);
        editProfileNomeContainer = findViewById(R.id.editProfileNome);
        editProfileEmailContainer = findViewById(R.id.editProfileEmail);
        editProfileBirthdayContainer = findViewById(R.id.editProfileBirthday);
        editProfileBirthdayText = findViewById(R.id.editProfileBirthdayText);
        editProfileCityContainer = findViewById(R.id.editProfileCity);
        editProfileNewPasswordContainer = findViewById(R.id.editProfileNewPassword);
        editProfileNewPasswordConfContainer = findViewById(R.id.editProfileNewPasswordConf);

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

    }

    public void loadData(final Context context) {
        fireStore = FirebaseFirestore.getInstance();

        userId  = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference df = fireStore.collection("users").document("iIiEW75WbSNdrAH1ycRzUC4zIMU2");

        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User userObject = documentSnapshot.toObject(User.class);

                DB.loadWithGlide(context, userObject.getPhoto(), userPhoto);

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


}