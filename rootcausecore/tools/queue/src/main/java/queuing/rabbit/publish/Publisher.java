package queuing.rabbit.publish;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Publisher {
    private final static String QUEUE_NAME = "logs";
    private final Channel channel;
    private final static String HOST ="localhost";
    private final static Logger LOGGER = Logger.getGlobal();
    public Publisher() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        try (Connection connection = factory.newConnection()) {
            this.channel = connection.createChannel();
        }
    }

    public void create() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(HOST);
            Connection connection = factory.newConnection();
            try (var newchannel = connection.createChannel()) {
                newchannel.exchangeDeclare(QUEUE_NAME, "fanout");
            }
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }

    }

    public void send(long id, String line) {
        LOGGER.log(Level.INFO,() -> "Sending : " + id + " -> " + line);
        try {
            channel.basicPublish(QUEUE_NAME,
                "",
                new AMQP.BasicProperties.Builder().headers(Map.of("id", id)).build(),
                line.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);

        Runnable runnable;
        try (Connection connection = factory.newConnection()) {
            try (var channel = connection.createChannel()) {
                channel.exchangeDeclare(QUEUE_NAME, "fanout", true, false, null);
                runnable = send7450(channel);
            }
        }
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);
        runnable.run();
    }

    public static Runnable send7450(Channel channel) {
        return () -> {
            try {
                printInputStream(new FileInputStream("rootcausecore/modules/queue/src/main/resources/log7450Lines.txt"), channel);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            LOGGER.log(Level.INFO,"Send 7450 lines.");
        };
    }

    private static void printInputStream(InputStream is, Channel channel) {
        try (InputStreamReader streamReader =
                     new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            readChannel(channel, reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readChannel(Channel channel, BufferedReader reader) throws IOException {
        String line;
        int i = 0;
        while ((line = reader.readLine()) != null) {
            try {
                channel.basicPublish(QUEUE_NAME,
                        "",
                        new AMQP.BasicProperties.Builder().headers(Map.of("id", i++)).build(),
                        line.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}