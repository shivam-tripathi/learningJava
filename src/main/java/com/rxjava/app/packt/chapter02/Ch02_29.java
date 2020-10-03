package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.core.Observable;

/**
 * Single can be converted into Observable, using toObservable.
 * Observable can be converted into Single, for example using `first` method. The `first` method accepts a defaul value
 * as well, in case the observable comes out as empty. Single MUST have one emission.
 */

public class Ch02_29 {
  public static void main(String[] args) {
    Observable<String> observable = Observable.just("Hello", "world");
    observable.first("Nil").subscribe(System.out::println);

    Observable<String> empty = Observable.empty();
    empty.first("Nil").subscribe(System.out::println);
  }
}
