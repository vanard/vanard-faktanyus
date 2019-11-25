package com.vanard.faktanyus.ui.profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {
    MutableLiveData<String> password = new MutableLiveData<>();
    MutableLiveData<String> username = new MutableLiveData<>();
    MutableLiveData<String> fullname = new MutableLiveData<>();
    MutableLiveData<String> email = new MutableLiveData<>();
    MutableLiveData<String> repassword = new MutableLiveData<>();
    MutableLiveData<String> phone = new MutableLiveData<>();

    void initData() {

    }
}
