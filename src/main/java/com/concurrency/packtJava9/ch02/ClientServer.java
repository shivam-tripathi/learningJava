package com.concurrency.packt.ch02;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

interface Constants {
  short SERIAL_PORT = 8080;
  short CONCURRENT_PORT = 8000;
}

class WDIDAO {
  static WDIDAO getDAO() {
    return new WDIDAO();
  }

  public String query(String... a) {
    return Arrays.stream(a).reduce("", String::concat);
  }

  public String query(Short c, String... a) {
    return Arrays.stream(a).reduce("", String::concat) + c;
  }
}

@AllArgsConstructor
abstract class Command {
  protected List<String> command;
  public abstract String execute();
}

class QueryCommand extends Command {
  QueryCommand(List<String> command) {
    super(command);
  }
  @Override
  public String execute() {
    WDIDAO dao = WDIDAO.getDAO();
    if (command.size() == 3) {
      return dao.query(command.get(1), command.get(2));
    } else if (command.size() == 4) {
      try {
        return dao.query(Short.parseShort(command.get(3)), command.get(1), command.get(2));
      } catch (Exception e) {
        return "Error;Bad Command: " + e.getMessage();
      }
    } else {
      return "Error;Bad Command";
    }
  }
}

class ReportCommand extends Command {
  public ReportCommand(List<String> command) {
    super(command);
  }

  @Override
  public String execute() {
    return WDIDAO.getDAO().query(command.get(1));
  }
}

class StopCommand extends Command {
  public StopCommand(List<String> command) {
    super(command);
  }

  @Override
  public String execute() {
    return "Server stopped";
  }
}

class ErrorCommand extends Command {
  public ErrorCommand(List<String> command) {
    super(command);
  }

  @Override
  public String execute() {
    return "Unknown command: " + command.get(0);
  }
}

class SerialServer {
  public void run() throws IOException {
    WDIDAO wdidao = WDIDAO.getDAO();
    boolean stopServer = false;
    System.out.println("Initialization complete");

    try (ServerSocket serverSocket = new ServerSocket(Constants.SERIAL_PORT)) {
      do {
        try (
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
          String line = in.readLine();
          Command command;
          List<String> commandData = Arrays.asList(line.split(";"));
          System.out.println("Command: " + commandData.get(0));
          switch (commandData.get(0)) {
            case "q":
              System.out.println("Query");
              command = new QueryCommand(commandData);
              break;
            case "r":
              System.out.println("Report");
              command = new ReportCommand(commandData);
              break;
            case "z":
              System.out.println("Stop");
              command = new StopCommand(commandData);
              stopServer = true;
              break;
            default:
              System.out.println("Error");
              command = new ErrorCommand(commandData);
          }
          String response = command.execute();
          out.println(response);
        } catch (IOException e) {
          e.printStackTrace();
        }
      } while (!stopServer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

class ConcurrentServer {
  @AllArgsConstructor
  @Getter
  @Setter
  static public class CacheItem {
    String command;
    String response;
    Date accessDate;
  }

  static public class ParallelCache {
    public ConcurrentHashMap<String, CacheItem> cache = new ConcurrentHashMap<>();
    Runnable clearCache = () -> {
      cache.values().removeIf(
              item -> item.getAccessDate() != null &&
                      new Date().getTime() - item.getAccessDate().getTime() > MAX_LIVING_TIME_MILLIS
      );
    };

    public Thread thread = new Thread(clearCache);
    public static int MAX_LIVING_TIME_MILLIS = 600_600;

    public ParallelCache() {
      thread.start();
    }

    public void put(String command, String response) {
      CacheItem item = new CacheItem(command, response, null);
      cache.put(command, item);
    }

    public String get(String command) {
      CacheItem item = cache.get(command);
      item.setAccessDate(new Date());
      return item.getResponse();
    }

    public void shutdown() {
      thread.interrupt();
    }

    public int getItemCount() {
      return cache.size();
    }
  }

  private ThreadPoolExecutor executor;
  private ParallelCache cache;
  private ServerSocket serverSocket;
  private volatile boolean stopped;

  public void run() throws IOException {
    WDIDAO wdidao = WDIDAO.getDAO();
    executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    cache = new ParallelCache();
    System.out.println("Initializing complete");

    this.serverSocket = new ServerSocket(Constants.CONCURRENT_PORT);

  }
}

public class ClientServer {
}
