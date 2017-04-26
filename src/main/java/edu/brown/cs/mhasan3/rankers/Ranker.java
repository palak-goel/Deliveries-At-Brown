package edu.brown.cs.mhasan3.rankers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;

import edu.brown.cs.jchaiken.deliveryobject.Location;
import edu.brown.cs.jchaiken.deliveryobject.Order;
import edu.brown.cs.jchaiken.deliveryobject.User;
import edu.brown.cs.jchaiken.projectcontrol.Manager;

public class Ranker {

  private Manager manage;
  private List<Order> orders;
  private User user;

  public Ranker(Manager m, User curr) {
    manage = m;
    orders = manage.getPendingOrders();
    user = curr;
  }

  public List<Order> rank() {
    PriorityQueue<Order> queue = new PriorityQueue<Order>(new RankerQueue());
    for (Order o : orders) {
      double time = this.considerTime(o);
      double dist = this.considerDistance(o);
      double stars = this.considerStars(o);
      double payment = this.considerPayment(o);
      double history = this.considerHistory(o);
      double weightedAverage = this.weightedAverage(time, dist, stars, payment,
          history);
      o.setRanking(weightedAverage);
      queue.add(o);
    }
    return this.returnAnswer(queue);
  }

  public List<Order> returnAnswer(PriorityQueue<Order> q) {
    List<Order> ans = new ArrayList<Order>();
    while (!q.isEmpty()) {
      Order tem = q.poll();
      ans.add(tem);
    }
    return ans;
  }

  public double weightedAverage(double t, double d, double s, double pay,
      double his) {
    double ans = t + d + (3 * s) + (5 * pay) + his;
    return ans;
  }

  public double considerTime(Order curr) {
    double userTime = user.getDeliveryTimePreference();
    double currTime = Instant.now().getEpochSecond();
    double endTime = curr.getDropoffTime();
    double timeDiffSecs = endTime - currTime;
    double timeDiffMins = timeDiffSecs / 60;
    double res = Math.abs(userTime - timeDiffMins);
    return res;
  }

  public double considerDistance(Order curr) {
    double userDist = user.getDeliveryDistancePreference();
    Location pickLoc = curr.getPickupLocation();
    double la1 = pickLoc.getLatitude();
    double lg1 = pickLoc.getLongitude();
    Location dropLoc = curr.getDropoffLocation();
    double la2 = dropLoc.getLatitude();
    double lg2 = dropLoc.getLongitude();
    double res = this.haversineFormula(la1, lg1, la2, lg2) * 1000;
    double rem = Math.abs(userDist - res);
    return rem;
  }

  public double haversineFormula(Double lat1, Double lng1, Double lat2,
      Double lng2) {
    double res = 0.0;
    double radius = 6378.137;
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lng2 - lng1);
    lat1 = Math.toRadians(lat1);
    lat2 = Math.toRadians(lat2);

    double a = Math.pow(Math.sin(dLat / 2), 2)
        + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
    double c = 2 * Math.asin(Math.sqrt(a));
    res = radius * c;
    return res;
  }

  public double considerPayment(Order curr) {
    double currentPay = curr.getPrice(); // change to something else
    double desiredPay = user.getDeliveryFeePreference();
    double res = desiredPay - currentPay;
    return res;
  }

  public double considerStars(Order curr) {
    User u = curr.getOrderer();
    double stars = u.getOrdererRating();
    stars = 5.0 - stars;
    return stars;
  }

  public double considerHistory(Order curr) {
    User u = curr.getOrderer();
    Collection<Order> past = u.pastDeliveries();
    List<Order> p = new ArrayList<Order>(past);
    double res = 5.0;
    for (Order o : p) {
      if (o.getOrderer().getId().equals(u.getId())) {
        if (res > 0.0) {
          res = res - 1.0;
        }
      }
    }
    return res;
  }
}
