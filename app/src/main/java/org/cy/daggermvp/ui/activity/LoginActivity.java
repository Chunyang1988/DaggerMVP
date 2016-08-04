package org.cy.daggermvp.ui.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.cy.daggermvp.R;
import org.cy.daggermvp.mvp.control.LoginControl;
import org.cy.daggermvp.mvp.dagger.DaggerLoginComponent;
import org.cy.daggermvp.mvp.dagger.LoginModule;
import org.cy.daggermvp.mvp.model.Login;
import org.cy.daggermvp.mvp.presenter.LoginPresenter;

import javax.inject.Inject;

public class LoginActivity extends AppCompatActivity implements LoginControl.LoginView {


    private ProgressDialog progressDialog;


    @Inject
    LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        DaggerLoginComponent.builder().loginModule(new LoginModule(this)).build().inject(this);

        loginPresenter.login();


    }

    @Override
    public void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.show();

    }

    @Override
    public void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onError(String mess) {
        Toast.makeText(LoginActivity.this, mess, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResult(Login login) {
        Toast.makeText(LoginActivity.this, "is Result --", Toast.LENGTH_SHORT).show();
    }
}
