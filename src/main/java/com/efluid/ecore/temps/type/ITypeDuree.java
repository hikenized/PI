package com.efluid.ecore.temps.type;

import com.imrglobal.framework.type.TimeGap;

/**
 * Interface d�crivant des types �num�r�s repr�sentant des dur�es
 * 
 * @author christophe.thibaut
 * 
 */
public interface ITypeDuree extends Comparable<ITypeDuree> {

	/**
	 * Retourne la dur�e de la valeur du type �num�r�.
	 * 
	 * <p>
	 * Pour les dur�es sup�rieure ou �gale � un mois, il s'agit d'une dur�e
	 * approximative (les mois n'ont pas tous la m�me dur�e, prise en compte des
	 * ann�es bissextiles etc..).
	 * 
	 * <p>
	 * Pour les dur�es sup�rieure ou �gale � un mois, la m�thode
	 * {@link #getNbMois()} permet de les exprimer en nombre de mois.
	 * 
	 * <p>
	 * Pour les dur�es sup�rieure ou �gale � un an, la m�thode
	 * {@link #getNbAnnees()} permet de les exprimer en nombre d'ann�es.
	 * 
	 * @return la dur�e de la valeur sous forme d'instance de {@link TimeGap}
	 */
	public TimeGap getDuree();

	/**
	 * Pour les valeurs repr�sentant des dur�es sup�rieure ou �gale � un mois,
	 * retourne la dur�e sous la forme d'un nombre de mois
	 * 
	 * @return le nombre de mois correspondant � la dur�e si celle-ci est
	 *         sup�rieure ou �gale � un mois, 0 sinon
	 */
	public int getNbMois();

	/**
	 * Pour les valeurs repr�sentant des dur�es sup�rieure ou �gale � une
	 * ann�es, retourne la dur�e sous la forme d'un nombre d'ann�es
	 * 
	 * @return le nombre d'ann�es correspondant � la dur�e si celle-ci est
	 *         sup�rieure ou �gale � une ann�es, 0 sinon
	 */
	public int getNbAnnees();

}
