package com.rxjava.app.packt.chapter02;

import com.rxjava.app.utils.Helper;
import io.reactivex.rxjava3.core.Observable;

/**
 * Observable.never never calls onComplete - subscription waits for an emission forever.
 */

public class Ch02_21 {
  public static void main(String[] args) {
    Observable<String> o = Observable.never();
    o.subscribe(System.out::println, Throwable::printStackTrace, () -> System.out.println("Done!"));
    Helper.sleep(5L); // waits for 5 seconds before giving up
  }
}
