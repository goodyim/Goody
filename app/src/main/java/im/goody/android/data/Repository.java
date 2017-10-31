package im.goody.android.data;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import im.goody.android.App;
import im.goody.android.Constants;
import im.goody.android.data.dto.Deal;
import im.goody.android.data.dto.User;
import im.goody.android.data.local.PreferencesManager;
import im.goody.android.data.network.RestService;
import im.goody.android.data.network.core.RestCallTransformer;
import im.goody.android.data.network.req.LoginReq;
import im.goody.android.data.network.req.NewCommentReq;
import im.goody.android.data.network.req.NewEventReq;
import im.goody.android.data.network.req.NewPostReq;
import im.goody.android.data.network.req.RegisterReq;
import im.goody.android.data.network.res.CommentRes;
import im.goody.android.data.network.res.EventStateRes;
import im.goody.android.data.network.res.FollowRes;
import im.goody.android.data.network.res.ParticipateRes;
import im.goody.android.data.network.res.UserRes;
import im.goody.android.di.components.DataComponent;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.HttpException;
import retrofit2.Retrofit;

public class Repository implements IRepository {
    @Inject
    PreferencesManager preferencesManager;
    @Inject
    RestService restService;
    @Inject
    Retrofit retrofit;

    public Repository() {
        DataComponent component = App.getDataComponent();
        if (component != null)
            component.inject(this);
    }

    //region ================= User =================

    @Override
    public Observable<UserRes> register(RegisterReq data, Uri avatarUri) {
        return restService.registerUser(
                RestCallTransformer.objectToPartMap(data, "user"),
                getPartFromUri(avatarUri, "user[avatar]"))
                .doOnNext(preferencesManager::saveUser)
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<UserRes> login(LoginReq data) {
        return restService.loginUser(data.getName(), data.getPassword())
                .doOnNext(preferencesManager::saveUser)
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public boolean isSigned() {
        return preferencesManager.isTokenPresent();
    }

    @Override
    public boolean isFirstLaunch() {
        return preferencesManager.isFirstStart();
    }

    @Override
    public void firstLaunched() {
        preferencesManager.saveFirstLaunched();
    }

    //endregion

    //region ================= News =================

    @Override
    public Observable<List<Deal>> getPosts(long id, int page) {
        Long resultId = id == Constants.ID_NONE ? null : id;

        return restService.getDeals(preferencesManager.getUserToken(), resultId, page)
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Deal> getDeal(long id) {
        return restService.getDeal(preferencesManager.getUserToken(), id)
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ResponseBody> createPost(NewPostReq body, Uri uri) {
        return restService.uploadDeal(preferencesManager.getUserToken(),
                RestCallTransformer.objectToPartMap(body, "good_deal"),
                getPartFromUri(uri, "upload"))
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ResponseBody> editPost(Long id, NewPostReq body, Uri imageUri) {
        return restService.updateDeal(preferencesManager.getUserToken(),
                id,
                RestCallTransformer.objectToPartMap(body, "good_deal"),
                getPartFromUri(imageUri, "upload"))
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public Observable<ResponseBody> createEvent(NewEventReq body, Uri imageUri) {
        return restService.uploadDeal(preferencesManager.getUserToken(),
                RestCallTransformer.objectToPartMap(body, "good_deal"),
                getPartFromUri(imageUri, "upload"))
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ResponseBody> editEvent(Long id, NewEventReq body, Uri imageUri) {
        return restService.updateDeal(preferencesManager.getUserToken(),
                id,
                RestCallTransformer.objectToPartMap(body, "good_deal"),
                getPartFromUri(imageUri, "upload"))
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<String> sendReport(long id) {
        return Observable.just("Submitted")
                .delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<User> getUserProfile(long id) {
        return restService.getUserProfile(preferencesManager.getUserToken(), id)
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<FollowRes> changeFollowState(long id) {
        return restService.changeFollowState(preferencesManager.getUserToken(), id)
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Deal> likeDeal(long id) {
        return restService.like(preferencesManager.getUserToken(), id)
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ParticipateRes> changeParticipateState(long id) {
        return restService.participate(preferencesManager.getUserToken(), id)
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EventStateRes> changeEventState(long id) {
        return restService.changeEventState(preferencesManager.getUserToken(), id)
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public UserRes getUserData() {
        return new UserRes()
                .setUser(new UserRes.User()
                        .setId(preferencesManager.getUserId())
                        .setAvatarUrl(preferencesManager.getUserAvatarUrl())
                        .setName(preferencesManager.getUserName())
                );
    }

    @Override
    public void logout() {
        preferencesManager.clearUserData();
    }

    @Override
    public Observable<List<Deal>> getEvents() {
        return restService.getActiveEvents(preferencesManager.getUserToken())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Uri> cacheWebImage(String imageUrl) {
        return Observable.just(imageUrl)
                .subscribeOn(Schedulers.io())
                .map(url -> {
                    Bitmap bmp = Picasso.with(App.getAppContext())
                            .load(imageUrl)
                            .get();
                    File file = cacheBitmap(bmp);

                    return Uri.fromFile(file);
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    //endregion

    // ======= region Comments =======

    @Override
    public Observable<CommentRes> sendComment(long dealId, NewCommentReq body) {
        return restService.sendComment(preferencesManager.getUserToken(), dealId, body)
                .observeOn(AndroidSchedulers.mainThread());
    }

    // endregion


    private File cacheBitmap(Bitmap bmp) throws IOException {
        File file = new File(Environment.getExternalStorageDirectory(), Constants.CACSE_FILE_NAME);
        if (!file.exists())
            file.createNewFile();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bitmapData = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bitmapData);
        fos.flush();
        fos.close();

        return file;
    }


    @Override
    public <T> T getError(Throwable t, Class<T> tClass) {
        try {
            ResponseBody body = ((HttpException) t).response().errorBody();
            Converter<ResponseBody, T> errorConverter =
                    retrofit.responseBodyConverter(tClass, new Annotation[0]);

            return errorConverter.convert(body);
        } catch (Exception e) {
            return null;
        }
    }


    private MultipartBody.Part getPartFromUri(Uri uri, String partName) {
        if (uri == null) return null;

        String path;
        Cursor cursor = App.getAppContext().
                getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            path = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            path = cursor.getString(idx);
            cursor.close();
        }

        File file = new File(path);

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);

        return MultipartBody.Part.createFormData(partName, file.getName(), reqFile);
    }
}
