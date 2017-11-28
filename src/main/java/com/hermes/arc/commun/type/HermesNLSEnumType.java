package com.hermes.arc.commun.type;

import java.lang.reflect.*;
import java.util.*;

import com.imrglobal.framework.nls.*;
import com.imrglobal.framework.type.EnumType;
import com.imrglobal.framework.utils.ReflectUtils;

import com.hermes.arc.commun.util.HermesEnumComparator;

/**
 * Classe m�re des types �num�r� multilingues Chaque valeur poss�de deux libell�s :
 * <ul>
 * <li>Le libell� long retourn� par la m�thode toString
 * <li>Le libell� court retourn� par la m�thode getShortLabel
 * </ul>
 * La traduction des deux libell�s est d�finie dans un fichier properties dont le nom correspond au nom de la classe du type. Les cl� utilis�es sont d�duites du nom de la constantes d�finissant la
 * valeur du type � traduire :
 * <ul>
 * <li><Nom de la constante> pour le libell� long
 * <li><Nom de la constante>_SHORT pour le libell� court
 * </ul>
 * Si la cl� pour le libell� court n'est pas renseign�, la m�thode getShortLabel retourne le libell� long.
 */
public abstract class HermesNLSEnumType extends EnumType implements IHermesNLSEnumType {

  private String nlsKey;
  private String shortNlsKey;

  public static final String SHORT_KEY_SUFFIX = "_SHORT";
  /**
   * Permet une recherche d'impl�mentations suppl�mentaires d'�num�r�s dans des plugins. <br>
   * S'utilise de la fa�on suivante : <br>
   * <code>PluginsAccess.getInstance().searchImplemsClassInBundleFromAllPlugin(ETypeAffaire.class, ENUM_TYPE_IMPLEMS_BUNDLE_NAME_PREFIX, "ETYPEAFFAIRE_IMPLEMS", Locale.getDefault());</code>
   */
  public static final String ENUM_TYPE_IMPLEMS_BUNDLE_NAME_PREFIX = "enumTypeImplems";

  @Override
  public String getNlsKey() {
    if (nlsKey == null) {
      nlsKey = defaultNlsKey();
    }
    return nlsKey;
  }

  protected String defaultNlsKey() {
    String key = null;
    final List list = ReflectUtils.getFields(getClass());
    if (list != null) {
      final Iterator itOnFields = list.iterator();
      while ((key == null) && (itOnFields.hasNext())) {
        final Field field = (Field) itOnFields.next();
        final int fieldModifiers = field.getModifiers();
        if ((Modifier.isStatic(fieldModifiers)) && (field.getType().equals(getClass()))) {
          try {
            Object value = field.get(null);
            if (this.equals(value)) {
              key = field.getName();
            }
          } catch (IllegalArgumentException e) {
            
          } catch (IllegalAccessException e) {
           }
        }
      }
    }
    return key;
  }

  protected void setNlsKey(String nlsKey) {
    this.nlsKey = nlsKey;
  }

  @Override
  public String getShortNlsKey() {
    if (this.shortNlsKey == null) {
      this.shortNlsKey = defaultShortNlsKey();
    }
    return shortNlsKey;
  }

  protected String defaultShortNlsKey() {
    return getNlsKey() + SHORT_KEY_SUFFIX;
  }

  protected void setShortNlsKey(String shortNlsKey) {
    this.shortNlsKey = shortNlsKey;
  }

  protected String getBundle() {
    return getClass().getName();
  }

  /**
   * Retourne le libell� long de cette valeur du type �num�r�.
   * 
   * @return le libell� long pour le language par d�faut.
   */
  @Override
  public String toString() {
    return toString(Language.DEFAULT);
  }

  /**
   * Retourne le libell� long de cette valeur du type �num�r�.
   * 
   * @return le libell� long pour le language sp�cifi�.
   */
  @Override
  public String toString(Language language) {
    String s;
    String nlsKey = getNlsKey();
    if (nlsKey != null) {
      try {
        s = NLS.getResource(getBundle(), nlsKey, language);
      } catch (Exception e) {
        s = nlsKey;
      }
    } else {
      s = "???";
    }
    return s;
  }

  /**
   * Retourne le libell� court de cette valeur si il existe. Sinon retourne le libell� long.
   * 
   * @param lg la langue
   * @return le libell� court de cette �l�ment d'�num�r�
   */
  @Override
  public String getShortLabel(Language lg) {
    String s;
    String nlsKey = getShortNlsKey();
    if (nlsKey != null) {
      try {
        s = NLS.getResource(getBundle(), nlsKey, lg != null ? lg : Language.DEFAULT);
      } catch (Exception e) {
        s = toString(lg);
      }
    } else {
      s = toString(lg);
    }
    return s;
  }

  @Override
  public String getLibelle() {
    return toString();
  }

  @Override
  public String getLibelleCourt() {
    return getShortLabel(Language.FRENCH);
  }

  @Override
  public int getOrdre() {
    return to_int();
  }

  @Override
  public Iterator sort(int mode, Language lg) {
    if (mode == HermesEnumComparator.SHORT_ALPHABETIC_ASC || mode == HermesEnumComparator.SHORT_ALPHABETIC_DESC) {
      return super.sort(new HermesEnumComparator(mode));
    }
    return super.sort(mode, lg);
  }

}
