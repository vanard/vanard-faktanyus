package com.vanard.faktanyus.ui.splash.register;

import android.util.Patterns;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegisterViewModel extends ViewModel {

    MutableLiveData<String> password = new MutableLiveData<>();
    MutableLiveData<String> username = new MutableLiveData<>();
    MutableLiveData<String> fullname = new MutableLiveData<>();
    MutableLiveData<String> email = new MutableLiveData<>();
    MutableLiveData<String> repassword = new MutableLiveData<>();
    MutableLiveData<Boolean> agreement = new MutableLiveData<>();

    String checkInput(){
        String usernameValue = username.getValue();
        String passwordValue = password.getValue();
        String fullnameValue = fullname.getValue();
        String emailValue = email.getValue();
        String repassValue = repassword.getValue();

        if (fullnameValue.isEmpty()) return "Full name must not empty";
        if (emailValue.isEmpty()) return "Email must not empty";
        if (!Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) return "Email is not valid";
        if (usernameValue.isEmpty()) return "Username must not empty";
        if (passwordValue.isEmpty()) return "Password must not empty";
        if (!(passwordValue.length() > 5)) return "Password at least 6 character";
        if (repassValue.isEmpty()) return "Re-Password must not empty";
        if (!(repassValue.length() > 5)) return "Re-Password at least 6 character";
        if (!passwordValue.equals(repassValue)) return "Password not match";

        if (agreement.getValue())
            return "";
        else return "You must check the agreement";
    }

}
