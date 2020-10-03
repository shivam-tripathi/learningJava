package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.ResourceObserver;

import java.util.concurrent.TimeUnit;

/**
 * If we do not wish control Disposable on subscription, we can use ResourceObserver which will use default Disposable
 * handling. On using ResourceObserver with Observable.subscribeWith, we get our default disposable.
 */

public class Ch02_34b {
  public static void main(String[] args) {
    Observable<Long> observable = Observable.interval(1, TimeUnit.SECONDS);

    ResourceObserver<Long> resourceObserver = new ResourceObserver<Long>() {
      @Override
      public void onNext(@NonNull Long aLong) {
        System.out.println("Received: " + aLong);
      }

      @Override
      public void onError(@NonNull Throwable e) {
        e.printStackTrace();
      }

      @Override
      public void onComplete() {
        System.out.println("Complete");
      }
    };

    Disposable disposable = observable.subscribeWith(resourceObserver);
    disposable.dispose();
  }
}
