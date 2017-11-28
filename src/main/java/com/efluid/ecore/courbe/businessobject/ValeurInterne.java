package com.efluid.ecore.courbe.businessobject;

import java.io.Serializable;

/**
 * classe contenant tous les champs sp�cifiques � la classe {@link Valeur}, � surcharger dans le cas d'une classe
 * fille de Valeur en ajoutant les champs sp�cifiques � la classe fille
 */
public class ValeurInterne implements Serializable {
  private double valeur;

  public ValeurInterne(){
  }
  
  public ValeurInterne(double val){
    this.valeur = val;
  }
  
  public double getValeur() {
    return valeur;
  }

  public void setValeur(double valeur) {
    this.valeur = valeur;
  }
  
  public Valeur creerValeur(ReferenceTemporelle referenceTemporelle) {
    return new Valeur(this, referenceTemporelle);
  }
}

