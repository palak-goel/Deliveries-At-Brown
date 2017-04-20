package edu.brown.cs.jchaiken.projectcontrol;

import edu.brown.cs.jchaiken.database.Database;
import edu.brown.cs.jchaiken.database.TableBuilder;
import freemarker.template.Configuration;

import java.io.BufferedReader;
import java.io.File;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.ExceptionHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

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
            TableBuilder builder = new TableBuilder();
            builder.buildLocations();
            builder.buildUsers();
            builder.buildOrders();
            builder.buildOrderStatus();
            builder.buildItems();
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
    if (options.has("gui")) {
      //TODO: do gui stuff here
    }
    try (BufferedReader br = new BufferedReader(
	        new InputStreamReader(System.in))) {
	      String written;
	      while ((written = br.readLine()) != null) {
	        String[] inputs = stringSplit(written);
	        if (inputs.length > 0) {
	          switch (inputs[0]) { // switch statement decides which command 
	          case "print":
	        	  System.out.println("printing");
	        	  break;
	          case "message":
	        	  //Sender sender = new Sender();
	          case "db":
	        	  if(inputs.length < 2 || inputs.length > 2){
	        		  System.out.println("Invalid Command");
	        		  break;
	        	  }
	        	  if (new File(inputs[1]).exists() && !inputs[1].equals(Database.getUrl())) {
	                Database.setUrl(inputs[1]);
	                if (Database.getConnection() != null) {
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

	    } catch (IOException ioe) {
	      ioe.printStackTrace();
	    }
	  }
  
    
  /**
	 * Splits the string input into an array to 
	 * use commands on 	  
	 * @param toSplit: the String that needs to be split 
	 * @return a string array that has been split
	 */
		  private static String[] stringSplit(String toSplit) {
			    toSplit += " ";
			    boolean opening = false;
			    ArrayList<String> lis = new ArrayList<String>();
			    StringBuilder builds = new StringBuilder();
			    return fileRead(toSplit, opening, builds, lis);
			  }

			  private static String[] fileRead(String toSplit, boolean opening,
			      StringBuilder builds, ArrayList<String> lis) {
			    for (int i = 0; i < toSplit.length(); i++) {
			      char c = toSplit.charAt(i);

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
	/**
	 * FreeMarker
	 * @return
	 */
			  private static FreeMarkerEngine createEngine() {
				    Configuration config = new Configuration();
				    File templates = new File("src/main/resources/spark/template/freemarker");
				    try {
				      config.setDirectoryForTemplateLoading(templates);
				    } catch (IOException ioe) {
				      System.out.printf("ERROR: Unable use %s for template loading.%n",
				          templates);
				      System.exit(1);
				    }
				    return new FreeMarkerEngine(config);
				  }
	/**
	 * runSparkServer
	 * @param port
	 */
				  private void runSparkServer(int port) {
				    Spark.port(port);
				    Spark.externalStaticFileLocation("src/main/resources/static");
				    Spark.exception(Exception.class, new ExceptionPrinter());

				    FreeMarkerEngine freeMarker = createEngine();

				    // Setup Spark Routes	
				    Spark.get("/login", new LoginHandler(), freeMarker);
				    Spark.get("/home", new HomeHandler(), freeMarker);
				  }
				  
				  private static class LoginHandler implements TemplateViewRoute {
					    @Override
					    public ModelAndView handle(Request req, Response res) {
					      Map<String, Object> variables = ImmutableMap.of("title", "Login"
					       );

					      return new ModelAndView(variables, "Login.ftl");
					    }
					  }
				  private static class HomeHandler implements TemplateViewRoute {
					    @Override
					    public ModelAndView handle(Request req, Response res) {
					      Map<String, Object> variables = ImmutableMap.of("title", "Home"
					       );

					      return new ModelAndView(variables, "Home.ftl");
					    }
					  }
				  private static class ExceptionPrinter implements ExceptionHandler {
					    @Override
					    public void handle(Exception e, Request req, Response res) {
					      res.status(500);
					      StringWriter stacktrace = new StringWriter();
					      try (PrintWriter pw = new PrintWriter(stacktrace)) {
					        pw.println("<pre>");
					        e.printStackTrace(pw);
					        pw.println("</pre>");
					      }
					      res.body(stacktrace.toString());
					    }
					  }
  
  
}

