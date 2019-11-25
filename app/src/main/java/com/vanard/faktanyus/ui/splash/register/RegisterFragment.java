package com.vanard.faktanyus.ui.splash.register;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vanard.faktanyus.R;
import com.vanard.faktanyus.databinding.RegisterFragmentBinding;
import com.vanard.faktanyus.models.auth.User;
import com.vanard.faktanyus.ui.main.MainActivity;
import com.vanard.faktanyus.ui.splash.login.LoginFragment;

public class RegisterFragment extends Fragment {
    private static final String TAG = "RegisterFragment";

    private RegisterViewModel mViewModel;
    private RegisterFragmentBinding binding;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.register_fragment, container, false);
        View view = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();

        dialog = new ProgressDialog(requireContext());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(RegisterViewModel.class);
        binding.setViewModel(mViewModel);

        initData();

        binding.signupRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInput();
            }
        });

        binding.backRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.splash_container, LoginFragment.newInstance()).addToBackStack(null);
                ft.commit();
            }
        });
    }

    private void initData() {
        mViewModel.email.setValue("");
        mViewModel.fullname.setValue("");
        mViewModel.username.setValue("");
        mViewModel.password.setValue("");
        mViewModel.repassword.setValue("");
        mViewModel.agreement.setValue(false);

        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
    }

    private void validateInput() {
        String fName = binding.nameRegisterInput.getText().toString().trim();
        String email = binding.emailRegisterInput.getText().toString().trim();
        String uName = binding.usernameRegisterInput.getText().toString().trim();
        String password = binding.passwordRegisterInput.getText().toString().trim();
        String rePass = binding.repasswordRegisterInput.getText().toString().trim();

        mViewModel.email.setValue(email);
        mViewModel.fullname.setValue(fName);
        mViewModel.username.setValue(uName);
        mViewModel.password.setValue(password);
        mViewModel.repassword.setValue(rePass);
        mViewModel.agreement.setValue(binding.aggreementCheckboxRegister.isChecked());

        String result = mViewModel.checkInput();
        if (result.equals("")){
            onRegist(email, password);
        }else{
            Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show();
        }
    }

    private void onRegist(String email, String password) {
        dialog.show();
        doRegister(email, password);

    }

    private void doRegister(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            saveNewAccount(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(requireContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                            dialog.dismiss();


                        }

                    }
                });
    }

    private void saveNewAccount(FirebaseUser userLogin){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        User user = new User();
        user.setEmail(mViewModel.email.getValue());
        user.setName(mViewModel.fullname.getValue());
        user.setPassword(mViewModel.password.getValue());
        user.setUsername(mViewModel.username.getValue());
        user.setPhone("");
        user.setProfilePicture("");

        db.collection("users").document(userLogin.getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                        openMain();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        dialog.dismiss();
                    }
                });

    }

    private void openMain(){
        requireActivity().startActivity(new Intent(requireContext(), MainActivity.class));
        requireActivity().finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null)
            openMain();
    }
}
