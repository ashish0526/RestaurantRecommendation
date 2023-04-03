package models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Restaurant {

    private String restaurantId;

    private Cuisine cuisine;

    private int costBracket;

    private float rating;

    private boolean isRecommended;

    private Date onboardedTime;

}
