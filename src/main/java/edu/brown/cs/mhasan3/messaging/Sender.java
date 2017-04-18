package edu.brown.cs.mhasan3.messaging;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class Sender {
	
	 public static final String ACCOUNT_SID = "AC8e8da5e13ff2bf0ea0577c353fc20a8e";
	 public static final String AUTH_TOKEN = "85d9ddc067c2869e83bb0419ba55089f";

	  public Sender() {
	    Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

	    Message message = Message.creator(new PhoneNumber("+14012257856"),
	        new PhoneNumber("+14014339860"), 
	        "This is the ship that made the Kessel Run in fourteen parsecs?").create();

	    System.out.println(message.getSid());
	  }

}
