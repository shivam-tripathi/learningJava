package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.core.Observable;

/**
 * One way to solve lack of state problem is to create a fresh observable everytime for each subscription.
 * This can be achieved using Observable.defer - which takes in a lambda which created a new observable for each
 * subscription and hence reflects change in state.
 */

public class Ch02_25 {
  private static int start = 24;
  private static int count = 5;
  public static void main(String[] args) {
    Observable<Integer> source = Observable.defer(() -> Observable.range(start, count));
    source.subscribe(val -> System.out.println("R1: " + val));
    count = 10;
    source.subscribe(val -> System.out.println("R2: " + val));
  }
}
