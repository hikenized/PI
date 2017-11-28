package com.efluid.ecore.courbe.businessobject;

import java.io.Serializable;
import java.util.*;

import com.imrglobal.framework.type.Period;
import com.imrglobal.framework.utils.UndefinedAttributes;

import com.efluid.ecore.temps.businessobject.BaseTemps;

/**
 * Cette classe impl�mente une liste de valeurs calcul�es associ�es � une p�riode de temps et une base de calcul donn�e.
 */
public class ValeursCalculees implements Serializable, IPossedeValeurs<SortedSet<Valeur>> {

  /** Num�ro de version pour l'interface {@link Serializable}. */
  private static final long serialVersionUID = -4322834406988717648L;

  /** La base de temps de ces valeurs calcul�es. */
  private BaseTemps baseTemps;

  /** La p�riode de ces valeurs calcul�es. */
  private Period periode;

  /** Les valeurs calcul�es. */
  private SortedSet<Valeur> valeurs = new TreeSet<>(Valeur.COMPARATEUR_PAR_DATE);

  /** @return the baseTemps */
  public BaseTemps getBaseTemps() {
    return this.baseTemps;
  }

  /** @param baseTemps the baseTemps to set */
  public void setBaseTemps(BaseTemps baseTemps) {
    this.baseTemps = baseTemps;
  }

  /** @return the periode */
  public Period getPeriode() {
    return this.periode;
  }

  /** @param periode the periode to set */
  public void setPeriode(Period periode) {
    this.periode = periode;
  }

  /** @return the valeurs pour d�tacher toutes les valeurs, utiliser {@link clearValeurs()} plut�t que {@link getValeurs().clear()} afin de mettre � jour la p�riode. */
  public SortedSet<Valeur> getValeurs() {
    return Collections.unmodifiableSortedSet(this.valeurs);
  }

  /**
   * Ajoute la valeur � la liste de valeurs, mettant � jour la p�riode.
   *
   */
  public boolean addToValeurs(Valeur valeur) {
    if (valeur != null) {
      if (this.valeurs.add(valeur)) {
        // Mise � jour de la p�riode
        if (null == getPeriode() || getPeriode().isInfinite()) {
          setPeriode(new Period(valeur.getDate(), valeur.getDate()));
        } else {
          if (valeur.getDate().before(getPeriode().getStartOfPeriod())) {
            getPeriode().setStartOfPeriod(valeur.getDate());
          } else if (valeur.getDate().after(getPeriode().getEndOfPeriod())) {
            getPeriode().setEndOfPeriod(valeur.getDate());
          }
        }
        return true;
      }
    }
    return false;
  }

  /**
   * Ajoute toutes les valeurs de la collection � la liste de valeurs.
   *
   * @see #addToValeurs(Valeur)
   */
  public void addAllToValeurs(Collection<Valeur> allValeurs) {
    for (Valeur valeur : allValeurs) {
      addToValeurs(valeur);
    }
  }

  /**
   * Supprime la valeur de la liste de valeurs, mettant � jour la p�riode.
   *
   */
  public boolean removeFromValeurs(Valeur valeur) {
    if (valeur != null) {
      if (this.valeurs.remove(valeur)) {
        // Mise � jour de la p�riode
        if (getPeriode().isEmpty()) {
          // Il ne restait qu'un point
          setPeriode(null);
        }
        if (null != getPeriode() && !getPeriode().isInfinite() && !this.valeurs.isEmpty()) {
          if (valeur.getDate().equals(getPeriode().getStartOfPeriod())) {
            getPeriode().setStartOfPeriod(this.valeurs.first().getDate());
          } else if (valeur.getDate().equals(getPeriode().getEndOfPeriod())) {
            getPeriode().setEndOfPeriod(this.valeurs.last().getDate());
          }
        }
        return true;
      }
    }
    return false;
  }

  /**
   * Supprime toutes les valeurs, en mettant � jour la p�riode.
   *
   */
  public void clearValeurs() {
    this.valeurs.clear();
    this.periode = null;
  }

  /** Retourne la difference entre le minimum et le maximum. */
  public double getAmplitude() {
    if (getMax() != UndefinedAttributes.UNDEF_DOUBLE && getMin() != UndefinedAttributes.UNDEF_DOUBLE) {
      return getMax() - getMin();
    }
    return UndefinedAttributes.UNDEF_DOUBLE;
  }

  /** Retourne un pourcentage (saisi en param�tre) de l'amplitude des valeurs. */
  public double getPourcentageAmplitude(double pourcentage) {
    if (getAmplitude() != UndefinedAttributes.UNDEF_DOUBLE && pourcentage != UndefinedAttributes.UNDEF_DOUBLE) {
      return getAmplitude() * pourcentage;
    }
    return UndefinedAttributes.UNDEF_DOUBLE;
  }

  /** Retourne la valeur Max + un pourcentage de l'amplitude. On prend une valeur enti�re arrondie. */
  public double getMaxPlusPourcentageAmplitude(double pourcentage) {
    if (getPourcentageAmplitude(pourcentage) != UndefinedAttributes.UNDEF_DOUBLE) {
      return getMax() + getPourcentageAmplitude(pourcentage);
    }
    return UndefinedAttributes.UNDEF_DOUBLE;
  }

  /** Retourne la valeur Min - un pourcentage de l'amplitude. On prend une valeur enti�re arrondie. */
  public double getMinMoinsPourcentageAmplitude(double pourcentage) {
    if (getPourcentageAmplitude(pourcentage) != UndefinedAttributes.UNDEF_DOUBLE) {
      return getMin() - getPourcentageAmplitude(pourcentage);
    }
    return UndefinedAttributes.UNDEF_DOUBLE;
  }

  /**
   * {@inheritDoc}
   *
   * <p>
   * Cette impl�mentation retourne {@link Collections#max(Collection)}.
   */
  @Override
  public double getMax() {
    Valeur max = getValeurs().isEmpty() ? null : Collections.max(getValeurs(), new Comparator<Valeur>() {
      public int compare(Valeur v1, Valeur v2) {
        if (v1 == null) {
          return -1;
        }
        if (v2 == null) {
          return 1;
        }
        Double o1 = v1.getValeur();
        Double o2 = v2.getValeur();

        if (o1 == o2) {
          return 0;
        }
        if (o1 == null || Double.isNaN(o1)) {
          return -1;
        }

        if (o2 == null || Double.isNaN(o2)) {
          return 1;
        }

        return o1.compareTo(o2);
      }
    });
    return null == max ? UndefinedAttributes.UNDEF_DOUBLE : max.getValeur();
  }

  /**
   * {@inheritDoc}
   *
   * <p>
   * Cette impl�mentation retourne {@link Collections#min(Collection)}.
   */
  @Override
  public double getMin() {
    Valeur min = getValeurs().isEmpty() ? null : Collections.min(getValeurs(), new Comparator<Valeur>() {
      public int compare(Valeur v1, Valeur v2) {
        if (v1 == null) {
          return 1;
        }
        if (v2 == null) {
          return -1;
        }
        Double o1 = v1.getValeur();
        Double o2 = v2.getValeur();

        if (o1 == o2) {
          return 0;
        }
        if (o1 == null || Double.isNaN(o1)) {
          return 1;
        }

        if (o2 == null || Double.isNaN(o2)) {
          return -1;
        }

        return o1.compareTo(o2);
      }
    });
    return null == min ? UndefinedAttributes.UNDEF_DOUBLE : min.getValeur();
  }

  /**
   * {@inheritDoc}
   *
   * <p>
   * Cette impl�mentation retourne les valeurs de {@link #getValeurs()}.
   */
  @Override
  public SortedSet<Valeur> getValeursBase() {
    return Collections.unmodifiableSortedSet(getValeurs());
  }

}
