package com.efluid.ecore.courbe.businessobject;

import java.util.*;
import java.util.stream.Stream;

import com.imrglobal.framework.type.Period;
import com.imrglobal.framework.utils.UndefinedAttributes;

import com.efluid.ecore.commun.utils.EcorePeriodUtils;
import com.efluid.ecore.courbe.type.EDureePartition;
import com.efluid.ecore.courbe.utils.MapPartitionsValeursTemporelles;
import com.efluid.ecore.temps.businessobject.*;

/**
 * Cette classe implémente une grandeur sous forme d'une map de partitions de valeurs.
 */
public class Grandeur implements IGrandeur {

  

  private EDureePartition dureePartition = EDureePartition.ANNEE;
  private Period periode;
  private Courbe courbe;

  private BaseTemps baseTemps;

  /**
   * Partitions valeurs persistées en base
   */
  private final SortedMap<Date, PartitionValeurs> partitionsValeurs = new TreeMap<>();

  /**
   * <b>Non persisté en base !</b>. Voir {@link #partitionsValeurs}. Contient les valeurs de la grandeur
   */
  private MapPartitionsValeursTemporelles valeurs = null;

  /** <b>Non persisté en base !</b>. Les valeurs issues d'un algorithme */
  private ValeursCalculees valeursCalculees;


  /** Constructeur générique */
  public Grandeur() {
    super();
  }

  public boolean isCalculee() {
    return false;
  }

  /**
   * Retourne les partitions valeurs.
   *
   */
  public SortedMap<Date, PartitionValeurs> getPartitionsValeurs() {
    return this.partitionsValeurs;
  }

  /**
   * Ajoute la partition valeur aux partitions valeurs, met à jour la grandeur dans les partitions valeurs et ajoute les valeurs de la partition valeur à la grandeur.
   *
   */
  public PartitionValeurs addToPartitionsValeurs(PartitionValeurs partitionValeurs) {
    if (partitionValeurs != null) {
      partitionValeurs.setGrandeur(this);
      SortedMap<Date, Valeur> valeurs = partitionValeurs.getValeurs();
      if (!valeurs.isEmpty()) {
        mettreAJourPeriode(valeurs.firstKey());
        mettreAJourPeriode(valeurs.lastKey());
      }
      return this.partitionsValeurs.put(partitionValeurs.getDateDebut(), partitionValeurs);
    }
    return null;
  }

  /**
   * Ajoute les partitions valeurs aux partitions valeurs, met à jour la grandeur dans les partitions valeurs et ajoute les valeurs de la partition valeur à la grandeur.
   *
   */
  public void addAllToPartitionsValeurs(Collection<PartitionValeurs> pv) {
    if (pv != null) {
      for (PartitionValeurs partitionValeurs : pv) {
        addToPartitionsValeurs(partitionValeurs);
      }
    }
  }

  /**
   * Supprime cette partition valeur de la liste des partitions valeurs. Rien d'autre n'est mis à jour.
   *
   */
  public PartitionValeurs removeFromPartitionsValeurs(PartitionValeurs partitionValeurs) {
    if (partitionValeurs != null) {
      return this.partitionsValeurs.remove(partitionValeurs.getDateDebut());
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public Valeur addToValeurs(Valeur valeur) {
    if (valeur != null) {
      getValeursOuInitialise().put(valeur.getDate(), valeur);
      mettreAJourPeriode(valeur.getDate());
      return valeur;
    }
    return null;
  }

  /**
   * met à jour la période de la grandeur par ajout d'une date correspondant à une Valeur 
   */
  public void mettreAJourPeriode(Date date) {
    if (EcorePeriodUtils.isNullOuInfinie(getPeriode())) {
      setPeriode(new Period(date, date));
    } else {
      if (date.before(getPeriode().getStartOfPeriod())) {
        getPeriode().setStartOfPeriod(date);
      } else if (date.after(getPeriode().getEndOfPeriod())) {
        getPeriode().setEndOfPeriod(date);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public void addAllToValeurs(Collection<? extends Valeur> v) {
    if (null != v) {
      for (Valeur valeur : v) {
        this.addToValeurs(valeur);
      }
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>
   * Cette implémentation supprime les partitions valeurs, les valeurs et la période.
   * </p>
   * 
   * @deprecated Utiliser plutôt {@link Grandeur#clearAllValeurs()}.
   */
  @Override
  @Deprecated
  public void clearValeurs() {
    clearAllValeurs();
  }

  /**
   * {@inheritDoc}
   *
   * <p>
   * Cette implémentation supprime les partitions valeurs, les valeurs et la période.
   * </p>
   */
  @Override
  public void clearAllValeurs() {
    this.partitionsValeurs.clear();
    this.valeurs = null;
    getValeursOuInitialise();
    this.periode = null;
  }

  /**
   * {@inheritDoc}
   *
   * <p>
   * Cette implémentation supprime les valeurs et la période.
   * </p>
   */
  @Override
  public void clearValeursSimple() {
    getValeursOuInitialise().clear();
    this.periode = null;
  }

  /** {@inheritDoc} */
  @Override
  public void clearValeursUniquement() {
    getValeursOuInitialise().clear();
  }

  /** {@inheritDoc} */
  @Override
  public Valeur removeFromValeurs(Valeur valeur) {
    if (this.valeurs != null && valeur != null) {

      Valeur oldValeur = this.valeurs.remove(valeur.getDate());

      if (null != oldValeur) {
        // Mise à jour de la période
        if (getPeriode().isEmpty() || this.valeurs.isEmpty()) {
          // Il ne restait qu'un point
          setPeriode(null);
        }
        if (null != getPeriode() && !getPeriode().isInfinite()) {
          if (valeur.getDate().equals(getPeriode().getStartOfPeriod())) {
            getPeriode().setStartOfPeriod(getValeursOuInitialise().firstKey());
          } else if (valeur.getDate().equals(getPeriode().getEndOfPeriod())) {
            getPeriode().setEndOfPeriod(getValeursOuInitialise().lastKey());
          }
        }
      }

      return oldValeur;
    }
    return null;
  }

  /** récupère un stream sur les valeurs internes de la grandeur */
  public Stream<ValeurInterne> getStream() {
    return valeurs.stream();
  }

  /**
   * {@inheritDoc}
   *
   * <p>
   * Cette implémentation se contente de faire une copie de surface sur la liste de valeurs à supprimer, <strong>c'est suffisant puisque le tableau qui contient les valeurs est reconstruit</strong>
   * (dans l'ArrayList). Les valeurs ne sont pas copiées et c'est pas plus mal. En plus, cela retourne deux itérateurs différents parce que ce sont deux instances différents de liste.
   *
   * <p>
   * Dans le cas d'une implémentation particulière de collection, il est possible que cette méthode itère sur la liste différemment (l'itérateur est celui de ArrayList), et il est possible que ça ne
   * fonctionne pas du tout, par exemple si le tableau des valeurs est contenu dans un sous-objet, il ne sera pas copié.
   */
  @Override
  public Collection<Valeur> removeFromValeurs(Collection<Valeur> v) {
    Collection<Valeur> valeursASupprimer = new ArrayList<>(v);
    Collection<Valeur> valeursSupprimee = new ArrayList<>();
    for (Valeur valeur : valeursASupprimer) {
      valeursSupprimee.add(removeFromValeurs(valeur));
    }
    return valeursSupprimee;
  }

  /** {@inheritDoc} */
  @Override
  public SortedMap<Date, Valeur> getValeurs() {
    return Collections.unmodifiableSortedMap(getValeursOuInitialise());
  }

  private SortedMap<Date, Valeur> getValeursOuInitialise() {
    if (this.valeurs == null) {
      this.valeurs = new MapPartitionsValeursTemporelles(this.partitionsValeurs, this.dureePartition, this.baseTemps != null ? this.baseTemps.getPasTempsValeurs() : null, this);
    }

    return this.valeurs;
  }

  /** {@inheritDoc} */
  @Override
  public Courbe getCourbe() {
    return this.courbe;
  }

  /** {@inheritDoc} */
  @Override
  public void setCourbe(Courbe courbe) {
    this.courbe = courbe;
  }

  /**
   * {@inheritDoc}
   * 
   * @deprecated utiliser {@link #getBaseTempsTransitif(ModeleCourbe)} ou {@link #getBaseTempsNonTransitif()}, cette méthode ne devrait être utilisée que par l'archi pour persister le champs baseTemps
   */
  @Override
  @Deprecated
  public BaseTemps getBaseTemps() {
    return this.baseTemps;
  }

  /** {@inheritDoc} */
  @Override
  public BaseTemps getBaseTempsNonTransitif() {
    return this.baseTemps;
  }


  /** {@inheritDoc} */
  @Override
  public void setBaseTemps(BaseTemps baseTemps) {
    this.baseTemps = baseTemps;
  }

  /** {@inheritDoc} */
  @Override
  public ValeursCalculees getValeursCalculees() {
    return this.valeursCalculees;
  }

  /** {@inheritDoc} */
  @Override
  public void setValeursCalculees(ValeursCalculees valeursCalculees) {
    this.valeursCalculees = valeursCalculees;
  }

  /** @return the selectionnable */
  @Override
  public boolean isSelectionnable() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public Period getPeriode() {

    return this.periode;
  }

  /** {@inheritDoc} */
  @Override
  public Period getPeriodeAffichage() {

    Collection<IPossedePeriodeEtBaseTemps> possedentPeriodeEtBaseTemps = new HashSet<>();
    possedentPeriodeEtBaseTemps.add(this);
    return EcorePeriodUtils.getPeriodeAffichage(possedentPeriodeEtBaseTemps);
  }

  /** {@inheritDoc} */
  @Override
  public void setPeriode(Period periode) {
    this.periode = periode;
  }

  /**
   * Retourne la date de début de la période.
   *
   */
  public Date getDateDebut() {
    return Optional.ofNullable(this.periode).map(Period::getStartOfPeriod).orElse(null);
  }

  /**
   * Retourne la date de fin de la période.
   *
   */
  public Date getDateFin() {
    return Optional.ofNullable(this.periode).map(Period::getEndOfPeriod).orElse(null);
  }

  /**
   * Retourne la date de fin de la période.
   *
   */
  public Date getDateFinAffichage() {
    return getPeriodeAffichage().getEndOfPeriod();
  }


  /**
   * {@inheritDoc}
   *
   * <p>
   * Cette implémentation retourne {@link Collections#max(Collection)}.
   */
  @Override
  public double getMax() {
    Valeur max = getValeurs().isEmpty() ? null : Collections.max(getValeurs().values());
    return null == max ? UndefinedAttributes.UNDEF_DOUBLE : max.getValeur();
  }

  /**
   * {@inheritDoc}
   *
   * <p>
   * Cette implémentation retourne {@link Collections#max(Collection)}.
   */
  @Override
  public double getMin() {
    Valeur min = getValeurs().isEmpty() ? null : Collections.min(getValeurs().values());
    return null == min ? UndefinedAttributes.UNDEF_DOUBLE : min.getValeur();
  }

  /**
   * {@inheritDoc}
   *
   * <p>
   * Cette implémentation retourne les valeurs de {@link #getValeurs()}.
   */
  @Override
  public Collection<Valeur> getValeursBase() {
    return Collections.unmodifiableCollection(getValeurs().values());
  }

  /**
   * Retourne la partition valeur en fonction de la valeur.
   *
   * @param valeur
   * @return
   */
  public PartitionValeurs getPartitionValeurs(Valeur valeur) {
    if (valeur != null) {
      // On récupère la partition de valeurs associées
      Date dateDebut = this.dureePartition.obtenirDateDebutPeriode(valeur.getDate());
      return this.partitionsValeurs.get(dateDebut);
    }
    return null;
  }

  /**
   * Met cette durée de partition valeurs.
   *
   */
  public void setDureePartition(EDureePartition dureePartition) {
    this.dureePartition = dureePartition;
  }

  /**
   * Retourne la durée des partitions valeurs.
   *
   */
  public EDureePartition getDureePartition() {
    return this.dureePartition;
  }
}
