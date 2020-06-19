package com.dlog.info_nest.db;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Converters {
    @TypeConverter
    public static List<String> fromString(String string) {
        String[] arr = string.split(" ");
        List<String> nouns = new ArrayList<>(Arrays.asList(arr));
        return nouns;
    }

    @TypeConverter
    public static String toString(List<String> list) {
        String nouns = "";
        for(String str : list) {
            nouns += str + " ";
        }
        return nouns;
    }
}
