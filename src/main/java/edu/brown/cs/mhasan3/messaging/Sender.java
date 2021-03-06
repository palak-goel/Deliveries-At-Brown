package edu.brown.cs.mhasan3.messaging;

import java.util.UUID;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import edu.brown.cs.jchaiken.deliveryobject.Order;

/**
 * Class uses the Twilio API to enable communication between two individuals.
 *
 * @author shehryarhasan
 *
 */
public class Sender {

  public static final String ACCOUNT_SID = "AC8e8da5e13ff2bf0ea0577c353fc20a8e";
  public static final String AUTH_TOKEN = "85d9ddc067c2869e83bb0419ba55089f";
  private String content;
  private String receiver;
  private static final String SEND_NUMBER = "+14014339860";

  /**
   * The constructor takes in the receiver.
   *
   * @param rec
   *          receiver
   */
  public Sender(String rec) {
    Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    receiver = rec;
    content = null;

  }

  /**
   * Sends a message through Twilio.
   */
  public void sendMessage() {
    final Message message = Message.creator(new PhoneNumber(receiver),
        new PhoneNumber(SEND_NUMBER), content).create();

    System.out.println(message.getSid());
  }

  /**
   * Updates who the receiver of the message is.
   *
   * @param re
   *          new receiver
   */
  public void updateReceiver(String re) {
    receiver = re;
  }

  /**
   * Returns the current recipient.
   *
   * @return receiver
   */
  public String getReceiver() {
    return receiver;
  }

  /**
   * Returns what the content of the message is.
   *
   * @return content
   */
  public String getContent() {
    return content;
  }

  /**
   * Allows for a custom message to be sent.
   *
   * @param str
   *          String of custom message
   */
  public void customMessage(String str) {
    content = str;
  }

  /**
   * Generates a code to be input by the user for a password change, and sends
   * this code to the user's phone number.
   *
   * @param rec
   *          Receiver's phone number
   * @return random code
   */
  public String resetPassword(String rec) {
    this.updateReceiver(rec);
    final String uuid = UUID.randomUUID().toString()
        .replaceAll("[^A-Za-z0-9]", "");
    final String ret = uuid.substring(0, 6);
    content = "Your verification code is " + ret;
    this.sendMessage();
    return ret;
  }

  /**
   * Allows for directions to be sent.
   *
   * @param str
   *          directions
   * @param rece
   *          the receiver of the message.
   */
  public void sendDirections(String[] str, String rece) {
    this.updateReceiver(rece);
    final StringBuilder builder = new StringBuilder();
    for (final String s : str) {
      builder.append(s);
      builder.append("\n");
    }
    content = builder.toString();
    this.sendMessage();
  }

  /**
   * Uses a switch statement to update the content of the message with certain
   * preset options.
   *
   * @param upd
   *          for switch statement
   * @param o
   *          order - used to get specifics for messages.
   */
  public void updateMessage(String upd, Order o) {
    switch (upd) {
      case "confirm": // Send this to a requester as soon as an order has been
                      // claimed
        content = "Your order has been claimed by" + " "
            + o.getDeliverer().getName();
        break;
      case "cancel": // When a delivery has been cancelled before it has been
                     // claimed
        content = "Your delivery has been cancelled";
        break;
      case "complete": // Send to deliverer when they have completed the task
        content = "Your delivery has succesfully been completed";
        break;
      default:
        break;
    }
  }

}
