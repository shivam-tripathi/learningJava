package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.core.Observable;

/**
 * If we use fromCallable, even if error is thrown during procedure invocation - it is handled by Observable.
 * If initialization of emission has a chance of throwing an error, it is better to use fromCallable.
 */

public class Ch02_27 {
  public static void main(String[] args) {
    Observable.fromCallable(() -> 1 / 0)
      .subscribe(val -> System.out.println("RECEIVED " + val), e -> System.out.println("Error::" + e));
  }
}
