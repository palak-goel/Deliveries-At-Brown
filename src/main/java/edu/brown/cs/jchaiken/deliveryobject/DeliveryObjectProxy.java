package edu.brown.cs.jchaiken.deliveryobject;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import edu.brown.cs.jchaiken.database.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;


public abstract class DeliveryObjectProxy<T extends DeliveryObject> {
  private String id;
  private T data;
  private static final int MAX_CACHE = 50000;
  private static Cache<String, DeliveryObject> cache = CacheBuilder.newBuilder()
      .maximumSize(MAX_CACHE).build(); 

  DeliveryObjectProxy(String newId) {
    if (newId == null) {
      throw new IllegalArgumentException("ID is null");
    }
    this.data = null;
    this.id = newId;
    checkCache();
  }

  private void checkCache() {
    if (data != null) {
      return;
    }
    @SuppressWarnings("unchecked")
    T internal = (T) cache.getIfPresent(id);
    data = internal;
  }

  /**
   * Returns an objects id.
   *
   * @return the id.
   */
  public String getId() {
    return id;
  }

  protected T getData() {
    return data;
  }

  protected Cache<String, DeliveryObject> getCache() {
    return cache;
  }

  protected void cache() {
    checkCache();
    if (data == null && Database.getConnection() != null) {
      try {
        cache(Database.getConnection());
      } catch (SQLException exc) {
        exc.printStackTrace();
      }
      cache.put(id, data);
    }
  }

  protected abstract void cache(Connection connection) throws SQLException;

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof DeliveryObject)) {
      return false;
    }
    DeliveryObject temp = (DeliveryObject) obj;
    if (temp.getId().equals(this.id)
        && data == cache.getIfPresent(temp.getId())) {
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  /**
   * Clears the cache.
   */
  public static void clearCache() {
    cache = CacheBuilder.newBuilder().maximumSize(MAX_CACHE).build();
  }
}
