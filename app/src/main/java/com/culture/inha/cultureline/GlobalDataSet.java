package com.culture.inha.cultureline;

import android.util.Log;

import com.culture.inha.cultureline.DataSet.User;

import java.util.ArrayList;

public class GlobalDataSet {

    public static ArrayList<String> categories;
    public static User user;

    public static String getCategoryName(String category) {
        String result = "";
        String[] numCategorySet = category.split(",");
        for (int i = 0; i < numCategorySet.length; i++) {
            int numCategory = Integer.valueOf(numCategorySet[i]);
            result += "#" + categories.get(numCategory - 1);
            if (i != numCategorySet.length - 1) result += " ";
        }
        result.replaceAll("\n", "");
        return result;
    }
    public static String getCategoryName(boolean[] isSelected) {
        String result = "";
        for (int i = 0; i < isSelected.length; i++) {
            if(isSelected[i]){
                result += "#" + categories.get(i);
                result += " ";
            }
        }
        return result.substring(0, result.length() - 1);
    }

    public static String setCategoryName(boolean[] isSelected) {
        String result = "";
        for (int i = 0; i < isSelected.length; i++) {
            if (isSelected[i])
                result += String.valueOf(i+1) + ",";
        }
        if(result.length() != 0)
            return result.substring(0, result.length() - 1);
        else
            return null;
    }

}
