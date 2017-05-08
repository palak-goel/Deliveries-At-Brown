package edu.brown.cs.jchaiken.projectcontrol;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
		Spark.get("/", (request, response) -> {
			if (request.session().attribute("webId") != null) {
				Map<String, Object> variables = new HashMap<>();
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
			// TODO: phone number check stuff
			String cell = request.queryMap().value("cell");
			String code = sender.resetPassword(cell);
			Map<String, Object> toServer = ImmutableMap.of("sent", true);
			sentCodes.put(cell, code);
			return GSON.toJson(toServer);
		});

		Spark.post("/profile", (req, response) -> {
			User u = User.byWebId(req.session().attribute("webId"));
			Map<String, Object> msg = new HashMap<>();
			msg.put("name", u.getName());
			msg.put("phoneNumber", u.getCell());
			msg.put("email", u.getId());
			msg.put("deliveryRating", u.getDelivererRating());
			msg.put("requestRating", u.getOrdererRating());
			return GSON.toJson(msg);
		});
		Spark.post("/sendText", (req, response) -> {
			String receiver = req.queryMap().value("number");
			String content = req.queryMap().value("content");
			sender.updateReceiver(receiver);
			sender.customMessage(content);
			sender.sendMessage();
			return new Object();
		});
		Spark.post("/submit-Ordering", (request, response) -> {
			try {
				QueryParamsMap qm = request.queryMap();
				User u = User.byWebId(request.session().attribute("webId"));
				String opt = qm.value("option");
				Ranker r = new Ranker(MANAGER, u);
				List<Order> os = new ArrayList<>();
				if (opt.equals("tip")) {
					os = r.orderByPrice();
				} else if (opt.equals("distance")) {
					os = r.orderByDistance();
				} else {
					os = r.orderByTime();
				}
				OrderWebSocket.sendOrders(request.session().id(), os);
				return "";
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
		});
		Spark.post("/submit-Preferences", (request, response) -> {
			try {
				QueryParamsMap qm = request.queryMap();
				User u = User.byWebId(request.session().attribute("webId"));
				double price = Double.parseDouble(qm.value("price"));
				double distance = Double.parseDouble(qm.value("distance"));
				double time = Double.parseDouble(qm.value("time"));
				u.addDeliveryPreferences(distance, price, time);
				Ranker r = new Ranker(MANAGER, u);
				OrderWebSocket.sendOrders(request.session().id(), r.rank());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		});
		Spark.post("/validate-code", (request, response) -> {
			String cell = request.queryMap().value("cell");
			String code = request.queryMap().value("code");
			Map<String, Object> toServer;
			if (sentCodes.containsKey(cell) && sentCodes.get(cell).equals(code)) {
				toServer = ImmutableMap.of("goodCode", true);
			} else {
				toServer = ImmutableMap.of("goodCode", false);
				return GSON.toJson(toServer);
			}
			return GSON.toJson(toServer);
		});
		Spark.post("/user-rating", (request, response) -> {
			QueryParamsMap qm = request.queryMap();
			Order o = Order.byId(qm.value("id"));
			double rtg = o.getOrderer().getOrdererRating();
			return GSON.toJson(ImmutableMap.of("rating", rtg));
		});
		Spark.post("/reset-password", (request, response) -> {
			String email = request.queryMap().value("email");
			String newPass = request.queryMap().value("password");
			boolean status = User.newPassword(email, newPass);
			Map<String, Object> toServer = new HashMap<>();
			toServer.put("status", status);
			return GSON.toJson(toServer);
		});
		Spark.post("/is-active", (request, response) -> {
			String jid = request.session().id();
			System.out.println(jid);
			return GSON.toJson(ImmutableMap.of("isActive", Manager.isActiveUser(jid)));
		});

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
		Spark.get("/delivering", (request, response) -> {
			String webId = request.session().attribute("webId");
			if (webId == null || User.byWebId(webId) == null) {
				response.redirect("/login?from=delivering");
				return new LoginHandler("delivering").handle(request, response);
			}
			Map<String, Object> variables = ImmutableMap.of("title", "Delivering");
			return freeMarker.render(new ModelAndView(variables, "delivering.ftl"));
		});
		Spark.get("/deliverycompleted", (request, response) -> {
			String webId = request.session().attribute("webId");
			if (webId == null || User.byWebId(webId) == null) {
				response.redirect("/login?from=deliverycompleted");
				return new LoginHandler("deliverycompleted").handle(request, response);
			}
			Map<String, Object> variables = ImmutableMap.of("title", "Delivering");
			return freeMarker.render(new ModelAndView(variables, "deliverycompleted.ftl"));
		});
		Spark.get("/ordercompleted", (request, response) -> {
			String webId = request.session().attribute("webId");
			if (webId == null || User.byWebId(webId) == null) {
				response.redirect("/login?from=ordercompleted");
				return new LoginHandler("ordercompleted").handle(request, response);
			}
			Map<String, Object> variables = ImmutableMap.of("title", "Delivering");
			return freeMarker.render(new ModelAndView(variables, "ordercompleted.ftl"));
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
			Map<String, Object> variables = ImmutableMap.of("title", "Deliver");
			return freeMarker.render(new ModelAndView(variables, "deliver.ftl"));
		});

		Spark.get("/delivering", (request, response) -> {
			String webId = request.session().attribute("webId");
			if (webId == null || User.byWebId(webId) == null) {
				response.redirect("/login?from=delivering");
				return new LoginHandler("delivering").handle(request, response);
			}
			Map<String, Object> variables = ImmutableMap.of("title", "Deliver");
			return freeMarker.render(new ModelAndView(variables, "delivering.ftl"));
		});
		Spark.get("/delivered", (request, response) -> {
			String webId = request.session().attribute("webId");
			if (webId == null || User.byWebId(webId) == null) {
				response.redirect("/login?from=delivered");
				return new LoginHandler("delivered").handle(request, response);
			}
			Map<String, Object> variables = ImmutableMap.of("title", "Deliver");
			return freeMarker.render(new ModelAndView(variables, "delivered.ftl"));
		});
		Spark.post("/delete-order", (request, response) -> {
			QueryParamsMap qm = request.queryMap();
			Order o = Order.byId(qm.value("id"));
			OrderWebSocket.sendRemoveOrder(o);
			Sender sender = new Sender(o.getOrderer().getCell());
			sender.updateMessage("cancel", o);
			sender.sendMessage();
			return GSON.toJson("");
		});
		Spark.post("/suggest", (request, response) -> {
			String jid = request.session().id();
			User u = User.byWebId(Manager.getSession(jid).attribute("webId"));
			Suggestor s = new Suggestor(u);
			Map<String, Object> m = new HashMap<>();
			m.put("pickup", s.suggestPickup());
			m.put("dropoff", s.suggestDropoff());
			m.put("items", s.suggestItem());
			return GSON.toJson(m);
		});
		Spark.post("/order-history", (request, response) -> {
			String jid = request.session().id();
			User u = User.byWebId(Manager.getSession(jid).attribute("webId"));
			Map<String, Object> res = new HashMap<>();
			for (Order o : u.pastDeliveries()) {
				o.getDropoffLocation().getName();
				o.getPickupLocation().getName();
			}
			for (Order o : u.pastOrders()) {
				o.getDropoffLocation().getName();
				o.getPickupLocation().getName();
			}
			res.put("deliver", u.pastDeliveries());
			res.put("order", u.pastOrders());
			return GSON.toJson(res);
		});
		Spark.post("/logout", (request, response) -> {
			request.session().attribute("webId", null);
			return GSON.toJson("");
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
	 * Handles login requests to the server.
	 *
	 * @author jacksonchaiken
	 *
	 */
	private static class LoginValidator implements Route {
		@Override
		public Object handle(Request arg0, Response arg1) {
			Map<String, Object> toServer = new HashMap<>();
			try {
				QueryParamsMap qm = arg0.queryMap();
				System.out.println("validating");
				String id = qm.value("id");
				String password = qm.value("password");
				if (!checkSql(id)) {
					toServer.put("result", false);
					return GSON.toJson(toServer);
				}
				if (User.userValidator(id, password)) {
					toServer.put("result", true);
					User user = User.byId(id);
					arg0.session().attribute("webId", user.getWebId());
					Manager.saveSession(arg0.session().id(), arg0.session());
				} else {
					toServer.put("result", false);
				}
			} catch (Exception e) {
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
			QueryParamsMap qm = arg0.queryMap();
			String name = qm.value("name");
			String email = qm.value("email");
			String stripeToken = qm.value("stripe");
			String cell = qm.value("cell");
			int password = qm.value("password").hashCode();
			Map<String, Object> toServer = new HashMap<>();
			if (!checkSql(name) || !checkSql(email) || !checkSql(cell)) {
				toServer.put("success", false);
				return GSON.toJson(toServer);
			}
			if (User.accountExists(email)) {
				toServer.put("success", false);
				toServer.put("error", "exists");
			} else {
				Map<String, Object> customerParams = new HashMap<>();
				customerParams.put("description", "Customer for Deliveries @ Brown");
				customerParams.put("source", stripeToken);
				// ^ obtained with Stripe.js
				Customer customer = Customer.create(customerParams);
				if (testCharge(customer.getId()).equals("error")) {
					toServer.put("error", "stripe error");
					toServer.put("success", false);
				} else {
					toServer.put("error", "");
					UserBuilder builder = new UserBuilder();
					User user = builder.setId(email).setName(name).setPassword(password).setCell(cell)
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
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("amount", TEST_CHARGE);
			params.put("currency", "usd");
			params.put("description", "Test charge");
			params.put("source", customerId);
			try {
				Charge charge = Charge.create(params);
				if (!charge.getPaid()) {
					return "error";
				} else {
					charge.refund();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}
	}

	private static boolean checkSql(String toCheck) {
		toCheck = toCheck.toLowerCase().trim();
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
			Map<String, Object> toServer = new HashMap<>();
			toServer.put("title", "Forgot Password");
			return new ModelAndView(toServer, "reset.ftl");
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
