package edu.brown.cs.jchaiken.deliveryobject;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import edu.brown.cs.jchaiken.database.Database;

/**
 * DeliveryObjectProxy is a superclass for the User and Order subclasses which
 * interact with the database.
 *
 * @author jacksonchaiken
 *
 * @param <T>
 *          the delivery object stored in the database.
 */
public abstract class DeliveryObjectProxy<T extends DeliveryObject> {
  private final String id;
  private T data;
  private static final int MAX_CACHE = 50000;
  private static final int TIMEOUT = 120;
  private static Cache<String, DeliveryObject> cache = CacheBuilder.newBuilder()
      .maximumSize(MAX_CACHE).expireAfterAccess(TIMEOUT, TimeUnit.MINUTES)
      .build();
  private static Set<String> pending = Collections
      .synchronizedSet(new HashSet<>());

  DeliveryObjectProxy(String newId) {
    if (newId == null) {
      throw new IllegalArgumentException("ID is null");
    }
    this.id = newId;
    this.data = null;
    checkCache();
  }

  private void checkCache() {
    if (data != null) {
      return;
    }
    @SuppressWarnings("unchecked")
    final T internal = (T) cache.getIfPresent(id);
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

  protected void setData(T newData) {
    data = newData;
  }

  protected static Cache<String, DeliveryObject> getCache() {
    return cache;
  }

  protected void check() {
    checkCache();
    if (pending.contains(id)
        || data == null && Database.getConnection() != null) {
      try {
        cache();
      } catch (final SQLException exc) {
        exc.printStackTrace();
      }
    }
    if (data != null) {
      cache.put(id, data);
    }
  }

  protected abstract void cache() throws SQLException;

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof DeliveryObject)) {
      return false;
    }
    final DeliveryObject temp = (DeliveryObject) obj;
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
