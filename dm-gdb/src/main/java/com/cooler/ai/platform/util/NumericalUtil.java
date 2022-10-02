package com.cooler.ai.platform.util;

public class NumericalUtil {

    public static boolean isNumerical(String str){
        try{
            Float.parseFloat(str);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }
}
