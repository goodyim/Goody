package im.goody.android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import im.goody.android.R;

public class BitmapUtils {
    public static RoundedBitmapDrawable prepareAvatar(Bitmap original, Context context) {
        int size = context.getResources().getDimensionPixelSize(R.dimen.register_avatar_size);
        Bitmap sized = ThumbnailUtils.extractThumbnail(original, size, size);
        RoundedBitmapDrawable result = RoundedBitmapDrawableFactory.create(context.getResources(), sized);
        result.setCircular(true);
        return result;
    }
}