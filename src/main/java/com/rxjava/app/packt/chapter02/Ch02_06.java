package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.core.Observable;

import java.util.Arrays;
import java.util.List;

public class Ch02_06 {
  public static void main(String[] args) {
    List<String> list = Arrays.asList("One", "Two", "Three", "Eleven");
    Observable.fromIterable(list).map(String::length).filter(l -> l >= 5).subscribe(System.out::println).dispose();
  }
}
