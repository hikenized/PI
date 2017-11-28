package com.efluid.type;

import java.io.Serializable;

import com.imrglobal.framework.type.*;

/**
 * Interface commune � tous les �num�r�s efluid.
 */
public interface IValeurEnumere extends Serializable {

  /**
   * @return le code de l'�num�r�: {@link EnumType#getValue() value} pour l'�num�r� non persistant, ou {@link PersistentEnumType#getItemCode() itemCode} pour les �num�r�s persistants.
   */
  public String getCodeValeur();

  /**
   * @return le libell� dit "long" de l'�num�r�.
   */
  public String getLibelle();

  /**
   * @return le libell� court de l'�num�r�.
   */
  public String getLibelleCourt();

  /**
   * @return l'ordre, {@link PersistentEnumType#getItemOrder() itemOrder}, pour un �num�r� persistant, la {@link EnumType#getValue() value} pour l'�num�r� statique.
   */
  public int getOrdre();

  /**
   * @return le type d'�num�r�.
   */
  public default IEnumere getTypeEnumere() {
    return null;
  }

  /**
   * @return un pseudo-code globalement unique pour l'�num�r�.
   */
  public default String getInternalPseudoCode() {
    return getClass().getName() + "#" + getCodeValeur();
  }

}
