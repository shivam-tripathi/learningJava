package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.core.Completable;

/**
 * Completable is only concerned with completion of actions, it does not receive any emissions
 * Methods:
 * - void onSubscribe@NonNull Disposable d);
 * - void onComplete();
 * - void onError(@NonNull Throwable error);
 * <p>
 * Completable.complete immediately invoked onComplete
 * Completable.fromRunnable runs specified action before invoking onComplete
 */

public class Ch02_32 {
  public static void main(String[] args) {
    Completable completable1 = Completable.complete();
    completable1.subscribe(() -> System.out.println("Completable1:complete"), Throwable::printStackTrace);

    Completable completable2 = Completable.fromRunnable(Ch02_32::runAction);
    completable2.subscribe(() -> System.out.println("Compeletable2:complete"), Throwable::printStackTrace);
  }

  private static void runAction() {
    System.out.println("RunAction");
  }
}
