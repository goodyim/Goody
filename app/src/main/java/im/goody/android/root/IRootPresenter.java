package im.goody.android.root;

import im.goody.android.ui.helpers.BarBuilder;

public interface IRootPresenter {
    BarBuilder newBarBuilder();

    void showIntroScreen();
    void showMainScreen();
    void showLoginScreen();
    void showRegisterScreen();

    void hideProgress();
    void showRegisterProgress();
    void showLoginProgress();
}