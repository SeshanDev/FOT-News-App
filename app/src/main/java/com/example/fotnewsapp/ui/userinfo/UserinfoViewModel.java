package com.example.fotnewsapp.ui.userinfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserinfoViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public UserinfoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is userinfo fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}