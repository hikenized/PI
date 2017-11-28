package com.efluid.ecore.courbe.utils;

import java.io.Serializable;
import java.util.Comparator;

import com.efluid.ecore.courbe.businessobject.Valeur;


/**
 * Cette classe correspond � un comparateur de {@link Valeur} s�rialisable.
 */
public class ComparateurValeur implements Comparator<Valeur>, Serializable {

	/** Ne doit jamais changer. */
	private static final long serialVersionUID = 3777481919894726546L;

	/**
	 * La comparaison des valeurs est bas�e sur la comparaison des valeurs des dates associ�es
	 * aux r�f�rences temportelles.
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Valeur o1, Valeur o2) {
		return o1.getDate().compareTo(o2.getDate());
	}
}