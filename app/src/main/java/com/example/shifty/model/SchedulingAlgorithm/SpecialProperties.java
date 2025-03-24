package com.example.shifty.model.SchedulingAlgorithm;

import java.util.Map;

public class SpecialProperties {

    private static Map<String, Integer> specialProperties;

    public static void setSpecialProperty(String name, int value) {
        specialProperties.put(name, value);
    }

    public static void removeSpecialProperty(String name) {
        specialProperties.remove(name);
    }

    public static int getSpecialPropertyWeight(String name) {
            if(specialProperties.get(name) == null) {
                return 0;
            }else{
                int weight = specialProperties.get(name);
                return weight;
            }

    }







}
