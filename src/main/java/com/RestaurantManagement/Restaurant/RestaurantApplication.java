package com.RestaurantManagement.Restaurant;

import models.Restaurant;
import models.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
public class RestaurantApplication {

	public static void main(String[] args) {

		User user = User.builder().userId(UUID.randomUUID().toString()).build();
		int n = 200;
		Restaurant[] availableRestaurants = new Restaurant[n];

		List<String> recommendedRestaurants = getRetaurantReecommendations(user, availableRestaurants);
		for(int i=0; i<recommendedRestaurants.size();i++) {
			System.out.println(recommendedRestaurants.get(i));
		}
		System.out.println();

	}

	// We can move the functions to helper packages and move design to be more constructive similar to rule engine

	private static List<String> getRetaurantReecommendations(User user, Restaurant[] availableRestaurants) {

		Arrays.sort(availableRestaurants, new Comparator<Restaurant>() {

			public int compare(Restaurant o1, Restaurant o2) {
				//one of the restaurants is features one
				if(oneRestaurantIsRecommended(o1, o2)) {
					Restaurant res = recommendedRestaurant(o1,o2);
					return findRestaurantResult(o1, o2, res);
				}
				//both the restaurants are featured  and one of them contains primary cuisine and other one doesn't
				else if(bothRestaurantsAreRecommended(o1, o2) && oneRestaurantWithPrimaryCuisine(o1,o2, user)) {
					Restaurant res = primaryCuisineRestaurant(o1, o2, user);
					return findRestaurantResult(o1, o2, res);
				}
				// both the restaurants are featured and with primary cuisine but one of them only  comes under primary cost
				else if (bothRestaurantsAreRecommended(o1, o2) && bothPrimaryCuisineWithOneOfThemPrimaryCost(o1, o2, user)) {
					Restaurant res = primaryCostRestaurant(o1, o2, user);
					return findRestaurantResult(o1, o2, res);
				}
				//both restaurants are featured but one of them contains seconday cuisine and the other one neither contains primary not secondary
				else if(bothRestaurantsAreRecommended(o1, o2) && oneOfTheRestaurantContainsSecondaryCuisine(o1, o2, user)) {
					Restaurant res = secondaryCuisineRestaurant(o1, o2, user);
					return findRestaurantResult(o1, o2, res);
				}
				//both restaurants are recommended and  contain secondary cuisine but one of them comes with primary cost
				else if(bothRestaurantsAreRecommended(o1, o2) && bothContainSecondaryCuisineWithOneOfThemPrimaryCost(o1, o2, user)) {
					Restaurant res = primaryCostRestaurant(o1, o2, user);
					return findRestaurantResult(o1, o2, res);
				}

				// now we compare either  both featured or  both not featured restaurants
				// First based on the rating
				else if(bothPrimaryCuisinePrimaryCostbutOneRestaurantWithHigherRating(o1, o2, user)) {
					Restaurant res = higherRatingRestaurant(o1, o2);
					return findRestaurantResult(o1, o2, res);
				}


				// both primary cuisine secondary costBracket with Higher Rating
				else if(bothPrimaryCuisineSecondaryCostBracketWithHigherRating(o1, o2, user)) {
					Restaurant res = higherRatingRestaurant(o1, o2);
					return findRestaurantResult(o1, o2, res);
				}
				// both secondary cuisine primary cost with higher Rating
				else if(bothSecondaryCuisinePrimaryCostWithHigherRating(o1, o2, user)) {
					Restaurant res = higherRatingRestaurant(o1, o2);
					return findRestaurantResult(o1, o2, res);
				}
				// one of the restaurant is newly created
				else if (newlyCreatedRestaurant(o1, o2)) {
                     Restaurant res = newlyCreatedRestaurantWithHigherRating(o1, o2);
					 return findRestaurantResult(o1, o2, res);
				}
				else
					return  1;

			}
	});
		return Arrays.stream(availableRestaurants).map(res -> res.getRestaurantId()).collect(Collectors.toList());
	}

	private static int findRestaurantResult(Restaurant o1, Restaurant o2, Restaurant res) {
		if(res.getRestaurantId() ==  o2.getRestaurantId())
			return 1;
		else
			return -1;
	}

	private static Restaurant newlyCreatedRestaurantWithHigherRating(Restaurant o1, Restaurant o2) {
		if(o1.getOnboardedTime().compareTo(new Date(System.currentTimeMillis() - 48 * 36000 * 1000)) > 0  && (o2.getOnboardedTime().compareTo(new Date(System.currentTimeMillis() - 48 * 3600 * 1000)) < 0)) {
			return o1;
		}
		else if(o1.getOnboardedTime().compareTo(new Date(System.currentTimeMillis() - 48 * 36000 * 1000)) < 0  && (o2.getOnboardedTime().compareTo(new Date(System.currentTimeMillis() - 48 * 3600 * 1000)) > 0))
			return o2;
		else if (o1.getRating() > o2.getRating()) {
			return o1;
		}
		else return o2;
	}

	private static boolean newlyCreatedRestaurant(Restaurant o1, Restaurant o2) {
		// in case both the restaurants were onboarded recently we return false
		if(o1.getOnboardedTime().compareTo(new Date(System.currentTimeMillis() - 48 * 36000 * 1000)) > 0  && (o2.getOnboardedTime().compareTo(new Date(System.currentTimeMillis() - 48 * 3600 * 1000)) > 0)) {
			return false;
		}
		if(o1.getOnboardedTime().compareTo(new Date(System.currentTimeMillis() - 48 * 36000 * 1000)) > 0  || (o2.getOnboardedTime().compareTo(new Date(System.currentTimeMillis() - 48 * 3600 * 1000)) > 0)) {
               return true;
		}
		return false;
	}

	private static boolean bothSecondaryCuisinePrimaryCostWithHigherRating(Restaurant o1, Restaurant o2, User user) {
		if(user.getSecondaryCuisineList().contains(o1.getCuisine()) && user.getSecondaryCuisineList().contains(o2.getCuisine())) {
			if(o1.getCostBracket() == user.getTopMostType() && o2.getCostBracket() ==  user.getTopMostType()) {
				if((o1.getRating() >= 4.5 && o2.getRating() < 4.5) || (o1.getRating() < 4.5 && o2.getRating() >= 4.5)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean bothPrimaryCuisineSecondaryCostBracketWithHigherRating(Restaurant o1, Restaurant o2, User user) {
		if(user.getTopMostCuisine().equals(o2.getCuisine()) && user.getTopMostCuisine().equals(o1.getCuisine())) {
			if(user.getSecondTopMostTypes().contains(o1.getCostBracket()) && user.getSecondTopMostTypes().contains(o2.getCostBracket()))  {
				if((o1.getRating() >= 4.5 && o2.getRating() < 4.5) || (o2.getRating() >= 4.5 && o1.getRating() < 4.5)) {
					return true;
				}
			}
		}
		return false;
	}

	private static Restaurant higherRatingRestaurant(Restaurant o1, Restaurant o2) {
		if(o1.getRating() > o2.getRating())return o1;
		else return o2;
	}

	private static boolean bothPrimaryCuisinePrimaryCostbutOneRestaurantWithHigherRating(Restaurant o1, Restaurant o2, User user) {
		if(user.getTopMostCuisine().equals(o1.getCuisine()) && user.getTopMostCuisine().equals(o2.getCuisine())) {
			if(user.getTopMostType().equals(o1.getCostBracket()) && user.getTopMostType().equals(o2.getCostBracket())) {
				if((o1.getRating() >= 4.0 && o2.getRating() <4.0) || (o2.getRating() >=4.0 && o1.getRating() < 4.0)) {
					return true;
				}
			}
		}
		return false;
	}

	private static Restaurant secondaryCostRestaurant(Restaurant o1, Restaurant o2, User user) {
		if(user.getSecondTopMostTypes().contains(o1.getCostBracket()))return o1;
		else return o2;
	}

	private static boolean bothContainsSecondaryCuisineWithOneofThemSecondaryCost(Restaurant o1, Restaurant o2, User user) {
		if(user.getSecondaryCuisineList().contains(o1.getCuisine()) && user.getSecondaryCuisineList().contains(o2.getCuisine())) {
			if((user.getSecondTopMostTypes().contains(o1.getCostBracket())) && !user.getSecondTopMostTypes().contains(o2.getCostBracket())) {
				return true;
			} else if (!(user.getSecondTopMostTypes().contains(o1.getCostBracket())) && user.getSecondTopMostTypes().contains(o2.getCostBracket())) {
				return true;
			}
		}
		return false;
	}

	private static boolean bothContainSecondaryCuisineWithOneOfThemPrimaryCost(Restaurant o1, Restaurant o2, User user) {
		if(user.getSecondaryCuisineList().contains(o1.getCuisine()) && user.getSecondaryCuisineList().contains(o2.getCuisine())) {
			if(user.getTopMostType() ==  o1.getCostBracket() || user.getTopMostType() ==  o2.getCostBracket()) return  true;
		}
		return false;
	}

	private static Restaurant secondaryCuisineRestaurant(Restaurant o1, Restaurant o2, User user) {
		if(user.getSecondaryCuisineList().contains(o1.getCuisine())) return o1;
		else return o2;
	}

	private static boolean oneOfTheRestaurantContainsSecondaryCuisine(Restaurant o1, Restaurant o2, User user) {
		if(user.getSecondaryCuisineList().contains(o1.getCuisine()) && (!user.getSecondaryCuisineList().contains(o2.getCuisine())) && !user.getTopMostCuisine().equals(o2.getCuisine()))
			return true;
		else if(user.getSecondaryCuisineList().contains(o2.getCuisine()) && (!user.getSecondaryCuisineList().contains(o1.getCuisine())) && !user.getTopMostCuisine().equals(o1.getCuisine())) {
			return true;
		}
		return false;
	}

	private static Restaurant primaryCostRestaurant(Restaurant o1, Restaurant o2, User user) {
		if(o1.getCostBracket() == user.getTopMostType()) return o1;
		else return o2;
	}

	private static boolean bothPrimaryCuisineWithOneOfThemPrimaryCost(Restaurant o1, Restaurant o2, User user) {
		if(o1.getCuisine().equals(user.getTopMostCuisine()) && o2.getCuisine().equals(user.getTopMostCuisine())) {
			if(o1.getCostBracket() != o2.getCostBracket() && ((o1.getCostBracket() == user.getTopMostType() && o1.isRecommended()) || (o2.getCostBracket() == user.getTopMostType() && o2.isRecommended()))) {
				return true;
			}
		}
		return false;
	}

	private static Restaurant primaryCuisineRestaurant(Restaurant o1, Restaurant o2, User user) {
		if(o1.getCuisine().equals(user.getTopMostCuisine()))return o1;
		else return o2;
	}

	private static boolean oneRestaurantWithPrimaryCuisine(Restaurant o1, Restaurant o2, User user) {
		if(o1.getCuisine().equals(user.getTopMostCuisine()) && !o2.getCuisine().equals(user.getTopMostCuisine()) ) {
			return true;
		} else if (!o1.getCuisine().equals(user.getTopMostCuisine()) && o2.getCuisine().equals(user.getTopMostCuisine())) {
			return true;
		}
		return false;
	}

	private static Restaurant recommendedRestaurant(Restaurant o1, Restaurant o2) {
		if(o1.isRecommended())return o1;
		else return o2;
	}

	private static boolean oneRestaurantIsRecommended(Restaurant o1, Restaurant o2) {
		if((o1.isRecommended() && !o2.isRecommended()) || (!o1.isRecommended() && o2.isRecommended())) {
		   return true;
		}
		return false;
	}

	private static boolean bothRestaurantsAreRecommended(Restaurant o1, Restaurant o2) {
		if(o1.isRecommended() && o2.isRecommended()) return true;

		return  false;
	}

}
