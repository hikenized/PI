package com.efluid.ecore.commun.utils;

/**
 * Utilitaire pour les comparateurs.
 */
public class EcoreComparators {

  /**
   * Retourne 0 pour deux strings �gaux ou nuls, {@link Integer#MIN_VALUE} si <code>s1</code> est nul et l'ordre est lexicographique (l'inverse sinon), {@link Integer#MAX_VALUE} si <code>s2</code> est
   * nul et l'ordre est lexicographique (l'inverse sinon), la valeur de {@link String#compareTo(String)} dans le reste des cas si l'ordre est lexicographique (l'inverse sinon), ce qui correspond � la
   * distance lexicographique entre les deux cha�nes de caract�res.
   *
   */
  public static int compareLexicographically(String s1, String s2, boolean lexicographic) {
    int compare;
    if (null == s1 && null == s2) {
      compare = 0;
    } else if (null == s1) {
      compare = lexicographic ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    } else if (null == s2) {
      compare = lexicographic ? Integer.MAX_VALUE : Integer.MIN_VALUE;
    } else {
      compare = lexicographic ? s1.compareTo(s2) : -s1.compareTo(s2);
    }
    return compare;
  }

  /**
   * Retourne 0 pour deux strings �gaux ou nuls, {@link Integer#MIN_VALUE} si <code>s1</code> est nul et l'ordre est croissant (l'inverse sinon), {@link Integer#MAX_VALUE} si si <code>s2</code> est
   * nul et l'ordre est croissant (l'inverse sinon), la soustraction de la valeur num�rique de <code>s1</code> � la valeur num�rique de <code>s2</code> si l'ordre est croissant (l'inverse sinon), ce
   * qui correspond � la distance num�rique entre les deux cha�nes de caract�res.
   *
   * @throws NumberFormatException si la cha�ne de caract�res ne contient pas un entier lisible
   */
  public static int compareNumerically(String s1, String s2, boolean ascending) {
    int compare;
    if (null == s1 && null == s2) {
      compare = 0;
    } else if (null == s1) {
      compare = ascending ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    } else if (null == s2) {
      compare = ascending ? Integer.MAX_VALUE : Integer.MIN_VALUE;
    } else {
      try {
        return ascending ? Integer.parseInt(s1) - Integer.parseInt(s2) : Integer.parseInt(s2) - Integer.parseInt(s1);
      } catch (NumberFormatException ex) {
        return 0; 
      }
    }
    return compare;
  }

}
