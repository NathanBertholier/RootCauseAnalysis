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
    private final static String QUEUE_NAME = "token";
    private final static String PATH = "C:\\Users\\05tra\\Documents\\Logs\\EOXA7WY4D4FK4.2020-06-15-12.42f83aef";
    private final static String HOST ="localhost";
    private final static Logger LOGGER = Logger.getGlobal();

    private final Channel channel;
    public Publisher() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        Connection connection = factory.newConnection();
        this.channel = connection.createChannel();
    }

    public void create() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(HOST);
            Connection connection = factory.newConnection();
            try (var newchannel = connection.createChannel()) {
                newchannel.exchangeDeclare(QUEUE_NAME, "direct", true, false, null);
            }
        } catch (IOException | TimeoutException e) {
            LOGGER.log(Level.SEVERE,"IOException | TimeoutException",e);
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
            LOGGER.log(Level.SEVERE,"IOException",e);
        }
    }

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);

        Runnable runnable;
        Connection connection = factory.newConnection();
        var channel = connection.createChannel();
        channel.exchangeDeclare(QUEUE_NAME, "direct", true, false, null);
        //channel.exchangeBind(QUEUE_NAME, QUEUE_NAME,"");
        channel.queueBind(QUEUE_NAME, QUEUE_NAME, "");
        runnable = send7450(channel);

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);
        runnable.run();
    }

    public static Runnable send7450(Channel channel) {
        return () -> {
            try {
                printInputStream(new FileInputStream(PATH), channel);
            } catch (FileNotFoundException e) {
                LOGGER.log(Level.SEVERE,"FileNotFoundException",e);
            }
            LOGGER.log(Level.INFO,"Send lines");
        };
    }

    private static void printInputStream(InputStream is, Channel channel) {
        try (InputStreamReader streamReader =
                     new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            readChannel(channel, reader);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"IOException",e);
        }
    }

    private static void readChannel(Channel channel, BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                String json = "{ \"log\":\"" + line + "\"}";
                channel.basicPublish(QUEUE_NAME,
                        "",
                        null,
                        json.getBytes());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE,"IOException",e);
            }
        }
    }
}