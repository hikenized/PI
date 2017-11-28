package com.efluid.ecore.courbe.businessobject;

import java.io.ObjectInput;
import java.util.*;

import com.imrglobal.framework.utils.UndefinedAttributes;

import com.efluid.ecore.courbe.type.EDureePartition;
import com.efluid.ecore.courbe.utils.MapOptimiseeValeursTemporelles;
import com.efluid.ecore.temps.type.EPasTemps;

/**
 * Cette classe repr�sente une partition de valeurs sous forme d'une map de valeurs par r�f�rence temporelle. Cette partition est actuellement impl�ment�e par ann�e.<br>
 * 
 * <p>
 * La classe est s�rialisable (avec gestion de version) par {@link SerializationExterne}.
 */
public class PartitionValeurs {

  /**
   * Sont enregistr�es dans l'ordre les attributs suivants :
   * 
   * <ul>
   * <li>L'attribut {@link #version} sous forme de <code>int</code>
   * <li>La valeur de {@link TreeMap#size()} de l'attribut {@link #valeurs} sous forme de <code>int</code>
   * <li>Toutes les valeurs de {@link #valeurs} sous forme de {@link Valeur} ou {@link ValeurCalendrier}
   * </ul>
   */
  private static final int VERSION_0 = 0;

  /**
   * Ne doit jamais changer. Utilis� par la VM pour d�terminer toutes les classes de {@link PartitionValeurs} qui sont "compatibles", donc mutuellement s�rialisables et d�s�rialisables. Cela n'est pas
   * la version, voir plut�t le champ {@link Valeur#version}.
   */
  private static final long serialVersionUID = 6636814913738974635L;

  /**
   * Incr�menter � chaque changement dans la structure de la classe pour la s�rialisation et d�s�rialisation (et ajouter un {@link #readExternal(ObjectInput)} correspondant).<br>
   * <br>
   * 
   * La m�thode {@link #getVersion()} doit �tre impl�ment�e pour obtenir le champs.
   */
  private static final int VERSION = VERSION_0;

  /** La grandeur de la partition valeur. */
  private Grandeur grandeur;

  /** Les valeurs dans la partition valeur sous forme de mapping date vers valeur. */
  private SortedMap<Date, Valeur> valeurs = null;

  /** La date de d�but de la partition valeur, qui correspond au mois, au trimestre ou � l'ann�e. */
  private Date dateDebut = null;

  /** Utilis� pour acc�l�rer la recherche d'une valeur d'une grandeur. */
  private Date datefin = null;

  /**
   * L'�tat sauvegard� de l'objet (pour ensuite savoir s'il a �t� modifi�). Attention, l'utilisation de {@link UndefinedAttributes#UNDEF_INT} ajoute une collision possible pour le hash code.
   */
  private int hashSauvegarde = UndefinedAttributes.UNDEF_INT;

  /**
   * Les valeurs compress�es issues de la base de donn�e. Elles sont gard�es en variable d'instance afin de garder la possibilit� de les envoyer (EDK / �changes) sans les d�compresser / recompresser.
   * 
   * TODO c'est encore n�cessaire ce truc ?
   */
  private byte[] valeursCompressees = null;

  private EDureePartition dureePartition = null;

  public PartitionValeurs() {
  }

  /** Constructeur par d�faut. <b>N�cessaire pour l'interface {@link SerializationExterne}</b>. */
  public PartitionValeurs(EPasTemps pasTemps, Date dateDebut, EDureePartition dureePartition) {
    super();
    setDateDebut(dateDebut);
    this.dureePartition = dureePartition;
    if (pasTemps != null && pasTemps.getDuree().getGap() < EPasTemps.DEUX_HEURES.getDuree().getGap()) {
      this.valeurs = new MapOptimiseeValeursTemporelles(dateDebut, pasTemps, dureePartition);
    } else {
      this.valeurs = new TreeMap<>();
    }
  }


  /** Retourne la grandeur. */
  public Grandeur getGrandeur() {
    return this.grandeur;
  }

  /** Associe la grandeur. */
  public void setGrandeur(Grandeur grandeur) {
    this.grandeur = grandeur;
  }

  /** Retourne la date de d�but de la partition valeur (en fonction du type de dur�e, mois, ann�e ou trimestre). */
  public Date getDateDebut() {
    return this.dateDebut;
  }

  /** Associe la date de d�but. */
  public void setDateDebut(Date dateDebut) {
    this.dateDebut = dateDebut;
  }

  /** Retourne les valeurs. */
  public SortedMap<Date, Valeur> getValeurs() {
    if (this.valeurs == null) {
      intitialiserValeurs();
    }
    return this.valeurs;
  }

  /** Associe les valeurs. */
  public void setValeurs(SortedMap<Date, Valeur> valeurs) {
    this.valeurs = valeurs;
  }

  /** Ajoute une valeur � la liste de valeurs et retourne l'ancienne valeur � cette date. */
  public Valeur addToValeurs(Valeur valeur) {
    if (valeur != null) {
      if (this.valeurs == null) {
        intitialiserValeurs();
      }

      return this.valeurs.put(valeur.getReferenceTemporelle().getDate(), valeur);
    }
    return null;
  }

  private void intitialiserValeurs() {
    if (this.valeurs == null && this.dateDebut != null && grandeur != null && grandeur.getBaseTempsNonTransitif() != null &&
        grandeur.getBaseTempsNonTransitif().getPasTempsValeurs() != null && grandeur.getBaseTempsNonTransitif().getPasTempsValeurs().getDuree().getGap() < EPasTemps.JOURNALIER.getDuree().getGap()) {
      this.valeurs = new MapOptimiseeValeursTemporelles(this.dateDebut, grandeur.getBaseTempsNonTransitif().getPasTempsValeurs(), this.grandeur.getDureePartition());
    } else {
      this.valeurs = new TreeMap<Date, Valeur>();
    }
  }

  /** Ajoute toutes les valeurs de la collection � la liste de valeurs. Voir {@link #addToValeurs(Valeur)}. */
  public void addAllToValeurs(Collection<? extends Valeur> v) {
    if (!(v == null || v.isEmpty())) {
      v.forEach(this::addToValeurs);
    }
  }

  /** Retire cette valeur de la liste de valeurs. */
  public Valeur removeFromValeurs(Valeur valeur) {
    if (valeur != null) {
      if (this.valeurs != null) {
        return this.valeurs.remove(valeur.getReferenceTemporelle().getDate());
      }
    }
    return null;
  }

  /** Retourne vrai si l'objet a �t� modifi� depuis le dernier appel � {@link #sauvegarderEtat()}. */
  public boolean estModifie() {
    if (UndefinedAttributes.UNDEF_INT == this.hashSauvegarde) {
      return true;
    }
    return this.hashSauvegarde != hashCodeSpecifique();
  }

  /** Sauvegarde l'�tat de cet objet dans l'attribut {@link #hashSauvegarde}. Permet d'appeler plus tard {@link #estModifie()} pour savoir si l'objet a �t� modifi� et a besoin d'�tre persist�. */
  public void sauvegarderEtat() {
    this.hashSauvegarde = hashCodeSpecifique();
  }

  /**
   * Retourne les valeurs compress�es.
   * 
   * <p>
   * TODO n�cessaire ?
   */
  public byte[] getValeursCompressees() {
    return this.valeursCompressees;
  }

  /**
   * Associe les valeurs compress�es.
   * 
   * <p>
   * TODO n�cessaire ?
   */
  public void setValeursCompressees(byte[] valeursCompressees) {
    this.valeursCompressees = valeursCompressees;
  }


  private int hashCodeSpecifique() {
    int result = 17;
    result = 31 * result + (null == this.dateDebut ? 0 : this.dateDebut.hashCode());
    result = 31 * result + getHashCodeValeurs();
    return result;
  }

  protected int getHashCodeValeurs() {
    int hash = 0;
    if (!(valeurs == null || valeurs.isEmpty())) {
      hash = valeurs.values().stream()
          .filter(Objects::nonNull)
          .mapToInt(this::hashCodeValeur)
          .sum();
    }
    return hash;
  }

  private int hashCodeValeur(Valeur valeur) {
    return (valeur.getDate() != null ? valeur.getDate().hashCode() : 0) ^ valeur.hashCode();
  }

  public EDureePartition getDureePartition() {
    if (dureePartition != null) {
      return this.dureePartition;
    }
    return grandeur.getDureePartition();
  }

  public Date getDatefin() {
    EDureePartition dureePartition;
    if (this.datefin == null && this.dateDebut != null && (dureePartition = getDureePartition()) != null) {
      this.datefin = dureePartition.prochaineDate(dureePartition.obtenirDateDebutPeriode(this.dateDebut));
    }
    return datefin;
  }

  /** m�thode optimis�e pour cloner rapidement une partition de valeur si elle contient des {@link MapOptimiseeValeursTemporelles} */
  public PartitionValeurs getCopie() {
    if (valeurs instanceof MapOptimiseeValeursTemporelles) {
      PartitionValeurs partitionValeurs = new PartitionValeurs(((MapOptimiseeValeursTemporelles) valeurs).getPasTemps(), dateDebut, dureePartition);
      ((MapOptimiseeValeursTemporelles) partitionValeurs.valeurs).copierTableau(((MapOptimiseeValeursTemporelles) valeurs).getValeurs());
      return partitionValeurs;
    } else if (grandeur != null && grandeur.getBaseTempsNonTransitif() != null && grandeur.getBaseTempsNonTransitif().getPasTempsValeurs() != null) {
      PartitionValeurs partitionValeurs = new PartitionValeurs(grandeur.getBaseTempsNonTransitif().getPasTempsValeurs(), dateDebut, dureePartition);
      partitionValeurs.addAllToValeurs(valeurs.values());
      return partitionValeurs;
    }
    return null;
  }
}
