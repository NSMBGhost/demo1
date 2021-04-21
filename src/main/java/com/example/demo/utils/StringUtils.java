package com.example.demo.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.collections.CollectionUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class StringUtils extends org.apache.commons.lang3.StringUtils {

    public static final String UTF_8 = "UTF-8";
    public static final String GBK = "GBK";

    public static final Charset CHARSET_UTF8 = Charset.forName(UTF_8);
    public static final Charset CHARSET_GBK = Charset.forName(GBK);

    public static String serialize(Object obj) {
        Gson gson = new GsonBuilder().setDateFormat(DateUtils.DATE_FORMAT_DEFAULT).create();
        return gson.toJson(obj);
    }

    public static <T> T deserialize(String strJson, Class<T> clazz) {
        Gson gson = new GsonBuilder().setDateFormat(DateUtils.DATE_FORMAT_DEFAULT).create();
        return gson.fromJson(strJson, clazz);
    }

    public static String readFromReader(BufferedReader reader) throws IOException {
        StringBuffer buffer = new StringBuffer();
        String strLine = null;
        while(null != (strLine = reader.readLine())){
            buffer.append(strLine);
        }
        return buffer.toString();
    }

    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    /**
     * 将对象toString().欢迎扩展
     *
     * @param o
     * @return
     * @date 2011-11-2 下午12:23:43
     * @author jiaxiao
     */
    public static String toString(Object o) {
        return toString(o, ",");
    }

    public static String toString(Object o, String spliter) {
        if (null == o) {
            return "";
        } else {
            if (o instanceof Integer || o instanceof Double || o instanceof Boolean || o instanceof Float
                    || o instanceof Long || o instanceof Short || o instanceof StringBuffer
                    || o instanceof StringBuilder) {
                return o.toString();
            } else if (o.getClass().isArray()) {
                StringBuffer buffer = new StringBuffer();
                int len = Array.getLength(o);
                for (int index = 0; index < len; index++) {
                    buffer.append(Array.get(o, index));
                    if (index < len - 1) {
                        buffer.append(spliter);
                    }
                }
                return buffer.toString();
            } else if (o instanceof Collection) {
                StringBuffer buffer = new StringBuffer();
                int len = CollectionUtils.size(o);
                for (int index = 0; index < len; index++) {
                    buffer.append(CollectionUtils.get(o, index));
                    if (index < len - 1) {
                        buffer.append(spliter);
                    }
                }
                return buffer.toString();
            } else {
                return o.toString();
            }
        }
    }

    public static String convertCamelNameToColumnName(String fieldName){
        StringBuffer buffer = new StringBuffer();
        for(int index=0; index<fieldName.length(); index ++){
            char ch = fieldName.charAt(index);
            if(Character.isUpperCase(ch)){
                buffer.append("_").append(Character.toLowerCase(ch));
            }else{
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }

    public static String convertColumnNameToCamelName(String columnName){
        StringBuffer buffer = new StringBuffer();
        String text = columnName.toLowerCase();

        boolean bUpper = false;
        for(int index=0; index< text.length(); index ++){
            char ch = text.charAt(index);
            if('_' == ch){
                bUpper = true;
            }else{
                buffer.append(bUpper?Character.toUpperCase(ch):ch);
                bUpper = false;
            }
        }
        return buffer.toString();
    }

    /**
     * 判断一个字符串是否为整型数字
     * @param value
     * @return
     * @author coraldane
     */
    public static boolean isInteger(String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[+|-]?[0-9]+");
        return pattern.matcher(value).matches();
    }

    public static boolean isPositive(Integer value) {
        if(null == value) {
            return false;
        }
        return value >= 0;
    }

    public static boolean isPositive(Long value) {
        if(null == value) {
            return false;
        }
        return value >= 0;
    }

    public static List<JSONObject> formatMessage(String strResponse, String delim) {
        List<JSONObject> result = new ArrayList<>();
        List<String> lineList = Splitter.on(StringUtils.isEmpty(delim) ? "\n" : delim).splitToList(strResponse);
        List<String> columnNames = null;
        for(int index=0; index < lineList.size(); index++) {
            String strLine = lineList.get(index);
            if(StringUtils.isBlank(strLine)) {
                continue;
            }

            List<String> args = Splitter.on(",").splitToList(strLine);
            if(0 == index) {
                columnNames = args;
                continue;
            }

            JSONObject rowMap = new JSONObject();
            for(int k = 0; k < args.size(); k++) {
                rowMap.put(columnNames.get(k), args.get(k));
            }
            result.add(rowMap);
        }
        return result;
    }
}
