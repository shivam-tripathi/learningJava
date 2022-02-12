package com.guides.cyclicBarrier;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A synchronization aid that allows a set of threads to all wait for one other
 * to reach a common
 * barrier point. CyclicBarrier is useful when a fixed party of threads that
 * must occasionally wait
 * for each other. The barrier is called cyclic, because it can be re-used after
 * the threads have
 * been released.
 *
 * A cyclic barrier supports an optional Runnable command that is run once per
 * barrier point, after
 * the last thread in the party arrives, but before any threads are released.
 * This barrier action is
 * useful for updating shared-state before any of the parties continue.
 */

public class CyclicBarrierDocs {
	static class Solver {
		private final int count;
		private final int threadCount = 10;
		private final int batchSize = 10;
		private int processed = 0;
		private CyclicBarrier barrier;

		/** Create next batch for processing by the cyclic barrier */
		List<List<Integer>> getNextBatch() {
			List<List<Integer>> batches = new ArrayList<>();

			/** there must be a batch for every thread to process */
			for (int i = 0; i < this.threadCount; i++) {
				batches.add(new ArrayList<>());
			}

			int curBatch = 0;

			/**
			 * Currently we are filling first batch first and then second (dfs) - this can
			 * cause in the final iteration some of the final batches to be empty. We can
			 * more evenly distribute the load by doing a bfs.
			 **/
			while (this.processed < count && curBatch < this.threadCount) {
				batches.get(curBatch).add(this.processed++);
				if (batches.get(curBatch).size() >= this.batchSize) {
					curBatch++;
				}
			}

			return batches;
		}

		Solver(int count) {
			this.count = count;

		}

		void solve() {
			long startTime = System.currentTimeMillis();

			final AtomicInteger total = new AtomicInteger(0);

			Queue<Integer> results = new ConcurrentLinkedQueue<>();
			// One more than total number of threads so that we can wait for it in the main
			// thread as well
			this.barrier = new CyclicBarrier(this.threadCount + 1, () -> {
				while (results.size() > 0) {
					total.addAndGet(results.poll());
				}
			});

			while (this.processed < this.count) {
				List<List<Integer>> batches = getNextBatch();
				for (int i = 0; i < this.threadCount; i++) {
					List<Integer> batch = batches.get(i);
					new Thread(() -> {
						// long taskStart = System.nanoTime();
						int sum = 0;
						for (int item : batch) {
							sum += item;
						}
						// long taskEnd = System.nanoTime();
						// System.out.printf("task complete, sum = %4d finished in %10d ns\n", sum,
						// taskEnd - taskStart);
						results.add(sum);
						try {
							barrier.await();
						} catch (InterruptedException | BrokenBarrierException e) {
							e.printStackTrace();
						}
					}).start();
				}
				try {
					barrier.await(1L, TimeUnit.MINUTES);
				} catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
					e.printStackTrace();
				}
			}

			long endTime = System.currentTimeMillis();
			System.out.printf("All done %d in %f seconds.\n", count, (endTime - startTime) / 1000D);
			System.out.println(total);
		}
	}

	public static void main(String[] args) {
		Solver solver = new Solver((int) 1e6);
		solver.solve();
	}
}
