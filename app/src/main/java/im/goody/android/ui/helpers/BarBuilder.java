package im.goody.android.ui.helpers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewPager;
import android.view.View;

import im.goody.android.core.IBarView;

public class BarBuilder {
    private IBarView view;

    private View.OnClickListener homeListener;
    private int homeState = HOME_HAMBURGER;

    private boolean toolbarVisible;

    private boolean statusBarVisible = true;

    public static final int HOME_GONE = 0;
    public static final int HOME_HAMBURGER = 1;
    public static final int HOME_ARROW = 2;

    private TabInfo tabInfo;

    @Nullable
    private Integer titleRes;

    public BarBuilder(IBarView view) {
        this.view = view;
    }
    public BarBuilder setView(IBarView view) {
        this.view = view;
        return this;
    }

    @NonNull
    public BarBuilder setTitleRes(@StringRes @NonNull Integer titleRes) {
        this.titleRes = titleRes;
        return this;
    }

    @NonNull
    public BarBuilder setHomeState(int enable) {
        this.homeState = enable;
        return this;
    }

    public BarBuilder setHomeListener(View.OnClickListener listener) {
        homeListener = listener;
        return this;
    }

    @NonNull
    public BarBuilder setToolbarVisible(boolean toolbarVisible) {
        this.toolbarVisible = toolbarVisible;
        return this;
    }

    public BarBuilder setTabs(ViewPager pager) {
        tabInfo = new TabInfo(pager);
        return this;
    }

    @NonNull
    public BarBuilder setStatusBarVisible(boolean statusBarVisible) {
        this.statusBarVisible = statusBarVisible;
        return this;
    }

    public void build() {
        if (view != null) {
            view.setToolBarVisible(toolbarVisible);

            if (titleRes != null)
                view.setToolbarTitle(titleRes);

            view.setHomeListener(homeListener);
            view.setHomeState(homeState);
            view.setStatusBarVisible(statusBarVisible);

            view.setTabs(tabInfo);
        }
    }

    public class TabInfo {
        private final ViewPager pager;

        private TabInfo(ViewPager pager) {
            this.pager = pager;
        }

        public ViewPager getPager() {
            return pager;
        }
    }
}
