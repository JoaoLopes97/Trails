package com.example.trails.ui.myTrails;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class  MyTrailsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MyTrailsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is my trails fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}