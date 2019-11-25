package com.vanard.faktanyus.ui.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.vanard.faktanyus.R;
import com.vanard.faktanyus.network.ApiClient;
import com.vanard.faktanyus.ui.main.mainscreen.MainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, MainFragment.newInstance())
                    .commitNow();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        ApiClient.clearClient();
    }
}
