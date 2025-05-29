package com.example.fotnewsapp.ui.devinfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DevinfoViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public DevinfoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is devinfo fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}