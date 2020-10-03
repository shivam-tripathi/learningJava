package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.core.Observable;

/**
 * If we want to do Observable creation in a deferred manner, instead of just we can use fromCallable.
 * If we get some Exception when using `just`, it is propagated in traditional java way - onError is not invoked.
 * This is because Observable has not been created yet. If errors after observable has been created, it is handled
 * by Observable - and onError is invoked.
 */

public class Ch02_26a {
  public static void main(String[] args) {
    Observable.just(100 / 0).subscribe(val -> System.out.println("RECEIVED " + val), e -> System.out.println("Error::" + e));
  }
}
