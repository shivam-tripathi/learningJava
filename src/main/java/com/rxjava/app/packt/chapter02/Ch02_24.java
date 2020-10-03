package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.core.Observable;

/**
 * Here, we use Observable.range using static properties start and count to build an observable.
 * If we subscribe to the observable, then update any of the static properties and subscribe again - we find
 * there has been no change in results. This is because no state has been associated with the observable.
 */

public class Ch02_24 {
  private static final int start = 0;
  private static int count = 5;

  public static void main(String[] args) {
    Observable<Integer> source = Observable.range(start, count);
    source.subscribe(val -> System.out.println("R1: " + val));
    count = 10;
    source.subscribe(val -> System.out.println("R2: " + val)); // Same result as R1
  }
}
