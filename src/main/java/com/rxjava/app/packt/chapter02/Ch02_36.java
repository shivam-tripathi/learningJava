package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * Observables created using Observable.create must constantly have the ObservableEmitter check if it has been disposed
 * If the Observable is wrapped around some other resource, it must be also be disposed else it will lead to memory
 * leaks. We can use observableEmitter.setCancellable which is called on disposal, lambda passed to it can dispose other
 * related resources.
 */

public class Ch02_36 {
  public static void main(String[] args) {
    Observable<Integer> source = Observable.create(emitter -> {
      try {
        for(int i = 0; i < 100 && !emitter.isDisposed(); i++) {
          emitter.onNext(i++);
        }
        if (emitter.isDisposed()) {
          System.out.println("Emitter has been disposed");
          return; // Return if disposed
        }
        emitter.onComplete();
      } catch (Throwable e) {
        emitter.onError(e);
      }
    });

    Observer<Integer> observer = new Observer<>() {
      private Disposable d;
      @Override
      public void onSubscribe(@NonNull Disposable d) {
        this.d = d;
      }

      @Override
      public void onNext(@NonNull Integer integer) {
        if (integer < 50) {
          System.out.println("Received: " + integer);
        } else {
          System.out.println("Disposing");
          d.dispose();
        }
      }

      @Override
      public void onError(@NonNull Throwable e) {
        e.printStackTrace();
      }

      @Override
      public void onComplete() {
        System.out.println("Done");
      }
    };

    source.subscribe(observer);
  }
}
