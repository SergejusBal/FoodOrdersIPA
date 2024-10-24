package FoodOrdersIPA.FoodOrdersIPA.Controllers;

import FoodOrdersIPA.FoodOrdersIPA.Models.FoodOrder;
import FoodOrdersIPA.FoodOrdersIPA.Services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500","http://localhost:7778/","http://127.0.0.1:7778/"})
@RequestMapping("/data")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/newOrder")
    public ResponseEntity<String> registerOrder(@RequestBody FoodOrder foodOrder) {

        String response = orderService.sendOrderToRabbit(foodOrder);
        HttpStatus status = checkHttpStatus(response);

        if(status == HttpStatus.OK) return new ResponseEntity<>(response, status);
        else return new ResponseEntity<>(response, status);

    }

    @PostMapping("/updateOrder")
    public ResponseEntity<String> updateOrder(@RequestBody FoodOrder foodOrder) {

        String response = orderService.sendOrderToRabbitToUpdate(foodOrder);
        HttpStatus status = checkHttpStatus(response);

        if(status == HttpStatus.OK) return new ResponseEntity<>(response, status);
        else return new ResponseEntity<>(response, status);

    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodOrder> getOrderByID(@PathVariable String id) {

        FoodOrder foodOrder = orderService.getOrderByID(id);

        if(foodOrder.getId() == null)  return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else return new ResponseEntity<>(foodOrder, HttpStatus.OK);
    }

    @GetMapping("/all/{status}")
    public ResponseEntity<List<FoodOrder>> getAllOrderByStatus(@PathVariable String status) {

        List<FoodOrder> foodOrderList = orderService.getOrdersByStatus(status);

        if(foodOrderList.isEmpty())  return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else return new ResponseEntity<>(foodOrderList, HttpStatus.OK);
    }





    private HttpStatus checkHttpStatus(String response){

        switch (response){
            case "Database connection failed":
                return HttpStatus.INTERNAL_SERVER_ERROR;
            case "Invalid data":
                return HttpStatus.BAD_REQUEST;
            case "Food order was successfully added","Order was successfully updated":
                return HttpStatus.OK;
            default:
                return HttpStatus.NOT_IMPLEMENTED;
        }

    }



}
