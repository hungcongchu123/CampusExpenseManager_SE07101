package com.example.campusexpensemanager_se07101;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.campusexpensemanager_se07101.budget.BudgetSettingsHelper;

public class settingsFragment extends Fragment {
    private BudgetSettingsHelper settingsHelper;
    private Switch switchNotifications;
    private SeekBar seekBarThreshold;
    private TextView tvThresholdValue;
    private Button btnReset;

    public settingsFragment() {
        // Required empty public constructor
    }

    public static settingsFragment newInstance(String param1, String param2) {
        settingsFragment fragment = new settingsFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("param1");
            String mParam2 = getArguments().getString("param2");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        
        // Khởi tạo helper
        settingsHelper = new BudgetSettingsHelper(requireContext());
        
        // Khởi tạo views
        initViews(view);
        
        // Load cài đặt hiện tại
        loadCurrentSettings();
        
        // Thiết lập listeners
        setupListeners();
        
        return view;
    }

    private void initViews(View view) {
        switchNotifications = view.findViewById(R.id.switchNotifications);
        seekBarThreshold = view.findViewById(R.id.seekBarThreshold);
        tvThresholdValue = view.findViewById(R.id.tvThresholdValue);
        btnReset = view.findViewById(R.id.btnReset);
    }

    private void loadCurrentSettings() {
        // Load trạng thái thông báo
        boolean notificationsEnabled = settingsHelper.areNotificationsEnabled();
        switchNotifications.setChecked(notificationsEnabled);
        
        // Load ngưỡng cảnh báo
        double threshold = settingsHelper.getWarningThreshold();
        int progress = (int) ((threshold - 0.5) * 100); // Chuyển từ 0.5-1.0 thành 0-50
        seekBarThreshold.setProgress(progress);
        updateThresholdText(threshold);
    }

    private void setupListeners() {
        // Switch thông báo
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settingsHelper.setNotificationsEnabled(isChecked);
            String message = isChecked ? getString(R.string.settings_notifications_enabled_toast) : getString(R.string.settings_notifications_disabled_toast);
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        });

        // SeekBar ngưỡng cảnh báo
        seekBarThreshold.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    double threshold = 0.5 + (progress / 100.0); // Chuyển từ 0-50 thành 0.5-1.0
                    settingsHelper.setWarningThreshold(threshold);
                    updateThresholdText(threshold);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Nút reset
        btnReset.setOnClickListener(v -> {
            settingsHelper.resetToDefault();
            loadCurrentSettings();
            Toast.makeText(requireContext(), getString(R.string.settings_reset_success), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateThresholdText(double threshold) {
        int percentage = (int) (threshold * 100);
        tvThresholdValue.setText(percentage + "%");
    }
}
