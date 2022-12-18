package com.cooler.ai.platform.client.biz;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Test {

    //测试字符串比较
    public static boolean test(String currentValueStr, String option, String compareValueStr){
        try{
            switch (option){
                case "gt" : {   //greater than  （大于）（比较值必须为数值）
                    float currentValue = Float.parseFloat(currentValueStr);
                    float compareValue = Float.parseFloat(compareValueStr);
                    return currentValue > compareValue;
                }
                case "ge" : {   //greater equals（大于等于）（比较值必须为数值）
                    float currentValue = Float.parseFloat(currentValueStr);
                    float compareValue = Float.parseFloat(compareValueStr);
                    return currentValue >= compareValue;
                }
                case "eq" : {   //equals        （等于）（比较值必须为数值）
                    float currentValue = Float.parseFloat(currentValueStr);
                    float compareValue = Float.parseFloat(compareValueStr);
                    //todo:保持3位数
                    return currentValue == compareValue;
                }
                case "ne" : {   //not equals    （不等于）（比较值必须为数值）
                    float currentValue = Float.parseFloat(currentValueStr);
                    float compareValue = Float.parseFloat(compareValueStr);
                    //todo:保持3位数
                    return currentValue != compareValue;
                }
                case "lt" : {   //less than     （小于）（比较值必须为数值）
                    float currentValue = Float.parseFloat(currentValueStr);
                    float compareValue = Float.parseFloat(compareValueStr);
                    return currentValue < compareValue;
                }
                case "le" : {   //less equals   （小于等于）（比较值必须为数值）
                    float currentValue = Float.parseFloat(currentValueStr);
                    float compareValue = Float.parseFloat(compareValueStr);
                    return currentValue <= compareValue;
                }

                case "bt" : {   //between       （在一个数值范围之中）（边界必须为数值）
                    String compareValueStrSub = compareValueStr.substring(1, compareValueStr.length() - 1);         //掐头去尾
                    String[] fromToValueStrs = compareValueStrSub.split(",");
                    if(fromToValueStrs.length != 2) return false;

                    float fromValue = Float.parseFloat(fromToValueStrs[0].trim());
                    float currentValue = Float.parseFloat(currentValueStr);
                    float toValue = Float.parseFloat(fromToValueStrs[1].trim());

                    boolean leftGE = compareValueStr.startsWith("[");
                    boolean leftGT = compareValueStr.startsWith("(");
                    boolean rightLE = compareValueStr.endsWith("]");
                    boolean rightLT = compareValueStr.endsWith(")");

                    boolean leftRes = false;
                    boolean rightRes = false;

                    if(leftGE) leftRes = currentValue >= fromValue;
                    else if(leftGT) leftRes = currentValue > fromValue;

                    if(rightLE) rightRes = currentValue <= toValue;
                    else if(rightLT) rightRes = currentValue < toValue;

                    return leftRes && rightRes;
                }
                case "nb" : {   //not between   （不在一个数值范围之中）（边界必须为数值）
                    String compareValueStrSub = compareValueStr.substring(1, compareValueStr.length() - 1);         //掐头去尾
                    String[] fromToValueStrs = compareValueStrSub.split(",");
                    if(fromToValueStrs.length != 2) return false;

                    float fromValue = Float.parseFloat(fromToValueStrs[0].trim());
                    float currentValue = Float.parseFloat(currentValueStr);
                    float toValue = Float.parseFloat(fromToValueStrs[1].trim());

                    boolean leftGE = compareValueStr.startsWith("[");
                    boolean leftGT = compareValueStr.startsWith("(");
                    boolean rightLE = compareValueStr.endsWith("]");
                    boolean rightLT = compareValueStr.endsWith(")");

                    boolean leftRes = false;
                    boolean rightRes = false;

                    if(leftGE) leftRes = currentValue < fromValue;
                    else if(leftGT) leftRes = currentValue <= fromValue;

                    if(rightLE) rightRes = currentValue > toValue;
                    else if(rightLT) rightRes = currentValue >= toValue;

                    return leftRes || rightRes;
                }

                case "is" : {   //is            （是）（比较值必须为字符串）
                    return currentValueStr.equals(compareValueStr);
                }
                case "not" : {  //not           （不是）（比较值必须为字符串）
                    return !currentValueStr.equals(compareValueStr);
                }

                case "in" : {   //in            （包含于）（比较值必须为逗号隔开的字符串，各个字符串前后不要空格）
                    String[] items = compareValueStr.split(",");
                    Set<String> itemSet = new HashSet<>(Arrays.asList(items));
                    return itemSet.contains(currentValueStr);
                }
                case "nn" : {   //not in        （不包含于）（比较值必须为逗号隔开的字符串，各个字符串前后不要空格）
                    String[] items = compareValueStr.split(",");
                    Set<String> itemSet = new HashSet<>(Arrays.asList(items));
                    return !itemSet.contains(currentValueStr);
                }
            }
        } catch (NumberFormatException e){
            e.printStackTrace();
        }

        return false;
    }

    public static void main(String[] args) {
        boolean gt = Test.test("0.9564", "gt", "0.956");//保持3位数

        boolean bt1 = Test.test("0.9", "bt", "[0.9, 1)");
        boolean bt2 = Test.test("0.9", "bt", "(0.9, 1)");
        boolean bt3 = Test.test("0.9", "bt", "[0.8, 0.9]");
        boolean bt4 = Test.test("0.9", "bt", "[0.8, 0.9)");

        boolean bt1a = Test.test("0.9", "nb", "[0.9, 1)");
        boolean bt2a = Test.test("0.9", "nb", "(0.9, 1)");
        boolean bt3a = Test.test("0.9", "nb", "[0.8, 0.9]");
        boolean bt4a = Test.test("0.9", "nb", "[0.8, 0.9)");

        String value = "0.9564";
        boolean eq = Test.test(value, "eq", "0.9564");//保持3位数
        boolean is = Test.test(value, "is", "0.9564");//保持3位数


        System.out.println();
    }
}
