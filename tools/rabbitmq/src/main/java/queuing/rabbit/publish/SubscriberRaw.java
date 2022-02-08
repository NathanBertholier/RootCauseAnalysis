package queuing.rabbit.publish;

public class SubscriberRaw {
    private final static String QUEUE_NAME = "logRaw";

    public static void main(String[] argv) throws Exception {
        SubscriberToken.runSubscriber(QUEUE_NAME);
    }
}