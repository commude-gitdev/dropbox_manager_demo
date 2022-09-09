package com.example.testdropbox.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dropbox.core.android.AuthActivity;
import com.example.testdropbox.ItemNav;
import com.example.testdropbox.activity.MainActivity;
import com.example.testdropbox.adapter.NavAdapter;
import com.example.testdropbox.R;
import com.example.testdropbox.callback.Listener;
import com.example.testdropbox.callback.ListenerInt;

import java.util.ArrayList;
import java.util.List;

public class NavSettingFragment extends Fragment {

    RecyclerView recyclerView;
    Listener listener;


    public NavSettingFragment(Listener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_nav_setting, container, false);
        initRecyclerView(view);
        return view;
    }

    private void initRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        List<ItemNav> itemNavList = new ArrayList<>();
        itemNavList.add(new ItemNav(R.drawable.ic_baseline_person_24, "Profile"));
        itemNavList.add(new ItemNav(R.drawable.ic_baseline_logout_24, "Logout"));
        NavAdapter adapter = new NavAdapter(itemNavList, new ListenerInt() {
            @Override
            public void listener(int i) {
                switch (i) {
                    case 0:
                        gotoProfileView();
                        break;
                    case 1:
                        logout();
                        break;
                }
            }
        });
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void gotoProfileView() {
        listener.listener();
    }

    private void logout() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AuthActivity.result = null;
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });
        alertDialog.setTitle("Logout");
        alertDialog.setMessage("Do you want to sign out ?");
        alertDialog.show();
    }
}
