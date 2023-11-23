package com.user.service.UserService.controllers;

import com.user.service.UserService.entities.User;
import com.user.service.UserService.services.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserService service;

    //create
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User user1 = service.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user1);
    }

    //get single user
    int retryCount = 1;

    @GetMapping("/{userId}")
//    @CircuitBreaker(name = "ratingHotelBreaker", fallbackMethod = "ratingHotelFallBack")
//    @Retry(name = "ratingHotelService", fallbackMethod = "ratingHotelFallBack")
    @RateLimiter(name = "userRateLimiter", fallbackMethod ="ratingHotelFallBack" )
    public ResponseEntity<User> getSingleUser(@PathVariable String userId) {
        logger.info("Get Single User Handler: UserController");
        logger.info("Retry count: {}", retryCount);
        retryCount++;
        User user = service.getUser(userId);
        return ResponseEntity.ok(user);

    }

    //creating fallback method for circuit breaker

    public ResponseEntity<User> ratingHotelFallBack(String userId, Exception ex) {

        logger.info("Fallback is executed because service is down :", ex.getMessage());
        User user = User.builder()
                .email("dummy@gmail.com")
                .name("Dummy")
                .about("This user is created dummy because some service is down")
                .userId("1412").build();

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    //get all user
    @GetMapping
    public ResponseEntity<List<User>> getAllUser() {
        List<User> users = service.getAllUser();
        return ResponseEntity.ok(users);
    }
}
