package escalate.jface

import org.eclipse.jface.preference.{ PreferenceStore, IPreferenceStore }
import org.eclipse.jface.util.{ PropertyChangeEvent, IPropertyChangeListener }

import OverlayPreferenceStore.{ TypeDescriptor, OverlayKey }

class OverlayPreferenceStore(private val parent: IPreferenceStore, private val overlayKeys: List[OverlayKey])
    extends IPreferenceStore {

  private val store: IPreferenceStore = new PreferenceStore
  private var propertyListener: PropertyListener = _
  private var loaded: Boolean = _

  def getBoolean(name: String) = store.getBoolean(name)
  def getDefaultBoolean(name: String) = store.getDefaultBoolean(name)
  def getDouble(name: String) = store.getDouble(name)
  def getDefaultDouble(name: String) = store.getDefaultDouble(name)
  def getFloat(name: String) = store.getFloat(name)
  def getDefaultFloat(name: String) = store.getDefaultFloat(name)
  def getInt(name: String) = store.getInt(name)
  def getDefaultInt(name: String) = store.getDefaultInt(name)
  def getLong(name: String) = store.getLong(name)
  def getDefaultLong(name: String) = store.getDefaultLong(name)
  def getString(name: String) = store.getString(name)
  def getDefaultString(name: String) = store.getDefaultString(name)

  def isDefault(name: String) = store.isDefault(name)
  def needsSaving = store.needsSaving
  def setToDefault(name: String) = store.setToDefault(name)
  def putValue(name: String, value: String) = covering(name) { store.putValue(name, value) }
  def contains(name: String) = store.contains(name)

  def setDefault(name: String, value: Int) = covering(name) { store.setDefault(name, value) }
  def setDefault(name: String, value: Long) = covering(name) { store.setDefault(name, value) }
  def setDefault(name: String, value: Float) = covering(name) { store.setDefault(name, value) }
  def setDefault(name: String, value: Double) = covering(name) { store.setDefault(name, value) }
  def setDefault(name: String, value: String) = covering(name) { store.setDefault(name, value) }
  def setDefault(name: String, value: Boolean) = covering(name) { store.setDefault(name, value) }

  def setValue(name: String, value: Int) = covering(name) { store.setValue(name, value) }
  def setValue(name: String, value: Long) = covering(name) { store.setValue(name, value) }
  def setValue(name: String, value: Float) = covering(name) { store.setValue(name, value) }
  def setValue(name: String, value: Double) = covering(name) { store.setValue(name, value) }
  def setValue(name: String, value: String) = covering(name) { store.setValue(name, value) }
  def setValue(name: String, value: Boolean) = covering(name) { store.setValue(name, value) }

  private def findOverlayKey(key: String) = overlayKeys.find(_.key == key)
  private def covers(key: String) = findOverlayKey(key).isDefined
  private def covering(key: String)(f: ⇒ Unit) = if (covers(key)) f

  private def propagateProperty(orgin: IPreferenceStore, key: OverlayKey, target: IPreferenceStore): Unit = {
    if (orgin.isDefault(key.key)) {
      if (!target.isDefault(key.key))
        target.setToDefault(key.key)
    } else {
      key.descriptor match {
        case TypeDescriptor.Boolean ⇒
          val originValue = orgin.getBoolean(key.key)
          val targetValue = target.getBoolean(key.key)
          if (targetValue != originValue)
            target.setValue(key.key, originValue)
        case TypeDescriptor.Double ⇒
          val originValue = orgin.getDouble(key.key);
          val targetValue = target.getDouble(key.key);
          if (targetValue != originValue)
            target.setValue(key.key, originValue);
        case TypeDescriptor.Float ⇒
          val originValue = orgin.getFloat(key.key);
          val targetValue = target.getFloat(key.key);
          if (targetValue != originValue)
            target.setValue(key.key, originValue);
        case TypeDescriptor.Int ⇒
          val originValue = orgin.getInt(key.key);
          val targetValue = target.getInt(key.key);
          if (targetValue != originValue)
            target.setValue(key.key, originValue);
        case TypeDescriptor.Long ⇒
          val originValue = orgin.getLong(key.key);
          val targetValue = target.getLong(key.key);
          if (targetValue != originValue)
            target.setValue(key.key, originValue);
        case TypeDescriptor.String ⇒
          val originValue = orgin.getString(key.key);
          val targetValue = target.getString(key.key);
          if (targetValue != null && originValue != null && !targetValue.equals(originValue))
            target.setValue(key.key, originValue);
      }
    }
  }

  def propagate = overlayKeys foreach (propagateProperty(store, _, parent))

  def start() {
    if (propertyListener == null) {
      propertyListener = new PropertyListener()
      parent.addPropertyChangeListener(propertyListener)
    }
  }

  def stop() {
    if (propertyListener != null) {
      parent.removePropertyChangeListener(propertyListener)
      propertyListener = null
    }
  }

  def loadDefaults() = overlayKeys foreach (k ⇒ setToDefault(k.key))

  def load() = {
    overlayKeys foreach (k ⇒ loadProperty(parent, k, store, true))
    loaded = true
  }

  def loadProperty(orgin: IPreferenceStore, key: OverlayKey, target: IPreferenceStore, forceInitialization: Boolean) = {
    key.descriptor match {
      case TypeDescriptor.Boolean ⇒
        if (forceInitialization)
          target.setValue(key.key, true)
        target.setValue(key.key, orgin.getBoolean(key.key))
        target.setDefault(key.key, orgin.getDefaultBoolean(key.key))
      case TypeDescriptor.Double ⇒
        if (forceInitialization)
          target.setValue(key.key, 1.0D)
        target.setValue(key.key, orgin.getDouble(key.key))
        target.setDefault(key.key, orgin.getDefaultDouble(key.key))
      case TypeDescriptor.Float ⇒
        if (forceInitialization)
          target.setValue(key.key, 1.0F)
        target.setValue(key.key, orgin.getFloat(key.key))
        target.setDefault(key.key, orgin.getDefaultFloat(key.key))
      case TypeDescriptor.Int ⇒
        if (forceInitialization)
          target.setValue(key.key, 1)
        target.setValue(key.key, orgin.getInt(key.key))
        target.setDefault(key.key, orgin.getDefaultInt(key.key))
      case TypeDescriptor.Long ⇒
        if (forceInitialization)
          target.setValue(key.key, 1L)
        target.setValue(key.key, orgin.getLong(key.key))
        target.setDefault(key.key, orgin.getDefaultLong(key.key))
      case TypeDescriptor.String ⇒
        if (forceInitialization)
          target.setValue(key.key, "1")
        target.setValue(key.key, orgin.getString(key.key))
        target.setDefault(key.key, orgin.getDefaultString(key.key))

    }
  }

  def addPropertyChangeListener(listener: IPropertyChangeListener) = store.addPropertyChangeListener(listener)
  def removePropertyChangeListener(listener: IPropertyChangeListener) = store.removePropertyChangeListener(listener)
  def firePropertyChangeEvent(name: String, oldValue: Object, newValue: Object) =
    store.firePropertyChangeEvent(name, oldValue, newValue)

  private class PropertyListener extends IPropertyChangeListener {

    def propertyChange(event: PropertyChangeEvent) {
      val key = findOverlayKey(event.getProperty())
      key foreach { k ⇒
        propagateProperty(parent, k, store)
      }
    }
  }
}

object OverlayPreferenceStore {
  object TypeDescriptor extends Enumeration {
    type TypeDescriptor = Value
    val Boolean, Double, Float, Int, Long, String = Value
  }

  case class OverlayKey(descriptor: TypeDescriptor.TypeDescriptor, key: String)
}