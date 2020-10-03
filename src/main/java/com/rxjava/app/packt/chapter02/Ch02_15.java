package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.core.Observable;

public class Ch02_15 {
  public static void main(String[] args) {
    Observable.range(0, 15).subscribe(x -> System.out.println("RECEIVED: " + x));
    Observable.rangeLong(Integer.MAX_VALUE, 20).subscribe(x -> System.out.println("RECEIVED: " + x));
  }
}
