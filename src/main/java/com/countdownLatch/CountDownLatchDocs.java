package com.countdownLatch;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * - A synchronization aid that allows one or more threads to wait until the set
 * of operations being performed in other threads have been completed. - A
 * countdown latch is initialized with a count - Method `await` blocks until the
 * current count reaches zero due to due to the invocations of the `countDown()`
 * method - after which all waiting threads are released and any subsequent
 * invocation of `await()` returns immediately. This is one shot phenomenon -
 * the count cannot be reset. If we need to reset the count, we need to consider
 * something like a CyclicBarrier. - A CountDownLatch initialized with a count
 * of one serves as a simple on/off latch, or gate: all threads invoking await
 * wait at the gate until it is opened by a thread invoking countDown(). - A
 * CountDownLatch initialized to N can be used to make one thread wait until N
 * threads have completed some action, or some action has been completed N
 * times. - A useful property of a CountDownLatch is that it doesn't require
 * that threads calling countDown wait for the count to reach zero before
 * proceeding, it simply prevents any thread from proceeding past an await until
 * all threads could pass. Basically, countDown is not blocking - but await is.
 *
 */

public class CountDownLatchDocs {
	public static void main(String[] args) throws Exception {
		System.out.println("Running two tasks:\n=====\nTask1\n=====");
		System.out.println("Calculate and store squares of first n natural numbers\n-----");
		int N = 10;
		CountDownLatch startSignal = new CountDownLatch(1);
		CountDownLatch doneSignal = new CountDownLatch(N);
		Map<Integer, Integer> results = new ConcurrentHashMap<>();

		for (int i = 0; i < N; i++) {
			new Thread(new Worker(startSignal, doneSignal, i, results)).start();
		}
		preRun(); // Execution doesn't start until we trigger it
		startSignal.countDown(); // Signal start
		duringRun(); // Main thread's execution doesn't stop while other threads are running
		doneSignal.await(); // Wait for all threads to complete
		postRun(results); // After all threads have finished, we print the results

		System.out.println("\n=====\nTask2\n=====");
		System.out.println("Merge sort an array using divide and conquer\n-----");
		CountDownLatch sortLatch = new CountDownLatch(1); // used to wait for this thread to finish
		int[] arr = new int[] { 4, 3, -1, 2, 3, 10, -23, 19 }; // input array
		System.out.println("Input Array: " + Arrays.toString(arr));
		new Thread(new MergeSortWorker(sortLatch, arr, 0, arr.length - 1)).start(); // start the sorting
		sortLatch.await(); // await sorting to finish
		System.out.println("Final Array: " + Arrays.toString(arr)); // Print the final array (inplace)
	}

	static void preRun() {
		System.out.println("Before we run the tasks");
	}

	static void duringRun() {
		System.out.println("During the run");
	}

	static void postRun(Map<Integer, Integer> results) {
		System.out.println("After we have run the tasks: " + results);
	}
}

class Worker implements Runnable {
	private final CountDownLatch startSignal;
	private final CountDownLatch doneSignal;
	Map<Integer, Integer> collector;
	int id;

	Worker(CountDownLatch startSignal, CountDownLatch doneSignal, int i, Map<Integer, Integer> collector) {
		this.startSignal = startSignal;
		this.doneSignal = doneSignal;
		this.id = i;
		this.collector = collector;
	}

	public void run() {
		try {
			startSignal.await();
			doWork();
			doneSignal.countDown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void doWork() throws Exception {
		collector.put(this.id, this.id * this.id);
	}
}

class MergeSortWorker implements Runnable {
	private final CountDownLatch doneSignal;
	int[] arr;
	int begin;
	int end;

	MergeSortWorker(CountDownLatch doneSignal, int[] arr, int begin, int end) {
		this.doneSignal = doneSignal;
		this.arr = arr;
		this.begin = begin;
		this.end = end;
	}

	public void sort() throws Exception {
		try {
			if (begin < end) {
				int mid = begin + (end - begin) / 2;

				CountDownLatch subTasks = new CountDownLatch(2);
				new Thread(new MergeSortWorker(subTasks, arr, begin, mid)).start();
				new Thread(new MergeSortWorker(subTasks, arr, mid + 1, end)).start();
				subTasks.await();

				int[] tmp = new int[end - begin + 1];
				int s1 = begin, s2 = mid+1, pos = 0;
				while (s1 <= mid && s2 <= end) tmp[pos++] = arr[s1] < arr[s2] ? arr[s1++] : arr[s2++];
				while (s1 <= mid) tmp[pos++] = arr[s1++];
				while (s2 <= end) tmp[pos++] = arr[s2++];

				// Copy back in place
				pos = 0;
				for (int i = begin; i <= end; i++) {
					arr[i] = tmp[pos++];
				}
			}
		} catch(Exception ex) {
			System.out.println(ex.getMessage());
		} finally {
			this.doneSignal.countDown();
		}
	}

	public void run() {
		try {
			sort();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
