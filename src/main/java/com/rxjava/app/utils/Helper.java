package com.rxjava.app.utils;

public class Helper {
  public static void sleep(Long seconds) {
    try {
      Thread.sleep(seconds * 1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
