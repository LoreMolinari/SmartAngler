package com.smartangler.smartangler.ui.fishLocationDatabase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FishDBViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public FishDBViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}