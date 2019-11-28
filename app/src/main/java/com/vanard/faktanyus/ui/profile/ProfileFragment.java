package com.vanard.faktanyus.ui.profile;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vanard.faktanyus.R;
import com.vanard.faktanyus.databinding.ProfileFragmentBinding;
import com.vanard.faktanyus.models.auth.User;
import com.vanard.faktanyus.ui.profile.edit.EditProfileFragment;

public class  ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private ProfileViewModel mViewModel;
    private ProfileFragmentBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private User user;

    private ProgressDialog dialog;

    public static ProfileFragment newInstance() {
        Bundle args = new Bundle();
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.profile_fragment, container, false);
        View view = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(requireContext());
        dialog.setMessage("Load profile data...");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialog.show();
    }

    private void setInitData() {
        binding.usernameProfile.setText(user.getUsername());
        binding.nameProfile.setText(user.getName());
        binding.emailProfile.setText(user.getEmail());
        if (!user.getPhone().isEmpty())
            binding.phoneProfile.setText(user.getPhone());
        else
            binding.phoneProfile.setText("Phone number is not set yet");

        if (!user.getProfilePicture().isEmpty())
            Glide.with(this).load(user.getProfilePicture()).apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.ic_account_circle_black_24dp).into(binding.profpicProfile);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        binding.setViewModel(mViewModel);

        initData();

        binding.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putParcelable("data", user);
                EditProfileFragment fragment = EditProfileFragment.newInstance();
                fragment.setArguments(data);

                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.profile_container, fragment);
                ft.commit();
            }
        });

        binding.backProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().finish();
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAuth.getCurrentUser() == null)
            requireActivity().finish();
        else
            getData();

    }

    private void getData() {
        db.collection("users").document(mAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            user = task.getResult().toObject(User.class);

                            dialog.dismiss();
                            setInitData();
                        } else {
                            Log.d(TAG, "onComplete: "+ task.getException());
                            dialog.dismiss();
                        }
                    }
                });
    }

    private void initData() {
        mViewModel.email.setValue("");
        mViewModel.fullname.setValue("");
        mViewModel.username.setValue("");
        mViewModel.password.setValue("");
        mViewModel.repassword.setValue("");
        mViewModel.phone.setValue("");
    }

}
