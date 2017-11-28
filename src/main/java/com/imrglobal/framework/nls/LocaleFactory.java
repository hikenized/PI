package com.imrglobal.framework.nls;

import java.util.Locale;

/**
 * Factory pour {@link Locale}
 */
public interface LocaleFactory {

  /**
   * @param lg language
   * @return l'instance de {@link Locale} d�pendant de {@link Language}
   */
  public Locale getLocale(Language lg);
}
