package com.efluid.ecore.courbe.utils;

import java.util.*;
import java.util.function.BinaryOperator;

import com.imrglobal.framework.type.Period;
import com.imrglobal.framework.utils.DateUtils;

import com.hermes.arc.commun.util.Paire;

import com.efluid.ecore.commun.utils.*;
import com.efluid.ecore.courbe.businessobject.*;
import com.efluid.ecore.courbe.type.EDureePartition;
import com.efluid.ecore.temps.businessobject.BaseTemps;
import com.efluid.ecore.temps.type.EPasTemps;

/**
 * Cette classe regroupe des m�thodes utilitaires pour les instances de {@link IGrandeur}.
 */
public class GrandeurUtils {

  /** Compare les dates de d�but (via la p�riode) des deux grandeurs et g�re les cas nuls. Voir {@link #getDateDebut(IGrandeur)} et {@link DateUtils#compareTo(Date, Date)}. */
  public static int compareDateDebut(IGrandeur grandeur1, IGrandeur grandeur2) {
    Date dateDebut1 = getDateDebut(grandeur1);
    Date dateDebut2 = getDateDebut(grandeur2);
    return EcoreDateUtils.compareTo(dateDebut1, dateDebut2);
  }

  /** Compare les dates de fin (via la p�riode) des deux grandeurs et g�re les cas nuls. Voir {@link #getDateFin(IGrandeur)} et {@link DateUtils#compareTo(Date, Date)}. */
  public static int compareDateFin(IGrandeur grandeur1, IGrandeur grandeur2) {
    Date dateFin1 = getDateFin(grandeur1);
    Date dateFin2 = getDateFin(grandeur2);
    return EcoreDateUtils.compareTo(dateFin1, dateFin2);
  }

  /** Retourne la date de d�but de cette grandeur via l'attribut p�riode et traite les cas null. Retourne null si la date d�but est indisponible. La grandeur doit �tre charg�e. */
  public static Date getDateDebut(IGrandeur grandeur) {
    if (null == grandeur || null == grandeur.getPeriode()) {
      return null;
    }
    return grandeur.getPeriode().getStartOfPeriod();
  }

  /** Retourne la date de fin de cette grandeur via l'attribut p�riode et traite les cas null. Retourne null si la date d�but est indisponible. La grandeur doit �tre charg�e. */
  public static Date getDateFin(IGrandeur grandeur) {
    if (null == grandeur || null == grandeur.getPeriode()) {
      return null;
    }
    return grandeur.getPeriode().getEndOfPeriod();
  }

  /** Retourne une liste de grandeurs � plat, c'est-�-dire toutes les grandeurs regroup�es des grandeurs de regroupement seront dans la liste r�sultante (et pas les grandeurs de regroupement). */
  public static Collection<IGrandeur> getGrandeursExplosees(Collection<? extends IGrandeur> grandeurs) {
    Collection<IGrandeur> grandeursExplosees = new ArrayList<>();
    for (IGrandeur grandeur : grandeurs) {

        grandeursExplosees.add(grandeur);
    }
    return grandeursExplosees;
  }

  /** Retourne une liste de grandeurs � plat, c'est-�-dire toutes les grandeurs regroup�es des grandeurs de regroupement seront dans la liste r�sultante (et les grandeurs de regroupement aussi). */
  public static Collection<IGrandeur> getGrandeursExploseesAvecRegroupement(Collection<IGrandeur> grandeurs) {
    Collection<IGrandeur> grandeursExplosees = new ArrayList<IGrandeur>();
    for (IGrandeur grandeur : grandeurs) {

      grandeursExplosees.add(grandeur);
    }
    return grandeursExplosees;
  }

  /**
   * Retourne la premi�re et la derni�re grandeur en se basant sur les informations de date de d�but et de date fin. <br>
   * La premi�re grandeur est la grandeur qui a la plus petite date d�but et la derni�re grandeur est la grandeur qui a la plus grande date de fin. <br>
   * Il est possible que la premi�re et la derni�re grandeur soit la m�me grandeur ! Ce test n'est pas effectu� et dans ce cas de figure l'emplacement A sera identique � l'emplacement B. <br>
   * Autrement, il n'est pas possible que A soit d�fini et pas B, les deux grandeurs sont n�cessairement toutes deux d�finies ou non.
   * 
   * @return la paire de grandeur avec � l'emplacement A la premi�re grandeur et � l'emplacement B la deuxi�me grandeur ; les deux emplacements peuvent �tre nuls.
   */
  public static Paire<IGrandeur, IGrandeur> getPremiereEtDerniereGrandeur(Collection<IGrandeur> grandeurs) {
    IGrandeur first = null, last = null;
    if (null != grandeurs) {
      for (IGrandeur grandeur : grandeurs) {
        if (null == first || ((getDateDebut(grandeur) != null) && GrandeurUtils.compareDateDebut(first, grandeur) > 0)) {
          first = grandeur;
        }
        if (null == last || ((getDateFin(grandeur) != null) && GrandeurUtils.compareDateFin(last, grandeur) < 0)) {
          last = grandeur;
        }
      }
    }
    return new Paire<>(first, last);
  }

  /** M�thode v�rifiant que la grandeur � des valeurs sur toute la p�riode pass�e en param�tre. */
  public static boolean isAvecValeursSurPeriode(IGrandeur grandeur, Period periode) {
    if (!grandeur.getPeriode().isInclude(periode)) {
      return false;
    }
    Date date = grandeur.getPeriode().getStartOfPeriod();
    SortedMap<Date, Valeur> valeurs = grandeur.getValeurs();
    if (valeurs == null || valeurs.isEmpty()) {
      return false;
    }
    while (grandeur.getPeriode().isInPeriod(date)) {
      Valeur valeur = valeurs.get(date);
      if (valeur == null) {
        return false;
      }
      date = grandeur.getBaseTempsNonTransitif().getPasTempsValeurs().obtenirProchaineDate(date);
    }
    return true;
  }

  /**
   * Applique des filtres aux grandeurs pass�es en param�tre pour retourner les grandeurs correspondant aux filtres en param�tres.
   */
  public static Collection<IGrandeur> getGrandeurs(Collection<IGrandeur> grandeurs) {
    return grandeurs;
  }

  /**
   * Met � jour ou retourne une grandeur, r�sultat de l'application de l'op�ration en param�tre sur les valeurs internes des grandeurs en param�tre pour la p�riode param�tr�e. Le calcul est restreint
   * sur l'union des p�riodes des grandeurs.
   */
  public static Grandeur appliquerOperationGrandeurs(Grandeur grandeurPivot, Grandeur grandeur, BinaryOperator<ValeurInterne> operation, Period periodeCalcul, boolean resultatDansNouvelleGrandeur) {
    return appliquerOperationGrandeurs(grandeurPivot, grandeur, operation, periodeCalcul, resultatDansNouvelleGrandeur, true);
  }

  /**
   * Met � jour ou retourne une grandeur, r�sultat de l'application de l'op�ration en param�tre sur les valeurs internes des grandeurs en param�tre pour la p�riode param�tr�e.
   */
  public static Grandeur appliquerOperationGrandeurs(Grandeur grandeurPivot, Grandeur grandeur, BinaryOperator<ValeurInterne> operation, Period periodeCalcul, boolean resultatDansNouvelleGrandeur,
      boolean restreindreCalculSurPeriodeGrandeur) {
    Grandeur grandeurResultat = initialiserGrandeurResultat(grandeurPivot, resultatDansNouvelleGrandeur);
    periodeCalcul = restreindrePeriodeCalculPourOperationNecessitantValeur(restreindreCalculSurPeriodeGrandeur, periodeCalcul, grandeurPivot, grandeur);
    if (!EcorePeriodUtils.isNullOuVide(periodeCalcul)) {
      EDureePartition dureePartition = grandeurPivot.getDureePartition();
      Date dateDebut = dureePartition.obtenirDateDebutPeriode(periodeCalcul.getStartOfPeriod());
      Date dateFin = dureePartition.obtenirDateDebutPeriode(periodeCalcul.getEndOfPeriod());
      appliquerOperationGrandeurs(grandeurResultat, grandeurPivot, grandeur, operation, periodeCalcul, dateDebut, dateFin, dureePartition);
    }
    return grandeurResultat;
  }

  private static Period restreindrePeriodeCalculPourOperationNecessitantValeur(boolean restreindreCalculSurPeriodeGrandeur, Period periodeCalcul, Grandeur grandeurPivot, Grandeur grandeur) {
    if (restreindreCalculSurPeriodeGrandeur) {
      return EcorePeriodUtils.getIntersection(periodeCalcul, EcorePeriodUtils.getUnionEnglobante(grandeurPivot.getPeriode(), grandeur.getPeriode()));
    }
    return periodeCalcul;
  }

  private static Grandeur initialiserGrandeurResultat(Grandeur grandeur, boolean resultatDansNouvelleGrandeur) {
    if (resultatDansNouvelleGrandeur) {
      Grandeur resultat = new Grandeur();
      resultat.setBaseTemps(grandeur.getBaseTempsNonTransitif());
      return resultat;
    }
    return null;
  }

  /** Applique l'op�ration PartitionValeurs par PartitionValeurs sur la p�riode de calcul. */
  private static void appliquerOperationGrandeurs(Grandeur resultat, Grandeur grandeurPivot, Grandeur grandeur, BinaryOperator<ValeurInterne> operation, Period periodeCalcul, Date dateDebut,
      Date dateFin, EDureePartition dureePartition) {
    BaseTemps baseTemps = grandeurPivot.getBaseTempsNonTransitif();
    Date date = dateDebut;
    while (!date.after(dateFin)) {
      if (resultat == null) {
        appliquerOperationSurGrandeurExistante(grandeurPivot, baseTemps, date, dureePartition, grandeurPivot.getPartitionsValeurs(), grandeur.getPartitionsValeurs(), operation, periodeCalcul);
      } else {
        appliquerOperationSurNouvellePartition(resultat, baseTemps, date, dureePartition, grandeurPivot.getPartitionsValeurs(), grandeur.getPartitionsValeurs(), operation, periodeCalcul);
      }
      date = dureePartition.prochaineDate(date);
    }
  }

  private static void appliquerOperationSurGrandeurExistante(Grandeur grandeurAModifier, BaseTemps baseTemps, Date date, EDureePartition dureePartition,
      Map<Date, PartitionValeurs> mapPartitionsGrandeurAModifier, Map<Date, PartitionValeurs> mapPartitionsGrandeur, BinaryOperator<ValeurInterne> operation, Period periodeCalcul) {
    PartitionValeurs partitionValeursInterne = mapPartitionsGrandeurAModifier.get(date);
    if (partitionValeursInterne == null) {
      appliquerOperationSurNouvellePartition(grandeurAModifier, baseTemps, date, dureePartition, null, mapPartitionsGrandeur, operation, periodeCalcul);
    } else {
      appliquerOperationSurPartitionExistante(grandeurAModifier, partitionValeursInterne, mapPartitionsGrandeur, date, operation, periodeCalcul, baseTemps, dureePartition);
    }
  }

  private static void appliquerOperationSurPartitionExistante(Grandeur grandeurAModifier, PartitionValeurs partitionValeursInterne, Map<Date, PartitionValeurs> mapPartitionsGrandeur, Date date,
      BinaryOperator<ValeurInterne> operation, Period periodeCalcul, BaseTemps baseTemps, EDureePartition dureePartition) {
    Date dateDebutValeursPartition = partitionValeursInterne.getValeurs().firstKey();
    Date dateFinValeursPartition = partitionValeursInterne.getValeurs().lastKey();
    PartitionValeurs partitionValeurs = mapPartitionsGrandeur.get(date);
    if (isCalculOptimisePossible(partitionValeursInterne, partitionValeurs, null)) {
      ((MapOptimiseeValeursTemporelles) partitionValeursInterne.getValeurs()).appliquerFonction(partitionValeursInterne, partitionValeurs, operation, periodeCalcul);
    } else {
      appliquerOperationSurSortedMap(partitionValeursInterne, partitionValeursInterne, partitionValeurs, operation, periodeCalcul, date, baseTemps.getPasTempsValeurs());
    }
    if (partitionValeursInterne.getValeurs().isEmpty()) {
      grandeurAModifier.getPartitionsValeurs().remove(date);
    }
    mettreAJourPeriodeGrandeurSuiteAOperation(grandeurAModifier, partitionValeursInterne, date, dateDebutValeursPartition, dateFinValeursPartition, dureePartition, baseTemps.getPasTempsValeurs());
  }

  private static void mettreAJourPeriodeGrandeurSuiteAOperation(Grandeur grandeurAModifier, PartitionValeurs partitionValeursInterne, Date date, Date dateDebutValeursPartition,
      Date dateFinValeursPartition, EDureePartition dureePartition, EPasTemps pasTemps) {
    mettreAJourPeriodeGrandeurSuiteAOperation(grandeurAModifier, pasTemps, dateDebutValeursPartition, partitionValeursInterne.getValeurs(), dureePartition, date, true);
    mettreAJourPeriodeGrandeurSuiteAOperation(grandeurAModifier, pasTemps, dateFinValeursPartition, partitionValeursInterne.getValeurs(), dureePartition, date, false);
  }

  private static void mettreAJourPeriodeGrandeurSuiteAOperation(Grandeur grandeurAModifier, EPasTemps pasTemps, Date dateValeursPartition, SortedMap<Date, Valeur> valeurs,
      EDureePartition dureePartition, Date date, boolean isDateDebut) {
    if (dateValeursPartition != null && dateValeursPartition.equals(isDateDebut ? grandeurAModifier.getDateDebut() : grandeurAModifier.getDateFin())) {
      if (valeurs.isEmpty()) {
        mettreAJourPeriodeGrandeurSiPartitionVidee(grandeurAModifier, dureePartition, date, pasTemps, isDateDebut);
      } else {
        if (isDateDebut) {
          grandeurAModifier.getPeriode().setStartOfPeriod(valeurs.firstKey());
        } else {
          grandeurAModifier.getPeriode().setEndOfPeriod(valeurs.lastKey());
        }
      }
    }
  }

  private static void mettreAJourPeriodeGrandeurSiPartitionVidee(Grandeur grandeurAModifier, EDureePartition dureePartition, Date date, EPasTemps pasTemps, boolean isDateDebut) {
    Date dateProchainePartition;
    if (isDateDebut && !grandeurAModifier.getDateFin().before(dateProchainePartition = dureePartition.prochaineDate(date))) {
      grandeurAModifier.mettreAJourPeriode(dateProchainePartition);
    } else if (!isDateDebut && grandeurAModifier.getDateDebut().before(date)) {
      grandeurAModifier.mettreAJourPeriode(pasTemps.obtenirDatePrecedente(date));
    } else {
      grandeurAModifier.setPeriode(null);
    }
  }

  private static void appliquerOperationSurNouvellePartition(Grandeur resultat, BaseTemps baseTemps, Date date, EDureePartition dureePartition, Map<Date, PartitionValeurs> mapPartitionsGrandeur1,
      Map<Date, PartitionValeurs> mapPartitionsGrandeur2, BinaryOperator<ValeurInterne> operation, Period periodeCalcul) {
    PartitionValeurs partitionValeursPivot = new PartitionValeurs(baseTemps.getPasTempsValeurs(), date, dureePartition);
    PartitionValeurs partitionValeursGrandeur1 = mapPartitionsGrandeur1 != null ? mapPartitionsGrandeur1.get(date) : null;
    PartitionValeurs partitionValeursGrandeur2 = mapPartitionsGrandeur2.get(date);
    if (isCalculOptimisePossible(partitionValeursPivot, partitionValeursGrandeur1, partitionValeursGrandeur2)) {
      MapOptimiseeValeursTemporelles mapOptimisee = (MapOptimiseeValeursTemporelles) partitionValeursPivot.getValeurs();
      mapOptimisee.appliquerFonction(partitionValeursGrandeur1, partitionValeursGrandeur2, operation, periodeCalcul);
    } else {
      appliquerOperationSurSortedMap(partitionValeursPivot, partitionValeursGrandeur1, partitionValeursGrandeur2, operation, periodeCalcul, date, baseTemps.getPasTempsValeurs());
    }
    if (!partitionValeursPivot.getValeurs().isEmpty()) {
      resultat.addToPartitionsValeurs(partitionValeursPivot);
    }
  }

  private static boolean isCalculOptimisePossible(PartitionValeurs partitionValeursPivot, PartitionValeurs partitionValeursGrandeur1, PartitionValeurs partitionValeursGrandeur2) {
    return partitionValeursPivot.getValeurs() instanceof MapOptimiseeValeursTemporelles
        && (partitionValeursGrandeur1 == null || partitionValeursGrandeur1.getValeurs() instanceof MapOptimiseeValeursTemporelles)
        && (partitionValeursGrandeur2 == null || partitionValeursGrandeur2.getValeurs() instanceof MapOptimiseeValeursTemporelles);
  }

  /** Pour traiter le cas des valeurs contenues dans des TreeMap (pas de temps sup�rieurs � une heure) */
  private static void appliquerOperationSurSortedMap(PartitionValeurs partitionValeursAMettreAJour, PartitionValeurs partitionValeurs1, PartitionValeurs partitionValeurs2,
      BinaryOperator<ValeurInterne> operation,
      Period periodeCalcul, Date dateDebutPartition, EPasTemps pasTemps) {
    SortedMap<Date, Valeur> valeurs1 = Optional.ofNullable(partitionValeurs1).map(PartitionValeurs::getValeurs).orElse(null);
    SortedMap<Date, Valeur> valeurs2 = Optional.ofNullable(partitionValeurs2).map(PartitionValeurs::getValeurs).orElse(null);
    Date dateFin = DateUtils.min(periodeCalcul.getEndOfPeriod(), DateUtils.addSeconds(partitionValeursAMettreAJour.getDureePartition().prochaineDate(dateDebutPartition), -1));
    Date dateValeur = recupererDateDebutParcoursValeur(valeurs1, valeurs2, pasTemps, dateDebutPartition, periodeCalcul.getStartOfPeriod());
    while (!dateValeur.after(dateFin)) {
      mettreAJourPartitionValeurSuiteCalculValeurSurSortedMap(dateValeur, operation, valeurs1, valeurs2, partitionValeursAMettreAJour);
      dateValeur = pasTemps.obtenirProchaineDate(dateValeur);
    }
  }

  private static void mettreAJourPartitionValeurSuiteCalculValeurSurSortedMap(Date date, BinaryOperator<ValeurInterne> operation, SortedMap<Date, Valeur> valeurs1, SortedMap<Date, Valeur> valeurs2,
      PartitionValeurs partitionValeursAMettreAJour) {
    ValeurInterne valeurInterne = operation.apply(recupererValeurInterne(valeurs1, date), recupererValeurInterne(valeurs2, date));
    if (valeurInterne != null) {
      Valeur valeur = new Valeur(valeurInterne, new ReferenceTemporelle(date));
      partitionValeursAMettreAJour.addToValeurs(valeur);
    } else {
      partitionValeursAMettreAJour.removeFromValeurs(new Valeur(valeurInterne, new ReferenceTemporelle(date)));
    }
  }

  private static ValeurInterne recupererValeurInterne(SortedMap<Date, Valeur> valeurs, Date date) {
    return Optional.ofNullable(valeurs).map(v -> v.get(date)).map(Valeur::getEnveloppeDouble).orElse(null);
  }

  private static Date recupererDateDebutParcoursValeur(SortedMap<Date, Valeur> valeurs1, SortedMap<Date, Valeur> valeurs2, EPasTemps ePasTemps, Date dateDebutPartition, Date dateDebutPeriodeCalcul) {
    Date dateDebutValeur = DateUtils.min(determinerDatePremiereValeur(valeurs1), determinerDatePremiereValeur(valeurs2));
    return ajusterDateDebutCalcul(dateDebutValeur, dateDebutPeriodeCalcul, dateDebutPartition, ePasTemps);
  }

  public static Date ajusterDateDebutCalcul(Date dateDebutValeur, Date dateDebutPeriodeCalcul, Date dateDebutPartition, EPasTemps ePasTemps) {
    if (dateDebutValeur == null) {
      dateDebutValeur = dateDebutPeriodeCalcul;
    }
    if (dateDebutPeriodeCalcul.after(dateDebutPartition)) {
      dateDebutPartition = dateDebutPeriodeCalcul;
    }
    int nombrePasTemps = (int) ((dateDebutPartition.getTime() - dateDebutValeur.getTime()) / ePasTemps.getDuree().getGap());
    dateDebutValeur = ePasTemps.obtenirProchaineDate(dateDebutValeur, nombrePasTemps);
    return affinerDateDebutCalculPasTempsPres(dateDebutValeur, dateDebutPartition, ePasTemps);
  }

  private static Date affinerDateDebutCalculPasTempsPres(Date dateDebutValeur, Date dateDebutPartition, EPasTemps ePasTemps) {
    if (dateDebutValeur.after(dateDebutPartition)) {
      Date dateTmp = ePasTemps.obtenirDatePrecedente(dateDebutValeur);
      return (dateTmp.before(dateDebutPartition)) ? dateDebutValeur : dateTmp;
    } else if (dateDebutValeur.before(dateDebutPartition)) {
      return ePasTemps.obtenirProchaineDate(dateDebutValeur);
    }
    return dateDebutValeur;
  }

  private static Date determinerDatePremiereValeur(SortedMap<Date, Valeur> valeurs) {
    return (valeurs != null && !valeurs.isEmpty()) ? valeurs.firstKey() : null;
  }
}