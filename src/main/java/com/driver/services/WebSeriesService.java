package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo

        WebSeries check = webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName());

        if(check!=null){
            throw new Exception("Series is already present");
        }

        WebSeries webSeries = new WebSeries(webSeriesEntryDto.getSeriesName(),
                webSeriesEntryDto.getAgeLimit(),
                 webSeriesEntryDto.getRating(),webSeriesEntryDto.getSubscriptionType());

        ProductionHouse productionHouse = productionHouseRepository.
                findById(webSeriesEntryDto.getProductionHouseId()).get();


        ///adding the webseries in productionHouse
        List<WebSeries> webSeriesList = productionHouse.getWebSeriesList();

        webSeriesList.add(webSeries);

        productionHouse.setWebSeriesList(webSeriesList);

        double productionRating = 0;

        List<WebSeries> webSeriesList1 = productionHouse.getWebSeriesList();

        for(WebSeries webSeries1 : webSeriesList1){

            productionRating +=webSeries1.getRating();
        }

        productionHouse.setRatings(productionRating/webSeriesList1.size());

        productionHouseRepository.save(productionHouse);

        ///setting the foreign key
        webSeries.setProductionHouse(productionHouse);

        ///saving the webseries in repository
        webSeriesRepository.save(webSeries);

        return webSeries.getId();
    }

}
