package com.concurrency.packt.ch07;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class ConcurrentStatistics {
  public static void jobDataFromSubscribers(List<Record> records) {
    System.out.println("**** Job info for Deposit subscribers ****");
    ConcurrentMap<String, List<Record>> map = records
            .parallelStream()
            .filter(r -> r.getSubscribe().equals("yes"))
            .collect(Collectors.groupingByConcurrent(Record::getJob));
    map.forEach((k, l) -> System.out.println(k + " : " + l.size()));
    System.out.println("*****");
  }
}
