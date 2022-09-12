package com.example.testdropbox.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.Metadata;
import com.example.testdropbox.DialogSelectFolder;
import com.example.testdropbox.DropboxAPI;
import com.example.testdropbox.R;
import com.example.testdropbox.adapter.BreadCrumbAdapter;
import com.example.testdropbox.adapter.FileAdapter;
import com.example.testdropbox.callback.BooleanCallback;
import com.example.testdropbox.callback.InputStreamCallback;
import com.example.testdropbox.callback.ListMetadataCallback;
import com.example.testdropbox.callback.ListenerMetadata;
import com.example.testdropbox.callback.ListenerView;
import com.example.testdropbox.callback.StringCallback;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class HomeFragment extends Fragment {

    FileAdapter fileAdapter;
    BreadCrumbAdapter breadCrumbAdapter;
    RecyclerView recyclerView;
    RecyclerView breadCrumbList;
    RelativeLayout loading;
    ImageView imgBack;
    String token;
    String curPath = "";
    Metadata currentMetadata;
    Menu currentMenu;
    DropboxAPI dropboxAPI;
    CompositeDisposable compositeDisposable;
    MediaPlayer mediaPlayer;
    LinearLayout layoutNavigation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_home, container, false);
        token = getArguments().getString("TOKEN");
        compositeDisposable = new CompositeDisposable();
        dropboxAPI = new DropboxAPI(compositeDisposable, token);
        initView(view);
        initRecyclerView();
        initBreadCrumb();
        return view;
    }

    public List<String> getFolderList() {
        String[] arrFolder = curPath.split("/");
        List<String> stringList = Arrays.asList(arrFolder);
        return stringList;
    }

    private void initBreadCrumb() {
        breadCrumbList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        breadCrumbList.setLayoutManager(linearLayoutManager);
        breadCrumbAdapter = new BreadCrumbAdapter(getFolderList(), new StringCallback() {
            @Override
            public void listener(String string) {
                loading.setVisibility(View.VISIBLE);
                if (string.equals("")) {
                    layoutNavigation.setVisibility(View.GONE);
                } else {
                    layoutNavigation.setVisibility(View.VISIBLE);
                }
                curPath = string;
                refresh();
            }
        });
        breadCrumbList.setAdapter(breadCrumbAdapter);
    }


    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        fileAdapter = new FileAdapter(new ListenerMetadata() {
            @Override
            public void listener(Metadata metadata) {
                if (currentMetadata != null && currentMetadata.equals(metadata)) {
                    if (metadata instanceof FolderMetadata) {
                        layoutNavigation.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.VISIBLE);
                        curPath = curPath + "/" + metadata.getName();
                        refresh();
                    } else if (metadata instanceof FileMetadata) {
                        loading.setVisibility(View.VISIBLE);
                        dropboxAPI.getLink(currentMetadata.getPathDisplay(), new StringCallback() {
                            @Override
                            public void listener(String string) {
                                loading.setVisibility(View.GONE);
                                if (string != null && (string.contains(".png?raw=1") ||
                                        string.contains(".jpeg?raw=1") ||
                                        string.contains(".jpg?raw=1") ||
                                        string.contains(".gif?raw=1") ||
                                        string.contains(".bitmap?raw=1"))) {
                                    previewImage(string);
                                } else if (string != null && string.contains(".mp4?raw=1")) {
                                    previewVideo(string);
                                } else if (string != null && string.contains(".pdf?raw=1")) {
                                    previewPdf(string);
                                } else if (string != null && string.contains(".mp3?raw=1")) {
                                    previewMedia(string);
                                } else {
                                    Toast.makeText(getContext(), "Can't Preview this file", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }

                } else {
                    changeMenu(true);
                    fileAdapter.setMetadata(metadata);
                    currentMetadata = metadata;
                }
            }
        }, new ListenerView() {
            @Override
            public void listener(View view, Metadata metadata) {
                popup_window(view, metadata);
            }
        });
        recyclerView.setAdapter(fileAdapter);
    }


    private void previewVideo(String string) {
        Dialog dialog = new Dialog(getContext(), R.style.full_screen_dialog);
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_preview_video, null, false);
        dialog.setContentView(view);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ImageButton btnClose = view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        VideoView videoView = view.findViewById(R.id.videoView);
        videoView.setVideoPath(string);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                MediaController mediaController = new MediaController(getContext()) {
                    @Override
                    public void show() {
                        super.show(0);
                    }
                };
                mediaController.setAnchorView(view);
                mediaController.requestFocus();
                videoView.setMediaController(mediaController);
                progressBar.setVisibility(View.GONE);

                ((ViewGroup) mediaController.getParent()).removeView(mediaController);
                ((FrameLayout) view.findViewById(R.id.mediacontroler))
                        .addView(mediaController);
                mediaController.setVisibility(View.VISIBLE);
                videoView.start();
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Toast.makeText(getContext(), "Can't play video", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return true;
            }
        });
        dialog.show();
    }

    private void previewPdf(String string) {
        Dialog dialog = new Dialog(getContext(), R.style.full_screen_dialog);
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_preview_pdf, null, false);
        dialog.setContentView(view);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        ImageButton btnClose = view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        PDFView pdfView = view.findViewById(R.id.pdfView);
        new RetrievePdfStream(new InputStreamCallback() {
            @Override
            public void listener(InputStream inputStream) {
                pdfView.fromStream(inputStream)
                        .onLoad(new OnLoadCompleteListener() {
                            @Override
                            public void loadComplete(int nbPages) {
                                progressBar.setVisibility(View.GONE);
                            }
                        })
                        .onError(new OnErrorListener() {
                            @Override
                            public void onError(Throwable t) {
                                Log.d("AAA", t.toString());
                                Toast.makeText(getContext(), "Load Fail", Toast.LENGTH_SHORT).show();
                            }
                        }).load();
            }
        }).execute(string);
        dialog.show();
    }


    private static class RetrievePdfStream extends AsyncTask<String, Void, InputStream> {

        InputStreamCallback callback;

        public RetrievePdfStream(InputStreamCallback callback) {
            this.callback = callback;
        }

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e) {
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            callback.listener(inputStream);
        }
    }

    private void previewMedia(String string) {
        MediaController mediaController = new MediaController(getContext()) {
            @Override
            public void show() {
                super.show(0);
            }
        };
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes
                        .Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build());

        Dialog dialog = new Dialog(getContext(), R.style.full_screen_dialog);
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_preview_media, null, false);
        dialog.setContentView(view);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ImageButton btnClose = view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaController.hide();
                mediaPlayer.stop();
                mediaPlayer.release();
                dialog.dismiss();
            }
        });
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);


        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(string);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {

                    mediaController.requestFocus();
                    mediaController.setAnchorView(view);
                    MediaController.MediaPlayerControl mediaPlayerControl = new MediaController.MediaPlayerControl() {
                        @Override
                        public void start() {
                            if (mediaPlayer != null && !mediaPlayer.isPlaying())
                                mediaPlayer.start();
                        }

                        @Override
                        public void pause() {
                            if (mediaPlayer != null && mediaPlayer.isPlaying())
                                mediaPlayer.pause();
                        }

                        @Override
                        public int getDuration() {
                            return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
                        }

                        @Override
                        public int getCurrentPosition() {
                            return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
                        }

                        @Override
                        public void seekTo(int i) {
                            if (mediaPlayer != null) mediaPlayer.seekTo(i);
                        }

                        @Override
                        public boolean isPlaying() {
                            return mediaPlayer != null ? mediaPlayer.isPlaying() : false;
                        }

                        @Override
                        public int getBufferPercentage() {
                            return 0;
                        }

                        @Override
                        public boolean canPause() {
                            return true;
                        }

                        @Override
                        public boolean canSeekBackward() {
                            return true;
                        }

                        @Override
                        public boolean canSeekForward() {
                            return true;
                        }

                        @Override
                        public int getAudioSessionId() {
                            return 0;
                        }
                    };

                    mediaController.setMediaPlayer(mediaPlayerControl);

                    progressBar.setVisibility(View.GONE);
                    ((ViewGroup) mediaController.getParent()).removeView(mediaController);
                    ((FrameLayout) view.findViewById(R.id.mediacontroler))
                            .addView(mediaController);
                    mediaController.setVisibility(View.VISIBLE);
                }
            });

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    Toast.makeText(getContext(), "Can't play song", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return true;
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Can't play song", Toast.LENGTH_SHORT).show();
        }

        dialog.show();


    }


    private void previewImage(String string) {
        Dialog dialog = new Dialog(getContext(), R.style.full_screen_dialog);
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_preview_image, null, false);
        dialog.setContentView(view);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ImageButton btnClose = view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ImageView imageView = view.findViewById(R.id.imgPreview);
        Glide.with(imageView.getContext())
                .load(string)
                .apply(new RequestOptions().placeholder(R.drawable.loading).error(R.drawable.img_fail))
                .into(imageView);
        dialog.show();
    }

    private void initView(View view) {
        layoutNavigation = view.findViewById(R.id.layoutNavigation);
        layoutNavigation.setVisibility(View.GONE);
        breadCrumbList = view.findViewById(R.id.breadcrumbList);
        recyclerView = view.findViewById(R.id.recyclerView);
        loading = view.findViewById(R.id.loadingView);
        imgBack = view.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                curPath = curPath.substring(0, curPath.lastIndexOf("/"));
                if (curPath.trim().equals("")) layoutNavigation.setVisibility(View.GONE);
                refresh();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    public void changeMenu(boolean isSelected) {
        Log.d("AAA", String.valueOf(currentMenu));
        if (currentMenu == null) return;
        MenuItem copy = currentMenu.findItem(R.id.copy);
        MenuItem delete = currentMenu.findItem(R.id.delete);
        MenuItem move = currentMenu.findItem(R.id.move);
        MenuItem download = currentMenu.findItem(R.id.download);
        MenuItem properties = currentMenu.findItem(R.id.properties);
        if (copy == null || delete == null || move == null || download == null) return;
        if (isSelected) {
            copy.setVisible(true);
            properties.setVisible(true);
            delete.setVisible(true);
            move.setVisible(true);
            download.setVisible(true);
        } else {
            properties.setVisible(false);
            copy.setVisible(false);
            delete.setVisible(false);
            move.setVisible(false);
            download.setVisible(false);
        }
    }

    public void refresh() {
        breadCrumbAdapter.setStringList(getFolderList());
        currentMetadata = null;
        changeMenu(false);
        loading.setVisibility(View.VISIBLE);
        dropboxAPI.loadFolder(curPath, new ListMetadataCallback() {
            @Override
            public void listener(List<Metadata> metadata) {
                fileAdapter.setMetadataList(metadata);
                loading.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                refresh();
                break;

            case R.id.upload:
                pickFile();
                break;

            case R.id.takePhoto:
                rqPermissions();
                break;

            case R.id.newFolder:
                newFolder();
                break;

            case R.id.properties:
                properties(currentMetadata);
                break;

            case R.id.delete:
                delete(currentMetadata);
                break;

            case R.id.move:
                move(currentMetadata);
                break;

            case R.id.copy:
                copy(currentMetadata);
                break;

            case R.id.download:
                download(currentMetadata);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.right_menu, menu);
        if (menu instanceof MenuBuilder) {

            MenuBuilder menuBuilder = (MenuBuilder) menu;
            menuBuilder.setOptionalIconsVisible(true);
        }
        currentMenu = menu;
    }

    private void pickFile() {
        Intent data = new Intent(Intent.ACTION_GET_CONTENT);
        data.addCategory(Intent.CATEGORY_OPENABLE);
        data.setType("*/*");
        Intent intent = Intent.createChooser(data, "Choose a file");
        startActivityForResult.launch(intent);
    }

    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            Uri uri = data.getData();
            if (uri != null) {
                loading.setVisibility(View.VISIBLE);
                dropboxAPI.uploadFile(getContext(), curPath, uri, new BooleanCallback() {
                    @Override
                    public void listener(Boolean b) {
                        if (b) {
                            Toast.makeText(getContext(), "Upload Success", Toast.LENGTH_SHORT).show();
                            refresh();
                        } else {
                            loading.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Upload Fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    });


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
                            loading.setVisibility(View.VISIBLE);
                            dropboxAPI.uploadFile(getContext(), curPath, uri, new BooleanCallback() {
                                @Override
                                public void listener(Boolean b) {
                                    if (b) {
                                        Toast.makeText(getContext(), "Upload Success", Toast.LENGTH_SHORT).show();
                                        refresh();
                                    } else {
                                        loading.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "Upload Fail", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
    }

    public void delete(Metadata metadata) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loading.setVisibility(View.VISIBLE);
                dropboxAPI.delete(metadata.getPathDisplay(), new BooleanCallback() {
                    @Override
                    public void listener(Boolean b) {
                        if (b) {
                            refresh();
                            Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                        } else {
                            loading.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        alertDialog.setTitle("Delete");
        alertDialog.setMessage("Do you want delete " + metadata.getName() + " ?");
        alertDialog.show();
    }

    public void newFolder() {
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
                    loading.setVisibility(View.VISIBLE);
                    dropboxAPI.createFolder(curPath + "/" + editText.getText().toString().trim(), new BooleanCallback() {
                        @Override
                        public void listener(Boolean b) {
                            if (b) {
                                refresh();
                                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                            } else {
                                loading.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void properties(Metadata metadata) {
        if (metadata != null) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.dialog_properties, null);
            Dialog dialog = new Dialog(getContext(), R.style.full_screen_dialog);
            dialog.setContentView(view);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
            if (metadata instanceof FolderMetadata) {
                TableRow rowSize = view.findViewById(R.id.tableRowSize);
                rowSize.setVisibility(View.GONE);

                TableRow rowCM = view.findViewById(R.id.tableRowCM);
                rowCM.setVisibility(View.GONE);

                TableRow rowSM = view.findViewById(R.id.tableRowSM);
                rowSM.setVisibility(View.GONE);

                TableRow rowIsDownload = view.findViewById(R.id.tableRowIsDownload);
                rowIsDownload.setVisibility(View.GONE);

                TextView tvName = view.findViewById(R.id.tvName);
                tvName.setText(metadata.getName());

                TextView tvType = view.findViewById(R.id.tvType);
                tvType.setText("Folder");

                TextView tvPath = view.findViewById(R.id.tvPathDisplay);
                tvPath.setText(metadata.getPathDisplay());

            } else if (metadata instanceof FileMetadata) {
                FileMetadata fileMetadata = (FileMetadata) metadata;

                TextView tvName = view.findViewById(R.id.tvName);
                tvName.setText(fileMetadata.getName());

                TextView tvType = view.findViewById(R.id.tvType);
                tvType.setText("File");

                TextView tvPath = view.findViewById(R.id.tvPathDisplay);
                tvPath.setText(fileMetadata.getPathDisplay());

                long size = fileMetadata.getSize();
                String size_text;
                if (size < 1024) {
                    size_text = size + "Byte";
                } else if (size > 1024 && size < 1048576) {
                    size_text = (size / 1024) + "KB";
                } else if (size > 1048576 && size < 1073741824) {
                    size_text = (size / 1048576) + "MB";
                } else {
                    size_text = (size / 1073741824) + "GB";
                }
                TextView tvSize = view.findViewById(R.id.tvSize);
                tvSize.setText(size_text);

                TextView isDownload = view.findViewById(R.id.tvIsDownload);
                isDownload.setText(String.valueOf(fileMetadata.getIsDownloadable()));
                SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                Date date_sm = fileMetadata.getServerModified();
                TextView tv_sm = view.findViewById(R.id.tvServerModified);
                tv_sm.setText(parser.format(date_sm));

                Date date_cm = fileMetadata.getClientModified();
                TextView tv_cm = view.findViewById(R.id.tvClientModified);
                tv_cm.setText(parser.format(date_cm));
            }

            ImageButton imageButton = view.findViewById(R.id.btnClose);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } else {
            Toast.makeText(getContext(), "Please select folder or file", Toast.LENGTH_SHORT).show();
        }

    }

    public void popup_window(View view, Metadata metadata) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.getMenuInflater().inflate(R.menu.context_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.properties:
                        properties(metadata);
                        break;

                    case R.id.delete:
                        delete(metadata);
                        break;

                    case R.id.move:
                        move(metadata);
                        break;

                    case R.id.copy:
                        copy(metadata);
                        break;

                    case R.id.download:
                        download(metadata);
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    private void download(Metadata metadata) {
        if (metadata instanceof FileMetadata) {
            loading.setVisibility(View.VISIBLE);
            dropboxAPI.downloadFile(metadata, new BooleanCallback() {
                @Override
                public void listener(Boolean b) {
                    loading.setVisibility(View.GONE);
                    if (b) {
                        Toast.makeText(getContext(), "Download Success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Download Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(getContext(), "Please select  file", Toast.LENGTH_SHORT).show();
        }
    }

    private void copy(Metadata metadata) {
        new DialogSelectFolder(getContext(), token, new StringCallback() {
            @Override
            public void listener(String string) {
                loading.setVisibility(View.VISIBLE);
                dropboxAPI.copyFile(metadata.getPathDisplay(), string + "/" + metadata.getName(), new BooleanCallback() {
                    @Override
                    public void listener(Boolean b) {
                        if (b) {
                            refresh();
                            Toast.makeText(getContext(), "Copy Success", Toast.LENGTH_SHORT).show();
                        } else {
                            loading.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Copy Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).show();
    }

    private void move(Metadata metadata) {
        new DialogSelectFolder(getContext(), token, new StringCallback() {
            @Override
            public void listener(String string) {
                loading.setVisibility(View.VISIBLE);
                dropboxAPI.moveFile(metadata.getPathDisplay(), string + "/" + metadata.getName(), new BooleanCallback() {
                    @Override
                    public void listener(Boolean b) {
                        if (b) {
                            refresh();
                            Toast.makeText(getContext(), "Move Success", Toast.LENGTH_SHORT).show();
                        } else {
                            loading.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Move Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).show();
    }


}
