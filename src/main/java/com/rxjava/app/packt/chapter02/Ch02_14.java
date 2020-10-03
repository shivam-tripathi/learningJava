package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observables.ConnectableObservable;

public class Ch02_14 {
  public static void main(String[] args) {
    ConnectableObservable<String> source = Observable.just("Hello", "Namaste", "Hola", "Konnichiwa").publish();
    source.subscribe(val -> System.out.println("Observer 1: " + val));
    source.map(String::length).subscribe(val -> System.out.println("Observer 2: " + val));
    source.connect();
  }
}
