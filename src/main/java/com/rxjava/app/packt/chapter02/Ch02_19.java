package com.rxjava.app.packt.chapter02;

import com.rxjava.app.utils.Helper;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observables.ConnectableObservable;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class Ch02_19 {
  public static void main(String[] args) {
    ConnectableObservable<Long> observable = Observable.interval(1, TimeUnit.SECONDS).publish();
    observable.subscribe(s -> System.out.printf("Observer 1: %s %s Bengaluru%n", LocalDateTime.now().getSecond(), s));
    observable.connect();
    Helper.sleep(3L);
    observable.subscribe(s -> System.out.printf("Observer 2: %s %s Bengaluru%n", LocalDateTime.now().getSecond(), s));
    observable.connect();
    Helper.sleep(5L);
  }
}
