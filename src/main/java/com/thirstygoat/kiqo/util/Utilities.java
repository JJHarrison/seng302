package com.thirstygoat.kiqo.util;

import java.util.List;

import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.model.Person;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Created by bradley on 9/04/15.
 */
public class Utilities {

    public static String concatenatePeopleList(List<Person> people, int max) {
        String list = "";
        for (int i = 0; i < Math.min(people.size(), max)-1; i++) {
            list += people.get(i).getShortName() + ", ";
        }
        list += people.get(Math.min(people.size(), max)-1).getShortName();
        if (Math.min(people.size(), max) < people.size()) {
            final int remaining = people.size() - max;
            final String others = (remaining % 2 == 0) ? "others" : "other";
            list += " and " + remaining + " " + others;
        }

        return list;
    }

    public static String commaSeparatedValues(List<? extends Item> list) {
        String concatenatedString = "";

        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                concatenatedString += list.get(i).getShortName();
                if (i != list.size() - 1) {
                    concatenatedString += ", ";
                }
            }
        }

        return concatenatedString;
    }

    public static StringProperty commaSeparatedValuesProperty(ObservableList<? extends Item> list) {
        StringProperty result = new SimpleStringProperty();
        result.set(commaSeparatedValues(list));
        list.addListener((ListChangeListener<Item>) c -> {
            result.set(commaSeparatedValues(list));
        });

        return result;
    }
}