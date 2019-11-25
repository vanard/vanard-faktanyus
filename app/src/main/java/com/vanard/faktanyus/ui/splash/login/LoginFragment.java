package com.vanard.faktanyus.ui.splash.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vanard.faktanyus.R;
import com.vanard.faktanyus.databinding.LoginFragmentBinding;
import com.vanard.faktanyus.ui.main.MainActivity;
import com.vanard.faktanyus.ui.splash.register.RegisterFragment;

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";

    private LoginViewModel mViewModel;
    private LoginFragmentBinding binding;
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.login_fragment, container, false);
        View view = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(requireContext());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        binding.setViewModel(mViewModel);

        setUpFacebookLogin();

        binding.registerLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                openRegister();
            }
        });

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateInput();
            }
        });

    }

    private void setUpFacebookLogin() {
        callbackManager = CallbackManager.Factory.create();
        binding.loginFacebookButton.setPermissions("email", "public_profile");
        binding.loginFacebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                openMain();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel: ");
                Toast.makeText(requireContext(), "Canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError: "+error.getLocalizedMessage());
                Toast.makeText(requireContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkLoginStatus() {
        if (AccessToken.getCurrentAccessToken() != null) openMain();


        if (mAuth.getCurrentUser() != null)
            openMain();

    }

    private void doLogin(String username, String password) {
        dialog.show();
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            dialog.dismiss();
                            openMain();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(requireContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void validateInput() {
        String username = binding.usernameLoginInput.getText().toString().trim();
        String password = binding.passwordLoginInput.getText().toString().trim();
        
        mViewModel.username.setValue(username);
        mViewModel.password.setValue(password);
        
        if (!mViewModel.checkInput().isEmpty())
            Toast.makeText(requireContext(), mViewModel.checkInput(), Toast.LENGTH_SHORT).show();
        else doLogin(username, password);

    }

    private void openMain(){
        startActivity(new Intent(requireContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        requireActivity().finish();
    }

    private void openRegister() {
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.splash_container, RegisterFragment.newInstance());
        ft.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        Fragment fragment = getFragmentManager().findFragmentByTag("login");
//        fragment.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dialog.setMessage("Please wait...");

    }

    @Override
    public void onStart() {
        super.onStart();
        checkLoginStatus();
    }
}
