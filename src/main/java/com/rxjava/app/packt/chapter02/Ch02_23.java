package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.core.Observable;

/**
 * We can pass an Lambda function as well to Observable.error to create a new Error for every subscription
 */

public class Ch02_23 {
  public static void main(String[] args) {
    Observable.error(() -> new Exception("Crash and burn"))
      .subscribe(
        System.out::println,
        (e) -> System.out.println("Error captured: " + e.getMessage()),
        () -> System.out.println("Done!")
      );
  }
}
