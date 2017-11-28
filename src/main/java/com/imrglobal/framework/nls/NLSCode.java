package com.imrglobal.framework.nls;

/**
 * Repr�sente un code multilingue.
 * 
 * Exemple d'utilisation :
 * 
 * <pre>
 * private enum MonEnumereLabels implements NLSCode {
 *   MA_PREMIERE_VALEUR,
 *   MA_DEUXIEME_VALEUR;
 *
 *   &#64;Override
 *   public String getBundle() {
 *     return BUNDLE;
 *   }
 *
 *   &#64;Override
 *   public String getCode() {
 *     return name();
 *   }
 * }
 * </pre>
 * 
 * Pour un besoin "standard", respecter les 2 r�gles suivantes :
 * <ul>
 * <li>Suffixer l'�num�r� par : <code>Labels</code></li>
 * <li>Ranger l'�num�r� dans le package : <code>[monDomaine].nls</code></li>
 * </ul>
 * 
 * Pour la gestion d'�num�r� d'erreur, utiliser la classe fille : {@link com.efluid.framework.erreur.CodeErreur}.
 */
public interface NLSCode {

  /** @return le bundle o� est d�fini le code */
  public String getBundle();

  /** @return le code */
  public String getCode();

  /**
   * @param params donn�es dynamiques n�cessaires � la traduction
   * @return la traduction du NLSCode avec la langue par d�faut de l'application {@link Language#DEFAULT}
   */
  default String translate(Object... params) {
    return translate(Language.DEFAULT, params);
  }

  /**
   * @param language langue � utiliser pour la traduction
   * @param params donn�es dynamiques n�cessaires � la traduction
   * @return la traduction du NLSCode
   */
  default String translate(Language language, Object... params) {
    return NLS.translate(language, this, params);
  }
}
