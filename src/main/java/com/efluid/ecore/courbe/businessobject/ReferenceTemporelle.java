package com.efluid.ecore.courbe.businessobject;

import java.io.Serializable;
import java.util.Date;

/**
 * Cette classe repr�sente une r�f�rence temporelle sous forme d'une date de
 * d�but.
 */
public class ReferenceTemporelle implements Serializable {

	/** Ne doit jamais changer. */
	private static final long serialVersionUID = 8284544119164483774L;
	
	/** La date de d�but de la r�f�rence temporelle */
	private Date date;

	/** Constructeur par d�faut. */
	public ReferenceTemporelle() {
		super();
	}

	/** Construction de la r�f�rence temporelle avec cette date. */
	public ReferenceTemporelle(Date date) {
		super();
		this.date = date;
	}

	/** Retourne la date de la r�f�rence temporelle */
	public Date getDate() {
		return this.date;
	}

	/** Met la date dans la r�f�rence temporelle */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Cette impl�mentation retourne la date de cette r�f�rence temporelle sous
	 * forme de {@link String}.
	 */
	@Override
	public String toString() {
		if (null == this.date) {
			return super.toString();
		}
		return this.date.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Voir
	 * 
	 * <ul>
	 * <li>Bloch J. (2011). <i>Effective Java (2nd edition)</i>. Stoughton: Sun
	 * Microsystems.</li>
	 * </ul>
	 */
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + (null == this.date ? 0 : this.date.hashCode());
		return result;
	}

}
