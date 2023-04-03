package models;

import lombok.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    private String userId;

    private CuisineTracking[] tracking;

    private CostTracking[] costBracket;

    /*
    // We would maintain Topmost cuisine of the user instead of evaluating it..
     However it's based on the fact that We call the top most restaurant for a particular User more frequently
      than the number of times User makes an order purchase/ Cuisine Tracking is updated... so it's much convenient to maintain it in tha data entity itself.
      since topMost cuisine and secondary cuisine are specific to User We must have it as part of User Entity

     */
    private Cuisine topMostCuisine;

    private List<Cuisine> secondaryCuisineList;

    private Integer topMostType;

    private List<Integer> secondTopMostTypes;



    public void updateTopMostAndSecondaryCuisines(CuisineTracking[] tracking) {

        // every time cuisine tracking is updated we maintain the top most  and two secondary top most cuisine.
    }

    public void updateTopMostCostTracking(CostTracking[] tracking) {
        // every time cost  tracking is updated we maintain the top most  and two secondary top most cuisine.
    }


}
