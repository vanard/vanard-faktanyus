package com.vanard.faktanyus.ui.profile.edit;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.vanard.faktanyus.R;
import com.vanard.faktanyus.databinding.EditProfileFragmentBinding;
import com.vanard.faktanyus.models.auth.User;
import com.vanard.faktanyus.ui.profile.ProfileFragment;
import com.vanard.faktanyus.utils.ImagePickerActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class EditProfileFragment extends Fragment {
    private static final String TAG = "EditProfileFragment";

    private static final int REQUEST_IMAGE = 100;

    private EditProfileViewModel mViewModel;
    private EditProfileFragmentBinding binding;
    private User user;
    private Bitmap imageBitmap;
    private String extension;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private StorageReference storageRef;

    private ProgressDialog dialog;
    private Boolean changeImage, changeEmail = false;

    public static EditProfileFragment newInstance() {
        Bundle args = new Bundle();
        EditProfileFragment fragment = new EditProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.edit_profile_fragment, container, false);
        View view = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference();
        dialog = new ProgressDialog(requireContext());
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(EditProfileViewModel.class);
        binding.setViewModel(mViewModel);

        ImagePickerActivity.clearCache(requireContext());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = bundle.getParcelable("data");

            if (user != null) {
                setInitData();
            }
        }
    }

    private void setInitData() {
        initData();

        mViewModel.verificationPassword.setValue(user.getPassword());
        mViewModel.changePhone.setValue(false);
        mViewModel.changePassword.setValue(false);

        binding.nameEditProfile.setText(user.getName());
        binding.emailEditProfile.setText(user.getEmail());

        mViewModel.fullname.setValue(user.getName());
        mViewModel.email.setValue(user.getEmail());

        if (!user.getPhone().isEmpty()) {
            binding.phoneEditProfile.setText(user.getPhone());
            mViewModel.phone.setValue(user.getPhone());
        }
        else {
            binding.phoneEditProfile.setText("");
            mViewModel.phone.setValue("");
        }

        if (!user.getProfilePicture().isEmpty()) {
            loadProfile(user.getProfilePicture());
            mViewModel.profpic.setValue(user.getProfilePicture());
        }


        binding.emailEditProfile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mViewModel.email.setValue(s.toString().trim());
            }
        });

        binding.phoneEditProfile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mViewModel.phone.setValue(s.toString().trim());
            }
        });

        binding.nameEditProfile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mViewModel.fullname.setValue(s.toString().trim());
            }
        });

        binding.oldpasswordEditProfile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mViewModel.oldPassword.setValue(s.toString().trim());
            }
        });

        binding.newpasswordEditProfile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mViewModel.password.setValue(s.toString().trim());
            }
        });

        binding.repasswordEditProfile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mViewModel.repassword.setValue(s.toString().trim());
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.backEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfile();
            }
        });

        binding.profpicEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });

        binding.editEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkData();
            }
        });

    }

    private void checkData() {
        String result = mViewModel.checkInput();
        dialog.show();
        if (result.equals("") && changeImage){
            uploadImage();

        }else if (result.equals("")) {
            updateDataUser();

        } else{
            dialog.dismiss();
            Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] data = baos.toByteArray();

        String uploadRef = mAuth.getCurrentUser().getUid()+extension;

        UploadTask uploadTask = storageRef.child(uploadRef).putBytes(data);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                Double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                dialog.setProgress(progress.intValue());
            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    dialog.dismiss();

                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return storageRef.child(uploadRef).getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.d(TAG, "onComplete: "+downloadUri);
                    
                    mViewModel.profpic.setValue(downloadUri.toString());

                    updateDataUser();
                } else {
                    dialog.dismiss();
                    Log.d(TAG, "onComplete: "+task.getException());
                }
            }
        });
    }

    private void goToProfile() {
        Bundle data = new Bundle();
        data.putParcelable("data", user);
        ProfileFragment fragment = ProfileFragment.newInstance();
        fragment.setArguments(data);

        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.profile_container, fragment).addToBackStack(null);
        ft.commit();
    }

    private void updateDataUser() {
        User user = new User();
        user.setUsername(this.user.getUsername());
        user.setEmail(mViewModel.email.getValue());
        user.setName(mViewModel.fullname.getValue());
        
        if (mViewModel.changePassword.getValue()) {
            user.setPassword(mViewModel.password.getValue());
            Log.d(TAG, "updateDataUser: "+mViewModel.password.getValue());
        }
        else 
            user.setPassword(this.user.getPassword());
        
        if (mViewModel.changePhone.getValue())
        {
            user.setPhone(mViewModel.phone.getValue());
            Log.d(TAG, "updateDataUser: "+mViewModel.phone.getValue());
        }
        else 
            user.setPhone(this.user.getPhone());
        
        if (changeImage)
        {
            user.setProfilePicture(mViewModel.profpic.getValue());
            Log.d(TAG, "updateDataUser: "+mViewModel.profpic.getValue());
        }
        else 
            user.setProfilePicture(this.user.getProfilePicture());
        
        if (this.user.getEmail().equals(mViewModel.email.getValue()))
            changeEmail = true;

        if (changeEmail && mViewModel.changePassword.getValue()) {
            updateEmailandPasswordUser(user);
        }
        else if (changeEmail && !mViewModel.changePassword.getValue())
            updateEmailUser(user);
        else if (mViewModel.changePassword.getValue() && !changeEmail)
            updatePasswordUser(user);
        else
            setNewData(user);



    }

    private void updatePasswordUser(User password) {
        mUser.updatePassword(password.getPassword())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User password updated.");

                            setNewData(password);
                        }
                    }
                });
    }

    private void updateEmailandPasswordUser(User user) {
        mUser.updateEmail(user.getEmail())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User email address updated.");

                            mUser.updatePassword(user.getPassword())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User password updated.");

                                                setNewData(user);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void updateEmailUser(User email) {
        mUser.updateEmail(email.getEmail())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User email address updated.");

                            setNewData(email);
                        }
                    }
                });
    }

    private void setNewData(User user) {
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                        Toast.makeText(requireContext(), "Successfully updated profile", Toast.LENGTH_SHORT).show();

                        goToProfile();
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

    private void initData() {
        mViewModel.oldPassword.setValue("");
        mViewModel.password.setValue("");
        mViewModel.repassword.setValue("");
        mViewModel.phone.setValue("");
    }

    private void checkPermission() {
        Dexter.withActivity(requireActivity())
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(requireContext(), new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(requireContext(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 800);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 800);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(requireContext(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);

                    extension = uri.toString().substring(uri.toString().lastIndexOf("."));

                    // loading profile image from local cache
                    loadProfile(uri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadProfile(String url) {
        Log.d(TAG, "loadProfile: "+url);
        changeImage = true;

        Glide.with(this).load(url).placeholder(R.drawable.ic_account_circle_black_24dp)
                .apply(RequestOptions.circleCropTransform()).into(binding.profpicEditProfile);
        binding.profpicEditProfile.setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.transparent));
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

}
