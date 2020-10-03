package com.rxjava.app.packt.chapter02;

import com.rxjava.app.utils.Helper;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

import java.util.concurrent.TimeUnit;

/**
 * Explicit disposal is needed to free up resources for long running subscriptions which are no longer needed (like
 * observables with infinite emissions) to avoid memory leaks.
 * Disposable is link between Observable and active Observer.
 * public interface Disposable {
 *       void dispose();
 *       boolean isDisposed();
 * }
 * Typically, onComplete for finite Observables will dispose of itself safely when all values have been emitted.
 */

public class Ch02_33 {
  public static void main(String[] args) {
    Observable<Long> seconds = Observable.interval(1, TimeUnit.SECONDS);
    Disposable disposable = seconds.subscribe(
      System.out::println,
      Throwable::printStackTrace,
      () -> System.out.println("Done") // Will not be invoked if dispose if called.
    );
    Helper.sleep(5L);
    disposable.dispose(); // No more emissions
    Helper.sleep(3L);
  }
}
