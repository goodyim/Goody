package im.goody.android.screens.main;

import android.support.annotation.NonNull;
import android.view.View;

import im.goody.android.R;
import im.goody.android.core.BaseController;
import im.goody.android.di.DaggerScope;
import im.goody.android.di.components.RootComponent;
import im.goody.android.ui.helpers.BarBuilder;


public class MainController extends BaseController<MainView> {
    @Override
    protected void initDaggerComponent(RootComponent parentComponent) {
        Component component = parentComponent.plusMain();
        if (component != null)
            component.inject(this);
    }

    @Override
    protected void initActionBar() {
        rootPresenter.newBarBuilder()
                .setTitleRes(R.string.news_title)
                .setToolbarVisible(true)
                .setHomeState(BarBuilder.HOME_HAMBURGER)
                .setTabs(view().getPager())
                .build();
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        view().setupPager();
    }

    void showNewPostScreen() {
        rootPresenter.showNewPostScreen();
    }

    void showNewEventScreen() {
        rootPresenter.showNewEventScreen();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.screen_main;
    }

    @dagger.Subcomponent
    @DaggerScope(MainController.class)
    public interface Component {

        void inject(MainController controller);
    }
}
