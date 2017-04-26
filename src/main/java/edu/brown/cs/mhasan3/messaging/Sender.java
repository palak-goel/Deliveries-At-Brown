package edu.brown.cs.mhasan3.messaging;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import edu.brown.cs.jchaiken.deliveryobject.Order;

public class Sender {

  public static final String ACCOUNT_SID = "AC8e8da5e13ff2bf0ea0577c353fc20a8e";
  public static final String AUTH_TOKEN = "85d9ddc067c2869e83bb0419ba55089f";
  private String content;
  private String receiver;
  private final String sendNumber = "+14014339860";

  public Sender(String rec) {
    Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    receiver = rec;
    content = null;

  }

  public void sendMessage() {
    Message message = Message.creator(new PhoneNumber(receiver),
        new PhoneNumber(sendNumber), content).create();

    System.out.println(message.getSid());
  }

  public void updateReceiver(String re) {
    receiver = re;
  }

  public String getReceiver() {
    return receiver;
  }

  public String getContent() {
    return content;
  }

  public void customMessage(String str) {
    content = str;
  }

  public void updateMessage(String upd, Order o) {
    switch (upd) {
    case "confirm":
      content = "Your order has been claimed by" + " "
          + o.getDeliverer().getName();
      break;
    case "estimate":
      content = "Your order will be delivered at approximately "
          + o.getDropoffTime();
      break;
    case "warning":
      content = "Your allocated time to deliver the item you have selected is running out";
      break;
    case "cancel":
      content = "Your delivery has been cancelled";
      break;
    case "tellDeliverer":
      content = "The user requesting the item has cancelled it";
      break;
    case "outside":
      content = "Your deliverer is outside your current location";
      break;
    case "complete":
      content = "Your delivery has succesfully been completed";
      break;
    }
  }

}
