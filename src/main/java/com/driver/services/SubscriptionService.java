package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        int amount =0;

        int mul = subscriptionEntryDto.getNoOfScreensRequired();
        if(mul==0){
            mul=1;
        }

        if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.BASIC)){
            amount = 500+ (200*mul);
        }else if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.PRO)){
            amount = 800+ (250*mul);
        }else{
            amount = 1000+ (350*mul);
        }

        ///it will Automatically captures current date and time , we dont have give any parameters to it
        Date date = new Date();

        Subscription subscription = new Subscription(subscriptionEntryDto.getSubscriptionType()
                ,subscriptionEntryDto.getNoOfScreensRequired(),date,amount);

        ///setting the foreign key
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();

        subscription.setUser(user);

        subscriptionRepository.save(subscription);

        return amount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId).get();

        Subscription subscription = user.getSubscription();

        if(subscription.getSubscriptionType().equals(SubscriptionType.ELITE)){
            throw new Exception("Already the best Subscription");
        }

        if(subscription.getSubscriptionType().equals(SubscriptionType.BASIC)){

            subscription.setSubscriptionType(SubscriptionType.PRO);
            int prevAmount = subscription.getTotalAmountPaid();
            Date date = new Date();
            subscription.setStartSubscriptionDate(date);
            int mul = subscription.getNoOfScreensSubscribed();
            if(mul==0){
                mul=1;
            }
            int totalAmount = 800+250;
            subscription.setTotalAmountPaid(totalAmount*mul);

            subscriptionRepository.save(subscription);

            return totalAmount-prevAmount;
        }

        else if(subscription.getSubscriptionType().equals(SubscriptionType.PRO)){

            subscription.setSubscriptionType(SubscriptionType.ELITE);
            int prevAmount = subscription.getTotalAmountPaid();
            Date date = new Date();
            subscription.setStartSubscriptionDate(date);
            int mul = subscription.getNoOfScreensSubscribed();
            if(mul==0){
                mul=1;
            }
            int totalAmount = 1000+350;
            subscription.setTotalAmountPaid(totalAmount*mul);

            subscriptionRepository.save(subscription);

            return totalAmount-prevAmount;
        }

        return 0;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> subscriptions = subscriptionRepository.findAll();

        if(subscriptions.size()==0){
            return 0;
        }

        Integer revenue = 0;

        for(Subscription subscription : subscriptions){

            revenue+=subscription.getTotalAmountPaid();
        }

        return revenue;
    }

}
