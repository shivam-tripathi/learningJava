package com.concurrency.packt.ch02;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Getter
@Setter
@ToString
@AllArgsConstructor
class Sample {
  List<Integer> vector;
  private String tag;

  static double distanceBetween(Sample a, Sample b) {
    List<Integer> dataA = a.getVector();
    List<Integer> dataB = b.getVector();
    if (dataA.size() != dataB.size()) {
      throw new IllegalArgumentException("Vectors don't have same length");
    }
    double dist = 0;
    for (int i = 0; i < dataA.size(); i++) {
      dist += Math.pow(dataA.get(i) - dataB.get(i), 2);
    }
    return Math.sqrt(dist);
  }
}

@AllArgsConstructor
abstract class KnnClassifer {
  @Getter
  @Setter
  @AllArgsConstructor
  static class Distance implements Comparable<Distance> {
    private int index;
    private double distance;

    @Override
    public int compareTo(Distance d) {
      int cmp = Double.compare(this.getDistance(), d.getDistance());
      if (cmp == 0) {
        return Integer.compare(this.getIndex(), d.getIndex());
      }
      return cmp;
    }
  }

  List<? extends Sample> dataSet;
  int k;

  abstract public String classify(Sample referencePoint) throws InterruptedException;
  abstract public void destroy();

  public String nearest(List<Distance> distances) {
    Map<String, Integer> results = new HashMap<>();
    for (int i = 0; i < k; i++) {
      Sample localExample = dataSet.get(distances.get(i).getIndex());
      results.merge(localExample.getTag(), 1, Integer::sum);
    }

    return Collections.max(results.entrySet(), Map.Entry.comparingByValue()).getKey();
  }
}


/**
 * Serial implementation of k-Nearest neighbour
 */
class Serial extends KnnClassifer {
  public Serial(List<? extends Sample> dataSet, int k) {
    super(dataSet, k);
  }

  public String classify(Sample referencePoint) {
    // Calculate distance for each point, with reference to original index
    List<Distance> distances = IntStream
            .range(0, dataSet.size())
            .mapToObj(index -> new Distance(index, Sample.distanceBetween(dataSet.get(index), referencePoint)))
            .sorted()
            .collect(Collectors.toList());
    return nearest(distances);
  }

  public void destroy() {
  }
}

/**
 * Fine grained implementation - one task per dataSet
 * We can parallelize at the following operations: finding distance, sorting and collecting tags in closest k samples
 */
class FineGrained extends KnnClassifer {
  private final ThreadPoolExecutor executor;
  private final boolean parallelSort;

  public FineGrained(List<? extends Sample> dataSet, int k, int factor, boolean parallelSort) {
    super(dataSet, k);
    int numThreads = factor * Runtime.getRuntime().availableProcessors();
    executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
    this.parallelSort = parallelSort;
  }

  public String classify(Sample referencePoint) throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(dataSet.size());
    Distance[] distances = new Distance[dataSet.size()];
    for (int i = 0; i < dataSet.size(); i++) {
      int index = i; // clone i to avoid race condition
      executor.execute(() -> {
        distances[index] = new Distance(index, Sample.distanceBetween(dataSet.get(index), referencePoint));
        latch.countDown();
      });
    }

    latch.await();

    if (parallelSort) {
      Arrays.parallelSort(distances);
    } else {
      Arrays.sort(distances);
    }

    return nearest(Arrays.asList(distances));
  }

  @Override
  public void destroy() {
    executor.shutdown();
  }
}

/**
 * Coarse grained implementation - tasks only equal to number of threads
 */
class CoarseGrained extends KnnClassifer {
  private final ThreadPoolExecutor executor;
  private final boolean parallelSort;
  private final int numThreads;

  public CoarseGrained(List<? extends Sample> dataSet, int k, int factor, boolean parallelSort) {
    super(dataSet, k);
    numThreads = factor * Runtime.getRuntime().availableProcessors();
    executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
    this.parallelSort = parallelSort;
  }

  public String classify(Sample referencePoint) throws InterruptedException {
    Distance[] distances = new Distance[dataSet.size()];
    CountDownLatch promises = new CountDownLatch(numThreads);

    final int batchSize = dataSet.size() / numThreads;

    for (int i = 0; i < dataSet.size(); i += batchSize) {
      int begin = i, end = Math.min(i + batchSize, dataSet.size());
      executor.execute(() -> {
        for (int j = begin; j < end; j++) {
          distances[j] = new Distance(j, Sample.distanceBetween(dataSet.get(j), referencePoint));;
        }
        promises.countDown();
      });
    }

    promises.await();

    if (parallelSort) {
      Arrays.parallelSort(distances);
    } else {
      Arrays.sort(distances);
    }
    return nearest(Arrays.asList(distances));
  }

  @Override
  public void destroy() {
    executor.shutdown();
  }
}

// Cleanup data and run classifier
public class KNNClassifier {
  static List<String> keys = new ArrayList<>(); // list of all keys
  static Map<String, Map<String, Integer>> normalizedData = new HashMap<>(); // normalized values of data
  static List<Sample> samples = new ArrayList<>(); // all samples

  private static List<String[]> readData() throws FileNotFoundException {
    try (Scanner sc = new Scanner(new File("data/bank-marketing/bank-full.csv"))) {
      // Collect and normalize data
      List<String[]> data = new ArrayList<>(); // all data
      int count = 0;
      while (sc.hasNext()) {
        String[] line = sc.next().split(";");
        if (count == 0) { // keys
          for (String key : line) {
            normalizedData.put(key, new HashMap<>());
            keys.add(key);
          }
        } else { // normalize data
          for (int i = 0; i < line.length; i++) {
            Map<String, Integer> attribute = normalizedData.get(keys.get(i));
            if (!attribute.containsKey(line[i])) {
              attribute.put(line[i], attribute.size());
            }
            data.add(line);
          }
        }
        count++;
      }
      System.out.println("Count: " + count);
      return data;
    }
  }

  private static void generateSamples(List<String[]> data) {
    for (String[] entry: data) {
      List<Integer> vector = new ArrayList<>();
      for (int i = 0; i < entry.length; i++) {
        vector.add(normalizedData.get(keys.get(i)).get(entry[i]));
      }
      samples.add(new Sample(vector, entry[1]));
    }
  }

  private static Sample generateRandomSample() {
    List<Integer> sampleVector = new ArrayList<>();
    List<String> sampleTags = new ArrayList<>();
    for (String key: keys) {
      Map<String, Integer> attribute = normalizedData.get(key);
      Map.Entry<String, Integer> entry = new ArrayList<>(attribute.entrySet()).get((int) (Math.random() * attribute.size()));
      sampleVector.add(entry.getValue());
      sampleTags.add(entry.getKey());
    }
    String tag = sampleTags.stream().reduce("", (a, x) -> a + x + ";");
    return new Sample(sampleVector, tag);
  }

  public static void runClassifier(KnnClassifer classifer, Sample randomSample, String msg) throws InterruptedException {
    long before = System.currentTimeMillis();
    String ans = classifer.classify(randomSample);
    long after = System.currentTimeMillis();
    System.out.printf("%30s %5d ms %s\n", msg, (after - before), ans);
    classifer.destroy();
  }

  public static void main(String[] args) throws FileNotFoundException, InterruptedException {
    generateSamples(readData());

    Sample randomSample = generateRandomSample();
    String[] tags = randomSample.getTag().split(";");
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    for (int i = 0; i < keys.size(); i++) {
      sb.append(keys.get(i)).append(":").append(tags[i]).append(";");
    }
    sb.append("}");
    System.out.println(sb);

    runClassifier(new Serial(samples, 10), randomSample, "Serial:");
    runClassifier(new FineGrained(samples, 10, 1, false), randomSample, "FineGrainedSerialSort:");
    runClassifier(new FineGrained(samples, 10, 1, true), randomSample, "FineGrainedParallelSort:");
    runClassifier(new CoarseGrained(samples, 10, 1, false), randomSample, "CoarseGrainedSerialSort:");
    runClassifier(new CoarseGrained(samples, 10, 1, true), randomSample, "CoarseGrainedParallelSort:");
  }
}
