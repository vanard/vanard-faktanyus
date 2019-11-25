



package com.vanard.faktanyus.ui.main.year;

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

import com.vanard.faktanyus.R;
import com.vanard.faktanyus.databinding.FragmentYearFactDescriptionBinding;
import com.vanard.faktanyus.models.rapidapi.YearFactResponse;
import com.vanard.faktanyus.network.ApiClient;
import com.vanard.faktanyus.ui.main.mainscreen.MainFragment;


public class YearFactDescriptionFragment extends Fragment {
    private static final String TAG = "YearFactDescriptionFrag";

    private YearDescriptionViewModel viewModel;
    private FragmentYearFactDescriptionBinding binding;

    public static YearFactDescriptionFragment newInstance() {
        return new YearFactDescriptionFragment();
    }

    public static YearFactDescriptionFragment newInstance(YearFactResponse response) {
        YearFactDescriptionFragment fragment = new YearFactDescriptionFragment();
        Bundle args = new Bundle();
        args.putParcelable("fact", response);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_year_fact_description, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(YearDescriptionViewModel.class);
        binding.setViewModel(viewModel);

        if (getArguments() != null){
            YearFactResponse factResponse = getArguments().getParcelable("fact");
            binding.factDescriptionYearDate.setText(factResponse.getNumber()+"");
            binding.factDescriptionYearText.setText(factResponse.getText());

            Log.d(TAG, "onViewCreated: "+factResponse);
        }

        binding.homeYearFact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.main_container, MainFragment.newInstance()).addToBackStack(null);
                ft.commit();

                ApiClient.clearClient();

            }
        });
    }
}
