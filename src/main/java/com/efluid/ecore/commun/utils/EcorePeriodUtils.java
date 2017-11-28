package com.efluid.ecore.commun.utils;

import java.util.*;

import com.imrglobal.framework.type.Period;
import com.imrglobal.framework.utils.DateUtils;

import com.efluid.ecore.commun.businessobject.IPossedePeriode;
import com.efluid.ecore.temps.businessobject.IPossedePeriodeEtBaseTemps;
import com.efluid.ecore.temps.type.EPasTemps;

/**
 * Classe utilitaire pour la manipulation des {@link Period}.
 */
public class EcorePeriodUtils {

  /**
   * Retourne une nouvelle période issue de la première, déplacée sur la nouvelle date de début.
   *
   */
  public static Period deplacerPeriode(Period periode, Date dateDebut) {
    return new Period(dateDebut, periode.getTimeGap());
  }

  /**
   * Retourne la période passée en argument, étendue avec la date nouvelleLimite Si nouvelleLimite est incluse dans la période passée en argument, la méthode retourne une période identique
   *
   */
  public static Period etendreLimite(Period period, Date nouvelleLimite) {
    Period result = null;
    if (EcorePeriodUtils.getPeriodeVide().getStartOfPeriod().equals(period.getStartOfPeriod()) && EcorePeriodUtils.getPeriodeVide().getEndOfPeriod().equals(period.getEndOfPeriod())) {
      result = new Period(nouvelleLimite, nouvelleLimite);
    } else if (period.isInPeriod(nouvelleLimite)) {
      result = period;
    } else {
      if (period.getStartOfPeriod().after(nouvelleLimite)) {
        result = new Period(nouvelleLimite, period.getEndOfPeriod());
      } else if (period.getEndOfPeriod().before(nouvelleLimite)) {
        result = new Period(period.getStartOfPeriod(), nouvelleLimite);
      }
    }
    return result;
  }

  /**
   * Retourne le moment de début de cette période. Si ce moment n'est pas défini (dans le cas d'une période, ouverte ou infinie), alors la valeur est {@link Long#MIN_VALUE}.
   *
   */
  public static long getDebutDePeriode(Period periode) {
    return periode.isInfinite() || periode.isStartless() ? Long.MIN_VALUE : periode.getStartOfPeriod().getTime();
  }

  /**
   * Retourne le moment de fin de cette période. Si ce moment n'est pas défini (dans le cas d'une période, ouverte ou infinie), alors la valeur est {@link Long#MAX_VALUE}.
   *
   */
  public static long getFinDePeriode(Period periode) {
    return periode.isInfinite() || periode.isEndless() ? Long.MAX_VALUE : periode.getEndOfPeriod().getTime();
  }

  /**
   * Retourne l'intersection des 2 périodes passées en argument. Cette intersection est une nouvelle instance de période. Si les 2 périodes sont disjointes (sans recouvrement), la méthode retourne une
   * période vide.
   *
   * @param period1 peut être nulle
   * @param period2 peut être nulle
   */
  public static Period getIntersection(Period period1, Period period2) {
    long debut = Long.MIN_VALUE, fin = Long.MAX_VALUE;
    if (null == period1 && null == period2) {
      return EcorePeriodUtils.getPeriodeVide();
    } else if (null == period1 || null == period2) {
      return EcorePeriodUtils.getPeriodeVide();
    } else if (period1.isEmpty() || period2.isEmpty()) {
      return EcorePeriodUtils.getPeriodeVide();
    } else {
      debut = Math.max(getDebutDePeriode(period1), getDebutDePeriode(period2));
      fin = Math.min(getFinDePeriode(period1), getFinDePeriode(period2));
      if (debut > fin) {
        return EcorePeriodUtils.getPeriodeVide();
      }
    }
    return new Period(new Date(debut), new Date(fin));
  }

  /**
   * Fait quelque chose.
   *
   */
  public static Period getPeriodComplete(Period period1) {
    Period periode = new Period();
    if (period1 != null) {
      if (!period1.equals(EcorePeriodUtils.getPeriodeInfinie())) {
        if (!period1.equals(EcorePeriodUtils.getPeriodeVide())) {
          Date startDate = EcoreDateUtils.constructDateGMT(period1.getStartOfPeriod(), 0, 0, 0);
          Date endDate = EcoreDateUtils.constructDateGMT(period1.getEndOfPeriod(), 23, 30, 0);
          Calendar simple = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
          simple.setTime(startDate);
          Date timeStart = simple.getTime();
          simple.setTime(period1.getStartOfPeriod());
          Date timeStartPeriod = simple.getTime();
          if (!timeStart.equals(timeStartPeriod)) {
            periode.setStartOfPeriod(EcoreDateUtils.constructDateGMT(com.imrglobal.framework.utils.DateUtils.addDays(period1.getStartOfPeriod(), 1), 0, 0, 0));
          } else {
            periode.setStartOfPeriod(period1.getStartOfPeriod());
          }
          simple.setTime(endDate);
          Date timeEnd = simple.getTime();
          simple.setTime(period1.getEndOfPeriod());
          Date timeEndPeriod = simple.getTime();
          if (timeEnd.equals(timeEndPeriod)) {
            periode.setEndOfPeriod(period1.getEndOfPeriod());
          } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(period1.getEndOfPeriod());
            cal.add(Calendar.DAY_OF_MONTH, -1);
            periode.setEndOfPeriod(EcoreDateUtils.constructDateGMT(cal.getTime(), 23, 30, 0));

          }
        }
      }
    }

    return periode;
  }

  /**
   * Cette méthode retourne la période sur laquelle des valeurs sont définies pour la courbe. S'il n'y a pas de grandeur, ça correspond à null.
   *
   */
  public static Period getPeriode(Collection<? extends IPossedePeriode> possedentPeriode) {
    Date dateDebut = null;
    Date dateFin = null;

    for (IPossedePeriode possedePeriode : possedentPeriode) {
      Period periode = possedePeriode.getPeriode();

      if (periode != null && !periode.isInfinite() && !periode.isStartless()) {
        if (dateDebut != null) {
          dateDebut = com.imrglobal.framework.utils.DateUtils.min(dateDebut, periode.getStartOfPeriod());
        } else {
          dateDebut = periode.getStartOfPeriod();
        }
      }

      if (periode != null && !periode.isInfinite() && !periode.isEndless()) {
        if (dateFin != null) {
          dateFin = com.imrglobal.framework.utils.DateUtils.max(dateFin, periode.getEndOfPeriod());
        } else {
          dateFin = periode.getEndOfPeriod();
        }
      }
    }

    if (null == dateDebut || null == dateFin) {
      return null;
    }
    return new Period(dateDebut, dateFin);
  }

  /**
   * Cette méthode retourne la période sur laquelle des valeurs sont définies pour la grandeur. Cette méthode diffère de la méthode {@link PeriodUtils#getPeriode()} dans le sens où elle étend la
   * période des pas de temps de chaque périodes.
   *
   */
  public static Period getPeriodeAffichage(Collection<? extends IPossedePeriodeEtBaseTemps> possedentPeriodeEtBaseTemps) {
    Date dateDebut = null;
    Date dateFin = null;

    for (IPossedePeriodeEtBaseTemps possedePeriodeEtBaseTemps : possedentPeriodeEtBaseTemps) {
      Period periode = possedePeriodeEtBaseTemps.getPeriode();
      EPasTemps pasTemps = null;
      if (possedePeriodeEtBaseTemps.getBaseTempsNonTransitif() != null && possedePeriodeEtBaseTemps.getBaseTempsNonTransitif().getPasTempsValeurs() != null) {
        pasTemps = possedePeriodeEtBaseTemps.getBaseTempsNonTransitif().getPasTempsValeurs();
      }

      if (periode != null && !periode.isInfinite() && !periode.isStartless()) {
        if (dateDebut != null) {
          dateDebut = com.imrglobal.framework.utils.DateUtils.min(dateDebut, periode.getStartOfPeriod());
        } else {
          dateDebut = periode.getStartOfPeriod();
        }
      }

      if (periode != null && !periode.isInfinite() && !periode.isEndless()) {
        Date dateFinAjusteeSuivante = (pasTemps != null) ? pasTemps.obtenirProchaineDate(periode.getEndOfPeriod()) : periode.getEndOfPeriod();
        if (dateFin != null) {
          dateFin = com.imrglobal.framework.utils.DateUtils.max(dateFin, com.imrglobal.framework.utils.DateUtils.addMinutes(dateFinAjusteeSuivante, -1));
        } else {
          dateFin = com.imrglobal.framework.utils.DateUtils.addMinutes(dateFinAjusteeSuivante, -1);
        }
      }
    }

    if (null == dateDebut || null == dateFin) {
      return null;
    }
    return new Period(dateDebut, dateFin);
  }

  /**
   * Retourne une nouvelle période infinie. <b>À utiliser dans tous les cas contre {@link Period#INFINITE}</b>.
   *
   */
  public static Period getPeriodeInfinie() {
    return new Period(new Date(Long.MIN_VALUE), new Date(Long.MAX_VALUE));
  }

  /**
   * Retourne une période ]limiteDebutOuverte;limiteFinFermee] à partir d'une période [limiteDebutFermee;limiteFinFermee] et d'un pas de temps.
   *
   */
  public static Period getPeriodLimiteDebutOuverte(Period periodLimiteDebutFermee, EPasTemps pasTemps) {
    Period result = new Period();
    if (periodLimiteDebutFermee.isEmpty() || periodLimiteDebutFermee.isStartless() || periodLimiteDebutFermee.isInfinite()) {
      Date limiteDebut = periodLimiteDebutFermee.getStartOfPeriod();
      result.setStartOfPeriod(limiteDebut);
    } else {
      Date limiteDebutOuverte = pasTemps.obtenirDatePrecedente(periodLimiteDebutFermee.getStartOfPeriod());
      result.setStartOfPeriod(limiteDebutOuverte);
    }
    result.setEndOfPeriod(periodLimiteDebutFermee.getEndOfPeriod());
    return result;
  }

  /** Retour une période [limiteDebutFermee;limiteFinFermee] à partir d'une période [limiteDebutFermee;limiteFinOuverte[ et d'un pas de temps. */
  public static Period getPeriodLimiteFinFermee(Period periodLimiteFinOuverte, EPasTemps pasTemps) {
    Period result = new Period();
    if (pasTemps == null || periodLimiteFinOuverte.isEmpty() ||
        periodLimiteFinOuverte.isEndless() || periodLimiteFinOuverte.isInfinite()) {
      Date limiteFin = periodLimiteFinOuverte.getEndOfPeriod();
      result.setEndOfPeriod(limiteFin);
    } else {
      Date limiteFinFermee = pasTemps.obtenirDatePrecedente(periodLimiteFinOuverte.getEndOfPeriod());
      result.setEndOfPeriod(limiteFinFermee);
    }
    result.setStartOfPeriod(periodLimiteFinOuverte.getStartOfPeriod());
    return result;
  }

  /**
   * Retourne une période [limiteDebutFermee;limiteFinOuverte[ à partir d'une période [limiteDebutFermee;limiteFinFermee] et d'un pas de temps.
   *
   */
  public static Period getPeriodLimiteFinOuverte(Period periodLimiteFinFermee, EPasTemps pasTemps) {
    Period result = null;
    if (periodLimiteFinFermee != null) {
      result = new Period();
      result.setStartOfPeriod(periodLimiteFinFermee.getStartOfPeriod());
      if (periodLimiteFinFermee.isEndless() || periodLimiteFinFermee.isInfinite()) {
        Date limiteFin = periodLimiteFinFermee.getEndOfPeriod();
        result.setEndOfPeriod(limiteFin);
      } else {
        Date limiteFinOuverte = pasTemps.obtenirProchaineDate(periodLimiteFinFermee.getEndOfPeriod());
        result.setEndOfPeriod(limiteFinOuverte);
      }
    }
    return result;
  }

  /**
   * Retourne une période [limiteDebutFermee;limiteFinOuverte[ à partir d'une période [limiteDebutFermee;limiteFinFermee].
   *
   */
  public static Period getPeriodLimiteFinOuverte(Period periodLimiteFinFermee) {
    Period result = null;
    if (periodLimiteFinFermee != null) {
      result = new Period();
      result.setStartOfPeriod(periodLimiteFinFermee.getStartOfPeriod());
      if (periodLimiteFinFermee.isEndless() || periodLimiteFinFermee.isInfinite()) {
        Date limiteFin = periodLimiteFinFermee.getEndOfPeriod();
        result.setEndOfPeriod(limiteFin);
      } else {
        Date limiteFinOuverte = DateUtils.addSeconds(periodLimiteFinFermee.getEndOfPeriod(), 1);
        result.setEndOfPeriod(limiteFinOuverte);
      }
    }
    return result;
  }

  /**
   * Retourne une période sans heure complète.
   *
   * A partir d'une période en heure locale, par exemple 01/01/2012 00:00 (local) - 31/12/2012 00:00 (local), cette méthode renvoie une période étendue sur toute la journée en heures GMT 01/01/2012
   * 00:00 (GMT) - 31/12/2012 23:30 (GMT).
   *
   */
  public static Period getPeriodSansHeureComplete(Period period1) {
    Period periode = new Period();
    if (period1 != null) {
      if (!period1.equals(EcorePeriodUtils.getPeriodeInfinie())) {
        if (!period1.equals(EcorePeriodUtils.getPeriodeVide())) {
          Date startDate = EcoreDateUtils.constructDateGMT(period1.getStartOfPeriod(), 0, 0, 0);
          Date endDate = EcoreDateUtils.constructDateGMT(period1.getEndOfPeriod(), 23, 30, 0);
          Calendar simple = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
          simple.setTime(startDate);
          Date timeStart = simple.getTime();
          periode.setStartOfPeriod(timeStart);

          simple.setTime(endDate);
          Date timeEnd = simple.getTime();
          periode.setEndOfPeriod(timeEnd);
        }
      }
    }
    return periode;
  }

  /**
   * Retourne une nouvelle période vide. <b>À utiliser dans tous les cas contre {@link Period#EMPTY}</b>.
   *
   */
  public static Period getPeriodeVide() {
    return new Period();
  }

  /**
   * Retourne la "reunion englobante" des 2 {@link Period} passées en argument. Cette réunion est une nouvelle instance de période. <br>
   * Si les 2 périodes sont disjointes (sans recouvrement), alors la méthode retourne une période allant de la date de début la plus petit a la date de fin la plus grande. Dans ce cas, on "englobe" la
   * période qui n'est couverte par aucune des 2 périodes. <br>
   * Si une des deux périodes est nulle, on l'ignore et on utilise l'autre. <br>
   * Le seul cas de retour null est lorsque les deux périodes sont nulles.
   *
   */
  public static Period getUnionEnglobante(Period period1, Period period2) {
    long debut = Long.MIN_VALUE;
    long fin = Long.MAX_VALUE;

    if (null == period1 && null == period2) {
      return null;
    } else if (EcorePeriodUtils.getPeriodeVide().equals(period1) && EcorePeriodUtils.getPeriodeVide().equals(period2)) {
      return EcorePeriodUtils.getPeriodeVide();
    } else if (((null == period1) && EcorePeriodUtils.getPeriodeVide().equals(period2)) || ((null == period2) && EcorePeriodUtils.getPeriodeVide().equals(period1))) {
      return EcorePeriodUtils.getPeriodeVide();
    } else if ((null == period1) || EcorePeriodUtils.getPeriodeVide().equals(period1)) {
      debut = getDebutDePeriode(period2);
      fin = getFinDePeriode(period2);
    } else if ((null == period2) || EcorePeriodUtils.getPeriodeVide().equals(period2)) {
      debut = getDebutDePeriode(period1);
      fin = getFinDePeriode(period1);
    } else {
      debut = Math.min(getDebutDePeriode(period1), getDebutDePeriode(period2));
      fin = Math.max(getFinDePeriode(period1), getFinDePeriode(period2));
    }

    return new Period(new Date(debut), new Date(fin));
  }

  /**
   * voir {@link PeriodUtils#getUnionEnglobante(Period, Period)}
   *
   */
  public static Period getUnionEnglobante(Collection<Period> periodes) {
    Period periodRetour = null;

    for (Period period : periodes) {
      if (periodRetour == null) {
        periodRetour = period;
      } else {
        periodRetour = EcorePeriodUtils.getUnionEnglobante(periodRetour, period);
      }
    }

    return periodRetour;
  }

  /**
   * Retourne la "réunion stricte" d'un ensemble de périodes avec une période. La réunion stricte est une collection de {@link Period} où aucune périodes ne se recouvre. La liste retournée est triée
   * par date de début de chaque périodes. <br>
   * <br>
   * <u>exemple 1</u> : si on passe en argument
   * <li>[[01/01/2010;31/01/2010], [01/04/2010;30/04/2010], [15/01/2010;15/02/2010]]</li> <br>
   * La méthode retourne la collection suivante :
   * <li>[[01/01/2010;15/02/2010], [01/04/2010;30/04/2010]]</li> <br>
   * <br>
   * <u>exemple 2</u> : si on passe en argument
   * <li>[[01/01/2010;31/01/2010], [01/04/2010;30/04/2010], [15/01/2010;15/04/2010]]</li> <br>
   * La méthode retourne la collection suivante :
   * <li>[[01/01/2010;30/04/2010]]</li>
   *
   */
  public static List<Period> getUnionStricte(Collection<Period> periodes) {
    List<Period> unionStricte;
    if ((periodes != null) && (periodes.size() > 1)) {
      unionStricte = new ArrayList<Period>();
      List<Period> periodesTriees = EcorePeriodUtils.trierParDateDebut(periodes);

      Period periodeReunie = new Period();
      periodeReunie.setStartOfPeriod(periodesTriees.get(0).getStartOfPeriod());
      periodeReunie.setEndOfPeriod(periodesTriees.get(0).getEndOfPeriod());
      int index = 1;
      while (index < periodesTriees.size()) {
        Period periodeCourante = periodesTriees.get(index);
        boolean periodeCouranteChevauchePeriodeReunie = !EcorePeriodUtils.getIntersection(periodeReunie, periodeCourante).isEmpty();
        if (periodeCouranteChevauchePeriodeReunie) {
          periodeReunie = EcorePeriodUtils.getUnionEnglobante(periodeReunie, periodeCourante);
        } else {
          unionStricte.add(periodeReunie);
          periodeReunie = new Period();
          periodeReunie.setStartOfPeriod(periodesTriees.get(index).getStartOfPeriod());
          periodeReunie.setEndOfPeriod(periodesTriees.get(index).getEndOfPeriod());
        }
        index++;
      }
      unionStricte.add(periodeReunie);
    } else {
      unionStricte = new ArrayList<Period>(periodes);
    }
    return unionStricte;
  }

  /**
   * Retourne une liste de périodes triées par date de début de chaque période.
   *
   */
  public static List<Period> trierParDateDebut(Collection<Period> periodes) {
    LinkedList<Period> periodesTriees = new LinkedList<Period>();
    for (Period periode : periodes) {
      int index = 0;
      for (Period periodeTriee : periodesTriees) {
        if (periodeTriee.getStartOfPeriod().before(periode.getStartOfPeriod())) {
          index++;
        } else {
          break;
        }
      }
      periodesTriees.add(index, periode);
    }
    return periodesTriees;
  }

  /**
   * Retourne la différence de [periode] - [periodeASoustraire]. Par exemple :
   * <li>[01/01/2000 00:00 - 01/01/2001 00:00] - [01/03/2000 00:00 - 01/03/2001 00:00] = [01/01/2000 00:00 - 01/03/2000 00:00]</li>
   * <li>[01/01/2000 00:00 - 01/01/2001 00:00] - [01/03/2000 00:00 - 01/06/2000 00:00] = [[01/01/2000 00:00 - 01/03/2000 00:00],[01/06/2000 00:00 01/01/2001 00:00]]</li>
   *
   * @return la liste retournée possède, 0, 1 ou 2 valeurs :
   *         <li>si le résultat est une période vide, la liste retournée est vide</li>
   *         <li>si la période à soustraire n'est pas incluse dans la période, le résultat est contigu, et la liste retournée a une seule valeur</li>
   *         <li>si la période à soustraire est incluse dans la période, le résultat est en 2 partie : une partie avant la période à soustraire et une partie après. Dans ce cas la liste retournée
   *         possède 2 valeurs.</li>
   */
  public static List<Period> getDifference(Period periode, Period periodeASoustraire) {
    List<Period> periodesDifference = null;
    if (Period.EMPTY.equals(periodeASoustraire)) {
      periodesDifference = Arrays.asList(new Period(periode.getStartOfPeriod(), periode.getEndOfPeriod()));
    } else if (Period.INFINITE.equals(periodeASoustraire)) {
      periodesDifference = Arrays.asList(new Period(Period.EMPTY.getStartOfPeriod(), Period.EMPTY.getEndOfPeriod()));
    } else if (periode.getStartOfPeriod().after(periodeASoustraire.getEndOfPeriod())) {
      // Cas 1 :
      // période : ---------------------------
      // période à soustraire : -----
      // résultat : ---------------------------
      Period difference = new Period(periode.getStartOfPeriod(), periode.getEndOfPeriod());
      periodesDifference = Arrays.asList(difference);
    } else if ((periodeASoustraire.getStartOfPeriod().before(periode.getStartOfPeriod())
        || periodeASoustraire.getStartOfPeriod().equals(periode.getStartOfPeriod()))
        && (periodeASoustraire.getEndOfPeriod().after(periode.getStartOfPeriod())
            || periodeASoustraire.getEndOfPeriod().equals(periode.getStartOfPeriod()))
        && periode.getEndOfPeriod().after(periodeASoustraire.getEndOfPeriod())) {
      // Cas 2 :
      // période : ---------------------------
      // période à soustraire : ---------------
      // résultat : --------------------
      Period difference = new Period(periodeASoustraire.getEndOfPeriod(), periode.getEndOfPeriod());
      periodesDifference = Arrays.asList(difference);
    } else if (periode.getStartOfPeriod().before(periodeASoustraire.getStartOfPeriod())
        && periode.getEndOfPeriod().after(periodeASoustraire.getStartOfPeriod())
        && periode.getStartOfPeriod().before(periodeASoustraire.getEndOfPeriod())
        && periode.getEndOfPeriod().after(periodeASoustraire.getEndOfPeriod())) {
      // Cas 3 :
      // période : ---------------------------
      // période à soustraire : ---------
      // résultat : --- ---------------
      Period difference1 = new Period(periode.getStartOfPeriod(), periodeASoustraire.getStartOfPeriod());
      Period difference2 = new Period(periodeASoustraire.getEndOfPeriod(), periode.getEndOfPeriod());
      periodesDifference = Arrays.asList(difference1, difference2);
    } else if (periode.getStartOfPeriod().before(periodeASoustraire.getStartOfPeriod())
        && (periode.getEndOfPeriod().after(periodeASoustraire.getStartOfPeriod())
            || periode.getEndOfPeriod().equals(periodeASoustraire.getStartOfPeriod()))
        && (periode.getEndOfPeriod().before(periodeASoustraire.getEndOfPeriod())
            || periode.getEndOfPeriod().equals(periodeASoustraire.getEndOfPeriod()))) {
      // Cas 4 :
      // période : ---------------------------
      // période à soustraire : ---------
      // résultat : ------------------------
      Period difference = new Period(periode.getStartOfPeriod(), periodeASoustraire.getStartOfPeriod());
      periodesDifference = Arrays.asList(difference);
    } else if (periode.getEndOfPeriod().before(periodeASoustraire.getStartOfPeriod())) {
      // Cas 5 :
      // période : ---------------------------
      // période à soustraire : ---------
      // résultat : ---------------------------
      Period difference = new Period(periode.getStartOfPeriod(), periode.getEndOfPeriod());
      periodesDifference = Arrays.asList(difference);
    } else if ((periodeASoustraire.getStartOfPeriod().before(periode.getStartOfPeriod())
        || periodeASoustraire.getStartOfPeriod().equals(periode.getStartOfPeriod()))
        && (periodeASoustraire.getEndOfPeriod().after(periode.getEndOfPeriod())
            || periodeASoustraire.getEndOfPeriod().equals(periode.getEndOfPeriod()))) {
      // Cas 6 :
      // période : ---------------------------
      // période à soustraire : ---------------------------------------
      // résultat : vide
      periodesDifference = new ArrayList<Period>();
    }
    return periodesDifference;
  }

  /**
   * Retourne vrai si la période est nulle ou infinie.
   *
   */
  public static boolean isNullOuInfinie(Period periode) {
    return null == periode || periode.isInfinite();
  }

  /**
   * Retourne vrai si la période est nulle ou ouverte. Une période ouverte est définie par un manque de fin ou de début.
   *
   */
  public static boolean isNullOuOuverte(Period periode) {
    return null == periode || periode.isStartless() || periode.isEndless();
  }

  /**
   * Retourne vrai si la période est nulle ou vide.
   *
   */
  public static boolean isNullOuVide(Period periode) {
    return null == periode || periode.isEmpty();
  }

  /**
   * Ajoute cette quantité de padding à gauche et à droite de la période.
   *
   * @param periode ne peut être nul, ouverte ou infinie
   */
  public static Period addPadding(Period periode, double pourcentage) {
    Date start = periode.getStartOfPeriod(), end = periode.getEndOfPeriod();
    long timeSpawn = end.getTime() - start.getTime();
    return new Period(new Date((long) (start.getTime() - timeSpawn * pourcentage)), new Date((long) (end.getTime() + timeSpawn * pourcentage)));
  }

  /** Cette méthode retourne true si au moins deux périodes se chevauchent */
  public static boolean isChevauchement(Collection<Period> periodes) {
    Stack<Period> pilePeriodes = new Stack<>();
    pilePeriodes.addAll(periodes);
    while (pilePeriodes.size() > 0) {
      if (isChevauchement(pilePeriodes, pilePeriodes.pop())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Teste s'il y a un chevauchement entre la liste des périodes et la période à tester<br/>
   * Attention la listes de périodes ne doit pas contenir la période à tester
   */
  public static boolean isChevauchement(List<? extends Period> periodes, Period periodeATester) {
    for (Period period : periodes) {
      if (isChevauchement(period, periodeATester)) {
        return true;
      }
    }
    return false;
  }

  /** Teste s'il y a un chevauchement entre deux périodes */
  public static boolean isChevauchement(Period periode1, Period periode2) {
    return !Period.EMPTY.equals(periode1.getIntersection(periode2));
  }

  /** Cette méthode retourne les années pleines sous forme de périodes. */
  public static List<Period> getAnneesPleines(Period periode, EPasTemps pasTemps) {
    List<Period> anneesPleines = new ArrayList<>();
    if (periode != null && periode.getEndOfPeriod() != null) {
      Date dateFin = periode.getEndOfPeriod();
      Date dateDebut = DateUtils.getDate(DateUtils.getYear(dateFin), 1, 1);
      dateFin = pasTemps.obtenirDatePrecedente(DateUtils.addYears(dateDebut, 1));
      if (!DateUtils.dateTimeAsString(dateFin).equals(DateUtils.dateTimeAsString(periode.getEndOfPeriod()))) {
        dateDebut = DateUtils.addYears(dateDebut, -1);
        dateFin = DateUtils.addYears(dateFin, -1);
      }
      anneesPleines = getAnneesPleines(periode, dateDebut, dateFin);
    }
    return anneesPleines;
  }

  /** Cette méthode retourne les années pleines sur une période donnée. */
  private static List<Period> getAnneesPleines(Period periode, Date dateDebut, Date dateFin) {
    List<Period> anneesPleines = new ArrayList<>();
    if (dateDebut.compareTo(periode.getStartOfPeriod()) >= 0 && dateFin.compareTo(periode.getEndOfPeriod()) <= 0) {
      anneesPleines.add(new Period(dateDebut, dateFin));
      dateDebut = DateUtils.addYears(dateDebut, -1);
      dateFin = DateUtils.addYears(dateFin, -1);
      anneesPleines.addAll(getAnneesPleines(periode, dateDebut, dateFin));
    }
    return anneesPleines;
  }

  /** Cette méthode retourne les années glissantes sous forme de périodes. */
  public static List<Period> getAnneesGlissantes(Period periode, EPasTemps pasTemps) {
    List<Period> anneesGlissantes = new ArrayList<>();
    if (periode != null && periode.getEndOfPeriod() != null) {
      Date dateFin = periode.getEndOfPeriod();
      Date dateDebut = pasTemps.obtenirProchaineDate(DateUtils.addYears(dateFin, -1));
      if (2 == DateUtils.getMonth(dateDebut) && 29 == DateUtils.getDay(dateDebut)) {
        dateDebut = DateUtils.addDays(dateDebut, 1);
      }
      anneesGlissantes = getAnneesGlissantes(periode, pasTemps, dateDebut, dateFin);
    }
    return anneesGlissantes;
  }

  /** Cette méthode retourne les années glissantes sur une période donnée. */
  private static List<Period> getAnneesGlissantes(Period periode, EPasTemps pasTemps, Date dateDebut, Date dateFin) {
    List<Period> anneesGlissantes = new ArrayList<>();
    if (dateDebut.compareTo(periode.getStartOfPeriod()) >= 0 && dateFin.compareTo(periode.getEndOfPeriod()) <= 0) {
      anneesGlissantes.add(new Period(dateDebut, dateFin));
      dateDebut = DateUtils.addYears(dateDebut, -1);
      dateFin = pasTemps.obtenirDatePrecedente(DateUtils.addYears(dateDebut, 1));
      anneesGlissantes.addAll(getAnneesGlissantes(periode, pasTemps, dateDebut, dateFin));
    }
    return anneesGlissantes;
  }

}
