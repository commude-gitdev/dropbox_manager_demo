package com.hoangt3k56.dropbox;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.CreateFolderResult;

public class FolderNewFragment extends Fragment {

    public ListenerString listenerString;

    private Button btn_tao, btn_huy;
    private EditText edt_name_fodel;
    String name, token, mpath;

    public FolderNewFragment(String token,String path, ListenerString listenerString){
        this.listenerString = listenerString;
        this.token = token;
        this.mpath = path;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_new_folder, container, false);

        btn_huy = (Button) view.findViewById(R.id.btn_cancel_new_folder);
        btn_tao = (Button) view.findViewById(R.id.btn_new_folder);
        edt_name_fodel = (EditText) view.findViewById(R.id.edt_name_folder);


        btn_tao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = edt_name_fodel.getText().toString();
                if(name != null  && !name.isEmpty()) {
                    listenerString.listenerString(name);
                    new NewFolder().execute(name);
                } else {
                    Toast.makeText(getContext(), "không được bỏ trống", Toast.LENGTH_SHORT).show();
                }
            }

        });

        btn_huy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = "hoang_dev_cancel";
                listenerString.listenerString(name);
            }
        });


        return view;
    }


    public class NewFolder extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
            DbxClientV2 client = new DbxClientV2(config, token);
            CreateFolderResult folder = null;
            try {
                String path = mpath+ "/" +name;
                Log.d("hoangdev","path them:  " +path);
                 folder = client.files().createFolderV2(path);
                 return true;
            } catch (DbxException e) {
                e.printStackTrace();
                Log.e("hoangdev", e.toString());
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                Log.d("hoangdev", "theem folder thanh cong");
            } else {
                Log.e("hoangdev", "ko them dc folder");
            }
        }
    }
}
