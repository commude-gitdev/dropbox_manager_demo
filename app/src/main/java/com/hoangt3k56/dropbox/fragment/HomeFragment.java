package com.hoangt3k56.dropbox.fragment;


import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.hoangt3k56.dropbox.api.ApiService;
import com.hoangt3k56.dropbox.listener.ListenerBoolean;
import com.hoangt3k56.dropbox.R;
import com.hoangt3k56.dropbox.model.Arg;
import com.hoangt3k56.dropbox.model.Enty;
import com.hoangt3k56.dropbox.model.FileUtils;
import com.hoangt3k56.dropbox.model.Folder;
import com.hoangt3k56.dropbox.model.RealPathUtil;
import com.hoangt3k56.dropbox.model.UpdatePost;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    String token;
    Toolbar toolbar;

    Uri uri_up_load, mUri;
    FolderFagment folderFagment;

    private int CAMERA_REQUEST =1221;
    private int SELESECT_FILE_REQUEST = 1111;
    public static String mpath="";



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_home, container, false);
        token = getArguments().getString("TOKEN");
        initToobar(view);
        folderFagment = new FolderFagment(token, mpath);
        replaceFragment(folderFagment);
        return view;
    }

    private void initToobar(View view) {
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
                        replaceFragment(new FolderFagment(token, mpath));
                        break;
                    case R.id.upload:
                        openFile();
                        break;
                    case R.id.takePhoto:
                        // loi camera
                        takePhoto();
                        break;
                }
                return false;
            }
        });
    }

    private void newFodel() {
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_add_folder, null);
        Dialog dialog = new Dialog(getContext(), R.style.full_screen_dialog);
        dialog.setContentView(view);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        EditText editText = view.findViewById(R.id.edtName);
        ImageButton imageButton = view.findViewById(R.id.btnClose);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        Button button = view.findViewById(R.id.btnCreate);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().length() == 0) {
                    Toast.makeText(getContext(), "Please enter folder name", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    String path = HomeFragment.mpath + "/" + editText.getText();
                    ApiService.apiService.createFolder(token, new Folder(path)).enqueue(new Callback<Enty>() {
                        @Override
                        public void onResponse(Call<Enty> call, Response<Enty> response) {
                            Toast.makeText(getContext(), "NewFolder success", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Enty> call, Throwable t) {
                            Toast.makeText(getContext(), "NewFolder error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }



    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, SELESECT_FILE_REQUEST);

    }

    private void upload() {
        File file = new File(uri_up_load.getPath());
        Log.e("hoangdev", "File " + file.toString()     );
        String pathFile = mpath + "/" + file.getName();
        Log.e("hoangdev", "pathFile " + pathFile);
        Arg arg = new Arg(pathFile);
        Log.d("hoangdev", "arg  " + arg.toString());
//        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
//        RequestBody filename = RequestBody.create(MediaType.parse("multipart/form-data"), file.getName());
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        Log.e("hoangdev", "File " + requestBodyee.toString());
        Map<String, String > map = new HashMap<>();
        map.put("Authorization", token);
        map.put("Dropbox-API-Arg", arg.toString());
        map.put("Content-Type", "application/octet-stream");

        ApiService.apiService.update(map, requestFile).enqueue(new Callback<UpdatePost>() {
            @Override
            public void onResponse(Call<UpdatePost> call, Response<UpdatePost> response) {
                Log.d("hoangdev", "Ket noi thanh cong");
                Log.e("hoangdev", "response " + response.code());
                UpdatePost a = response.body();
                if (a != null){
                    Log.d("hoangdev", "Success - name:" + a.getName());
                }

            }

            @Override
            public void onFailure(Call<UpdatePost> call, Throwable t) {
//                Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                Log.d("hoangdev", "error - name:" + t.toString());
            }
        });

//        replaceFragment(new FolderFagment(token, mpath));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST ) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                 uri_up_load = getImageUri(getContext(), bitmap);
                Log.d("hoangdev", "uri take photo:  " + uri_up_load.toString());
                upload();
                Log.d("hoangdev", "bitmap take photo:  " + bitmap.toString());
            }

            if (requestCode == SELESECT_FILE_REQUEST && data.getData() != null) {

                uri_up_load = data.getData();
                Log.d("","URI = "+ uri_up_load);
                upload();
            }
        }
    }

    public void pickImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,CAMERA_REQUEST);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "image", null);
        return Uri.parse(path);
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



    private void replaceFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction=getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_home, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void removeFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction=getParentFragmentManager().beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }

}
