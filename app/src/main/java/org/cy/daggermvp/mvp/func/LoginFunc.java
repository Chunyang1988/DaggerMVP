package org.cy.daggermvp.mvp.func;

import org.cy.daggermvp.mvp.model.Login;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by TDD-08 on 16/8/3.
 */
public class LoginFunc {


    public void onLogin(Subscriber subscriber) {

        Observable.timer(1500, TimeUnit.MILLISECONDS).map(new Func1<Long, Login>() {
            @Override
            public Login call(Long aLong) {
                return new Login();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
        //API地方就不给
//        ApiManager.getDefault().login(subscriber);
    }

}
