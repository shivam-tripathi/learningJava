package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.core.Observable;

/**
 * Observable.empty produces no emissions and directly calls onComplete
 */

public class Ch02_20 {
  public static void main(String[] args) {
    Observable<String> o = Observable.empty();
    o.subscribe(
      x -> System.out.println("RECEIVED: " + x),
      Throwable::printStackTrace,
      () -> System.out.println("Done!")
    );
  }
}
