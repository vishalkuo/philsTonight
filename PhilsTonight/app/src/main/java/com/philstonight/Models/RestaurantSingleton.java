package com.philstonight.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vishalkuo on 15-09-21.
 */
public class RestaurantSingleton {
    private static List<String> restaurants = null;

    public static List<String> getInstance() {
        if (restaurants == null) {
            restaurants = new ArrayList<>();
            restaurants.add("Phil's");
            restaurants.add("Chainsaw");
            restaurants.add("Beta");
            restaurants.add("Night School");
            restaurants.add("Bomber");
            restaurants.add("Study");
        }
        return restaurants;
    }
}
