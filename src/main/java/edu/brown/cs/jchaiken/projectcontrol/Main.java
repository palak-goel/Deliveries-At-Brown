package edu.brown.cs.jchaiken.projectcontrol;

import edu.brown.cs.jchaiken.database.Database;
import java.io.File;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * Starts the program and delegates functionality to the REPL and GUI.
 * @author jacksonchaiken
 *
 */
public final class Main {
  private static final int DEFAULT_PORT = 4567;

  /**
   * Runs the program.
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  /**
   * The controller of the program, everything besides the gui is directed by
   * this method.
   *
   */
  private void run() {
    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);
    parser.accepts("db").withRequiredArg().ofType(String.class);
    OptionSet options = parser.parse(args);
    if (options.has("db") && options.valueOf("db") != null) {
      String command = (String) options.valueOf("db");
      if (command.contains(".sqlite3")) {
        // only try to connect on a new database
        if (new File(command).exists() && !command.equals(Database.getUrl())) {
          Database.setUrl(command);
          if (Database.getConnection() != null) {
            System.out.println("db set to " + command);
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
      if (options.has("gui")) {
        //TODO: do gui stuff here
      }
      //TODO: REPL stuff here
    }
  }
}
