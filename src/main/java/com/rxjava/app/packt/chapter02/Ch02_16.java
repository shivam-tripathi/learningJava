package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.core.Observable;

public class Ch02_16 {
  public static void main(String[] args) {
    Observable.range(5, 10).subscribe(x -> System.out.println("RECEIVED: " + x));
  }
}
