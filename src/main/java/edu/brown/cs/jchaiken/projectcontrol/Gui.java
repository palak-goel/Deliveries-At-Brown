package edu.brown.cs.jchaiken.projectcontrol;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.jchaiken.deliveryobject.User;
import edu.brown.cs.jchaiken.deliveryobject.User.UserBuilder;
import freemarker.template.Configuration;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * Gui class supports all front end interactions and communications with
 * the server.
 * @author jacksonchaiken
 *
 */
public class Gui {
  private static final Gson GSON = new Gson();

  /**
   * Instantiates a Gui instance on the specified port number.
   * @param port the port used for hosting.
   */
  public Gui(int port) {
    this.runSparkServer(port);
  }

  /**
   * Stops the server.
   */
  public void stop() {
    Spark.stop();
  }

  /**
   * FreeMarker.
   * @return freemarker engine.
   */
  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
    }
    return new FreeMarkerEngine(config);
  }

  /**
   * runSparkServer.
   * @param port the port to run on.
   */
  private void runSparkServer(int port) {
    Spark.port(port);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());
    FreeMarkerEngine freeMarker = createEngine();

    // Setup Spark Routes
    Spark.get("/login", new LoginHandler(), freeMarker);
    Spark.get("/home/:id", (request, response) -> {
      Map<String, Object> variables = ImmutableMap.of("title", "Home");
      /*
       * 
       * 
       * 
       * 
       * 
       * 
       * TODO: do home stuff here
       * 
       * 
       * 
       * 
       */
      return freeMarker.render(new ModelAndView(variables, "home.ftl"));
    });
    Spark.post("/create-account", new AccountCreator());
    Spark.post("validate-login", new LoginValidator());
  }

  /**
   * Handler for the login page.
   * @author jacksonchaiken
   *
   */
  private static class LoginHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Login");
      return new ModelAndView(variables, "login.ftl");
    }
  }

  /**
   * Handles account creation and validation.
   * @author jacksonchaiken
   *
   */
  private static class AccountCreator implements Route {
    @Override
    public Object handle(Request arg0, Response arg1) throws Exception {
      QueryParamsMap qm = arg0.queryMap();
      String name = qm.value("name");
      String email = qm.value("email");
      String stripe = qm.value("stripe");
      String cell = qm.value("cell");
      int password = qm.value("password").hashCode();
      Map<String, Object> toServer = new HashMap<>();
      if (User.accountExists(email)) {
        toServer.put("success", false);
        toServer.put("error", "Account already exists");
      } else {
        UserBuilder builder = new UserBuilder();
        User user = builder.setId(email)
            .setName(name)
            .setPassword(password)
            .setCell(cell)
            .setPayment(stripe)
            .setDelivererRatings(new ArrayList<Double>())
            .setOrdererRatings(new ArrayList<Double>())
            .build();
        user.addToDatabase();
        toServer.put("success", true);
      }
      return GSON.toJson(toServer);
    }
  }

  private static class LoginValidator implements Route {

    @Override
    public Object handle(Request arg0, Response arg1) throws Exception {
      QueryParamsMap qm = arg0.queryMap();
      String id = qm.value("id");
      String password = qm.value("password");
      Map<String, Object> toServer = new HashMap<>();
      if (User.userValidator(id, password)) {
        toServer.put("result", true);
      } else {
        toServer.put("result", false);
      }
      return GSON.toJson(toServer);
    }
  }

  /**
   * Handler for exceptions.
   * @author jacksonchaiken
   *
   */
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
