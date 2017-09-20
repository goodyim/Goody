package im.goody.android.screens.profile;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import im.goody.android.Constants;
import im.goody.android.core.BaseView;
import im.goody.android.databinding.ScreenProfileBinding;


public class ProfileView extends BaseView<ProfileController, ScreenProfileBinding>
implements SwipeRefreshLayout.OnRefreshListener {
    public ProfileView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttached() {
        binding.profileRefresh.setRefreshing(true);
        binding.profileRefresh.setColorSchemeColors(Constants.PROGRESS_COLORS);
        binding.profileRefresh.setOnRefreshListener(this);
    }

    @Override
    protected void onDetached() {

    }

    public void hideFollowButton() {
        binding.profileFollow.setVisibility(GONE);
    }

    @Override
    public void onRefresh() {
        controller.loadData();
    }

    public void setData(ProfileViewModel viewModel) {
        finishRefresh();
        binding.setUser(viewModel);

        if (binding.profileContainer.getVisibility() == GONE)
            binding.profileContainer.setVisibility(VISIBLE);
    }

    public void startRefresh() {
        binding.profileRefresh.setRefreshing(true);
    }

    public void finishRefresh() {
        binding.profileRefresh.setRefreshing(false);
    }
}