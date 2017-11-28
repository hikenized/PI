package com.efluid.ecore.courbe.businessobject;

import java.io.*;

/** Interface ayant un r�le similaire � {@link Externalizable}, mais tout en �vitant de contourner la s�rialisation standard */
public interface SerializationExterne extends Serializable {
  /** m�thode permettant d'�crire des donn�es � serializer dans au format externe, voir {@link Externalizable#writeExternal(ObjectOutput)} */
  void writeExternal(ObjectOutput out) throws IOException;

  /** m�thode permettant de lire les donn�es serializer � partir d'un format externe, voir {@link Externalizable#readExternal(ObjectInput)} */
  void readExternal(ObjectInput in) throws IOException, ClassNotFoundException;
}
