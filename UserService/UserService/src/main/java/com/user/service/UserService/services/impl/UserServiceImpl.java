package com.user.service.UserService.services.impl;

import com.user.service.UserService.entities.Hotel;
import com.user.service.UserService.entities.Ratings;
import com.user.service.UserService.entities.User;
import com.user.service.UserService.exceptions.ResourceNotFoundException;
import com.user.service.UserService.external.services.HotelService;
import com.user.service.UserService.repositories.UserRepository;
import com.user.service.UserService.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private HotelService hotelService;
    private Logger logger = LoggerFactory.getLogger(UserService.class);
    @Override
    public User saveUser(User user) {
       String randomUserID= UUID.randomUUID().toString();
       user.setUserId(randomUserID);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(String userId) {
        // get user from database with the help of user repository
        User user=userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with given id is not found on server !!:"+userId));
        // fetch rating of the above user from RATING SERVICE
       Ratings[] ratingsForUser = restTemplate.getForObject("http://RATING-SERVICE/ratings/users/"+user.getUserId(), Ratings[].class);
       logger.info("{}",ratingsForUser);
        List<Ratings> ratings1 = Arrays.stream(ratingsForUser).toList();
        List<Ratings> ratingsList = ratings1.stream().map(ratings -> {
            //api call to hotel service to get the hotel
            //http://localhost:8082/hotels/643ecf8f-4135-4587-8046-047af35526f8
         //   ResponseEntity<Hotel> forEntity = restTemplate.getForEntity("http://HOTEL-SERVICE/hotels/" + ratings.getHotelId(), Hotel.class);
            Hotel hotel = hotelService.getHotel(ratings.getHotelId());
        //    logger.info("response status code: {}", forEntity.getStatusCode());
            //set the hotel to rating
            ratings.setHotel(hotel);
            // return the rating
            return ratings;
        }).collect(Collectors.toList());
        user.setRatings(ratingsList);
    return user;
    }

    @Override
    public String deleteBYId(String userId) {
        return null;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }
}
