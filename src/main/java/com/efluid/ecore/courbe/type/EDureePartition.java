package com.efluid.ecore.courbe.type;

import java.util.*;

import com.imrglobal.framework.type.EnumType;
import com.imrglobal.framework.utils.DateUtils;

import com.hermes.arc.commun.type.HermesNLSEnumType;

import com.efluid.ecore.temps.type.EPasTemps;

/**
 * Cette classe repr�sente les dur�es possibles des partitions valeurs
 */
public class EDureePartition extends HermesNLSEnumType {

  private static final int _MOIS = 0;
  public static final EDureePartition MOIS = new EDureePartition(_MOIS);
  private static final int _TRIMESTRE = 1;
  public static final EDureePartition TRIMESTRE = new EDureePartition(_TRIMESTRE);
  private static final int _ANNEE = 2;
  public static final EDureePartition ANNEE = new EDureePartition(_ANNEE);
  private static final int _DEUX_ANNEES = 3;
  public static final EDureePartition DEUX_ANNEES = new EDureePartition(_DEUX_ANNEES);

  private EDureePartition(int v) {
    setValue(v);
  }

  public static EnumType from_int(int value) {
    switch (value) {
      case _MOIS:
        return MOIS;
      case _TRIMESTRE:
        return TRIMESTRE;
      case _ANNEE:
        return ANNEE;
      case _DEUX_ANNEES:
        return DEUX_ANNEES;
      default:
        return null;
    }
  }

  @Override
  public Iterator<EDureePartition> iterator() {
    List<EDureePartition> l = new ArrayList<EDureePartition>();

    l.add(MOIS);
    l.add(TRIMESTRE);
    l.add(ANNEE);
    l.add(DEUX_ANNEES);

    return l.iterator();
  }

  /**
   * Retourne une date sans temps pour la premi�re journ�e de la p�riode contenant cette date. Par exemple, si la dur�e de la partition est � un mois, et que la date pass�e en argument est le 12
   * f�vrier 2010 12:00, alors la date retourn�e sera le 1 f�vrier 2010 00:00.
   */
  public Date obtenirDateDebutPeriode(Date date) {
    switch (getValue()) {
      case _MOIS:
        return EPasTemps.MENSUEL.obtenirDateDebutPeriode(date);
      case _TRIMESTRE:
        return EPasTemps.TRIMESTRIEL.obtenirDateDebutPeriode(date);
      case _ANNEE:
        return EPasTemps.ANNUEL.obtenirDateDebutPeriode(date);
      case _DEUX_ANNEES:
        /* on met le d�but aux ann�es paires */
        return DateUtils.getDate(DateUtils.getYear(date) - (DateUtils.getYear(date) % 2), 1, 1);
      default:
        return null;
    }
  }

  /** Retourne la premi�re date de la prochaine p�riode pour cette dur�e. */
  public Date prochaineDate(Date date) {
    return ajouterDureeADate(date, 1);
  }

  /** Retourne la date pr�c�dente pour cette dur�e. */
  public Date obtenirDatePrecedente(Date date) {
    return ajouterDureeADate(date, -1);
  }

  private Date ajouterDureeADate(Date date, int nombreApplicationsDuree) {
    switch (getValue()) {
      case _MOIS:
        return DateUtils.addMonths(date, 1 * nombreApplicationsDuree);
      case _TRIMESTRE:
        return DateUtils.addMonths(date, 3 * nombreApplicationsDuree);
      case _ANNEE:
        return DateUtils.addYears(date, 1 * nombreApplicationsDuree);
      case _DEUX_ANNEES:
        return DateUtils.addYears(date, 2 * nombreApplicationsDuree);
      default:
        return null;
    }
  }
}