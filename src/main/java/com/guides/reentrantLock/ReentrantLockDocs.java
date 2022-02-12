package com.guides.reentrantLock;

/**
 * A reentrant mutual exclusion Lock with the same basic behavior and semantics as the implicit
 * monitor lock accessed using synchronized methods and statements, but with extended capabilities.
 *
 * The constructor for this class accepts an optional fairness parameter. When set true, under
 * contention, locks favor granting access to the longest-waiting thread. Otherwise this lock does
 * not guarantee any particular access order. Programs using fair locks accessed by many threads may
 * display lower overall throughput (i.e., are slower; often much slower) than those using the
 * default setting, but have smaller variances in times to obtain locks and guarantee lack of
 * starvation. Note however, that fairness of locks does not guarantee fairness of thread
 * scheduling. Thus, one of many threads using a fair lock may obtain it multiple times in
 * succession while other active threads are not progressing and not currently holding the lock.
 * Also note that the untimed tryLock method does not honor the fairness setting. It will succeed if
 * the lock is available even if other threads are waiting.
 * It is recommended practice to always immediately follow a call to lock with a try block, most
 * typically in a before/after construction.
 */

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

class Account {
	String id;
	String holder;

	double balance;

	private ReentrantLock lock = new ReentrantLock();

	Account(String holder) {
		this.id = UUID.randomUUID().toString();
		this.holder = holder;
	}

	void tryLock(long millis) throws Exception {
		assert this.lock.getHoldCount() == 0;
		this.lock.tryLock(millis, TimeUnit.MILLISECONDS);
		if (!this.lock.isHeldByCurrentThread()) {
			throw new Exception("Could not acquire lock");
		}
	}

	void setBalance(double balance) {
		this.balance = balance;
	}

	void transfer(Account to, double amount) throws Exception {
		System.out.printf("Attempting to transfer amount %f from %s to %s\n", amount, this.id, to.id);
		this.tryLock(5000);
		try {
			to.tryLock(5000);
			try {
				if (this.balance < amount) {
					throw new Exception("Insufficient funds");
				}
				this.balance -= amount;
				to.balance += amount;
				System.out.printf("Transferring %f from %s to %s\n", amount, this.holder, to.holder);
				TimeUnit.MILLISECONDS.sleep(1500);
				System.out.printf("Transfer complete: %s->%s amount %f\n", this.holder, to.holder, amount);
			} finally {
				while (to.lock.isHeldByCurrentThread() && to.lock.getHoldCount() != 0)
					to.lock.unlock();
			}
		} finally {
			while (this.lock.isHeldByCurrentThread() && this.lock.getHoldCount() != 0)
				this.lock.unlock();
		}
	}
}

public class ReentrantLockDocs {
	public static void main(String[] args) throws Exception {
		Account a = new Account("A");
		Account b = new Account("B");
		Account c = new Account("C");
		a.setBalance(11000);
		b.setBalance(5000);
		c.setBalance(500);

		List.of(a, b, c).forEach(acc -> System.out.printf("%s %10f\n", acc.holder, acc.balance));

		Thread t1 = new Thread(() -> {
			try {
				a.transfer(b, 500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		Thread t2 = new Thread(() -> {
			try {
				a.transfer(c, 5000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		t1.start();
		t2.start();

		t1.join();
		t2.join();

		List.of(a, b, c).forEach(acc -> System.out.printf("%s %10f\n", acc.holder, acc.balance));
	}
}
