package com.hoangt3k56.dropbox.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.account.PhotoSourceArg;
import com.dropbox.core.v2.account.SetProfilePhotoResult;
import com.dropbox.core.v2.users.FullAccount;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.hoangt3k56.dropbox.listener.Listener;
import com.hoangt3k56.dropbox.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;


public class ProfileFragment extends Fragment {

    private static final int CAMERA_PIC_REQUEST = 1111;
    String token;

    Listener listener;
    ImageView imageView;
    TextView tvCancel,tvSave;
    TextView tvName;
    RelativeLayout relativeLayout;

    String base64;

    public String imgUrl;
    private int CAMERA_CODE =12345;

    public ProfileFragment(String token, Listener listener) {
        this.token=token;
        this.listener = listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=LayoutInflater.from(getContext()).inflate(R.layout.fragment_profile,container,false);
        initView(view);

        new GetUser().execute("");
        return view;
    }

    private void initView(View view) {
        imageView       = view.findViewById(R.id.imgAvatar);
        tvCancel        = view.findViewById(R.id.tvCancel);
        tvSave          = view.findViewById(R.id.tvSave);
        tvName          = view.findViewById(R.id.tvName);
        relativeLayout   =    view.findViewById(R.id.loadingView);

        relativeLayout.setVisibility(View.VISIBLE);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.listener();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rqPermissions();
            }
        });

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relativeLayout.setVisibility(View.VISIBLE);
                new saveImg().execute("");
            }
        });
    }


    private class GetUser extends AsyncTask<String, Void, FullAccount>{

        @Override
        protected FullAccount doInBackground(String... strings) {
            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
            DbxClientV2 client = new DbxClientV2(config, token);

            try {
                FullAccount account = client.users().getCurrentAccount();
                return account;
            } catch (DbxException e) {
                e.printStackTrace();
                Log.e("hoangdev", "ket noi tk khong thanh cong trong Frofile");
                return null;
            }


        }

        @Override
        protected void onPostExecute(FullAccount fullAccount) {
            super.onPostExecute(fullAccount);

            relativeLayout.setVisibility(View.GONE);
            tvName.setText(fullAccount.getName().getDisplayName());

            Glide.with(imageView.getContext())
                    .load(fullAccount.getProfilePhotoUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.loading)).error(R.drawable.img_fail)
                    .into(imageView);
        }
    }

    private class saveImg extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
            DbxClientV2 client = new DbxClientV2(config, token);
            try {
                SetProfilePhotoResult account = client.account().setProfilePhoto(PhotoSourceArg.base64Data(base64));
                return true;
            } catch (DbxException e) {
                e.printStackTrace();
                Log.e("hoangdev", "ket noi tk khong thanh cong trong Frofile\n" + e.toString());
            }
            return false;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                Log.d("hoangdev", "save img");
            } else {
                Log.e("hoangdev", "error save img");
            }
            relativeLayout.setVisibility(View.GONE);
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
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, CAMERA_PIC_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_PIC_REQUEST && data.getData() != null && data != null) {
                 Glide.with(this).load(data.getData()).into(imageView);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                    base64 = encodeImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("hoangdev", "uri take photo:  " + data.toString());
            }

        }
    }
    private String encodeImage(Bitmap bm)
    {
        if(bm==null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

}