package edu.brown.cs.jchaiken.projectcontrol;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import edu.brown.cs.jchaiken.database.Database;
import edu.brown.cs.jchaiken.database.TableBuilder;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * Starts the program and delegates functionality to the REPL and GUI.
 *
 * @author jacksonchaiken
 *
 */
public final class Main {
  private static final int DEFAULT_PORT = 4567;

  /**
   * Runs the program.
   *
   * @param args
   *          Command line arguments.
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private final String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  /**
   * The controller of the program, everything besides the gui is directed by
   * this method.
   *
   */
  private void run() {
    final OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);
    parser.accepts("db").withRequiredArg().ofType(String.class);
    final OptionSet options = parser.parse(args);
    if (options.has("db") && options.valueOf("db") != null) {
      final String command = (String) options.valueOf("db");
      if (command.contains(".sqlite3")) {
        // only try to connect on a new database
        if (new File(command).exists() && !command.equals(Database.getUrl())) {
          Database.setUrl(command);
          if (Database.getConnection() != null) {
            System.out.println("db set to " + command);
            final TableBuilder builder = new TableBuilder();
            builder.buildLocations();
            builder.buildUsers();
            builder.buildOrders();
            builder.buildOrderStatus();
            builder.buildItems();
            builder.buildAccountStatus();
            builder.buildRatingsTable();
          } else {
            System.out.println("ERROR: Cannot connect to database");
          }
        } else if (command.equals(Database.getUrl())) {
          System.out.println("database set to " + command);
        } else {
          System.out.println("No database found");
        }
      } else {
        System.out.println("Database url must end in .sqlite3");
      }
    }
    Gui gui = null;
    if (options.has("gui")) {
      gui = new Gui((int) options.valueOf("port"));
    }
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(System.in, Charset.forName("UTF-8")))) {
      String written;
      while ((written = br.readLine()) != null) {
        final String[] inputs = stringSplit(written);
        if (inputs.length > 0) {
          switch (inputs[0]) {
            // switch statement decides which command
            case "print":
              System.out.println("printing");
              break;
            case "message":
              // Sender sender = new Sender();
              break;
            case "db":
              if (inputs.length < 2 || inputs.length > 2) {
                System.out.println("Invalid Command");
                break;
              }
              if (new File(inputs[1]).exists()
                  && !inputs[1].equals(Database.getUrl())) {
                Database.setUrl(inputs[1]);
                if (Database.getConnection() != null) {
                  final TableBuilder builder = new TableBuilder();
                  builder.buildLocations();
                  builder.buildUsers();
                  builder.buildOrders();
                  builder.buildOrderStatus();
                  builder.buildItems();
                  builder.buildAccountStatus();
                  builder.buildRatingsTable();
                  System.out.println("db set to " + inputs[1]);
                } else {
                  System.out.println("ERROR: Cannot connect to database");
                }
              }
              System.out.println("add database here");
              break;
            default:
              System.out.println("ERROR: Invalid command");
          }
        }
      }
    } catch (final IOException ioe) {
      ioe.printStackTrace();
    }
    // shutdown gui before exit.
    if (gui != null) {
      gui.stop();

    }
  }

  /**
   * Splits the string input into an array to use commands on.
   *
   * @param toSplit:
   *          the String that needs to be split
   * @return a string array that has been split
   */
  private static String[] stringSplit(String toSplit) {
    toSplit += " ";
    final boolean opening = false;
    final ArrayList<String> lis = new ArrayList<>();
    final StringBuilder builds = new StringBuilder();
    return fileRead(toSplit, opening, builds, lis);
  }

  private static String[] fileRead(String toSplit, boolean opening,
      StringBuilder builds, ArrayList<String> lis) {
    for (int i = 0; i < toSplit.length(); i++) {
      final char c = toSplit.charAt(i);
      if (c == '\"' || c == ' ' && !opening) {
        if (c == '\"') {
          opening = !opening;
        }
        if (!opening && builds.length() > 0) {
          lis.add(builds.toString());
          builds.delete(0, builds.length());
        }
      } else {
        builds.append(c);
      }
    }
    return lis.toArray(new String[lis.size()]);
  }
}
