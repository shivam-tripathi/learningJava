package com.rxjava.app.packt.chapter02;

import com.rxjava.app.utils.Helper;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

import java.util.concurrent.TimeUnit;

/**
 * CompositeDisposable can be used to handle multiple disposables at the same time
 */

public class Ch02_35 {
  private static final CompositeDisposable disposables = new CompositeDisposable();

  public static void main(String[] args) {
    Observable<Long> source = Observable.interval(1, TimeUnit.SECONDS);
    Disposable d1 = source.subscribe(i -> System.out.println("P1: " + i));
    Disposable d2 = source.subscribe(i -> System.out.println("P2: " + i));
    disposables.addAll(d1, d2);
    Helper.sleep(5L);
    disposables.dispose(); // No more emissions from now on
    Helper.sleep(2L);
  }
}
