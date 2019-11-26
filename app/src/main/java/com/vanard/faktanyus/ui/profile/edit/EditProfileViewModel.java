package com.vanard.faktanyus.ui.profile.edit;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EditProfileViewModel extends ViewModel {
    MutableLiveData<String> password = new MutableLiveData<>();
    MutableLiveData<String> username = new MutableLiveData<>();
    MutableLiveData<String> fullname = new MutableLiveData<>();
    MutableLiveData<String> repassword = new MutableLiveData<>();
    MutableLiveData<String> phone = new MutableLiveData<>();
    MutableLiveData<String> oldPassword = new MutableLiveData<>();
    MutableLiveData<String> profpic = new MutableLiveData<>();
}
