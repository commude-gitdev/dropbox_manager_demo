package com.example.testdropbox.fragment;

import android.Manifest;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.dropbox.core.v2.users.FullAccount;
import com.example.testdropbox.DropboxAPI;
import com.example.testdropbox.R;
import com.example.testdropbox.callback.BooleanCallback;
import com.example.testdropbox.callback.Listener;
import com.example.testdropbox.callback.UserCallback;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.ByteArrayOutputStream;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class ProfileFragment extends Fragment {
    String token;
    Listener listener;

    ImageView imageView;
    TextView tvName, tvCancel, tvSave;
    RelativeLayout relativeLayout;

    String base64;

    CompositeDisposable compositeDisposable;
    DropboxAPI dropboxAPI;

    public ProfileFragment(String token, Listener listener) {
        this.token = token;
        this.listener = listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_profile, container, false);
        initView(view);
        compositeDisposable = new CompositeDisposable();
        dropboxAPI = new DropboxAPI(compositeDisposable, token);
        getUser();
        return view;
    }

    private void getUser() {
        relativeLayout.setVisibility(View.VISIBLE);
        dropboxAPI.getUser(new UserCallback() {
            @Override
            public void listener(FullAccount fullAccount) {
                relativeLayout.setVisibility(View.GONE);
                if (fullAccount != null) {
                    tvName.setText(fullAccount.getName().getDisplayName());
                    Glide.with(imageView.getContext())
                            .load(fullAccount.getProfilePhotoUrl())
                            .apply(new RequestOptions().placeholder(R.drawable.loading).error(R.drawable.img_fail))
                            .into(imageView);
                } else {
                    Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initView(View view) {
        imageView = view.findViewById(R.id.imgAvatar);
        tvCancel = view.findViewById(R.id.tvCancel);
        tvSave = view.findViewById(R.id.tvSave);
        tvName = view.findViewById(R.id.tvName);
        relativeLayout = view.findViewById(R.id.loadingView);

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
                if (base64 != null) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    dropboxAPI.updateImage(base64, new BooleanCallback() {
                        @Override
                        public void listener(Boolean b) {
                            if (b) {
                                base64 = null;
                                getUser();
                                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                            } else {
                                getUser();
                                Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
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
                .setDeniedMessage("If you deny permission, you cannot use this service\n\nPlease enable permissions at [Setting]> [Permission]")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void pickImage() {
        TedBottomPicker.with(getActivity())
                .show(new TedBottomSheetDialogFragment.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(Uri uri) {
                        if (uri != null) {
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                                imageView.setImageBitmap(bitmap);
                                base64 = encodeImage(bitmap);
                            } catch (Exception e) {
                                base64 = null;
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private String encodeImage(Bitmap bm) {
        if (bm == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }
}
