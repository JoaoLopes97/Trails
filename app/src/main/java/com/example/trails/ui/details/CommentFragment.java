package com.example.trails.ui.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.trails.R;

import java.util.List;

public class CommentFragment extends Fragment {

    private TextView nameUser;
    private RatingBar ratingUser;
    private TextView commentUser;

    private List<Comment> commentsList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.comments_fragment, container, false);

        setButtons(root);
        return root;
    }

    public void setButtons(View root){
        nameUser = root.findViewById(R.id.NameUser);
        ratingUser = root.findViewById(R.id.RatingUser);
        commentUser = root.findViewById(R.id.CommentUser);
    }

    public void createComments(){
        Comment comment = new Comment(3,"Ricardo","Gostei muito!");
        commentsList.add(comment);
        comment = new Comment(5,"Teresa","Melhor Caminho que já fiz!");
        commentsList.add(comment);
        comment = new Comment(4,"Francisco","Para repetir.");
        commentsList.add(comment);
        comment = new Comment(4,"Daniel","Gostei muito!");
        commentsList.add(comment);
        comment = new Comment(2,"Inês","Melhor Caminho que já fiz!");
        commentsList.add(comment);
        comment = new Comment(1,"Tiago","Para repetir.");
        commentsList.add(comment);
        comment = new Comment(5,"Andreia","Gostei muito!");
        commentsList.add(comment);
        comment = new Comment(4,"Miguel","Melhor Caminho que já fiz!");
        commentsList.add(comment);
    }
}
