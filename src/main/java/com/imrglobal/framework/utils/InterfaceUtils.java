package com.imrglobal.framework.utils;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

/**
 * Utilitaire de reflexion sur les interfaces
 */
public class InterfaceUtils {

  /**
   * @return Retourne la liste des interfaces impl�ment�es par une classe et celles impl�ment�es par ses classe m�res
   */
  public static List<Class> getAllInterfaces(Class cls) {
    return getInterfaces(cls, null);
  }

  /**
   * @param stopClass si non nulle, classe m�re qui stoppe la r�cup�ration des interfaces. Toutes les interfaces d�clar�es par cette classe ou de ces classes parentes ne seront pas prises en compte
   * @return Retourne la liste des interfaces impl�ment�es par une classe et celles impl�ment�es par ses classe m�res jusqu'� la stopClass si sp�cifi�e
   */
  public static List<Class> getInterfaces(Class cls, Class stopClass) {
    if (stopClass != null && !ClassUtils.isSuperClass(cls, stopClass)) {
      throw new RuntimeException("InterfaceUtils#getInterfaces stopClass "+stopClass.getName()+" not a superClass of "+cls.getName());
    }
    List<Class> interfaces = new ArrayList<>();
    addInterfaces(interfaces, cls, stopClass);
    return interfaces;
  }

  /**
   * @return la m�thode d'une interfaces de la classe correspondant � l'ago d�finie par methodFinder et qui est 'utilisable' sur une interface (soit defaut soit statique)
   */
  public static Method getMethodOnInterface(Class cls, Function<Class,Method> methodFinder) {
    return findMethodOnInterfaces(cls, methodFinder, new HashSet<>());
  }

  private static Method findMethodOnInterfaces(Class cls, Function<Class, Method> methodFinder, Set<Class> interfacesAnalyzed) {
    List<Class> allInterfaces = getAllInterfaces(cls);
    Method res = null;
    for (Class anInterface : allInterfaces) {
      if (!interfacesAnalyzed.contains(anInterface)) {
        interfacesAnalyzed.add(anInterface);
        res = findPossibleInterfaceMethod(anInterface, methodFinder);
        if (res == null) {
          res = findMethodOnInterfaces(anInterface, methodFinder, interfacesAnalyzed);
        }
        if (res != null) {
          return res;
        }
      }
    }
    return res;
  }

  private static Method findPossibleInterfaceMethod(Class anInterface, Function<Class, Method> methodFinder) {
    Method method = methodFinder.apply(anInterface);
    if (method != null && isValidInterfaceMethod(method)) {
      return method;
    }
    return null;
  }

  private static boolean isValidInterfaceMethod(Method method) {
    return method.isDefault() || Modifier.isStatic(method.getModifiers());
  }


  private static void addInterfaces(List<Class> allInterfaces, Class cls, Class stopClass) {
    List<Class> clsInterfaces = new ArrayList<>(Arrays.asList(cls.getInterfaces()));
    clsInterfaces.removeIf(i -> allInterfaces.contains(i));
    allInterfaces.addAll(clsInterfaces);
    Class superclass = cls.getSuperclass();
    if (superclass != null && (!Object.class.equals(superclass) && (stopClass ==null || !stopClass.equals(superclass)))) {
      addInterfaces(allInterfaces, superclass, stopClass);
    }
  }
}
