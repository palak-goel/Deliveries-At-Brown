package edu.brown.cs.jchaiken.projectcontrol;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.stripe.Stripe;
import com.stripe.model.Charge;

import edu.brown.cs.jchaiken.deliveryobject.User;
import edu.brown.cs.jchaiken.deliveryobject.User.AccountStatus;
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
 * Gui class supports all front end interactions and communications with the
 * server.
 *
 * @author jacksonchaiken
 *
 */
public class Gui {
  private static final Gson GSON = new Gson();
  private static final int MAX_CACHE = 50000;
  private static final int TIMEOUT = 120;
  private static Cache<String, String> iPCache = CacheBuilder
      .newBuilder().maximumSize(MAX_CACHE)
      .expireAfterWrite(TIMEOUT, TimeUnit.MINUTES).build();
  private static final Manager MANAGER = new Manager();

  /**
   * Instantiates a Gui instance on the specified port number.
   *
   * @param port
   *            the port used for hosting.
	 */
  public Gui(int port) {
    this.runSparkServer(port);
    Stripe.apiKey = "sk_test_esEbCKY1kAoxod12YXCJe0IS";
  }

	/**
	 * Stops the server.
	 */
	public void stop() {
		Spark.stop();
	}

	/**
	 * FreeMarker.
	 *
	 * @return freemarker engine.
	 */
	private static FreeMarkerEngine createEngine() {
		Configuration config = new Configuration();
		File templates = new File("src/main/resources/spark/template/freemarker");
		try {
			config.setDirectoryForTemplateLoading(templates);
		} catch (IOException ioe) {
			System.out.printf("ERROR: Unable use %s for template loading.%n", templates);
		}
		return new FreeMarkerEngine(config);
	}

	/**
	 * runSparkServer.
	 *
	 * @param port
	 *            the port to run on.
	 */
  private void runSparkServer(int port) {
    Spark.port(port);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());
    FreeMarkerEngine freeMarker = createEngine();

		// Setup Spark Routes
    Spark.get("/login", new LoginHandler(), freeMarker);
    Spark.post("check-ip", new IpChecker());
    Spark.post("/create-account", new AccountCreator());
    Spark.post("validate-login", new LoginValidator());
  }

	/**
	 * Handler for the login page.
	 *
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

  private static final int TEST_CHARGE = 50;

	/**
	 * Handles account creation and validation.
	 *
	 * @author jacksonchaiken
	 *
	 */
  private static class AccountCreator implements Route {
    @Override
    public Object handle(Request arg0, Response arg1) throws Exception {
      QueryParamsMap qm = arg0.queryMap();
      String name = qm.value("name");
      String email = qm.value("email");
      String stripeToken = qm.value("stripe");
      String cell = qm.value("cell");
      int password = qm.value("password").hashCode();
      Map<String, Object> toServer = new HashMap<>();
      if (User.accountExists(email)) {
        toServer.put("success", false);
        toServer.put("error", "Account already exists");
      } else {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("amount", TEST_CHARGE);
        params.put("currency", "usd");
        params.put("description", "Test charge");
        params.put("source", stripeToken);
        try {
          Charge charge = Charge.create(params);
          if (!charge.getPaid()) {
            toServer.put("error", "stripe error");
          } else {
            charge.refund();
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
        UserBuilder builder = new UserBuilder();
        User user = builder.setId(email).setName(name)
            .setPassword(password).setCell(cell)
            .setPayment(stripeToken).setOrdererRatings(new ArrayList<Double>())
            .setDelivererRatings(new ArrayList<Double>())
            .setStatus(AccountStatus.ACTIVE)
            .setDelivererRatings(new ArrayList<Double>())
            .setOrdererRatings(new ArrayList<Double>())
            .build();
        user.addToDatabase();
        iPCache.put(arg0.ip(), user.getWebId());
        toServer.put("success", true);
        toServer.put("url", user.getWebId());
      }
      return GSON.toJson(toServer);
    }
  }

	/**
	 * Handles login requests to the server.
	 *
	 * @author jacksonchaiken
	 *
	 */
  private static class LoginValidator implements Route {
    @Override
		public Object handle(Request arg0, Response arg1) {
      QueryParamsMap qm = arg0.queryMap();
      String id = qm.value("id");
      String password = qm.value("password");
      Map<String, Object> toServer = new HashMap<>();
      if (User.userValidator(id, password)) {
        toServer.put("result", true);
        toServer.put("url", User.byId(id).getWebId());
        iPCache.put(arg0.ip(), User.byId(id).getWebId());
      } else {
        toServer.put("result", false);
      }
      return GSON.toJson(toServer);
    }
  }

	/**
	 * Checks the IP address to see if it is logged in.
	 *
	 * @author jacksonchaiken
	 *
	 */
  private static class IpChecker implements Route {
    @Override
		public Object handle(Request arg0, Response arg1) {
      Map<String, Object> response = new HashMap<>();
      if (iPCache.getIfPresent(arg0.ip()) == null) {
        response.put("valid", false);
      } else {
        response.put("valid", true);
      }
      return GSON.toJson(response);
    }
  }

	/**
	 * Handler for exceptions.
	 *
	 * @author jacksonchaiken
	 *
	 */
  private static class ExceptionPrinter implements ExceptionHandler {
    @Override
		public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
			// req.session().
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }
}
