package com.example.testdropbox;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.account.PhotoSourceArg;
import com.dropbox.core.v2.files.DbxUserListFolderBuilder;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.RelocationResult;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.sharing.ListSharedLinksResult;
import com.dropbox.core.v2.users.FullAccount;
import com.example.testdropbox.callback.BooleanCallback;
import com.example.testdropbox.callback.ListMetadataCallback;
import com.example.testdropbox.callback.StringCallback;
import com.example.testdropbox.callback.UserCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okio.BufferedSink;
import okio.Okio;

public class DropboxAPI {

    DbxClientV2 client;
    CompositeDisposable compositeDisposable;

    public DropboxAPI(CompositeDisposable compositeDisposable, String token) {
        this.compositeDisposable = compositeDisposable;
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
        this.client = new DbxClientV2(config, token);
    }

    public void loadFolder(String path, ListMetadataCallback callback) {
        compositeDisposable.add(observableLoadFolder(path).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<List<Metadata>>() {
            @Override
            public void onNext(@NonNull List<Metadata> metadata) {
                callback.listener(metadata);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                callback.listener(null);
            }

            @Override
            public void onComplete() {

            }
        }));
    }


    private Observable<List<Metadata>> observableLoadFolder(String path) {
        return Observable.create((ObservableOnSubscribe<List<Metadata>>) subscriber -> {
            try {
                DbxUserListFolderBuilder dbxUserListFolderBuilder = client.files().listFolderBuilder(path).withIncludeMediaInfo(true);
                ListFolderResult result = dbxUserListFolderBuilder.start();
                List<Metadata> metadataList = new ArrayList<>();
                while (true) {
                    for (Metadata metadata : result.getEntries()) {
                        metadataList.add(metadata);
                    }

                    if (!result.getHasMore()) {
                        break;
                    }

                    result = client.files().listFolderContinue(result.getCursor());
                }
                subscriber.onNext(metadataList);
                subscriber.onComplete();
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        });
    }

    public void getLink(String path, StringCallback callback) {
        compositeDisposable.add(observableGetLink(path).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<String>() {
            @Override
            public void onNext(@NonNull String link) {
                callback.listener(link);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                callback.listener(null);
            }

            @Override
            public void onComplete() {

            }
        }));
    }


    private Observable<String> observableGetLink(String path) {
        return Observable.create((ObservableOnSubscribe<String>) subscriber -> {
            try {
                ListSharedLinksResult listSharedLinksResult = client.sharing()
                        .listSharedLinksBuilder()
                        .withPath(path).withDirectOnly(true)
                        .start();
                String previewUrl;

                if (listSharedLinksResult.getLinks() != null && listSharedLinksResult.getLinks().size() > 0) {
                    previewUrl = listSharedLinksResult.getLinks().get(0).getUrl();
                } else {

                    previewUrl = client.sharing().createSharedLinkWithSettings(path).getUrl();
                }
                Log.d("AAA", "PreviewUrl:" + previewUrl);
                subscriber.onNext(previewUrl.replace("?dl=0", "?raw=1"));
                subscriber.onComplete();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("AAA", e.toString());
                subscriber.onError(e);
            }
        });
    }

    public void uploadFile(Context context, String currentPath, Uri uri, BooleanCallback callback) {
        compositeDisposable.add(observableUploadFile(context, currentPath, uri).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<Boolean>() {
            @Override
            public void onNext(@NonNull Boolean isSuccess) {
                callback.listener(isSuccess);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d("AAA", e.toString());
                callback.listener(false);
            }

            @Override
            public void onComplete() {

            }
        }));
    }


    private Observable<Boolean> observableUploadFile(Context context, String currentPath, Uri uri) {
        return Observable.create((ObservableOnSubscribe<Boolean>) subscriber -> {
            try (InputStream in = context.getContentResolver().openInputStream(uri)) {
                FileMetadata metadata = client.files().uploadBuilder(currentPath + "/" + uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1))
                        .withMode(WriteMode.OVERWRITE)
                        .uploadAndFinish(in);
                subscriber.onNext(true);
                subscriber.onComplete();
            } catch (Exception e) {
                subscriber.onError(e);
                e.printStackTrace();
            }
        });
    }

    public void downloadFile(Metadata metadata, BooleanCallback callback) {
        compositeDisposable.add(observableDownloadFile(metadata).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<Boolean>() {
            @Override
            public void onNext(@NonNull Boolean isSuccess) {
                callback.listener(isSuccess);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                callback.listener(false);
            }

            @Override
            public void onComplete() {

            }
        }));
    }

    private Observable<Boolean> observableDownloadFile(Metadata metadata) {
        return Observable.create((ObservableOnSubscribe<Boolean>) subscriber -> {
            try {
                DbxDownloader<FileMetadata> dbxDownloader =
                        client.files().download(metadata.getPathDisplay());
                String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + metadata.getName();
                OutputStream outputStream = new FileOutputStream(downloadPath);
                dbxDownloader.download(outputStream);
                subscriber.onNext(true);
                subscriber.onComplete();
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        });
    }

    public void moveFile(String fromPath, String toPath, BooleanCallback callback) {
        compositeDisposable.add(observableMoveFile(fromPath, toPath).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<Boolean>() {
            @Override
            public void onNext(@NonNull Boolean isSuccess) {
                callback.listener(isSuccess);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                callback.listener(false);
            }

            @Override
            public void onComplete() {

            }
        }));
    }

    private Observable<Boolean> observableMoveFile(String fromPath, String toPath) {
        return Observable.create((ObservableOnSubscribe<Boolean>) subscriber -> {
            try {
                client.files().moveV2(fromPath, toPath);
                subscriber.onNext(true);
                subscriber.onComplete();
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        });
    }

    public void createFolder(String path, BooleanCallback callback) {
        compositeDisposable.add(observableCreateFolder(path).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<Boolean>() {
            @Override
            public void onNext(@NonNull Boolean isSuccess) {
                callback.listener(isSuccess);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                callback.listener(false);
            }

            @Override
            public void onComplete() {

            }
        }));
    }

    private Observable<Boolean> observableCreateFolder(String path) {
        return Observable.create((ObservableOnSubscribe<Boolean>) subscriber -> {
            try {
                client.files().createFolderV2(path);
                subscriber.onNext(true);
                subscriber.onComplete();
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        });
    }

    public void delete(String path, BooleanCallback callback) {
        compositeDisposable.add(observableDelete(path).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<Boolean>() {
            @Override
            public void onNext(@NonNull Boolean isSuccess) {
                callback.listener(isSuccess);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                callback.listener(false);
            }

            @Override
            public void onComplete() {

            }
        }));
    }

    private Observable<Boolean> observableDelete(String path) {
        return Observable.create((ObservableOnSubscribe<Boolean>) subscriber -> {
            try {
                client.files().deleteV2(path);
                subscriber.onNext(true);
                subscriber.onComplete();
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        });
    }

    public void getUser(UserCallback callback) {
        compositeDisposable.add(observableUser().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<FullAccount>() {
            @Override
            public void onNext(@NonNull FullAccount fullAccount) {
                callback.listener(fullAccount);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                callback.listener(null);
            }

            @Override
            public void onComplete() {

            }
        }));
    }

    private Observable<FullAccount> observableUser() {
        return Observable.create((ObservableOnSubscribe<FullAccount>) subscriber -> {
            try {
                FullAccount fullAccount = client.users().getCurrentAccount();
                subscriber.onNext(fullAccount);
                subscriber.onComplete();
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        });
    }

    public void updateImage(String base64, BooleanCallback callback) {
        compositeDisposable.add(observableUpdateImage(base64).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<Boolean>() {
            @Override
            public void onNext(@NonNull Boolean isSuccess) {
                callback.listener(isSuccess);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                callback.listener(false);
            }

            @Override
            public void onComplete() {

            }
        }));
    }

    private Observable<Boolean> observableUpdateImage(String base64) {
        return Observable.create((ObservableOnSubscribe<Boolean>) subscriber -> {
            try {
                client.account().setProfilePhoto(PhotoSourceArg.base64Data(base64));
                subscriber.onNext(true);
                subscriber.onComplete();
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        });
    }

    public void copyFile(String fromPath, String toPath, BooleanCallback callback) {
        compositeDisposable.add(observableCopyFile(fromPath, toPath).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<Boolean>() {
            @Override
            public void onNext(@NonNull Boolean isSuccess) {
                callback.listener(isSuccess);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                callback.listener(false);
            }

            @Override
            public void onComplete() {

            }
        }));
    }

    private Observable<Boolean> observableCopyFile(String fromPath, String toPath) {
        return Observable.create((ObservableOnSubscribe<Boolean>) subscriber -> {
            try {
                client.files().copyV2(fromPath, toPath);
                subscriber.onNext(true);
                subscriber.onComplete();
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        });
    }
}
