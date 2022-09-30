package com.hoangt3k56.dropbox;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.sharing.ListFilesResult;

import java.util.ArrayList;
import java.util.List;

public class FolderFagment extends Fragment {

    RecyclerView recyclerView;
    FileAdapter fileAdapter;
    HomeFragment homeFragment;
    RelativeLayout relativeLayout;

    public String token, mpath;

    public FolderFagment(String token, String path){
        this.token = token;
        this.mpath = path;
    }

    public void setMpath(String mpath) {
        this.mpath = mpath;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder, container, false);
        relativeLayout = view.findViewById(R.id.loadingViewListFolder);
        relativeLayout.setVisibility(View.VISIBLE);
        recyclerView=view.findViewById(R.id.rcView_home_fragment);
        recyclerView.setHasFixedSize(true);
        homeFragment = new HomeFragment();
        LinearLayoutManager linearLayoutManager=new GridLayoutManager(getContext(),3,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        fileAdapter=new FileAdapter(new ListenerString() {
            @Override
            public void listenerString(String path) {
                Log.d("hoangdev",  "den path:   " + path);
                replaceFragment(new FolderFagment(token, path));
                HomeFragment.mpath = path;
            }
        });
        recyclerView.setAdapter(fileAdapter);
        loadData();

        return view;
    }


    public void loadData() {
        relativeLayout.setVisibility(View.VISIBLE);
        new GetFileAndFolder().execute("");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private class GetFileAndFolder extends AsyncTask<String, Void, List<Metadata>> {

        @Override
        protected List<Metadata> doInBackground(String... strings) {
            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
            DbxClientV2 client = new DbxClientV2(config,token);
            // Get files and folder metadata from Dropbox root directory
            List<Metadata> list = new ArrayList<>();
            ListFolderResult result = null;
            try {
                result = client.files().listFolder(mpath);
                Log.d("hoangdev", "List metadata path: " + mpath);
                while (true) {
                    for (Metadata metadata : result.getEntries()) {
//                        Log.d("hoangdev", metadata.getPathLower().toString());
                        list.add(metadata);
                    }

                    if (!result.getHasMore()) {
                        break;
                    }

                    result = client.files().listFolderContinue(result.getCursor());
                }
            } catch (DbxException e) {
                e.printStackTrace();
                Log.e("hoangdev", "loi get FileFolder\n"+ e.toString());
            }


            return list;

        }

        @Override
        protected void onPostExecute(List<Metadata> metadataList) {
            super.onPostExecute(metadataList);
            fileAdapter.setMetadataList(metadataList);
            relativeLayout.setVisibility(View.GONE);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction=getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_home, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
