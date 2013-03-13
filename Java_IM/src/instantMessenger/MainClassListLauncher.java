package instantMessenger;
import util.models.ListenableVector;
import bus.uigen.ObjectEditor;

public class MainClassListLauncher {
  public static void launch(Class[] classes) {
    ListenableVector<Class> classList = new AMainClassList();
    for (Class aClass:classes) {
      classList.add(aClass);
    }
    ObjectEditor.edit(classList);
  }
}
