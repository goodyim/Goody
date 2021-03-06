package im.goody.android.screens.login;

import android.content.Context;
import android.util.AttributeSet;

import im.goody.android.core.BaseView;
import im.goody.android.databinding.ScreenLoginBinding;
import im.goody.android.utils.UIUtils;

public class LoginView extends BaseView<LoginController, ScreenLoginBinding> {
    public LoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAuthData(LoginViewModel data) {
        binding.setUser(data);
    }

    @Override
    protected void onAttached() {
        binding.signInSubmit.setOnClickListener(v -> {
            UIUtils.hideKeyboard(getFocusedChild());
            controller.login();
        });
        binding.signInRedirect.setOnClickListener(v -> controller.goToRegister());
    }

    @Override
    protected void onDetached() {
    }
}
