package im.goody.android.screens.common;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.File;

import im.goody.android.Constants;
import im.goody.android.R;
import im.goody.android.core.BaseController;
import im.goody.android.core.BaseView;
import im.goody.android.ui.dialogs.ChooseImageOptionsDialog;
import im.goody.android.ui.dialogs.OptionsDialog;
import im.goody.android.utils.FileUtils;
import io.reactivex.disposables.Disposable;

import static android.app.Activity.RESULT_OK;

public abstract class NewController<V extends BaseView> extends BaseController<V> {
    private static final int IMAGE_PICK_REQUEST = 0;
    private static final int CAMERA_REQUEST = 1;
    private static final int PLACE_PICKER_REQUEST = 2;
    protected static final int CACHE_IMAGE_REQUEST = 3;

    private static final int LOCATION_PERMISSION_REQUEST = 1;
    private static final int STORAGE_PERMISSION_REQUEST = 2;

    private static final String[] LOCATION_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};
    protected static final String[] STORAGE_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private OptionsDialog dialog = new ChooseImageOptionsDialog();

    protected String tempImageUri;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makePlacePickerRequest();
                } else {
                    view().showMessage(R.string.location_permission_denied);
                }
                break;
            case STORAGE_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeGalleryRequest();
                } else {
                    view().showMessage(R.string.read_permission_denied);
                }
                break;
            case CACHE_IMAGE_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadImage(tempImageUri);
                } else {
                    view().showMessage(R.string.cache_image_permission_denied);
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case PLACE_PICKER_REQUEST:
                if (resultCode == RESULT_OK && getActivity() != null && data != null) {
                    Place place = PlacePicker.getPlace(getActivity(), data);
                    placeChanged(place);
                }
                break;
            case IMAGE_PICK_REQUEST:
                if (resultCode == RESULT_OK && data != null) {

                    Uri uri = data.getData();

                    loadImage(uri.toString());
                }
                break;
            case CAMERA_REQUEST:
                if (resultCode == RESULT_OK) {
                    loadImage(tempImageUri);
                }
        }
    }

    public void showLocationDialog() {
        Disposable d = new OptionsDialog(R.array.choose_location_options)
                .show(getActivity()).subscribe(integer -> {
                    switch (integer) {
                        case 0:
                            chooseLocation();
                            break;
                        case 1:
                            placeChanged(null);
                    }
                });

        compositeDisposable.add(d);
    }

    public void choosePhoto() {
        if (isPermissionGranted(STORAGE_PERMISSIONS)) {
            showDialog();
        } else {
            requestPermissions(STORAGE_PERMISSIONS, STORAGE_PERMISSION_REQUEST);
        }
    }

    public void clearPhoto() {
        imageChanged(null);
    }

    protected void loadImage(String url) {
        Disposable disposable = repository.cacheWebImage(url)
                .subscribe(uri -> {
                    tempImageUri = null;
                    imageChanged(uri);
                }, this::showError);

        compositeDisposable.add(disposable);
    }


    // ======= region private methods =======

    private void chooseLocation() {
        if (isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            makePlacePickerRequest();
        } else {
            requestPermissions(LOCATION_PERMISSIONS, LOCATION_PERMISSION_REQUEST);
        }
    }

    private void showDialog() {
        Disposable disposable = dialog.show(getActivity()).subscribe(index -> {
            switch (index) {
                case IMAGE_PICK_REQUEST:
                    makeGalleryRequest();
                    break;
                case CAMERA_REQUEST:
                    takePhoto();
            }
        });

        compositeDisposable.add(disposable);
    }

    private void makePlacePickerRequest() {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            if (getActivity() != null)
                startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makeGalleryRequest() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/**");
        startActivityForResult(intent, IMAGE_PICK_REQUEST);
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(FileUtils.getCacheFolder(), Constants.CACHE_FILE_NAME);

        Uri uri = FileUtils.uriFromFile(photo);

        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                uri);

        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        tempImageUri = uri.toString();

        startActivityForResult(intent, CAMERA_REQUEST);
    }

    // end

    protected abstract void imageChanged(Uri uri);

    protected abstract void placeChanged(Place place);
}
