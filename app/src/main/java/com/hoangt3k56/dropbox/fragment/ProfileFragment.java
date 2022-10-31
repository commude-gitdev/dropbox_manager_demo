package com.hoangt3k56.dropbox.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.hoangt3k56.dropbox.api.ApiService;
import com.hoangt3k56.dropbox.listener.Listener;
import com.hoangt3k56.dropbox.R;
import com.hoangt3k56.dropbox.model.Account;
import com.hoangt3k56.dropbox.model.Avatar;
import com.hoangt3k56.dropbox.model.Enty;
import com.hoangt3k56.dropbox.model.Photo;
import com.hoangt3k56.dropbox.model.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {

    ImageView imageView;
    TextView tvCancel, tvSave, tvName;
    RelativeLayout relativeLayout;

    Listener listener;
    String token, base64, accountId;
    Bitmap bitmap;

    private int CAMERA_CODE =12345;
    private static final int CAMERA_PIC_REQUEST = 1111;

    CompositeDisposable compositeDisposable;


    public ProfileFragment(String token, Listener listener) {
        this.token=token;
        this.listener = listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=LayoutInflater.from(getContext()).inflate(R.layout.fragment_profile,container,false);
        initView(view);
        compositeDisposable = new CompositeDisposable();
        getAccountId();
        return view;
    }

    private void initView(View view) {
        imageView       = view.findViewById(R.id.imgAvatar);
        tvCancel        = view.findViewById(R.id.tvCancel);
        tvSave          = view.findViewById(R.id.tvSave);
        tvName          = view.findViewById(R.id.tvName);
        relativeLayout  =    view.findViewById(R.id.loadingView);
        base64          = "error";

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAvatar();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rqPermissions();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.listener();
            }
        });
    }

    private void getAccountId() {
        ApiService.apiService.getAccountId(token).enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                Account a = response.body();
                if (a != null) {
                    accountId = a.getAccount_id();
                    getFullAccount();
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                Log.e("hoangdev", "error" + accountId);
            }
        });
    }

    private void getFullAccount() {
        relativeLayout.setVisibility(View.VISIBLE);
        Log.e("hoangdev", "accountId " + accountId);
        ApiService.apiService.getUser(token, new Account(accountId)).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = (User) response.body();
                if (user != null) {
//                    Log.e("hoangdev", "name" + user.getName().getSurname());
                    tvName.setText(user.getName().getDisplayName());
                    Glide.with(imageView.getContext())
                            .load(user.getProfilePhotoUrl())
                            .apply(new RequestOptions().placeholder(R.drawable.loading)).error(R.drawable.img_fail)
                            .into(imageView);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("hoangdev", "error load avatar");
            }
        });

        relativeLayout.setVisibility(View.GONE);

    }

    private void saveAvatar() {
        if (!base64.equals("error")) {
            ApiService.apiService.setAvatar(token, new Avatar(new Photo(base64))).enqueue(new Callback<Enty>() {
                @Override
                public void onResponse(Call<Enty> call, Response<Enty> response) {
                    Toast.makeText(getContext(), "save  Avatar Success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Enty> call, Throwable t) {
                    Toast.makeText(getContext(), "save Avatar Error", Toast.LENGTH_SHORT).show();
                }
            });
            base64 = "error";
        }
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
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, CAMERA_PIC_REQUEST);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, CAMERA_PIC_REQUEST);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_PIC_REQUEST ) {
                if (data.getData() != null) {
                    Uri uri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                        Glide.with(this).load(bitmap).into(imageView);
                        base64 = encodeImage(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                Log.d("hoangdev", "uri take photo:  " + bitmap);
            }
        }
    }

    private String encodeImage(Bitmap bm) {
        if(bm==null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

}