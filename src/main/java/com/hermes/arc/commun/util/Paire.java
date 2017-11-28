package com.hermes.arc.commun.util;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;

/**
 * Repr�sente une paire d'objets quelconque.
 * 
 * @param <A>
 * @param <B>
 */
public class Paire<A, B> implements Serializable {


  private static final long serialVersionUID = 523101421282059751L;
  final A valeur1;
	final B valeur2;

	public Paire(A valeur1, B valeur2) {
		this.valeur1 = valeur1;
		this.valeur2 = valeur2;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Cette impl�mentation permet de comparer deux objets de types diff�rents
	 * (utile pour mettre les {@link Paire} dans des Map).
	 */
	@Override
	public boolean equals(Object obj) {
    if (this == obj) { return true;}
    if (obj == null || getClass() != obj.getClass()) { return false;}
		return this.hashCode() == obj.hashCode();
	}

	/**
	 * Retourne la valeur A de la paire (soit la valeur "gauche").
	 * 
	 * @return
	 */
	public A getA() {
		return this.valeur1;
	}

	/**
	 * Retourne la valeur B de la paire (soit la valeur "droite").
	 * 
	 * @return
	 */
	public B getB() {
		return this.valeur2;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Retourne un hash code issue des hash code des deux objets {@link #valeur1} et
	 * {@link #valeur2}, en les additionnant.
	 * 
	 * <p>
	 * Il est fort possible qu'il y ait un integer overflow, mais vu que Java ne
	 * fait que "wrapper" le int dans les n�gatifs, le hash code reste distribu�
	 * et sans risque suppl�mentaire de collision.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.valeur1, this.valeur2);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return une repr�sentation {@link #toString()} utilisant les repr�sentation des deux objets s'ils
	 * sont tous deux d�finis ou null si non d�fini.
	 */
	@Override
	public String toString() {
		return Arrays.toString(new Object[]{this.valeur1, this.valeur2});
	}

  /**
   * @param format le format � utiliser
   * @return une repr�sentation de ce couple de valeur en utilisant le format sp�cifi�
   */
  public String toString(String format) {
    return MessageFormat.format(format, this.valeur1, this.valeur2);
  }

	/**
	 * Retourne vrai si les deux emplacements A et B sont nuls.
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return null == getA() && null == getB();
	}

}
