package com.imrglobal.framework.cache;

import java.util.*;

/**
 * Impl�mentation simple d'une map LRU.
 * <p>
 * Attention - l'impl�mentation n'est pas thread-safe, m�me avec le flag <i><code>synchro</code></i>.<br>
 * Pour obtenir une version thread-safe :
 * 
 * <pre>
 * int taille = ...
 * Map&ltX,Y&gt mySafeMap = Collections.synchronizedMap(new FIFOMap&ltX,Y&gt(taille));
 * </pre>
 */
public class FIFOMap<K, V> extends LinkedHashMap<K, V> {

  private final int maxCapacity;
  private final boolean synchro;

  /**
   * Cr��e une {@link FIFOMap} sans limite de capacit�
   */
  public FIFOMap() {
    this(0);
  }

  /**
   * Cr��e une {@link FIFOMap} avec limite de capacit�
   * 
   * @param maxcapacity limite de capacit�. Note: aucune limite n'est appliqu�e si maxcapacity n'est pas > 0.
   */
  public FIFOMap(int maxCapacity) {
    synchro = false;
    this.maxCapacity = (maxCapacity > 0) ? maxCapacity : 0;
  }

  /**
   * @param synchro active un mode <i>synchro</i> sur la classe (restreint � put , putAll, remove).
   * @deprecated Ne pas utiliser synchro=<code>true</code> car seule 3 m�thodes sont alors synchronis�es.<br>
   *             Utiliser le constructeur sans param�tres, et faire appel � {@link Collections#synchronizedMap(Map)} si besoin de synchro.
   */
  @Deprecated
  public FIFOMap(boolean synchro) {
    this(synchro, 0);
  }

  /**
   * <b>Attention :</b> La notion de <i>synchro</i> ne concerne que 3 m�thodes... Cela ne rend pas la classe thread-safe...
   * 
   * @param synchro active un mode <i>synchro</i> sur la classe (restreint � put , putAll, remove).
   * @param maxcapacity limite de capacit�. Note: aucune limite n'est appliqu�e si maxcapacity n'est pas > 0.
   * @deprecated Ne pas utiliser synchro=<code>true</code> car seule 3 m�thodes sont alors synchronis�es.<br>
   *             Utiliser le constructeur avec param�tre entier, et faire appel � {@link Collections#synchronizedMap(Map)} si besoin de synchro.
   */
  @Deprecated
  public FIFOMap(boolean synchro, int maxCapacity) {
    this.synchro = synchro;
    this.maxCapacity = (maxCapacity > 0) ? maxCapacity : 0;
  }

  @Override
  protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
    return maxCapacity > 0 && size() > maxCapacity;
  }

  @Override
  public V put(K key, V value) {
    if (synchro) {
      synchronized (this) {
        return super.put(key, value);
      }
    }
    return super.put(key, value);
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    if (synchro) {
      synchronized (this) {
        super.putAll(m);
      }
    } else {
      super.putAll(m);
    }
  }

  @Override
  public V remove(Object key) {
    if (synchro) {
      synchronized (this) {
        return super.remove(key);
      }
    }
    return super.remove(key);
  }
}
