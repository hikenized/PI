package com.imrglobal.framework.utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Fonctions utilitaires de recherches sur des classes et des types primitifs.
 */
public class ClassUtils {

  private static final Map<String, Class<?>> CLASS_CACHE = new ConcurrentHashMap<>();

  static final Map<Class, Class> PRIMITIVE_WRAPPER = new HashMap<>();
  static final Map<String, Class> PRIMITIVE_CLASS_NAMES = new HashMap<>();
  static {
    PRIMITIVE_WRAPPER.put(Boolean.TYPE, Boolean.class);
    PRIMITIVE_WRAPPER.put(Byte.TYPE, Byte.class);
    PRIMITIVE_WRAPPER.put(Character.TYPE, Character.class);
    PRIMITIVE_WRAPPER.put(Short.TYPE, Short.class);
    PRIMITIVE_WRAPPER.put(Integer.TYPE, Integer.class);
    PRIMITIVE_WRAPPER.put(Long.TYPE, Long.class);
    PRIMITIVE_WRAPPER.put(Float.TYPE, Float.class);
    PRIMITIVE_WRAPPER.put(Double.TYPE, Double.class);

    PRIMITIVE_WRAPPER.keySet().forEach(c -> PRIMITIVE_CLASS_NAMES.put(c.getSimpleName(), c));
  }

  /**
   * Recherche une classe � partir de son nom (y compris dans les plugins)
   * <p>
   * Equivaut � <code>classForName(String name, Parameters.USE_CONTEXT_CLASSLOADER);</code>
   * 
   * @see #classForName(String, boolean)
   * @param name nom complet de la classe � rechercher
   * @return la {@link Class}e recherch�e
   * @throws ClassNotFoundException si la classe n'a pu �tre trouv�e
   */
  public static Class classForName(String name) throws ClassNotFoundException {
    return classForName(name, true);
  }

  /**
   * Cherche une classe � partir de son nom (y compris dans les plugins)
   * <p>
   * Note: la classe est mise en cache (associ�e au nom).
   * 
   * @param name nom complet de la classe
   * @param useContextClassLoader <code>true</code> pour utiliser le ClassLoader contextuel.
   * @return la {@link Class}e recherch�e
   * @throws ClassNotFoundException si la classe n'a pu �tre trouv�e
   */
  public static Class classForName(String name, boolean useContextClassLoader) throws ClassNotFoundException {
    Class resClass = CLASS_CACHE.get(name);
    if (resClass == null) {
      resClass = getClassForName(name, useContextClassLoader);
      CLASS_CACHE.put(name, resClass);
    }
    return resClass;
  }

  private static Class getClassForName(String name, boolean useContextClassLoader) throws ClassNotFoundException {

      if (useContextClassLoader) {
        return Class.forName(name, true, Thread.currentThread().getContextClassLoader());
      }
      return Class.forName(name);

  }



  /**
   * La classe est-elle une classe d'encapsulation du type primitif?
   * 
   * @param thisClass la classe a tester
   * @return <code>true</code> si la classe est une classe d'encapsulation du type primitif.
   */
  public static boolean isPrimitiveWrapper(Class thisClass) {
    return PRIMITIVE_WRAPPER.containsValue(thisClass);
  }

  /**
   * Teste si le nom de classe sp�cifi� est un type natif.
   *
   * @param name le nom de classe � �valuer
   * @return true s'il s'agit d'un type primitif.
   */
  public static boolean isPrimitiveClassName(String name) {
    return PRIMITIVE_CLASS_NAMES.containsKey(name);
  }

  /**
   * Teste si la classe d�signe un type primitif
   *
   * @param cls la {@link Class}e � �valuer
   * @return <code>true</code> s'il s'agit d'un type primitif.
   */
  public static boolean isPrimitiveClass(Class cls) {
    return cls != null && cls.isPrimitive();
  }

  /**
   * Retourne la classe primitive correspondant au nom pass� en param�tre
   *
   * @param name nom dy type primitif: "int", "short", "long", "float", "double", "boolean", "char" ou "byte"
   * @return la classe primitive associ�e ( int.class, ...)
   */
  public static Class getPrimitiveClass(String name) {
    return PRIMITIVE_CLASS_NAMES.get(name);
  }

  /**
   * La classe pass�e en param�tre est-elle une classe num�rique?
   *
   * @param cls la classe � �valuer
   * @return <code>true</code> si cette classe ou type primitif peut �tre ramen�e � un {@link Number}
   */
  public static boolean isNumberClass(Class cls) {
    return cls != null && Number.class.isAssignableFrom(toWrapper(cls));
  }

  /**
   * Teste si une classe est pr�sente dans l'environnement (y compris dans les plugins)
   *
   * @param clsName nom complet de la classe
   * @return <code>true</code> si la classe a pu �tre trouv�e, <code>false</code> sinon
   */
  public static boolean isClassExists(String clsName) {
    boolean ok = false;
    try {
      ok = classForName(clsName) != null;
    } catch (Throwable e) {
      ok = false;
    }
    return ok;
  }

  /**
   * Retourne la classe encapsulant le type primitif pass� en param�tre
   *
   * @param primitive le type primitif (byte, int, ...)
   * @return La classe correspondant au type primitif, ou <code>null</code> sir le param�tres n'�tait pas un type primitif.
   */
  public static Class getWrapper(Class primitive) {
    return primitive != null && primitive.isPrimitive() ? PRIMITIVE_WRAPPER.get(primitive) : null;
  }

  /**
   * Encapsule les types primitifs.
   *
   * @param aClass une classe
   * @return la classe d'encapsulation du type primitif, le cas �ch�ant. Sinon, la classe initiale <b>aClass</b> est retourn�e.
   */
  public static Class toWrapper(Class aClass) {
    Class wrapper = getWrapper(aClass);
    return wrapper != null ? wrapper : aClass;
  }

  /**
   * @return {@code true} si la superClass fait partie des classes parentes de cls, false sinon
   */
  public static boolean isSuperClass(Class cls, Class superClass) {
    for (Class<?> c = cls.getSuperclass(); c != null; c = c.getSuperclass()) {
      if (c == superClass) {
        return true;
      }
    }
    return false;
  }

  /**
   * Vide le cache des classes.
   */
  public static void viderCache() {
    CLASS_CACHE.clear();
  }
}