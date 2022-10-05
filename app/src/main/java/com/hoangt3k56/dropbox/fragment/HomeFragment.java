package com.hoangt3k56.dropbox.fragment;


import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.hoangt3k56.dropbox.listener.ListenerString;
import com.hoangt3k56.dropbox.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class HomeFragment extends Fragment {

    String token;
    Toolbar toolbar;
    FolderFagment folderFagment;
    FolderNewFragment folderNewFragment;
    private int CAMERA_REQUEST =1221;
    private int SELESECT_FILE_REQUEST = 1111;
    public static String mpath="";
    private Uri uri_up_load;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_home, container, false);
        token = getArguments().getString("TOKEN");



        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.right_menu);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case  R.id.newFolder:
                        newFodel();
                        break;
                    case R.id.refresh:
                        HomeFragment.mpath = "";
                        replaceFragment(new FolderFagment(token, mpath));
                        break;
                    case R.id.upload:
                        upload();
                        break;
                    case R.id.takePhoto:
                        takePhoto();
                        break;
                }
                return false;
            }
        });

        folderFagment = new FolderFagment(token, mpath);
        replaceFragment(folderFagment);

        return view;
    }




    private void takePhoto() {
        rqPermissions();
    }

    public void rqPermissions() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                pickImage();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getContext(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }

        };
        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("Nếu bạn từ chối quyền, bạn không thể sử dụng dịch vụ này\n\nVui lòng bật quyền tại [Setting]> [Permission]")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    public void pickImage() {
        Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cInt,CAMERA_REQUEST);
    }

    private void upload() {
        openFile();
    }

    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, SELESECT_FILE_REQUEST);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST && data != null && data.getData() != null) {
                Uri uri = data.getData();
                Log.d("hoangdev", "uri take photo:  " + uri.toString());
            }

            if (requestCode == SELESECT_FILE_REQUEST && data.getData() != null) {
                uri_up_load = data.getData();
//                Log.d("hoangdev", "uri name:  " + DocumentFile.fromSingleUri(getContext(), uri_up_load).getName());
//                Log.d("hoangdev", "uri uri:  " + DocumentFile.fromSingleUri(getContext(), uri_up_load).getUri());
//                Log.d("hoangdev", "uri type:  " + DocumentFile.fromSingleUri(getContext(), uri_up_load).getType());
//                Log.d("hoangdev", "uri parentfile:  " + DocumentFile.fromSingleUri(getContext(), uri_up_load).getParentFile());
                Log.d("","URI = "+ uri_up_load);
                new upLoad().execute("");
                replaceFragment(new FolderFagment(token, mpath));
            }
        }
    }


    public class upLoad extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
            DbxClientV2 client = new DbxClientV2(config, token);
            try {
                InputStream in = getContext().getContentResolver().openInputStream(uri_up_load);
                String name_file = DocumentFile.fromSingleUri(getContext(), uri_up_load).getName();

                FileMetadata metadata = client.files().uploadBuilder(mpath+"/"+name_file).uploadAndFinish(in);
                return true;
            } catch (DbxException e) {
                e.printStackTrace();
                Log.e("hoangdev", e.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("hoangdev", e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("hoangdev", e.toString());
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                Log.d("hoangdev", "upload file thanh cong");
            } else {
                Log.e("hoangdev", "ko upload dc file");
            }
        }
    }



    private void newFodel() {

        folderNewFragment = new FolderNewFragment( token, mpath,new ListenerString() {
            @Override
            public void listenerString(String name_folder) {
//                Log.d("hoangdev", "mpath"+mpath);
                removeFragment(folderNewFragment);
                replaceFragment(new FolderFagment(token, mpath));
            }
        });
        replaceFragment(folderNewFragment);
    }

    private void replaceFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction=getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_home, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }



    private void removeFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction=getParentFragmentManager().beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }

}
