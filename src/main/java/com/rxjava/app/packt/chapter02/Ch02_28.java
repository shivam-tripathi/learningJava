package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.core.Single;

/**
 * Single returns just one emission, implements SingleSource.
 * Does not have onComplete - rather onSuccess. Does not have onNext method. onSuccess in combo of onComplete and onNext
 */

public class Ch02_28 {
  public static void main(String[] args) {
    Single.just("Hello!")
      .map(String::length)
      .subscribe(System.out::println, e -> System.out.println("Error:" + e)); // only onSuccess and onError
  }
}
