package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;

/**
 * Certain observable yield a Maybe. For example, firstElement (which yield no emission if Observable is empty)
 */

public class Ch02_31 {
  public static void main(String[] args) {
    Observable<String> source = Observable.just("Hello");
    Maybe<String> maybeSource = source.firstElement();
    maybeSource.subscribe(
      s -> System.out.println("Maybe 1: " + s),
      Throwable::printStackTrace,
      () -> System.out.println("Maybe 1: Complete")
    );

    Observable<String> emptySource = Observable.empty();
    Maybe<String> emptyMaybeSource = emptySource.firstElement();
    emptyMaybeSource.subscribe(
      System.out::println,
      Throwable::printStackTrace,
      () -> System.out.println("Maybe 2: Complete")
    );
  }
}
