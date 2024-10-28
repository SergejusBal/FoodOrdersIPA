package FoodOrdersIPA.FoodOrdersIPA.Services;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQService {

    private String queueName;
    private static final String HOST = "localhost";
    private final ConnectionFactory factory;

    public RabbitMQService(String queueName) {
        this.factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        this.queueName = queueName;
    }

    public  void  sendObjectToQueue(String jsonMessage) {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(queueName, false, false, false, null);

            channel.basicPublish("", queueName, null, jsonMessage.getBytes());
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
