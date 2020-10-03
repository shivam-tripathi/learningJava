package com.rxjava.app.packt.chapter02;

import com.rxjava.app.utils.Helper;
import io.reactivex.rxjava3.core.Observable;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class Ch02_18 {
  public static void main(String[] args) {
    Observable<Long> o = Observable.interval(1, TimeUnit.SECONDS);
    o.subscribe(s -> System.out.printf("Observer 1: %s %s Bengaluru%n", LocalDateTime.now().getSecond(), s));
    Helper.sleep(3L);
    o.subscribe(s -> System.out.printf("Observer 2: %s %s Bengaluru%n", LocalDateTime.now().getSecond(), s));
    Helper.sleep(3L);
  }
}
