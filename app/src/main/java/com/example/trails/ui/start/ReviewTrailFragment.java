package com.example.trails.ui.start;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trails.R;
import com.example.trails.controller.DB;
import com.example.trails.model.ImageData;
import com.example.trails.model.Pair;
import com.example.trails.model.Review;
import com.example.trails.model.Trail;
import com.example.trails.ui.details.DetailsTrailFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static com.example.trails.MainActivity.setFragment;
import static com.example.trails.MainActivity.storage;

public class ReviewTrailFragment extends Fragment {

    private LinearLayout linearLayout;
    private Button imageBtn, saveBtn;
    private RatingBar ratingBar;
    private TextView comment;

    private Trail trail;
    private List<Uri> imageUris;

    public ReviewTrailFragment(Trail trail) {
        this.trail = trail;
    }

    public ReviewTrailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.review_trail_fragment, container, false);

        linearLayout = view.findViewById(R.id.images_layout);
        saveBtn = view.findViewById(R.id.save_trail);
        imageBtn = view.findViewById(R.id.images_btn);
        ratingBar = view.findViewById(R.id.rating_bar);
        comment = view.findViewById(R.id.comment);

        imageUris = new ArrayList<>();

        for (Pair<ImageData, LatLng> img : trail.getImagesWithCoords()) {
            createNewImageView(img.first.getBitmap());
            imageUris.add(img.first.getUri());
        }

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
                Review review = new Review(trail.getId(), "1", comment.getText().toString(), ratingBar.getRating()); //TODO get userId

                //TODO save user review

                //saveTrail();

                DetailsTrailFragment dt = new DetailsTrailFragment(trail);
                setFragment(R.id.start_fragment, dt, getActivity());
            }
        });
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
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
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams((int) (100 * scale + 0.5f), (int) (120 * scale + 0.5f));

        iv.setLayoutParams(lp);

        // Finally, add the ImageView to layout
        linearLayout.addView(iv);
    }

    private void saveTrail() {
        final List<UploadTask> uploadTasks = new LinkedList<>();
        final List<Task<Uri>> downloadUriTask = new LinkedList<>();
        for (final Uri image : imageUris) {
            String imageName = UUID.randomUUID().toString();
            final StorageReference ref = storage.getReference().child("images/" + imageName);
            UploadTask uploadFile = ref.putFile(image);
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
        Tasks.whenAllComplete(uploadTasks).

                addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Task<?>>> task) {
                        Tasks.whenAllComplete(downloadUriTask).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                            @Override
                            public void onComplete(@NonNull Task<List<Task<?>>> task) {
                                DB.insertTrail(trail);
                            }
                        });
                    }
                });
    }
}