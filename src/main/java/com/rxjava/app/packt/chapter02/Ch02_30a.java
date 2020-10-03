package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.core.Maybe;

/**
 * Maybe allows for 0 or 1 emission. It is similar to regular Observable, except it has onSuccess instead of onNext.
 * Methods:
 *  - void onSubscribe(@NonNull Disposable d)
 *  - void onSuccess(T value);
 *  - void onError@NonNull Throwable e);
 *  - void onComplete();
 *  If emission occurs, onComplete will not be invoked. Maybe makes one emission at most - if it calls onSuccess,
 *  it will not call onComplete. It is completed implicitly.
 */

public class Ch02_30a {
  public static void main(String[] args) {
    Maybe<Integer> source = Maybe.just(100);
    source.subscribe(
      s -> System.out.println("P1: " + s), // onSuccess: invoked
      e -> System.out.println("E1: " + e),
      () -> System.out.println("P1: complete") // onComplete: will not be invoked
    );

    Maybe<Integer> emptySource = Maybe.empty();
    emptySource.subscribe(
      s -> System.out.println("P2: " + s), // onSuccess: will not be invoked
      e -> System.out.println("E2: " + e),
      () -> System.out.println("P2: complete") // onComplete: invoked
    );
  }
}
