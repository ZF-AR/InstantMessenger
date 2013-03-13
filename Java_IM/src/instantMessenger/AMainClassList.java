package instantMessenger;
import java.util.ArrayList;
import java.util.List;
import util.annotations.Visible;
import util.models.AListenableVector;
import util.remote.ProcessExecer;
import bus.uigen.misc.OEMisc;

public class AMainClassList extends AListenableVector<Class> implements
		Runnable {
	List<ProcessExecer> executed = new ArrayList();

	public AMainClassList() {
		Thread thread = new Thread(this);
		Runtime.getRuntime().addShutdownHook(thread);
	}

	@Visible(false)
	public void run() {
		killAllChildren();
	}

	public void open(Class element) {
		executed.add(OEMisc.runWithObjectEditorConsole(element, ""));
	}

	public void execute(Class element) {
		open(element);
	}

	public void terminateChildren() {
		killAllChildren();
	}

	public void terminateAll() {
		System.exit(0);
	}

	void killAllChildren() {
		for (ProcessExecer processExecer : executed) {
			processExecer.getProcess().destroy();
		}
	}
}
