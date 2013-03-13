package instantMessenger;
import util.annotations.ComponentWidth;
import util.annotations.DisplayToString;
import util.trace.TraceableBus;
import util.trace.TraceableInfo;

@DisplayToString(true)
@ComponentWidth(1000)
public class TracerInfo extends TraceableInfo {

	public TracerInfo(String aMessage, Object aFinder) {
		super(aMessage, aFinder);
		setDisplay(true);
	}

	public static void addListener(ATraceableListener aListener) {
		TraceableBus.addTraceableListener(aListener);
	}

	public static TracerInfo newInfo(String aMessage, Object aFinder) {
		System.out.println("Message announced");
		TracerInfo retVal = new TracerInfo(aMessage, aFinder);
		retVal.announce();
		return retVal;
	}
}
