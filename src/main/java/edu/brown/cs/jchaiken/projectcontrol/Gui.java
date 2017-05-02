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
import com.stripe.Stripe;
import com.stripe.model.Charge;

import edu.brown.cs.jchaiken.deliveryobject.User;
import edu.brown.cs.jchaiken.deliveryobject.User.AccountStatus;
import edu.brown.cs.jchaiken.deliveryobject.User.UserBuilder;
import edu.brown.cs.mhasan3.messaging.Sender;
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
	private static final Manager MANAGER = new Manager();
	private static Sender sender = new Sender();

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
		Spark.webSocket("/deliverysocket", OrderWebSocket.class);
		// Setup Spark Routes
		Spark.get("/login", new LoginHandler(""), freeMarker);
		Spark.post("/create-account", new AccountCreator());
		Spark.post("validate-login", new LoginValidator());
		Spark.post("/submit-request", new Manager.OrderMaker());
		Spark.get("/forgot-password", new PasswordReset(), freeMarker);
		// Palak's Stuff
		Spark.get("/request", (request, response) -> {
			String webId = request.session().attribute("webId");
			if (webId == null || User.byWebId(webId) == null) {
				response.redirect("/login?from=request");
				return new LoginHandler("request").handle(request, response);
			}
			Map<String, Object> variables = ImmutableMap.of("title", "Request");
			return freeMarker.render(new ModelAndView(variables, "request.ftl"));
		});
		Spark.get("/requesting", (request, response) -> {
			String webId = request.session().attribute("webId");
			if (webId == null || User.byWebId(webId) == null) {
				response.redirect("/login?from=requesting");
				return new LoginHandler("requesting").handle(request, response);
			}
			Map<String, Object> variables = ImmutableMap.of("title", "Request");
			return freeMarker.render(new ModelAndView(variables, "requesting.ftl"));
		});
		Spark.get("/requested", (request, response) -> {
			String webId = request.session().attribute("webId");
			if (webId == null || User.byWebId(webId) == null) {
				response.redirect("/login?from=requested");
				return new LoginHandler("requested").handle(request, response);
			}
			Map<String, Object> variables = ImmutableMap.of("title", "Request");
			return freeMarker.render(new ModelAndView(variables, "requested.ftl"));
		});
		Spark.get("/deliver", (request, response) -> {
			String webId = request.session().attribute("webId");
			if (webId == null || User.byWebId(webId) == null) {
				response.redirect("/login?from=deliver");
				return new LoginHandler("deliver").handle(request, response);
			}
			Map<String, Object> variables = ImmutableMap.of("title", "Request");
			return freeMarker.render(new ModelAndView(variables, "deliver.ftl"));
		});
		Spark.get("/delivering", (request, response) -> {
			String webId = request.session().attribute("webId");
			if (webId == null || User.byWebId(webId) == null) {
				response.redirect("/login?from=delivering");
				return new LoginHandler("delivering").handle(request, response);
			}
			Map<String, Object> variables = ImmutableMap.of("title", "Request");
			return freeMarker.render(new ModelAndView(variables, "delivering.ftl"));
		});
		Spark.get("/delivered", (request, response) -> {
			String webId = request.session().attribute("webId");
			if (webId == null || User.byWebId(webId) == null) {
				response.redirect("/login?from=delivered");
				return new LoginHandler("delivered").handle(request, response);
			}
			Map<String, Object> variables = ImmutableMap.of("title", "Request");
			return freeMarker.render(new ModelAndView(variables, "delivered.ftl"));
		});
		Spark.get("/map", (request, response) -> {
			String webId = request.session().attribute("webId");
			if (webId == null || User.byWebId(webId) == null) {
				response.redirect("/login?from=map");
				return new LoginHandler("map").handle(request, response);
			}
			Map<String, Object> variables = ImmutableMap.of("title", "Request");
			return freeMarker.render(new ModelAndView(variables, "maps.ftl"));
		});
		Spark.get("/profile", (request, response) -> {
			String webId = request.session().attribute("webId");
			if (webId == null || User.byWebId(webId) == null) {
				response.redirect("/login?from=profile");
				return new LoginHandler("profile").handle(request, response);
			}
			Map<String, Object> variables = ImmutableMap.of("title", "Profile");
			return freeMarker.render(new ModelAndView(variables, "profile.ftl"));
		});
	}

	/**
	 * Handler for the login page.
	 *
	 * @author jacksonchaiken
	 *
	 */
	private static class LoginHandler implements TemplateViewRoute {
		private String from = "";

		LoginHandler(String redirect) {
			if (redirect == null) {
				throw new IllegalArgumentException("redirect is null");
			}
			from = redirect;
		}

		@Override
		public ModelAndView handle(Request req, Response res) {
			Map<String, Object> variables = new HashMap<>();
			variables.put("title", "Login");
			variables.put("from", from);
			System.out.println(from);
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
				User user = builder.setId(email).setName(name).setPassword(password).setCell(cell)
						.setPayment(stripeToken).setOrdererRatings(new ArrayList<Double>())
						.setDelivererRatings(new ArrayList<Double>()).setStatus(AccountStatus.ACTIVE)
						.setDelivererRatings(new ArrayList<Double>()).setOrdererRatings(new ArrayList<Double>())
						.build();
				user.addToDatabase();
				arg0.session().attribute("webId", user.getWebId());
				Manager.saveSession(arg0.session().id(), arg0.session());
				toServer.put("success", true);
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
				User user = User.byId(id);
				arg0.session().attribute("webId", user.getWebId());
				Manager.saveSession(arg0.session().id(), arg0.session());
			} else {
				toServer.put("result", false);
			}
			return GSON.toJson(toServer);
		}
	}

	private static class PasswordReset implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request arg0, Response arg1) throws Exception {
      String webId = arg0.session().attribute("webId");
      User user;
      Map<String, Object> toServer = new HashMap<>();
      if (webId != null && (user = User.byWebId(webId)) != null) {
        toServer.put("sent", true);
      }
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
