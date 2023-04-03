package models;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CuisineTracking {

    private Cuisine type;

    private int numberOfOrders;

}
