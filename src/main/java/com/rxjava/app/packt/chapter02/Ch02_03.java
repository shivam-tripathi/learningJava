package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.core.Observable;

public class Ch02_03 {
  public static void main(String[] args) {
    Observable<String> source = Observable.create(emitter -> {
      try {
        emitter.onNext("One");
        emitter.onNext("Two");
        emitter.onNext("Three");
        emitter.onNext("Eleven");
        emitter.onComplete();
      } catch (Exception e) {
        emitter.onError(e);
      }
    });
    Observable<Integer> lengths = source.map(String::length);
    Observable<Integer> filtered = lengths.filter(i -> i >= 5);
    filtered.subscribe(x -> System.out.println("RECEIVED: " + x), Throwable::printStackTrace).dispose();
  }
}
