package com.vanard.faktanyus.ui.main.mainscreen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.vanard.faktanyus.R;
import com.vanard.faktanyus.databinding.MainFragmentBinding;
import com.vanard.faktanyus.models.openweather.OpenWeatherResponse;
import com.vanard.faktanyus.network.ApiClient;
import com.vanard.faktanyus.network.ApiService;
import com.vanard.faktanyus.ui.main.date.DateFactFragment;
import com.vanard.faktanyus.ui.main.year.YearFactFragment;
import com.vanard.faktanyus.ui.profile.ProfileActivity;
import com.vanard.faktanyus.ui.splash.SplashActivity;
import com.vanard.faktanyus.utils.Common;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vanard.faktanyus.network.ApiClient.OPENWEATHER_API_KEY;
import static com.vanard.faktanyus.network.ApiClient.OPENWEATHER_IMAGE_URL;

public class MainFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {
    private static final String TAG = "MainFragment";

    private MainViewModel mViewModel;
    private MainFragmentBinding binding;
    private ApiService service;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.main_fragment, container, false);
        View view = binding.getRoot();

        ApiClient.clearClient();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        binding.setViewModel(mViewModel);
        service = ApiClient.getClient(ApiClient.OPENWEATHER_BASE_URL).create(ApiService.class);

        binding.dateFactMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateFact();
            }
        });

        binding.yearFactMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openYearFact();
            }
        });

        binding.menuIconMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMenu(v);
            }
        });

        Call<OpenWeatherResponse> call = service.getWeather("Dayeuhkolot", OPENWEATHER_API_KEY);
        call.enqueue(new Callback<OpenWeatherResponse>() {
            @Override
            public void onResponse(Call<OpenWeatherResponse> call, Response<OpenWeatherResponse> response) {

                if (response.isSuccessful() && response != null)
                    insertWeather(response.body());
                else Toast.makeText(requireContext(), "Fact not found, please try another date!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<OpenWeatherResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getLocalizedMessage());
                Toast.makeText(requireContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertWeather(OpenWeatherResponse response) {
        Glide.with(this).load(OPENWEATHER_IMAGE_URL+response.getWeather().get(0).getIcon()+".png")
                .into(binding.weatherIcon);
        double d = response.getMain().getTemp();
        String s = Double.toString(d);

        if (s.length() > 4)
            binding.weatherTemperature.setText(s.substring(0,2)+"°C");

        binding.weatherDate.setText(Common.convertUnixToDate(response.getDt()));
        binding.weatherTime.setText(Common.convertUnixToHour(response.getDt()));
    }


    private void openMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), v);
        popupMenu.setOnMenuItemClickListener(this);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.main_menu, popupMenu.getMenu());
        popupMenu.show();
    }

    private void openDateFact() {
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.main_container, DateFactFragment.newInstance());
        ft.commit();
    }

    private void openYearFact(){
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.main_container, YearFactFragment.newInstance());
        ft.commit();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_account:
                startActivity(new Intent(requireContext(), ProfileActivity.class));
                return true;
            case R.id.menu_logout:
                Toast.makeText(requireContext(), "Logout", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                facebookLogout();
                return true;
        }
        return false;
    }

    private void checkLoginStatus() {
        if (AccessToken.getCurrentAccessToken() == null) doLogout();
    }

    private void doLogout() {
        requireActivity().finish();
        startActivity(new Intent(requireContext(), SplashActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    private void facebookLogout() {
        AccessToken.setCurrentAccessToken(null);
        LoginManager.getInstance().logOut();
        doLogout();
    }


    @Override
    public void onStart() {
        super.onStart();
//        checkLoginStatus();
    }
}
