package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;

public class Ch02_11 {
  public static void main(String[] args) {
    Observable<String> source = Observable.just("One", "Two", "Three", "Eleven");
    source
      .map(String::length)
      .filter(l -> l >= 5)
      .subscribe(data -> System.out.println("OBSERVER1: " + data),
        Throwable::printStackTrace,
        () -> System.out.println("Done!")
      );

    Consumer<Integer> onNext = data -> System.out.println("OBSERVER2: " + data);
    Consumer<Throwable> onError = Throwable::printStackTrace;
    Action onComplete = () -> System.out.println("Done!");
    source
      .map(String::length)
      .filter(l -> l >= 5)
      .subscribe(onNext,
        onError,
        onComplete
      );
  }
}
