package com.efluid.ecore.courbe.businessobject;

import java.util.*;

import com.imrglobal.framework.type.Period;

import com.efluid.ecore.commun.utils.EcoreComparators;
import com.efluid.ecore.temps.businessobject.*;

/**
 * Cette interface repr�sente une grandeur sous forme d'une liste de valeurs.
 */
public interface IGrandeur extends IPossedePeriodeEtBaseTemps, IPossedeValeurs<Collection<Valeur>> {


  /**
   * Ajoute cette valeur � la grandeur, en mettant � jour les diff�rentes structures qui contiennent des valeurs, et met � jour la p�riode.
   *
   */
  public Valeur addToValeurs(Valeur valeur);

  /**
   * Ajoute ces valeurs � la grandeur, en mettant � jour les diff�rentes structures qui contiennent des valeurs, et met � jour la p�riode.
   *
   */
  public void addAllToValeurs(Collection<? extends Valeur> valeurs);

  /**
   * Supprime cette valeur de la grandeur, en mettant � jour les diff�rentes structures qui contiennent des valeurs, et met � jour la p�riode.
   *
   * <p>
   * <strong>Attention :</strong> Il est impossible de supprimer une valeur lors de l'it�ration sur la structure de valeurs (m�me si c'est une sous structure, via subMap() ou values() par exemple).
   * Voir 74605. <strong>Le code suivant est erron� :</strong>
   *
   * <pre>
   * // BRIS� -- ne fonctionne pas
   * for (Valeur valeur : grandeur.getValeurs().subMap(dateDebut, dateFin)) {
   *   this.grandeur.removeFromValeurs(valeur);
   * }
   * </pre>
   *
   * Il faut utiliser la m�thode {@link #removeFromValeurs(Collection)}.
   *
   * @see #removeFromValeurs(Collection)
   */
  public Valeur removeFromValeurs(Valeur valeur);

  /**
   * Supprime ces valeurs de la grandeur, en mettant � jour les diff�rentes structures qui contiennent des valeurs, et met � jour la p�riode.
   *
   * <p>
   * Pour palier au manque de la m�thode {@link #removeFromValeurs(Valeur)}, cette m�thode peut-�tre utilis�e de la mani�re suivante :
   *
   * <pre>
   * Collection<Valeur> valeursASupprimer = grandeur.getValeurs().subMap(dateDebut, dateFin).values());
   * grandeur.removeFromValeurs(valeursASupprimer);
   * </pre>
   *
   * Il n'est pas garanti que les valeurs seront supprim�es dans le m�me ordre que les valeurs dans la liste pass� en argument, mais il est garanti qu'elles le seront toutes.
   *
   * @throws NullPointerException si valeurs est nul
   */
  public Collection<Valeur> removeFromValeurs(Collection<Valeur> valeur);

  /**
   * Supprime toutes les valeurs de la grandeur, en mettant � jour les diff�rentes structures qui contiennent des valeurs, et met � jour la p�riode.
   *
   * @deprecated utiliser {@link #clearAllValeurs()} pour supprimer toutes les valeurs et les structures correspondantes ou {@link #clearValeursSimple()} pour supprimer seulement les valeurs
   */
  @Deprecated
  public void clearValeurs();

  /** Supprime toutes les valeurs de la grandeur, en mettant � jour les diff�rentes structures qui contiennent des valeurs, et met � jour la p�riode. */
  public void clearAllValeurs();

  /** Supprime toutes les valeurs de la grandeur seulement, sans le reste, et met � jour la p�riode. */
  public void clearValeursSimple();

  /**
   * Supprime toutes les valeurs de la grandeur seulement, sans le reste, et sans mettre � jour la p�riode.
   * Present principalement pour etre sur que cela ne provoque pas de r�gression pour la 2.01 d'enercom lorsque l'on decharge
   * les partions de valeur
   */
  public void clearValeursUniquement();
  
  /**
   * Retourne les valeurs.
   *
   * @return les valeurs associ�es � la grandeur.
   */
  public SortedMap<Date, Valeur> getValeurs();

  /**
   * Retourne la courbe.
   *
   * @return the courbe
   */
  public Courbe getCourbe();

  /**
   * Met cette courbe.
   *
   * @param courbe the courbe to set
   */
  public void setCourbe(Courbe courbe);

  /**
   * Retourne la base de temps.
   *
   * @return the baseTemps
   * @deprecated utiliser {@link #getBaseTempsTransitif(ModeleCourbe)} ou {@link #getBaseTempsNonTransitif()}, cette m�thode ne devrait utilis�e que par l'archi pour persister le champs baseTemps
   */
  @Deprecated
  public BaseTemps getBaseTemps();

  /**
   * Retourne la base de temps de mani�re non transitive, donc retourne le champs base temps de la grandeur.
   *
   * @return the baseTemps
   */
  @Override
public BaseTemps getBaseTempsNonTransitif();


  /**
   * Met cette base de temps.
   *
   * @param baseTemps the baseTemps to set
   */
  public void setBaseTemps(BaseTemps baseTemps);

  /** @return the selectionnable */
  public boolean isSelectionnable();

  /**
   * Retourne la p�riode.
   *
   * @return the p�riode
   */
  @Override
public Period getPeriode();

  /**
   * Cette m�thode retourne la p�riode sur laquelle des valeurs sont d�finies pour la grandeur. Cette m�thode diff�re de la m�thode {@link getPeriode()} dans le sens o� elle �tend la p�riode des pas
   * de temps de chaque grandeur.
   *
   */
  public Period getPeriodeAffichage();

  /**
   * Met cette p�riode.
   *
   * @param period the p�riode to set
   */
  public void setPeriode(Period periode);

  /**
   * Retourne les valeurs calcul�es.
   *
   * @return les valeurs calcul�es associ�es � la grandeur.
   */
  public ValeursCalculees getValeursCalculees();

  /**
   * Met ces valeurs calcul�es.
   *
   * @param valeursCalculees the valeursCalculees to set
   */
  public void setValeursCalculees(ValeursCalculees valeursCalculees);

}
