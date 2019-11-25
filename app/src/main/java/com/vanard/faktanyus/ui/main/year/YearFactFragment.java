package com.vanard.faktanyus.ui.main.year;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.vanard.faktanyus.R;
import com.vanard.faktanyus.databinding.YearFactFragmentBinding;
import com.vanard.faktanyus.models.rapidapi.YearFactResponse;
import com.vanard.faktanyus.network.ApiClient;
import com.vanard.faktanyus.network.ApiService;
import com.vanard.faktanyus.ui.main.mainscreen.MainFragment;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YearFactFragment extends Fragment {
    private static final String TAG = "YearFactFragment";

    private YearFactViewModel mViewModel;
    private YearFactFragmentBinding binding;
    private ApiService service;

    int year = Calendar.getInstance().get(Calendar.YEAR);

    public static YearFactFragment newInstance() {
        return new YearFactFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.year_fact_fragment, container, false);
        View view = binding.getRoot();

        ApiClient.clearClient();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(YearFactViewModel.class);
        binding.setViewModel(mViewModel);
        service = ApiClient.getClient(ApiClient.BASE_URL).create(ApiService.class);

        binding.yearInput.setFocusable(false);
        binding.yearInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        binding.backYearFact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.main_container, MainFragment.newInstance()).addToBackStack(null);
                ft.commit();

            }
        });

        binding.factButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = binding.yearInput.getText().toString().trim();
                if (input.isEmpty()) {
                    Toast.makeText(requireContext(), "WAOW" + input, Toast.LENGTH_SHORT).show();
                }
                else {
                    Call<YearFactResponse> call = service.getYearFact(input, "true", "true");
                    call.enqueue(new Callback<YearFactResponse>() {
                        @Override
                        public void onResponse(Call<YearFactResponse> call, Response<YearFactResponse> response) {

                            if (response.body() != null) {

                                if (response.body().isFound())
                                    openDescription(response);
                                else
                                    Toast.makeText(requireContext(), "Fact not found, please try another date!", Toast.LENGTH_SHORT).show();
                            }
                            else Toast.makeText(requireContext(), "Fact not found, please try another date!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<YearFactResponse> call, Throwable t) {
                            Toast.makeText(requireContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void openDescription(Response<YearFactResponse> response) {
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.main_container, YearFactDescriptionFragment.newInstance(response.body()));
        ft.commit();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void openDatePicker(){

        final Dialog d = new Dialog(requireActivity());
        d.setTitle("Year Picker");
        d.setContentView(R.layout.year_dialog);
        Button set = (Button) d.findViewById(R.id.button1);
        Button cancel = (Button) d.findViewById(R.id.button2);
        TextView year_text=(TextView)d.findViewById(R.id.year_text);
        year_text.setText(""+year);
        final NumberPicker nopicker = (NumberPicker) d.findViewById(R.id.numberPicker);

        nopicker.setMaxValue(year+1000);
        nopicker.setMinValue(year-1000);
        nopicker.setWrapSelectorWheel(false);
        nopicker.setValue(year);
        nopicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.yearInput.setText(String.valueOf(nopicker.getValue()));
                d.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.setTitle("Select Year");
        d.show();

    }

}
