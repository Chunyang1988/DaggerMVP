package org.cy.daggermvp.mvp.presenter;

import org.cy.daggermvp.mvp.control.LoginControl;
import org.cy.daggermvp.mvp.func.LoginFunc;
import org.cy.daggermvp.mvp.model.Login;

import javax.inject.Inject;

import rx.Subscriber;

/**
 * Created by TDD-08 on 16/8/3.
 */
public class LoginPresenter implements LoginControl.LoginPresenter {


    private LoginControl.LoginView view;
    @Inject
    LoginFunc loginFunc;

    @Inject
    public LoginPresenter(LoginControl.LoginView view) {
        this.view = view;
    }


    @Override
    public void login() {
        view.showProgress();
        loginFunc.onLogin(new Subscriber<Login>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                view.dismissProgress();
                view.onError(e.getMessage());
            }

            @Override
            public void onNext(Login login) {
                view.dismissProgress();
                view.onResult(login);
            }
        });


    }
}
