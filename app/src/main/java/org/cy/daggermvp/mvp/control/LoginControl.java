package org.cy.daggermvp.mvp.control;

import org.cy.daggermvp.mvp.model.Login;

/**
 * Created by TDD-08 on 16/8/3.
 */
public interface LoginControl {

    interface LoginView {

        void showProgress();

        void dismissProgress();

        void onError(String mess);

        void onResult(Login login);
    }


    interface LoginPresenter {

        void login();
    }
}
