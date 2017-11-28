package com.efluid.type;

import java.util.Collection;

/**
 * Type �num�r� qui contient ses valeurs.
 */
public interface IEnumere {

  /**
   * @return le code de l'�num�r�, le {@link Class#getSimpleName()} pour un �num�r� statique.
   */
  public String getCode();

  /**
   * @return le libell� de l'�num�r�.
   */
  public String getLibelle();

  /**
   * @return les valeurs de l'�num�r�.
   */
  public Collection getValeurs();

}
