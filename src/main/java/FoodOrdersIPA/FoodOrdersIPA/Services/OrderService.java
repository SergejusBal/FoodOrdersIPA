package FoodOrdersIPA.FoodOrdersIPA.Services;

import FoodOrdersIPA.FoodOrdersIPA.Models.FoodOrder;
import FoodOrdersIPA.FoodOrdersIPA.Repositories.MongoDBRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.jshell.Snippet;
import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private RedisService redisService;
    @Autowired
    private MongoDBRepository mongoDBRepository;

    private final ObjectMapper objectMapper;
    @Autowired
    public OrderService() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }



    public String sendOrderToRabbit(FoodOrder foodOrder){
        foodOrder.setStatus("pending");
        foodOrder.setOrderTime(LocalDateTime.now());
        RabbitMQService rabbitMQService = new RabbitMQService("FoodOrder");
        rabbitMQService.sendObjectToQueue(generateJson(foodOrder));
        return "Food order was successfully added";

    }
    public String sendOrderToRabbitToUpdate(FoodOrder foodOrder){
        if (foodOrder.getId().length() != 24) return "Invalid data";

        foodOrder.setOrderEndTime(LocalDateTime.now());
        RabbitMQService rabbitMQService = new RabbitMQService("FoodOrderUpdate");
        rabbitMQService.sendObjectToQueue(generateJson(foodOrder));
        return "Order was successfully updated";

    }

    public String sendOrderToUpdatePaymentStatus(FoodOrder foodOrder){
        if (foodOrder.getId().length() != 24) return "Invalid data";

        RabbitMQService rabbitMQService = new RabbitMQService("PaymentUpdate");
        rabbitMQService.sendObjectToQueue(generateJson(foodOrder));
        return "Payment was successfully updated";
    }

    public FoodOrder getOrderByID(String id) {

        if (id.length() != 24) return new FoodOrder();

        try {
           String jsonMessage = redisService.get(id);
           if(jsonMessage != null){
               return generateObjectFromJSon(jsonMessage, FoodOrder.class);
           }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        return mongoDBRepository.getOrderById(id);
    }

    public List<FoodOrder> getOrdersByStatus(String status){
        return mongoDBRepository.getOrderByStatus(status);
    }

    private  <T> T generateObjectFromJSon(String json, Class<T> clazz){

        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    private <T> String generateJson(T t)   {
        try {
            return objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return "{}";
        }
    }





}
