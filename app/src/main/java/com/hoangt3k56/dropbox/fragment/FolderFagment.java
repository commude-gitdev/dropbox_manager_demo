package com.hoangt3k56.dropbox.fragment;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DeleteResult;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.RelocationResult;
import com.dropbox.core.v2.sharing.PathLinkMetadata;
import com.hoangt3k56.dropbox.listener.ListenerMetadata;
import com.hoangt3k56.dropbox.R;
import com.hoangt3k56.dropbox.adapter.FileAdapter;

import java.util.ArrayList;
import java.util.List;


public class FolderFagment extends Fragment {

    RecyclerView recyclerView;
    FileAdapter fileAdapter;
    HomeFragment homeFragment;
    RelativeLayout relativeLayout;

    public String token, mpath;
    String type_file;
    public static String copy_move_path ="";
    public static int paste = 0;

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
        fileAdapter=new FileAdapter(new ListenerMetadata() {
            @Override
            public void listener(Metadata metadata, int i) {
                if (i == 0) {
                    if (metadata instanceof FolderMetadata) {
                        Log.d("hoangdev",  "den path:   " + metadata.getPathLower());
                        replaceFragment(new FolderFagment(token, metadata.getPathLower()));
                        HomeFragment.mpath = metadata.getPathLower();
                    } else if (metadata instanceof FileMetadata) {
                        String file_name = metadata.getName();
                        type_file = typeFile(file_name);
                        new showFileMetadata().execute(metadata.getPathLower());
                    }

                } else if (i == 1) {
                    showMenu(metadata);
                }
            }
        });
        recyclerView.setAdapter(fileAdapter);
        loadData();

        return view;
    }

    private String typeFile(String file_name) {
        String type_file = "error";
        if (file_name.contains(".jpg") || file_name.contains(".jpge")) {
            type_file = "img";
        } else if (file_name.contains(".mp4")) {
            type_file = "mp4";
        }
        return type_file;
    }

    private class showFileMetadata extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
            DbxClientV2 client = new DbxClientV2(config,token);
            try {
                PathLinkMetadata sharedLinkMetadata = client.sharing().createSharedLink(strings[0]);
//                Log.d("hoangdev", a.getResult().getFileLockInfo().getCreated().toString());

//                Log.d("hoangdev", sharedLinkMetadata.getName());
                Log.d("hoangdev", sharedLinkMetadata.getUrl());
                return sharedLinkMetadata.getUrl().replace("?dl=0", "?raw=1");
            } catch (DbxException e) {
                e.printStackTrace();
                Log.e("hoangdev", "Loi tao url link \n" + e.getRequestId());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String url) {
            if (url != null) {
//                Log.d("hoangdev", url);
                if ( !type_file.equals("error")) {
                    replaceFragment(new ViewFileFragment(url, type_file));
                } else {
                    Toast.makeText(getContext(), "App không hỗ trợ mở file này !", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getContext(), "App không hỗ trợ mở file này !", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showMenu(Metadata metadata) {
        PopupMenu popupMenu = new PopupMenu(getContext(), getView().findViewById(R.id.item_file));
        popupMenu.getMenuInflater().inflate(R.menu.onclick_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.properties:
                        properties(metadata);
                        break;
                    case R.id.delete:
                        delete(metadata);
                        break;
                    case R.id.move:
                        FolderFagment.paste = 1;
                        FolderFagment.copy_move_path = metadata.getPathLower();
                        Log.d("hoangdev", copy_move_path);
                        break;
                    case R.id.copy:
                        FolderFagment.paste = 2;
                        FolderFagment.copy_move_path = metadata.getPathLower();
                        Log.d("hoangdev", copy_move_path);
                        break;
                    case R.id.paste:
                        paste(metadata);
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void paste(Metadata metadata) {
        if (paste == 0) {
            Log.d("hoangdev", "" + paste);
        } else if (paste == 1){
            // move Folder/file
            new moveFolder().execute(metadata.getPathLower());
            FolderFagment.paste = 0;
            replaceFragment(new FolderFagment(token, metadata.getPathLower()));
        }  else if (paste == 2){
            // copy Folder/file
            new copyFolder().execute(metadata.getPathLower());
            FolderFagment.paste = 0;
            replaceFragment(new FolderFagment(token, metadata.getPathLower()));
        }
    }

    private class copyFolder extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
            DbxClientV2 client = new DbxClientV2(config,token);
            String [] a = FolderFagment.copy_move_path.split("/");
            try {
                Log.d("hoangdev", "copy:  " + FolderFagment.copy_move_path + "  -->  " + strings[0]);
                RelocationResult copyV2 =
                        client.files().copyV2(FolderFagment.copy_move_path, strings[0] + "/" + a[a.length-1]);
                return true;
            } catch (DbxException e) {
                e.printStackTrace();
                Log.e("hoangdev", "Loi copy file " + e.toString());
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                Log.d("hoangdev", "copy thanh cong thanh cong");
            } else {
                Log.e("hoangdev", "Copy khong thanh cong");
            }
        }
    }

    private class moveFolder extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
            DbxClientV2 client = new DbxClientV2(config,token);
            String [] a = FolderFagment.copy_move_path.split("/");

            try {
                Log.d("hoangdev", "move:   " + FolderFagment.copy_move_path + "  -->  " + strings[0]);
                RelocationResult copyV2 =
                        client.files().moveV2(FolderFagment.copy_move_path, strings[0] + "/" + a[a.length-1]);
                return true;
            } catch (DbxException e) {
                e.printStackTrace();
                Log.e("hoangdev", "Loi di chuyen file " + e.toString());
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                Log.d("hoangdev", "di chuyen thanh cong thanh cong");
            } else {
                Log.e("hoangdev", "move khong thanh cong");
            }
        }
    }

    private void delete(Metadata metadata) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Delete");
        dialog.setMessage("Bạn chắc chắn muốn xóa "+ metadata.getName() + " ?");
        dialog.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new deleteFolder().execute(metadata.getPathLower());
                replaceFragment(new FolderFagment(token, HomeFragment.mpath));
            }
        });
        dialog.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog.show();

    }

    private class deleteFolder extends AsyncTask<String, Void, Boolean> {


        @Override
        protected Boolean doInBackground(String... strings) {
            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
            DbxClientV2 client = new DbxClientV2(config,token);

            try {
                DeleteResult deleteV2 = client.files().deleteV2(strings[0]);
                return true;
            } catch (DbxException e) {
                e.printStackTrace();
                Log.e("hoangdev", "Loi xoa file "+ e.toString());
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                Log.d("hoangdev", "xoa thanh cong");
            }
        }
    }

    private void properties(Metadata metadata) {
        String message = "";
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Properties");

        if (metadata instanceof FileMetadata) {
            FileMetadata  fileMetadata = (FileMetadata) metadata;
            message = "Tên:  " + fileMetadata.getName() +"\n"
                    + "Path:  " + fileMetadata.getPathLower() +"\n"
                    + "Size:  " + fileMetadata.getSize() +" B\n"
                    + "Ngày tạo:  " + fileMetadata.getServerModified() +"\n"
                    + "Ngày edit:  " + fileMetadata.getClientModified();
        } else {
            FolderMetadata folderMetadata = (FolderMetadata) metadata;
            message = "Tên:  " + folderMetadata.getName() +"\n"
                    + "Path:  " + folderMetadata.getPathLower();
        }

        dialog.setMessage(message);
        dialog.show();
    }


    public void loadData() {
        relativeLayout.setVisibility(View.VISIBLE);
        new GetFileAndFolder().execute(mpath);
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
                result = client.files().listFolder(strings[0]);
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
