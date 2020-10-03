package com.rxjava.app.packt.chapter02;

import com.rxjava.app.utils.Helper;
import io.reactivex.rxjava3.core.Observable;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class Ch02_17 {
  public static void main(String[] args) {
    Observable<Long> o = Observable.interval(1, TimeUnit.SECONDS);
    o.subscribe(s ->
      System.out.println(String.format("%s %s Bengaluru", LocalDateTime.now().getSecond(), s))
    );
    Helper.sleep(3L);
  }
}
