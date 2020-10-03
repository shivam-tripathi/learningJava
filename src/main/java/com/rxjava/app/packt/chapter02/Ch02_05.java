package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.core.Observable;

public class Ch02_05 {
  public static void main(String[] args) {
    Observable<String> source = Observable.just("One", "Two", "Three", "Eleven");
    source.map(String::length).filter(l -> l >= 5).subscribe(System.out::println, Throwable::printStackTrace).dispose();
  }
}
