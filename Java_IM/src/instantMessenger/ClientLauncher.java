package instantMessenger;


import java.awt.Color;
import java.rmi.Naming;
import java.rmi.Remote;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import bus.uigen.ObjectEditor;
import bus.uigen.uiFrame;
import bus.uigen.trace.TraceableDisplayAndWaitManagerFactory;

public class ClientLauncher {
	public static void main(String[] args) {

		BlockingQueue<AMessageWithSource> msgQueue = new ArrayBlockingQueue<AMessageWithSource>(
				100);
		BlockingQueue<DelayLength> delayQueue = new ArrayBlockingQueue<DelayLength>(
				100);
		BlockingQueue<AMessageWithSource> msgJitterQueue = new ArrayBlockingQueue<AMessageWithSource>(
				100);

		String name;
		System.out.println("Enter your name : ");
		Scanner scanIn = new Scanner(System.in);
		name = scanIn.nextLine();
		scanIn.close();

		MessengerModel imModel = new MessengerModel(name);
		MessengerView view = new MessengerView(name);
		MessengerController controller = new MessengerController(imModel, view);

		TelepointerModel tpModel = new TelepointerModel(10, 10, 20, 20);
		tpModel.setColor(Color.RED);
		tpModel.setFilled(true);

//		GlasspaneInteractor gpInteractor = new GlasspaneInteractor(tpModel,
//				view);
		TelepointerInteracter gpInteractor = new TelepointerInteracter(tpModel,
				view);

//		uiFrame oeFrame = ObjectEditor.edit(imModel);
//		oeFrame.setTelePointerModel(tpModel);
		ModelListener modelListener = new ModelListener(imModel, tpModel);
		
		OperationalTransformation topicOT = new OperationalTransformation(name + ": Topic", false, true);
		OperationalTransformation msgOT = new OperationalTransformation(name + ": MessageList", false, false);
		
//		ObjectEditor.edit(topicOT);
//		ObjectEditor.edit(msgOT);
		
		modelListener.setTopicOTandMsgOT(topicOT, msgOT);

		// ObjectEditor.edit(
		// TraceableDisplayAndWaitManagerFactory.
		// getTraceableDisplayAndPrintManager());
		// ATraceableListener aListener = new ATraceableListener(name);
		// TracerInfo.addListener(aListener);

		SessionServerInterface sessionServerProxy = null;
		RMIClientInterface client = null;
		OutCoupler outCoupler = null;
		try {
			Remote remoteObject = Naming.lookup("SessionServer");
			if (remoteObject instanceof SessionServerInterface) {
				sessionServerProxy = (SessionServerInterface) remoteObject;

				client = new InCoupler(imModel,tpModel);
				client.setTopicOTandMsgOT(topicOT, msgOT);
				Naming.rebind(name, client);
				RMIClientInterface clientProxy = (RMIClientInterface) Naming
						.lookup(name);

				sessionServerProxy.joinAsClient(clientProxy);

				outCoupler = new OutCoupler(name, sessionServerProxy, msgQueue,
						delayQueue, msgJitterQueue);
			} else {
				System.out.println("Server not a Chat Server.");
				System.exit(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

//		gpInteractor.setClient(client);
		controller.setClient(client);
		modelListener.setOutCoupler(outCoupler);
		
		gpInteractor.setOutCouper(outCoupler);

		Delayer delayer = new Delayer(outCoupler, msgQueue, delayQueue);
		JitterAdder jitterAdder = new JitterAdder(outCoupler, msgJitterQueue);
		// outCoupler.setDelayer(delayer);

		Thread t = new Thread(delayer);
		t.start();

		Thread t1 = new Thread(jitterAdder);
		t1.start();
	}
}
