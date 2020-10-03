package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.core.Observable;

/**
 * Observable.error immediately generates an Error with specified exception.
 */

public class Ch02_22 {
  public static void main(String[] args) {
    Observable.error(new Exception("Crash and burn"))
      .subscribe(System.out::println, Throwable::printStackTrace, () -> System.out.println("Done!"));
  }
}
