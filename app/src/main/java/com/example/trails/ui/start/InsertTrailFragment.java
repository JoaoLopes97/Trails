package com.example.trails.ui.start;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.trails.R;
import com.example.trails.model.Characteristics;
import com.example.trails.model.TerrainType;
import com.example.trails.model.Trail;
import com.example.trails.model.TrailDifficulty;
import com.example.trails.ui.Details.DetailsTrailFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class InsertTrailFragment extends Fragment {

    private Trail trail;
    private Button imageBtn, saveBtn;
    private LinearLayout linearLayout;
    private EditText trailName, trailDescription;
    private RadioGroup trailType, trailDifficulty;
    private List<Uri> imageUris;

    private long startTime;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public InsertTrailFragment(Trail trail) {
        this.trail = trail;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.insert_trail_fragment, container, false);

        saveBtn = view.findViewById(R.id.save_trail);
        imageBtn = view.findViewById(R.id.images_btn);
        linearLayout = view.findViewById(R.id.images_layout);

        trailName = view.findViewById(R.id.name);
        trailDescription = view.findViewById(R.id.trail_description);
        trailType = view.findViewById(R.id.trail_type);
        trailDifficulty = view.findViewById(R.id.trail_difficulty);


        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                startActivityForResult(Intent.createChooser(gallery, "Select images"), 1);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton type = getView().findViewById(trailType.getCheckedRadioButtonId());
                RadioButton difficulty = getView().findViewById(trailDifficulty.getCheckedRadioButtonId());

                Characteristics ch = trail.getCharacteristics();
                ch.setName(trailName.getText().toString());
                ch.setDescription(trailDescription.getText().toString());
                ch.setDifficulty(TrailDifficulty.valueOf((String) difficulty.getText()));
                ch.setTerrainType(TerrainType.valueOf((String) type.getText()));

                saveTrail();

                DetailsTrailFragment dt = new DetailsTrailFragment(trail);
                setFragment(R.id.insert_trail_frag,dt);
            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {

            imageUris = new ArrayList<>();
            ClipData clipData = data.getClipData();
            Uri imageUri;
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    imageUri = clipData.getItemAt(i).getUri();
                    imageUris.add(imageUri);
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                        createNewImageView(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                imageUri = data.getData();
                imageUris.add(imageUri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                    createNewImageView(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void createNewImageView(Bitmap bitmap) {

        ImageView iv = new ImageView(getContext());

        // Set an image for ImageView
        iv.setImageBitmap(bitmap);
        // Create layout parameters for ImageView
        final float scale = getContext().getResources().getDisplayMetrics().density;
        LayoutParams lp = new LayoutParams((int) (100 * scale + 0.5f), (int) (120 * scale + 0.5f));

        iv.setLayoutParams(lp);

        // Finally, add the ImageView to layout
        linearLayout.addView(iv);
    }

    private void saveTrail() {
        final List<UploadTask> uploadTasks = new LinkedList<>();
        final List<Task<Uri>> downloadUriTask = new LinkedList<>();
        for (Uri imageUri : imageUris) {
            final StorageReference ref = storage.getReference().child("images/" + UUID.randomUUID().toString());
            UploadTask uploadFile = ref.putFile(imageUri);
            uploadTasks.add(uploadFile);
            uploadFile.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    Task<Uri> t = ref.getDownloadUrl();
                    downloadUriTask.add(t);
                    return t;
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String downloadURL = downloadUri.toString();
                        trail.getImages().add(downloadURL);
                    }
                }
            });
        }
        // might need to review
        Tasks.whenAllComplete(uploadTasks).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
            @Override
            public void onComplete(@NonNull Task<List<Task<?>>> task) {
                Tasks.whenAllComplete(downloadUriTask).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Task<?>>> task) {
                        insertTrail(trail);
                    }
                });
            }
        });
    }

    private void insertTrail(Object object) {

        db.collection("trails")
                .add(object)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }


    private void setFragment(int layout, Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}