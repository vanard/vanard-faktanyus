package com.vanard.faktanyus.ui.main.date;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
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
import com.vanard.faktanyus.databinding.DateFactFragmentBinding;
import com.vanard.faktanyus.models.rapidapi.DateFactResponse;
import com.vanard.faktanyus.network.ApiClient;
import com.vanard.faktanyus.network.ApiService;
import com.vanard.faktanyus.ui.main.mainscreen.MainFragment;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DateFactFragment extends Fragment {
    private static final String TAG = "DateFactFragment";

    private DateFactViewModel mViewModel;
    private DateFactFragmentBinding binding;
    private ApiService service;

    public static DateFactFragment newInstance() {
        return new DateFactFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.date_fact_fragment, container, false);
        View view = binding.getRoot();

        ApiClient.clearClient();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(DateFactViewModel.class);
        binding.setViewModel(mViewModel);
        service = ApiClient.getClient(ApiClient.BASE_URL).create(ApiService.class);

        binding.dayDateInput.setFocusable(false);
        binding.dayDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDayPicker();
            }
        });

        binding.monthDateInput.setFocusable(false);
        binding.monthDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMonthPicker();
            }
        });

        binding.backDateFact.setOnClickListener(new View.OnClickListener() {
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
                final String inputMonth = binding.monthDateInput.getText().toString().trim();
                final String inputDay = binding.dayDateInput.getText().toString().trim();
                

                
                if (inputMonth.isEmpty() || inputDay.isEmpty()) {
                    Toast.makeText(requireContext(), "Month " + inputMonth
                            + "\n Day " + inputDay, Toast.LENGTH_SHORT).show();
                }
                else {
                    Call<DateFactResponse> call = service.getDateFact(inputMonth, inputDay, "true", "true");
                    call.enqueue(new Callback<DateFactResponse>() {
                        @Override
                        public void onResponse(Call<DateFactResponse> call, Response<DateFactResponse> response) {

                            if (response.body() != null) {
                                
                                if (response.body().isFound())
                                    openDescription(response, inputMonth, inputDay);
                                else
                                    Toast.makeText(requireContext(), "Fact not found, please try another date!", Toast.LENGTH_SHORT).show();
                            }
                            else Toast.makeText(requireContext(), "Fact not found, please try another date!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<DateFactResponse> call, Throwable t) {
                            Toast.makeText(requireContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void openDescription(Response<DateFactResponse> response, String inputMonth, String inputDay) {
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        Log.d(TAG, "openDescription: "+inputMonth.length());
        if (inputMonth.length() == 1 && inputDay.length() == 1)
            ft.add(R.id.main_container, DateFactDescriptionFragment.newInstance(response.body(), "0"+inputMonth, "0"+inputDay));
        else if (inputDay.length() == 1)
            ft.add(R.id.main_container, DateFactDescriptionFragment.newInstance(response.body(), "0"+inputMonth, inputDay));
        else if (inputMonth.length() == 1)
            ft.add(R.id.main_container, DateFactDescriptionFragment.newInstance(response.body(), inputMonth, "0"+inputDay));
        else
            ft.add(R.id.main_container, DateFactDescriptionFragment.newInstance(response.body(), inputMonth, inputDay));

        ft.commit();
    }

    private void openMonthPicker() {
        final Calendar today = Calendar.getInstance();
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(requireContext(), new MonthPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int selectedMonth, int selectedYear) {
                binding.monthDateInput.setText(""+(selectedMonth+1));
            }
        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));


        builder .setTitle("Select month")
                .setMonthRange(Calendar.JANUARY, Calendar.DECEMBER)
                .showMonthOnly()
                .setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                    @Override
                    public void onMonthChanged(int selectedMonth) {
                        binding.monthDateInput.setText(""+(selectedMonth+1));
                    } })
                .build()
                .show();
    }

    private void openDayPicker(){
        Calendar c = Calendar.getInstance();
        final Dialog d = new Dialog(requireActivity());
        d.setTitle("Year Picker");
        d.setContentView(R.layout.year_dialog);
        Button set = (Button) d.findViewById(R.id.button1);
        Button cancel = (Button) d.findViewById(R.id.button2);
        TextView year_text=(TextView)d.findViewById(R.id.year_text);
        year_text.setText(""+c.get(Calendar.DAY_OF_MONTH));
        final NumberPicker nopicker = (NumberPicker) d.findViewById(R.id.numberPicker);

        nopicker.setMaxValue(30);
        nopicker.setMinValue(1);
        nopicker.setWrapSelectorWheel(false);
        nopicker.setValue(c.get(Calendar.DAY_OF_MONTH));
        nopicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.dayDateInput.setText(String.valueOf(nopicker.getValue()));
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
        d.setTitle("Select Day");
        d.show();

    }

}
