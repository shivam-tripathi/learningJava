package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * Handling disposable inside observer - Save Disposable object received on subscription - we can use it at any stage of
 * emission - onNext, onError, onComplete.
 * Observable.subscribe(Observer<T> observer) returns void
 */

public class Ch02_34a {
  public static void main(String[] args) {
    Observable<String> source = Observable.just("Hello", "World", "This", "Is", "Me");
    Observer<String> observer = new Observer<>() {
      private Disposable d;
      @Override
      public void onSubscribe(@NonNull Disposable d) {
        this.d = d;
      }

      @Override
      public void onNext(@NonNull String s) {
        System.out.println("Received: " + s);
      }

      @Override
      public void onError(@NonNull Throwable e) {
        e.printStackTrace();
        d.dispose(); // Dispose on error
      }

      @Override
      public void onComplete() {
        System.out.println("Complete");
        d.dispose(); // Dispose on complete
      }
    };

    source.subscribe(observer); // Return void
  }
}
