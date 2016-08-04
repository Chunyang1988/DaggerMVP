package org.cy.daggermvp.mvp.dagger;

import org.cy.daggermvp.mvp.control.LoginControl;
import org.cy.daggermvp.mvp.func.LoginFunc;
import org.cy.daggermvp.mvp.presenter.LoginPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by TDD-08 on 16/8/3.
 */
@Module
public class LoginModule {

    private LoginControl.LoginView view;

    public LoginModule(LoginControl.LoginView view) {
        this.view = view;
    }

    @Provides
    LoginControl.LoginView provideView() {
        return view;
    }

    @Provides
    LoginFunc provideFunc() {
        return new LoginFunc();
    }
}
