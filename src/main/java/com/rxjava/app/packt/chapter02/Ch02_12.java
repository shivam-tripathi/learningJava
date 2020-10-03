package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.core.Observable;

public class Ch02_12 {
  public static void main(String[] args) {
    Observable<String> source = Observable.just("One", "Two", "Three", "Eleven");
    source.subscribe(x -> System.out.println("Observer1: " + x));

    source.map(String::length).filter(l -> l >= 5).subscribe(x -> System.out.println("Observer2: " + x));
  }
}
