package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class Ch02_07 {
  public static void main(String[] args) {
    Observable<String> source = Observable.just("One", "Two", "Three", "Eleven");
    Observer<Integer> observer = new Observer<>() {
      @Override
      public void onSubscribe(@NonNull Disposable d) {
      }

      @Override
      public void onNext(@NonNull Integer s) {
        System.out.println("RECEIVED: " + s);
      }

      @Override
      public void onError(@NonNull Throwable e) {
        e.printStackTrace();
      }

      @Override
      public void onComplete() {
        System.out.println("Done!");
      }
    };

    source.map(String::length).filter(l -> l >= 5).subscribe(observer);
  }
}
