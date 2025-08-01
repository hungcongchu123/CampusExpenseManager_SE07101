package com.example.campusexpensemanager_se07101;

import android.content.Intent;
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
import com.example.campusexpensemanager_se07101.database.BudgetModel;
import com.example.campusexpensemanager_se07101.database.BudgetRepository;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link budgetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class budgetFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<BudgetModel> budgetModelArrayList;
    private BudgetRVAdapter budgetRVAdapter;
    private BudgetModel budgetModel;
    private BudgetRepository budgetRepository;
    private RecyclerView budgetRv;

    public budgetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment budgetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static budgetFragment newInstance(String param1, String param2) {
        budgetFragment fragment = new budgetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_budget, container, false);
        Button btnCreteBudget = view.findViewById(R.id.btnCreateBudget);
        budgetRv = view.findViewById(R.id.rvBudget);
        budgetModelArrayList = new ArrayList<>();
        budgetRepository = new BudgetRepository(getActivity());
        budgetModelArrayList = budgetRepository.getListBudget();
        budgetRVAdapter = new BudgetRVAdapter(budgetModelArrayList, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,false);
        budgetRv.setLayoutManager(linearLayoutManager);
        budgetRv.setAdapter(budgetRVAdapter);
        budgetRVAdapter.setOnClickListener(new BudgetRVAdapter.OnClickListener() {
            @Override
            public void onClick(int postition) {
                BudgetModel model = budgetModelArrayList.get(postition);
                String name = model.getBudgetName();
                int money = model.getBudgetMoney();
                int id = model.getId();
                String description = model.getBudgetDescription();
                //dung intent + Bundle de chuyen data sang EditBudget
                Intent intent = new Intent(getActivity(), EditBudgetActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("ID_BUDGET", id);
                bundle.putString("NAME_BUDGET", name);
                bundle.putInt("MONEY_BUDGET", money);
                bundle.putString("DESCRIPTION_BUDGET", description);
                intent.putExtras(bundle);
                startActivity(intent);
                //Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();
            }
        });



        btnCreteBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // chuyen sang man add budget
                Intent intent = new Intent(getActivity(), AddBudgetActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}