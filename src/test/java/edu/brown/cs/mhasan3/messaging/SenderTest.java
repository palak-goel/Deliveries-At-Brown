package edu.brown.cs.mhasan3.messaging;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SenderTest {

  @Test
  public void testSettersReceiver() {
    final Sender sender = new Sender("+14012257856");
    assertEquals(sender.getReceiver(), "+14012257856");
    sender.updateReceiver("+14012257852");
    assertEquals(sender.getReceiver(), "+14012257852");

  }

  @Test
  public void testSettersMessage() {
    final Sender sender = new Sender("+14012257856");
    assertEquals(sender.getContent(), null);
    sender.updateReceiver("+16783829540");
    assertEquals(sender.getReceiver(), "+16783829540");
    sender.customMessage("outside");
    // sender.sendMessage();
    // assertEquals(sender.getContent(),
    // "Your deliverer is outside your current location");

  }

}
