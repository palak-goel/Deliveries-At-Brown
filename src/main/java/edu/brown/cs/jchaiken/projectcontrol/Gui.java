package edu.brown.cs.jchaiken.projectcontrol;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.stripe.Stripe;
import com.stripe.model.Charge;
import com.stripe.model.Customer;

import edu.brown.cs.jchaiken.deliveryobject.Order;
import edu.brown.cs.jchaiken.deliveryobject.User;
import edu.brown.cs.jchaiken.deliveryobject.User.AccountStatus;
import edu.brown.cs.jchaiken.deliveryobject.User.UserBuilder;
import edu.brown.cs.mhasan3.messaging.Sender;
import edu.brown.cs.mhasan3.rankers.Ranker;
import edu.brown.cs.mhasan3.rankers.Suggestor;
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
	private static Sender sender = new Sender("");

	/**
	 * Instantiates a Gui instance on the specified port number.
	 *
	 * @param port
	 *            the port used for hosting.
	 */
	public Gui(int port) {
		setStripe();
		System.out.println("STARTING PROGRAM");
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
	 *
	 * @return freemarker engine.
	 */
	private static FreeMarkerEngine createEngine() {
		final Configuration config = new Configuration();
		final File templates = new File("src/main/resources/spark/template/freemarker");
		try {
			config.setDirectoryForTemplateLoading(templates);
		} catch (final IOException ioe) {
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
		final FreeMarkerEngine freeMarker = createEngine();
		Spark.webSocket("/deliverysocket", OrderWebSocket.class);
		// Setup Spark Routes
		Spark.get("/", (request, response) -> {
			if (request.session().attribute("webId") != null) {
				final Map<String, Object> variables = new HashMap<>();
				variables.put("title", "profile");
				response.redirect("/profile");
				return freeMarker.render(new ModelAndView(variables, "profile.ftl"));
			} else {
				response.redirect("/login");
				return new LoginHandler("");
			}
		});
		Spark.get("/login", new LoginHandler(""), freeMarker);
		Spark.post("/create-account", new AccountCreator());
		Spark.post("validate-login", new LoginValidator());
		Spark.post("complete-order", new Manager.CompletedOrder());
		Spark.post("/submit-request", new Manager.OrderMaker());
		Spark.get("/forgot-password", new PasswordReset(), freeMarker);
		Spark.post("/pending-orders", new Manager.PendingOrders());
		Spark.post("/rating", new Manager.Rating());
		Spark.post("/send-code", (request, response) -> {
			return sendCode(request);
		});

		Spark.post("/profile", (req, response) -> {
			return profile(req);
		});
		Spark.post("/sendText", (req, response) -> {
			sendText(req);
			return new Object();
		});
		Spark.post("/submit-Ordering", (request, response) -> {
			submitOrdering(request);
			return "";
		});
		Spark.post("/submit-Preferences", (request, response) -> {
			submitPreferences(request);
			return "";
		});
		Spark.post("/validate-code", (request, response) -> {
			return validateCode(request);
		});
		Spark.post("/user-rating", (request, response) -> {
			return userRating(request);
		});
		Spark.post("/reset-password", (request, response) -> {
			return resetPassword(request);
		});
		Spark.post("/is-active", (request, response) -> {
			final String jid = request.session().id();
			System.out.println(jid);
			return GSON.toJson(ImmutableMap.of("isActive", Manager.isActiveUser(jid)));
		});
		Spark.get("/request", new RequestHandler(), freeMarker);
		Spark.get("/requesting", (request, response) -> {
			final String webId = request.session().attribute("webId");
			if (webId == null || User.byWebId(webId) == null) {
				response.redirect("/login?from=requesting");
				return new LoginHandler("requesting").handle(request, response);
			}
			final Map<String, Object> variables = ImmutableMap.of("title", "Request");
			return freeMarker.render(new ModelAndView(variables, "requesting.ftl"));
		});
		Spark.get("/delivering", (request, response) -> {
			final String webId = request.session().attribute("webId");
			if (webId == null || User.byWebId(webId) == null) {
				response.redirect("/login?from=delivering");
				return new LoginHandler("delivering").handle(request, response);
			}
			final Map<String, Object> variables = ImmutableMap.of("title", "Delivering");
			return freeMarker.render(new ModelAndView(variables, "delivering.ftl"));
		});
		Spark.get("/deliverycompleted", (request, response) -> {
			final String webId = request.session().attribute("webId");
			if (webId == null || User.byWebId(webId) == null) {
				response.redirect("/login?from=deliverycompleted");
				return new LoginHandler("deliverycompleted").handle(request, response);
			}
			final Map<String, Object> variables = ImmutableMap.of("title", "Delivering");
			return freeMarker.render(new ModelAndView(variables, "deliverycompleted.ftl"));
		});
		Spark.get("/ordercompleted", new OrderCompleted(), freeMarker);
		Spark.get("/requested", (request, response) -> {
			final String webId = request.session().attribute("webId");
			if (webId == null || User.byWebId(webId) == null) {
				response.redirect("/login?from=requested");
				return new LoginHandler("requested").handle(request, response);
			}
			final Map<String, Object> variables = ImmutableMap.of("title", "Request");
			return freeMarker.render(new ModelAndView(variables, "requested.ftl"));
		});

		Spark.get("/deliver", (request, response) -> {
			final String webId = request.session().attribute("webId");
			if (webId == null || User.byWebId(webId) == null) {
				response.redirect("/login?from=deliver");
				return new LoginHandler("deliver").handle(request, response);
			}
			final Map<String, Object> variables = ImmutableMap.of("title", "Deliver");
			return freeMarker.render(new ModelAndView(variables, "deliver.ftl"));
		});
		Spark.get("/delivering", (request, response) -> {
			final String webId = request.session().attribute("webId");
			if (webId == null || User.byWebId(webId) == null) {
				response.redirect("/login?from=delivering");
				return new LoginHandler("delivering").handle(request, response);
			}
			final Map<String, Object> variables = ImmutableMap.of("title", "Deliver");
			return freeMarker.render(new ModelAndView(variables, "delivering.ftl"));
		});
		Spark.get("/delivered", (request, response) -> {
			final String webId = request.session().attribute("webId");
			if (webId == null || User.byWebId(webId) == null) {
				response.redirect("/login?from=delivered");
				return new LoginHandler("delivered").handle(request, response);
			}
			final Map<String, Object> variables = ImmutableMap.of("title", "Deliver");
			return freeMarker.render(new ModelAndView(variables, "delivered.ftl"));
		});
		Spark.post("/delete-order", (request, response) -> {
			deleteOrder(request);
			return GSON.toJson("");
		});
		Spark.post("/suggest", (request, response) -> {
			return suggest(request);
		});
		Spark.post("/order-history", (request, response) -> {
			return orderHistory(request);
		});
		Spark.post("/logout", (request, response) -> {
			request.session().attribute("webId", null);
			return GSON.toJson("");
		});
		Spark.get("/profile", new ProfileHandler(), freeMarker);
	}

	private static void sendText(Request request) {
		final String receiver = request.queryMap().value("number");
		final String content = request.queryMap().value("content");
		sender.updateReceiver(receiver);
		sender.customMessage(content);
		sender.sendMessage();
	}

	private static Object userRating(Request request) {
		final QueryParamsMap qm = request.queryMap();
		final Order o = Order.byId(qm.value("id"));
		final double rtg = o.getOrderer().getOrdererRating();
		return GSON.toJson(ImmutableMap.of("rating", rtg));
	}

	private static Object profile(Request req) {
		final User u = User.byWebId(req.session().attribute("webId"));
		final Map<String, Object> msg = new HashMap<>();
		msg.put("name", u.getName());
		msg.put("phoneNumber", u.getCell());
		msg.put("email", u.getId());
		msg.put("deliveryRating", u.getDelivererRating());
		msg.put("requestRating", u.getOrdererRating());
		return GSON.toJson(msg);
	}

	private static void submitOrdering(Request request) {
		try {
			final QueryParamsMap qm = request.queryMap();
			final User u = User.byWebId(request.session().attribute("webId"));
			final String opt = qm.value("option");
			final Ranker r = new Ranker(MANAGER, u);
			if (opt.equals("tip")) {
				OrderWebSocket.sendOrders(request.session().id(), r.orderByPrice());
			} else if (opt.equals("distance")) {
				OrderWebSocket.sendOrders(request.session().id(), r.orderByDistance());
			} else {
				OrderWebSocket.sendOrders(request.session().id(), r.orderByTime());
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handler for the order completed page.
	 *
	 */
	private static class OrderCompleted implements TemplateViewRoute {
		@Override
		public ModelAndView handle(Request arg0, Response arg1) throws Exception {
			final String webId = arg0.session().attribute("webId");
			if (webId == null || User.byWebId(webId) == null) {
				arg1.redirect("/login?from=ordercompleted");
				return new LoginHandler("ordercompleted").handle(arg0, arg1);
			}
			final Map<String, Object> variables = ImmutableMap.of("title", "Delivering");
			return new ModelAndView(variables, "ordercompleted.ftl");
		}
	}

	/**
	 * Handler for the request page.
	 *
	 */
	private static class RequestHandler implements TemplateViewRoute {
		@Override
		public ModelAndView handle(Request arg0, Response arg1) throws Exception {
			final String webId = arg0.session().attribute("webId");
			if (webId == null || User.byWebId(webId) == null) {
				arg1.redirect("/login?from=request");
				return new LoginHandler("request").handle(arg0, arg1);
			}
			final Map<String, Object> variables = ImmutableMap.of("title", "Request");
			return new ModelAndView(variables, "request.ftl");
		}
	}

	private static Object resetPassword(Request request) {
		final String email = request.queryMap().value("email");
		final String newPass = request.queryMap().value("password");
		if (!checkSql(email)) {
			return GSON.toJson(ImmutableMap.of("error", "sql"));
		}
		final boolean status = User.newPassword(email, newPass);
		final Map<String, Object> toServer = new HashMap<>();
		toServer.put("status", status);
		toServer.put("error", "");
		return GSON.toJson(toServer);
	}

	private static Object orderHistory(Request request) {
		System.out.println("here");
		final String jid = request.session().id();
		final User u = User.byWebId(Manager.getSession(jid).attribute("webId"));
		final Map<String, Object> res = new HashMap<>();
		System.out.println("here");
		for (final Order o : u.pastDeliveries()) {
			o.getDropoffLocation().getName();
			o.getPickupLocation().getName();
		}
		for (final Order o : u.pastOrders()) {
			o.getDropoffLocation().getName();
			o.getPickupLocation().getName();
		}
		System.out.println("here");
		List<Order> dRev = new ArrayList<>(u.pastDeliveries());
		List<Order> oRev = new ArrayList<>(u.pastOrders());
		Collections.reverse(dRev);
		Collections.reverse(oRev);
		res.put("deliver", dRev);
		res.put("order", oRev);
		return GSON.toJson(res);
	}

	private static Object suggest(Request request) {
		final String jid = request.session().id();
		final User u = User.byWebId(Manager.getSession(jid).attribute("webId"));
		final Suggestor s = new Suggestor(u);
		final Map<String, Object> m = new HashMap<>();
		m.put("pickup", s.suggestPickup());
		m.put("dropoff", s.suggestDropoff());
		m.put("items", s.suggestItem());
		return GSON.toJson(m);
	}

	private static void deleteOrder(Request request) {
		final QueryParamsMap qm = request.queryMap();
		final Order o = Order.byId(qm.value("id"));
		OrderWebSocket.sendRemoveOrder(o);
		final Sender s = new Sender(o.getOrderer().getCell());
		s.updateMessage("cancel", o);
		s.sendMessage();
	}

	private static Object validateCode(Request request) {
		final String cell = request.queryMap().value("cell");
		final String code = request.queryMap().value("code");
		Map<String, Object> toServer;
		if (sentCodes.containsKey(cell) && sentCodes.get(cell).equals(code)) {
			toServer = ImmutableMap.of("goodCode", true);
		} else {
			toServer = ImmutableMap.of("goodCode", false);
			return GSON.toJson(toServer);
		}
		return GSON.toJson(toServer);
	}

	private static Object sendCode(Request request) {
		final String cell = request.queryMap().value("cell");
		final String email = request.queryMap().value("email");
		if (User.resetCombination(email, cell)) {
			final String code = sender.resetPassword(cell);
			final Map<String, Object> toServer = ImmutableMap.of("sent", true, "error", "");
			sentCodes.put(cell, code);
			return GSON.toJson(toServer);
		} else {
			return GSON.toJson(ImmutableMap.of("error", "bad combo"));
		}
	}

	private static void submitPreferences(Request request) {
		try {
			final QueryParamsMap qm = request.queryMap();
			final User u = User.byWebId(request.session().attribute("webId"));
			final double price = Double.parseDouble(qm.value("price"));
			final double distance = Double.parseDouble(qm.value("distance"));
			final double time = Double.parseDouble(qm.value("time"));
			u.addDeliveryPreferences(distance, price, time);
			final Ranker r = new Ranker(MANAGER, u);
			OrderWebSocket.sendOrders(request.session().id(), r.rank());
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handler for the login page.
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
			final Map<String, Object> variables = new HashMap<>();
			variables.put("title", "Login");
			variables.put("from", from);
			System.out.println(from);
			return new ModelAndView(variables, "login.ftl");
		}
	}

	private static final int TEST_CHARGE = 50;

	/**
	 * Handles login requests to the server.
	 *
	 * @author jacksonchaiken
	 *
	 */
	private static class LoginValidator implements Route {
		@Override
		public Object handle(Request arg0, Response arg1) {
			final Map<String, Object> toServer = new HashMap<>();
			try {
				final QueryParamsMap qm = arg0.queryMap();
				System.out.println("validating");
				final String id = qm.value("id");
				final String password = qm.value("password");
				if (!checkSql(id)) {
					toServer.put("result", false);
					return GSON.toJson(toServer);
				}
				if (User.userValidator(id, password)) {
					toServer.put("result", true);
					final User user = User.byId(id);
					arg0.session().attribute("webId", user.getWebId());
					Manager.saveSession(arg0.session().id(), arg0.session());
				} else {
					toServer.put("result", false);
				}
			} catch (final Exception e) {
				e.printStackTrace();
				toServer.put("result", false);
			}
			return GSON.toJson(toServer);
		}
	}

	private static ConcurrentMap<String, String> sentCodes = new ConcurrentHashMap<>();

	/**
	 * Handles account creation and validation.
	 *
	 * @author jacksonchaiken
	 *
	 */
	private static class AccountCreator implements Route {
		@Override
		public Object handle(Request arg0, Response arg1) throws Exception {
			final QueryParamsMap qm = arg0.queryMap();
			final String name = qm.value("name");
			final String email = qm.value("email");
			final String stripeToken = qm.value("stripe");
			final String cell = qm.value("cell");
			final int password = qm.value("password").hashCode();
			final Map<String, Object> toServer = new HashMap<>();
			if (!checkSql(name) || !checkSql(email) || !checkSql(cell)) {
				toServer.put("success", false);
				return GSON.toJson(toServer);
			}
			if (User.accountExists(email)) {
				toServer.put("success", false);
				toServer.put("error", "exists");
			} else {
				final Map<String, Object> customerParams = new HashMap<>();
				customerParams.put("description", email);
				customerParams.put("source", stripeToken);
				// ^ obtained with Stripe.js
				final Customer customer = Customer.create(customerParams);
				if (testCharge(customer.getId()).equals("error")) {
					toServer.put("error", "stripe error");
					toServer.put("success", false);
				} else {
					toServer.put("error", "");
					final UserBuilder builder = new UserBuilder();
					final User user = builder.setId(email).setName(name).setPassword(password).setCell(cell)
							.setPayment(customer.getId()).setOrdererRatings(new ArrayList<Double>())
							.setDelivererRatings(new ArrayList<Double>()).setStatus(AccountStatus.ACTIVE)
							.setDelivererRatings(new ArrayList<Double>()).setOrdererRatings(new ArrayList<Double>())
							.build();
					user.addToDatabase();
					arg0.session().attribute("webId", user.getWebId());
					Manager.saveSession(arg0.session().id(), arg0.session());
					toServer.put("success", true);
				}
			}
			return GSON.toJson(toServer);
		}

		private String testCharge(String customerId) {
			final Map<String, Object> params = new HashMap<>();
			params.put("amount", TEST_CHARGE);
			params.put("currency", "usd");
			params.put("description", "Test charge");
			params.put("source", customerId);
			try {
				final Charge charge = Charge.create(params);
				if (!charge.getPaid()) {
					return "error";
				} else {
					charge.refund();
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
			return "";
		}
	}

	private static boolean checkSql(String toCheck) {
		toCheck = toCheck.toLowerCase(Locale.getDefault()).trim();
		if (toCheck.contains("insert into ") || toCheck.contains("update ") || toCheck.contains("select ")
				|| toCheck.contains("remove ")) {
			return false;
		}
		return true;
	}

	/**
	 * Handler for resetting the password.
	 *
	 * @author jacksonchaiken
	 *
	 */
	private static class PasswordReset implements TemplateViewRoute {
		@Override
		public ModelAndView handle(Request arg0, Response arg1) throws Exception {
			final Map<String, Object> toServer = new HashMap<>();
			toServer.put("title", "Forgot Password");
			return new ModelAndView(toServer, "reset.ftl");
		}
	}

	/**
	 * Handler for the profile page.
	 * 
	 * @author jacksonchaiken
	 *
	 */
	private static class ProfileHandler implements TemplateViewRoute {
		@Override
		public ModelAndView handle(Request arg0, Response arg1) throws Exception {
			final String webId = arg0.session().attribute("webId");
			if (webId == null || User.byWebId(webId) == null) {
				arg1.redirect("/login?from=profile");
				return new LoginHandler("profile").handle(arg0, arg1);
			}
			final Map<String, Object> variables = ImmutableMap.of("title", "Profile");
			return new ModelAndView(variables, "profile.ftl");
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
			final StringWriter stacktrace = new StringWriter();
			// req.session().
			try (PrintWriter pw = new PrintWriter(stacktrace)) {
				pw.println("<pre>");
				e.printStackTrace(pw);
				pw.println("</pre>");
			}
			res.body(stacktrace.toString());
		}
	}

	private static void setStripe() {
		Stripe.apiKey = "sk_test_esEbCKY1kAoxod12YXCJe0IS";
	}
}
