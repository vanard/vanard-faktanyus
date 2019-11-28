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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vanard.faktanyus.R;
import com.vanard.faktanyus.databinding.LoginFragmentBinding;
import com.vanard.faktanyus.models.auth.User;
import com.vanard.faktanyus.ui.main.MainActivity;
import com.vanard.faktanyus.ui.splash.register.RegisterFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";

    private LoginViewModel mViewModel;
    private LoginFragmentBinding binding;
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressDialog dialog;
    private String imageUrl, email, name, username;

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
        db = FirebaseFirestore.getInstance();
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
                dialog.show();

                checkUserLogin();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel: ");
                Toast.makeText(requireContext(), "Canceled", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError: "+error.getLocalizedMessage());
                Toast.makeText(requireContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });
    }

    private void checkUserLogin() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.d(TAG, "onCompleted: "+object);
                getData(object);

            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email, first_name, last_name");
        request.setParameters(parameters);
        request.executeAsync();


    }

    private void getData(JSONObject object) {
        try {
            URL imageUrl = new URL("https://graph.facebook.com/" +object.getString("id")+"/picture?width=250&height=250");
            this.imageUrl = imageUrl.toString();
            this.email = object.getString("email");
            this.username = object.getString("first_name");
            this.name = object.getString("name");
        } catch (JSONException | MalformedURLException e) {
            e.printStackTrace();
        }

        if (!email.isEmpty()) {
            db.collection("users")
                    .whereEqualTo("email", email)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        if (task.getResult().isEmpty())
                            createUser();
                        else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                Log.d(TAG, "onComplete: " + document.getData().get("email"));

                                loginUser(document.getData());
                                openMain();


                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onComplete: " + task.getException());
                    }
                }
            });
        }
    }

    private void loginUser(Map<String, Object> data) {
        String email = data.get("email").toString();
        String password = data.get("password").toString();

        doLogin(email, password);
    }

    private void createUser() {
        mAuth.createUserWithEmailAndPassword(email, "123456")
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
        user.setEmail(email);
        user.setName(name);
        user.setPassword("123456");
        user.setUsername(username);
        user.setPhone("");
        user.setProfilePicture(imageUrl);

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

                            dialog.dismiss();
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

        Log.d(TAG, "onActivityResult: "+data);

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
