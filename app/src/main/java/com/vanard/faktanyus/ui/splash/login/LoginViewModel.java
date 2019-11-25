package com.vanard.faktanyus.ui.splash.login;

import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    MutableLiveData<String> password = new MutableLiveData<>();
    MutableLiveData<String> username = new MutableLiveData<>();

    String checkInput(){
        String usernameValue = username.getValue();
        String passwordValue = password.getValue();

        if (usernameValue.isEmpty()) return "Email must be not empty";
        if (!Patterns.EMAIL_ADDRESS.matcher(usernameValue).matches()) return "Email is not valid";
        if (passwordValue.isEmpty()) return "Password must be not empty";
        if (!(passwordValue.length() > 5)) return "Password at least 6 character";

        return "";
    }

    public void onForgetPassword(View v){
        Toast.makeText(v.getContext(), "Forget Password Button", Toast.LENGTH_SHORT).show();
    }

}
