package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.core.Observable;

/**
 * If we replace Maybe with plain observable, onComplete is called even after an emission to point out that subcriber
 * should not expect any more emissions.
 */

public class Ch02_30b {
  public static void main(String[] args) {
    Observable<Integer> source = Observable.just(100);
    source.subscribe(
      s -> System.out.println("P1: "  + s),
      Throwable::printStackTrace,
      () -> System.out.println("P1: Complete")
    );

    Observable<Integer> emptySource = Observable.empty();
    emptySource.subscribe(
      System.out::println,
      Throwable::printStackTrace,
      () -> System.out.println("P2: Complete")
    );
  }
}
