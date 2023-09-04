package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        Integer result =0;

        try{
             result = userRepository.save(user).getId();
        }catch(Exception e){
            return null;
        }
        return result;
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        List<WebSeries> webSeriesList = webSeriesRepository.findAll();

        Integer count=0;

        User user = userRepository.findById(userId).get();

        int userAge = user.getAge();

        SubscriptionType userSubType = user.getSubscription().getSubscriptionType();

        for(WebSeries webSeries : webSeriesList){

            int age = webSeries.getAgeLimit();

            SubscriptionType type = webSeries.getSubscriptionType();

            if(userAge>=age && userSubType.equals(type)){
                count++;
            }
        }

        return count;
    }


}
