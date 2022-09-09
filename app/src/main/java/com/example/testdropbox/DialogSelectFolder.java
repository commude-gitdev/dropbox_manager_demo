package com.example.testdropbox;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.RelocationResult;
import com.example.testdropbox.adapter.FileAdapter;
import com.example.testdropbox.callback.ListMetadataCallback;
import com.example.testdropbox.callback.Listener;
import com.example.testdropbox.callback.ListenerMetadata;
import com.example.testdropbox.callback.ListenerView;
import com.example.testdropbox.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class DialogSelectFolder extends AlertDialog {

    RelativeLayout loadingView;
    RecyclerView recyclerView;
    String token;
    FileAdapter fileAdapter;
    ImageView imgBack;
    String curPath = "";
    StringCallback stringCallback;
    DropboxAPI dropboxAPI;
    CompositeDisposable compositeDisposable;

    public DialogSelectFolder(Context context, String token, StringCallback callback) {
        super(context);
        this.token = token;
        this.stringCallback = callback;
        compositeDisposable = new CompositeDisposable();
        dropboxAPI = new DropboxAPI(compositeDisposable, token);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_select_folder, null, false);
        setContentView(view);
        initView(view);
        initRecyclerView();
        loadFolder();
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        fileAdapter = new FileAdapter(new ListenerMetadata() {
            @Override
            public void listener(Metadata metadata) {
                fileAdapter.setMetadata(metadata);
                if (metadata instanceof FolderMetadata) {
                    imgBack.setVisibility(View.VISIBLE);
                    curPath = curPath + "/" + metadata.getName();
                    loadFolder();
                }
            }
        }, new ListenerView() {
            @Override
            public void listener(View view, Metadata metadata) {

            }
        });
        recyclerView.setAdapter(fileAdapter);
    }

    private void initView(View view) {
        loadingView = view.findViewById(R.id.loadingView);
        ImageButton btnClose = view.findViewById(R.id.btnClose);
        ImageButton btnSubmit = view.findViewById(R.id.btnSubmit);
        recyclerView = view.findViewById(R.id.recyclerView);
        imgBack = view.findViewById(R.id.imgBack);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stringCallback.listener(curPath);
                dismiss();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                curPath = curPath.substring(0, curPath.lastIndexOf("/"));
                if (curPath.trim().equals("")) imgBack.setVisibility(View.GONE);
                loadFolder();
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                stringCallback = null;
                compositeDisposable.clear();
            }
        });
    }

    public void loadFolder() {
        loadingView.setVisibility(View.VISIBLE);
        dropboxAPI.loadFolder(curPath, new ListMetadataCallback() {
            @Override
            public void listener(List<Metadata> metadata) {
                fileAdapter.setMetadataList(metadata);
                loadingView.setVisibility(View.GONE);
            }
        });
    }


}
