package edu.brown.cs.jchaiken.deliveryobject;

import java.util.Objects;

/**
 * DeliveryObjectBean is a super-class for the bean subclasses which provide
 * User and Order functionality.
 *
 * @author jacksonchaiken
 *
 * @param <T>
 *          The delivery object modeled as a bean.
 */
public abstract class DeliveryObjectBean<T extends DeliveryObject> {
  private final String id;

  DeliveryObjectBean(String newId) {
    this.id = newId;
  }

  /**
   * Returns the objects id.
   *
   * @return the id.
   */
  public String getId() {
    return id;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof DeliveryObjectBean)) {
      return false;
    }
    final DeliveryObjectBean<?> temp = (DeliveryObjectBean<?>) obj;
    if (temp.getId().equals(this.id)) {
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
