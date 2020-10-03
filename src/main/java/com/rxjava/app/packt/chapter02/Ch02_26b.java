package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.core.Observable;

/**
 * Here error occurs after observable has been created, so it propagates in desired manner and onError is invoked
 */
public class Ch02_26b {
  public static void main(String[] args) {
    Observable.just(1)
      .map(i -> i)
      .subscribe(val -> System.out.println("RECEIVED " + val), e -> System.out.println("Error::" + e));
  }
}
