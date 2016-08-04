package org.cy.daggermvp.mvp.dagger;

import org.cy.daggermvp.ui.activity.LoginActivity;

import dagger.Component;

/**
 * Created by TDD-08 on 16/8/3.
 */
@Component(modules = LoginModule.class)
public interface LoginComponent {

    void inject(LoginActivity activity);

}
