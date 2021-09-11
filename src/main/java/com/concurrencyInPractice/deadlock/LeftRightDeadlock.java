package com.concurrencyInPractice.deadlock;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.*;

/*
* This module demonstrates leftRightDeadlock and how to mitigate it. It defines a DeadlockDemo class, which fires 1
* million transaction a finite set of accounts. We use two agents to implement transfer of money - each of which
* implement TransferAgent interface. The first one relies on no optimisation to avoid deadlocks - and quickly runs into
* RejectExecutionException. The second one relies on strict ordering on locks (which fallback to tiebreaker in case it
* is not possible to determine order of locks).
* */


/*
 * Account class
 * Has a unique uuid to identify each account
 * */
@Getter
class Account {
  private final String identifier = UUID.randomUUID().toString();
  @Setter private String accountHolder;
  private double balance = 0;
  private final Date createdAt = new Date();

  Account(String accountHolder) {
    this.accountHolder = accountHolder;
  }

  void debit(double amount) {
    balance -= amount;
  }

  void credit(double amount) {
    balance += amount;
  }
}

class InsufficientFundsException extends Exception {}

interface TransferAgent {
  String getAgentType();

  void transferMoney(Account fromAcc, Account toAcc, double amount)
      throws InsufficientFundsException;
}

class RegularTransferAgent implements TransferAgent {
  private static String type = "regular";

  public void transferMoney(
      @NonNull final Account fromAcc, @NonNull final Account toAcc, final double amount)
      throws InsufficientFundsException {
    synchronized (fromAcc) {
      synchronized (toAcc) {
        if (fromAcc.getBalance() < amount) {
          throw new InsufficientFundsException();
        }
        fromAcc.debit(amount);
        toAcc.credit(amount);
      }
    }
  }

  public String getAgentType() {
    return RegularTransferAgent.type;
  }
}

/*
 * SyncTransferAgent implements TransferAgent and enforces a strict ordering on sequence of locks being acquired.
 * By default it attempts to order resources using System.identityHashCode - which is usually unique for all objects.
 * In cases where it is same (which is assumed here to be very rare - it attempts to lock a global tiebreaker lock first.
 * As it is very rare to have two hashcode be similar, global tiebreaker lock would be rarely acquired.
 * */
class SyncTransferAgent implements TransferAgent {
  private final Object tiebreaker = new Object();
  private static final String type = "sync";

  private void transferMoneyPostLock(Account fromAcc, Account toAcc, double amount)
      throws InsufficientFundsException {
    if (fromAcc.getBalance() < amount) {
      throw new InsufficientFundsException();
    }
    fromAcc.debit(amount);
    toAcc.credit(amount);
  }

  public void transferMoney(
      @NonNull final Account fromAcc, @NonNull final Account toAcc, final double amount)
      throws InsufficientFundsException {
    int fromHashCode = System.identityHashCode(fromAcc);
    int toHashCode = System.identityHashCode(toAcc);

    try {

      if (fromHashCode < toHashCode) {
        synchronized (fromAcc) {
          synchronized (toAcc) {
            this.transferMoneyPostLock(fromAcc, toAcc, amount);
          }
        }
      } else if (toHashCode < fromHashCode) {
        synchronized (toAcc) {
          synchronized (fromAcc) {
            this.transferMoneyPostLock(fromAcc, toAcc, amount);
          }
        }
      } else {
        System.out.println("Locking tiebreaker");
        synchronized (tiebreaker) {
          synchronized (fromAcc) {
            synchronized (toAcc) {
              this.transferMoneyPostLock(fromAcc, toAcc, amount);
            }
          }
        }
      }
    } catch (RuntimeException e) {
      System.out.println("Failed for " + fromAcc + " " + toAcc);
      throw e;
    }
  }

  public String getAgentType() {
    return SyncTransferAgent.type;
  }
}

class DeadLockDemo {
  private final int NUM_ACCOUNTS = 5;

  public void run(TransferAgent agent, Map<String, Integer> collector) throws InterruptedException {
    int NUM_THREADS = 20;
    ExecutorService executor =
        new ThreadPoolExecutor(NUM_THREADS, 100, 0, TimeUnit.SECONDS, new SynchronousQueue<>());
    int NUM_ITERATIONS = 1000000;
    CountDownLatch latch = new CountDownLatch(NUM_ITERATIONS);
    try {
      final Random random = new Random();
      final Account[] accounts = new Account[NUM_ACCOUNTS];
      for (int i = 0; i < NUM_ACCOUNTS; i++) {
        accounts[i] = new Account(String.valueOf('A' + i));
        accounts[i].credit(10000);
      }
      for (int i = 0; i < NUM_ITERATIONS; i++) {
        executor.execute(
            () -> {
              Account fromAcc = accounts[random.nextInt(NUM_ACCOUNTS)];
              Account toAcc = accounts[random.nextInt(NUM_ACCOUNTS)];
              if (fromAcc != toAcc) {
                double amount = random.nextInt(1000);
                try {
                  agent.transferMoney(toAcc, fromAcc, amount);
                  collector.merge("Success", 1, Integer::sum);
                } catch (InsufficientFundsException e) {
                  collector.merge("InsufficientFunds", 1, Integer::sum);
                }
              } else {
                collector.merge("SameAccount", 1, Integer::sum);
              }
              collector.merge("all", 1, Integer::sum);
              latch.countDown();
            });
      }
      latch.await();
    } finally {
      executor.shutdownNow();
    }
  }
}

public class LeftRightDeadlock {
  public static void main(String[] args) throws InterruptedException {
    List<TransferAgent> agents = List.of(new RegularTransferAgent(), new SyncTransferAgent());
    agents.forEach(agent -> System.out.println(agent.getAgentType()));
    for (TransferAgent agent : agents) {
      System.out.println("Processing agent: " + agent.getAgentType());
      Map<String, Integer> results = new ConcurrentHashMap<>();
      boolean success = false;
      try {
        var demo = new DeadLockDemo();
        demo.run(agent, results);
        success = true;
      } catch (RuntimeException | InterruptedException e) {
        e.printStackTrace();
      } finally {
        System.out.println(agent.getAgentType() + " " + success + " " + results);
      }
      System.out.println("Execution over for agent " + agent.getAgentType());
    }
    System.exit(0);
  }
}
