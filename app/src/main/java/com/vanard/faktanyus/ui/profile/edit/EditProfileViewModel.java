package com.vanard.faktanyus.ui.profile.edit;

import android.util.Patterns;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EditProfileViewModel extends ViewModel {
    MutableLiveData<String> email = new MutableLiveData<>();
    MutableLiveData<String> password = new MutableLiveData<>();
    MutableLiveData<String> username = new MutableLiveData<>();
    MutableLiveData<String> fullname = new MutableLiveData<>();
    MutableLiveData<String> repassword = new MutableLiveData<>();
    MutableLiveData<String> phone = new MutableLiveData<>();
    MutableLiveData<String> oldPassword = new MutableLiveData<>();
    MutableLiveData<String> profpic = new MutableLiveData<>();
    MutableLiveData<String> verificationPassword = new MutableLiveData<>();
    MutableLiveData<Boolean> changePassword = new MutableLiveData<>();
    MutableLiveData<Boolean> changePhone = new MutableLiveData<>();

    String checkInput(){
        String passwordValue = password.getValue();
        String fullnameValue = fullname.getValue();
        String emailValue = email.getValue();
        String repassValue = repassword.getValue();
        String oldPassValue = oldPassword.getValue();
        String phoneValue = phone.getValue();
        String vertifPass = verificationPassword.getValue();

        if (fullnameValue.isEmpty()) return "Full name must not empty";
        if (!(fullnameValue.length() > 2)) return "Full name at least 3 character";
        if (emailValue.isEmpty()) return "Email must not empty";
        if (!Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) return "Email is not valid";

        if (phoneValue.isEmpty())
            changePhone.setValue(false);
        else {
            changePhone.setValue(true);

            if (!(phoneValue.length() > 9)) return "Phone number at least 10 character";

        }

        if (oldPassValue.isEmpty() || passwordValue.isEmpty() || repassValue.isEmpty()) {
            changePassword.setValue(false);

        } else {
            changePassword.setValue(true);

            if (!(passwordValue.length() > 5)) return "Password at least 6 character";
            if (!(repassValue.length() > 5)) return "Re-Password at least 6 character";
            if (!passwordValue.equals(repassValue)) return "Password not match";
            if (!(oldPassValue.length() > 5)) return "Old Password at least 6 character";
            if (!vertifPass.equals(oldPassValue)) return "Your old password isn't match";
            if (passwordValue.equals(oldPassValue)) return "Your new password is same with new password";

        }

        return "";

    }
}
