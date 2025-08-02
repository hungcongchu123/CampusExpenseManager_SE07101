package com.example.campusexpensemanager_se07101;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.campusexpensemanager_se07101.adapter.BudgetRVAdapter;
import com.example.campusexpensemanager_se07101.budget.AddBudgetActivity;
import com.example.campusexpensemanager_se07101.budget.EditBudgetActivity;
import com.example.campusexpensemanager_se07101.database.BudgetRepository;
import com.example.campusexpensemanager_se07101.model.Budget;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class budgetFragment extends Fragment {

    private List<Budget> budgetArrayList;
    private BudgetRVAdapter budgetRVAdapter;
    private BudgetRepository budgetRepository;
    private RecyclerView budgetRv;
    private int userId;

    public budgetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);
        Button btnCreteBudget = view.findViewById(R.id.btnCreateBudget);
        budgetRv = view.findViewById(R.id.rvBudget);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        if (userId != -1) {
            budgetRepository = new BudgetRepository(getActivity());
            budgetArrayList = budgetRepository.getAllBudgetsByUserId(userId);

            if (budgetArrayList != null) {
                budgetRVAdapter = new BudgetRVAdapter(budgetArrayList, getContext());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
                budgetRv.setLayoutManager(linearLayoutManager);
                budgetRv.setAdapter(budgetRVAdapter);

                budgetRVAdapter.setOnClickListener(position -> {
                    Budget budget = budgetArrayList.get(position);
                    int id = budget.getId();

                    Intent intent = new Intent(getActivity(), EditBudgetActivity.class);
                    intent.putExtra("budget_id", id);
                    startActivity(intent);
                });
            } else {
                Toast.makeText(getActivity(), "Không có ngân sách nào được tìm thấy.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "User not logged in!", Toast.LENGTH_SHORT).show();
        }

        btnCreteBudget.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddBudgetActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (userId != -1 && budgetRepository != null && budgetRVAdapter != null) {
            budgetArrayList = budgetRepository.getAllBudgetsByUserId(userId);
            budgetRVAdapter.updateData(budgetArrayList);
        }
    }
}