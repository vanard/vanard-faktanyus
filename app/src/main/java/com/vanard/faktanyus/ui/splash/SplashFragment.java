package com.vanard.faktanyus.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.vanard.faktanyus.R;
import com.vanard.faktanyus.ui.main.MainActivity;
import com.vanard.faktanyus.ui.splash.login.LoginFragment;

public class SplashFragment extends Fragment {

    public static SplashFragment newInstance() {
        return new SplashFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.splash_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        splashScreen();
    }

    private void splashScreen() {
        if (true){

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    while (getFragmentManager() == null) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.splash_container, LoginFragment.newInstance(), "login");
                    ft.commitAllowingStateLoss();

                }
            }, 2000);

        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent home = new Intent(requireContext(), MainActivity.class);
                    startActivity(home);
                    requireActivity().finish();

                }
            }, 2000);
        }
    }

}
