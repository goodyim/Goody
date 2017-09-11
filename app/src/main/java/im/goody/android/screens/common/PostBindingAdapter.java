package im.goody.android.screens.common;

import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import im.goody.android.R;
import im.goody.android.data.dto.Deal;
import im.goody.android.data.dto.Location;
import im.goody.android.utils.DateUtils;
import im.goody.android.utils.NetUtils;

@SuppressWarnings("unused")
public class PostBindingAdapter {
    @BindingAdapter("post_image")
    public static void bindImage(ImageView view, Deal deal) {
        Picasso.with(view.getContext()).cancelRequest(view);

        if (deal.getImageUrl() == null) {
            view.setVisibility(View.GONE);
            return;
        }
        view.setVisibility(View.VISIBLE);
        Picasso.with(view.getContext())
                .load(NetUtils.buildDealImageUrl(deal))
                .placeholder(R.color.placeholder_color)
                .fit()
                .into(view);
    }

    @BindingAdapter("author_avatar")
    public static void bindAvatar(ImageView view, String url) {
        Picasso.with(view.getContext()).cancelRequest(view);

        view.setImageResource(R.drawable.round_drawable);

        Picasso.with(view.getContext())
                .load(url)
                .placeholder(R.drawable.round_drawable)
                .fit()
                .into(view);
    }

    @BindingAdapter("event_date")
    public static void bindDate(TextView view, String date) {
        if (date != null) {
            view.setText(DateUtils.getAbsoluteDate(date));
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @BindingAdapter("address")
    public static void bindLocation(TextView view, Location location) {
        if (location != null && location.getAddress() != null) {
            view.setText(location.getAddress());
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }
}
