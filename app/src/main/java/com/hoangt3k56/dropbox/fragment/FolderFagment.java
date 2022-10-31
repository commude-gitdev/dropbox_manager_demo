package com.hoangt3k56.dropbox.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hoangt3k56.dropbox.api.ApiService;
import com.hoangt3k56.dropbox.listener.ListenerMetadata;
import com.hoangt3k56.dropbox.R;
import com.hoangt3k56.dropbox.adapter.FileAdapter;
import com.hoangt3k56.dropbox.model.Copy;
import com.hoangt3k56.dropbox.model.Delete;
import com.hoangt3k56.dropbox.model.Entrie;
import com.hoangt3k56.dropbox.model.Enty;
import com.hoangt3k56.dropbox.model.Path;
import com.hoangt3k56.dropbox.model.PathShare;
import com.hoangt3k56.dropbox.model.PathShareRoot;

import java.io.InputStream;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FolderFagment extends Fragment {

    RecyclerView recyclerView;
    FileAdapter fileAdapter;
    HomeFragment homeFragment;
    RelativeLayout relativeLayout;

    String token, mpath;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        HomeFragment.mpath = mpath;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        relativeLayout = view.findViewById(R.id.loadingViewListFolder);
        relativeLayout.setVisibility(View.VISIBLE);
        recyclerView=view.findViewById(R.id.rcView_home_fragment);
        recyclerView.setHasFixedSize(true);
        homeFragment = new HomeFragment();
        LinearLayoutManager linearLayoutManager=new GridLayoutManager(getContext(),3,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);


        fileAdapter = new FileAdapter(new ListenerMetadata() {
            @Override
            public void listener(Entrie entrie, int i) {
                if (i == 0) {
                    if (entrie.getTag().equals("folder")) {
                        Log.d("hoangdev",  "path:   " + entrie.getPathLower());
                        replaceFragment(new FolderFagment(token, entrie.getPathLower()));
                        HomeFragment.mpath = entrie.getPathLower();
                    } else if (entrie.getTag().equals("file")) {
                        String file_name = entrie.getName();
                        type_file = typeFile(file_name);
                        ApiService.apiService.getLink(token, new PathShare(entrie.getPathLower())).enqueue(new Callback<PathShareRoot>() {
                            @Override
                            public void onResponse(Call<PathShareRoot> call, Response<PathShareRoot> response) {
                                PathShareRoot pathShareRoot = response.body();
                                if (pathShareRoot != null && !type_file.equals("error")) {
                                    String url_View = pathShareRoot.getUrl();
                                    viewFile(url_View, type_file);
                                } else {
                                    Toast.makeText(getContext(), "File khong dc ho tro!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<PathShareRoot> call, Throwable t) {

                            }
                        });

                    }

                } else if (i == 1) {
                    showMenu(entrie);
                    Log.d("hoangdev",  "path:   " + entrie.getPathLower());
                }
            }
        });
        recyclerView.setAdapter(fileAdapter);
        loadData();

    }

    private void viewFile(String url_view, String type_file) {
        Uri uri = Uri.parse(url_view.replace("?dl=0", "?raw=1"));
        Log.d("hoangdev", url_view.replace("?dl=0", "?raw=1"));
        
        if (type_file.equals("img")) {
            showDialogImg(uri);
        } else if (type_file.equals("mp4")) {
            showDialogVideo(uri);
        } else if (type_file.equals("pdf")) {
            showDialogPdf(uri);
        }
    }


    private void showDialogImg(Uri uri) {
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
                .load(uri)
                .apply(new RequestOptions().placeholder(R.drawable.loading).error(R.drawable.img_fail))
                .into(imageView);
        dialog.show();
    }

    private void showDialogVideo(Uri uri) {
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
        videoView.setVideoURI(uri);

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


    private void showDialogPdf(Uri uri) {
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


        dialog.show();
    }



    public void loadData() {
        relativeLayout.setVisibility(View.VISIBLE);
        ApiService.apiService.getListEmty(token, new Path(mpath)).enqueue(new Callback<Enty>() {
            @Override
            public void onResponse(Call<Enty> call, Response<Enty> response) {
                Enty enty = (Enty) response.body();
                if (enty != null) {
                    Log.d("hoangdev", enty.toString());
                    List<Entrie> list = enty.getEntries();
                    if (list != null) {
                        fileAdapter.setEntrieList(list);
                    } else {
                        Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Enty> call, Throwable t) {
                Log.e("hoangdev", "error call list folder");
            }
        });
        relativeLayout.setVisibility(View.GONE);

    }

    private String typeFile(String file_name) {
        String type_file = "error";
        if (file_name.contains(".jpg") || file_name.contains(".jpge") || !file_name.contains(".")) {
            type_file = "img";
        } else if (file_name.contains(".mp4")) {
            type_file = "mp4";
        } else if (file_name.contains(".pdf")) {
            type_file = "pdf";
        }
        return type_file;
    }


    private void showMenu(Entrie entrie) {
        PopupMenu popupMenu = new PopupMenu(getContext(), getView().findViewById(R.id.item_file));
        popupMenu.getMenuInflater().inflate(R.menu.onclick_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.properties:
                        properties(entrie);
                        break;
                    case R.id.delete:
                        delete(entrie);
                        break;
                    case R.id.move:
                        FolderFagment.paste = 1;
                        FolderFagment.copy_move_path = entrie.getPathLower();
                        Log.d("hoangdev", copy_move_path);
                        break;
                    case R.id.copy:
                        FolderFagment.paste = 2;
                        FolderFagment.copy_move_path = entrie.getPathLower();
                        Log.d("hoangdev", copy_move_path);
                        break;
                    case R.id.paste:
                        paste(entrie);
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void paste(Entrie entrie) {
        if (paste == 0) {
            Log.d("hoangdev", "" + paste);
        } else if (paste == 1){
            // move Folder/file
            String []a = copy_move_path.split("/");
            String to_path = entrie.getPathLower() + "/" + a[a.length-1];
            ApiService.apiService.move(token, new Copy(copy_move_path, to_path)).enqueue(new Callback<Enty>() {
                @Override
                public void onResponse(Call<Enty> call, Response<Enty> response) {
                    Toast.makeText(getContext(), "Move success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Enty> call, Throwable t) {
                    Toast.makeText(getContext(), "Error Move", Toast.LENGTH_SHORT).show();
                }
            });
            FolderFagment.paste = 0;
//            replaceFragment(new FolderFagment(token, entrie.getPathLower()));
        }  else if (paste == 2){
            // copy Folder/file
            String []a = copy_move_path.split("/");
            String to_path = entrie.getPathLower() + "/" + a[a.length-1];
            ApiService.apiService.copy(token, new Copy(copy_move_path, to_path)).enqueue(new Callback<Enty>() {
                @Override
                public void onResponse(Call<Enty> call, Response<Enty> response) {
                    Toast.makeText(getContext(), "Copy success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Enty> call, Throwable t) {
                    Toast.makeText(getContext(), "Error copy", Toast.LENGTH_SHORT).show();
                }
            });
            FolderFagment.paste = 0;
//            replaceFragment(new FolderFagment(token, entrie.getPathLower()));
        }
    }



    private void delete(Entrie entrie)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Delete");
        dialog.setMessage("Bạn chắc chắn muốn xóa "+ entrie.getName() + " ?");
        dialog.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ApiService.apiService.delete(token, new Delete(entrie.getPathDisplay())).enqueue(new Callback<Enty>() {
                    @Override
                    public void onResponse(Call<Enty> call, Response<Enty> response) {
                        replaceFragment(new FolderFagment(token, HomeFragment.mpath));
                        Toast.makeText(getContext(), "Delete success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Enty> call, Throwable t) {
                        Toast.makeText(getContext(), "Delete error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        dialog.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog.show();

    }

    private void properties(Entrie entrie) {
        String message = "";
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Properties");

        if (entrie.getTag().equals("file")) {
            message = "Tên:  " + entrie.getName() +"\n"
                    + "Path:  " + entrie.getPathLower() +"\n"
                    + "Size:  " + entrie.getSize() +" B\n"
                    + "Ngày tạo:  " + entrie.getServerModified() +"\n"
                    + "Ngày edit:  " + entrie.getClientModified();
        } else {

            message = "Tên:  " + entrie.getName() +"\n"
                    + "Path:  " + entrie.getPathLower();
        }

        dialog.setMessage(message);
        dialog.show();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction=getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_home, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}