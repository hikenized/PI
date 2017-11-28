package com.efluid.ecore.temps.businessobject;

import com.efluid.ecore.commun.businessobject.IPossedePeriode;

/**
 * Cette interface d�finit un objet qui poss�de une p�riode et une base de temps.
 */
public interface IPossedePeriodeEtBaseTemps extends IPossedePeriode {

  /** Cette m�thode retourne la base de temps. */
  public BaseTemps getBaseTempsNonTransitif();

}
