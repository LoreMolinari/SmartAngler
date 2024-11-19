package com.smartangler.smartangler.ui.fishing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FishingViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public FishingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Loading");
    }

    public LiveData<String> getText() {
        return mText;
    }
}