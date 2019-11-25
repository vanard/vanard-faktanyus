package com.vanard.faktanyus.ui.main.date;


import android.os.Bundle;
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
import com.vanard.faktanyus.databinding.FragmentDateFactDescriptionBinding;
import com.vanard.faktanyus.models.rapidapi.DateFactResponse;
import com.vanard.faktanyus.network.ApiClient;
import com.vanard.faktanyus.ui.main.mainscreen.MainFragment;

public class DateFactDescriptionFragment extends Fragment {
    private static final String TAG = "DateFactDescriptionFrag";

    private DateDescriptionViewModel viewModel;
    private FragmentDateFactDescriptionBinding binding;

    public static DateFactDescriptionFragment newInstance() {
        return new DateFactDescriptionFragment();
    }

    public static DateFactDescriptionFragment newInstance(DateFactResponse response, String m, String d) {
        DateFactDescriptionFragment fragment = new DateFactDescriptionFragment();
        Bundle args = new Bundle();
        args.putParcelable("fact", response);
        args.putString("month", m);
        args.putString("day", d);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_date_fact_description, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(DateDescriptionViewModel.class);
        binding.setViewModel(viewModel);

        if (getArguments() != null){
            DateFactResponse factResponse = getArguments().getParcelable("fact");
            binding.factDescriptionDateText.setText(factResponse.getText());

            binding.factDescriptionMonthDate.setText(getArguments().getString("month"));
            binding.factDescriptionDayDate.setText(getArguments().getString("day"));
        }

        binding.homeDateFact.setOnClickListener(new View.OnClickListener() {
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
