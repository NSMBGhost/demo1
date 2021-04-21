package com.example.demo.utils;



import com.example.demo.bean.LimitThreshold;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class BizUtils {
    public static <T> boolean hasElem(T[] array, T value) {
        for(T key: array) {
            if(key.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsElem(String[] array, String value) {
        for(String key: array) {
            if(StringUtils.isNotBlank(value) && value.contains(key)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isIgnoreSecond(Integer second) {
        return isIgnoreMinute(second / 100);
    }

    public static boolean isIgnoreMinute(Integer minute) {
        int hour = minute / 100;
        int min = minute % 100;
        if (hour == 9 && min < 30) {
            return true;
        }
        if (hour == 11 && min >= 30) {
            return true;
        }
        if (hour == 15 && min > 0) {
            return true;
        }
        if (hour < 9 || 12 == hour || hour > 16) {
            return true;
        }
        return false;
    }

    public static String convertCodeToOriginal(String code) {
        return code.replaceAll("\\..*", "");
    }

    public static String convertDateKey(int dateKey) {
        String strDate = String.valueOf(dateKey);
        StringBuffer buffer = new StringBuffer();
        buffer.append(strDate.substring(0, 4)).append("-")
                .append(strDate.substring(4, 6)).append("-")
                .append(strDate.substring(6, 8));
        return buffer.toString();
    }

    public static int convertDealDate(String dealDate) {
        return Integer.parseInt(dealDate.replaceAll("-", ""));
    }

    public static String parseTimestampFromMin(Integer dateKey, Integer timeKey) {
        StringBuilder builder = new StringBuilder();
        builder.append(dateKey / 10000).append("-");

        long dateLeft = dateKey % 10000;
        long month = dateLeft / 100;
        if(month < 10) {
            builder.append("0");
        }
        builder.append(month).append("-");

        long dayOfMonth = dateLeft % 100;
        if(dayOfMonth < 10) {
            builder.append("0");
        }
        builder.append(dayOfMonth).append(" ");

        long hour = timeKey / 100;
        if(hour < 10) {
            builder.append("0");
        }
        builder.append(hour).append(":");

        long minute = timeKey % 100;
        if(minute < 10) {
            builder.append("0");
        }
        builder.append(minute).append(":00");
        return builder.toString();
    }

    public static int findNearlyStartMinute(int startTime) {
        int result = 0;
        if(startTime >= 1459) {
            result = 1500;
        } else if(startTime > 1300) {
            result = startTime;
        } else if(startTime > 1130) {
            result = 1130;
        } else if(startTime > 930) {
            result = startTime;
        } else {
            result = 930;
        }
        return result;
    }

    public static int findNearlyEndMinute(int lastTime) {
        int result = 0;
        if(lastTime > 1500) {
            result = 1500;
        } else if(lastTime > 1300) {
            result = lastTime;
        } else if(lastTime > 1130) {
            result = 1130;
        } else if(lastTime > 930) {
            result = lastTime;
        } else {
            result = 930;
        }
        return result;
    }

    public static Integer getDateIntValueForNow() {
        String strTime = DateUtils.formatNow("yyyyMMdd");
        return Integer.parseInt(strTime);
    }

    public static Integer getMinuteIntValueForNow() {
        String strTime = DateUtils.formatNow("HHmm");
        return Integer.parseInt(strTime);
    }

    public static Integer getDateIntValue(Date date) {
        String strTime = DateUtils.formatTime(date, "yyyyMMdd");
        return Integer.parseInt(strTime);
    }

    public static Integer getMinuteIntValue(Date date) {
        String strTime = DateUtils.formatTime(date, "HHmm");
        return Integer.parseInt(strTime);
    }

    public static Long getMinuteKeyForNow() {
        String strTime = DateUtils.formatNow("yyyyMMddHHmm");
        return Long.parseLong(strTime);
    }

    public static Long getMinuteKey(Integer date, Integer time) {
        return date * 10000L + time;
    }

    public static Long getMinuteKey(Date date) {
        String strTime = DateUtils.formatTime(date, "yyyyMMddHHmm");
        return Long.parseLong(strTime);
    }

    public static String parseFuturesName(String securityId) {
        int pos = securityId.indexOf(".");
        if(pos > 4) {
            return securityId.substring(0, pos - 4);
        }
        return securityId.substring(0, pos);
    }

    public static String getExtension(String securityId) {
        int pos = securityId.indexOf(".");
        return securityId.substring(pos);
    }

    public static String convertToMainContract(String securityId) {
        int pos = securityId.indexOf(".");
        return parseFuturesName(securityId) + "6666" + securityId.substring(pos);
    }

    public static String convertToAssistantContract(String securityId) {
        int pos = securityId.indexOf(".");
        return parseFuturesName(securityId) + "7777" + securityId.substring(pos);
    }

    public static String parseStockCodeFromSecurityId(String securityId) {
        if(StringUtils.isBlank(securityId)) {
            return securityId;
        } else if(StringUtils.contains(securityId, ".")) {
            int dotIndex = securityId.indexOf(".");
            return securityId.substring(0, dotIndex);
        }
        return securityId;
    }

    public static String parseIndexCodeFromSecurityId(String securityId) {
        if(StringUtils.isBlank(securityId)) {
            return securityId;
        } else if(StringUtils.contains(securityId, ".")) {
            if(securityId.contains("XSHG")) {
                return securityId.replaceAll("XSHG", "SH");
            } else if(securityId.contains("XSHE")) {
                return securityId.replaceAll("XSHE", "SZ");
            }
        }
        return securityId;
    }

    public static String convertCodeToJuKuan(String code) {
        if(code.contains(".") && code.length() - code.indexOf(".") > 4) {
            return code;
        }
        if(code.endsWith(".SH")) {
            return code.replaceAll("\\..*", "") + ".XSHG";
        } else if(code.endsWith(".SZ")) {
            return code.replaceAll("\\..*", "") + ".XSHE";
        } else {
            if(code.startsWith("6")) {
                return code.replaceAll("\\..*", "") + ".XSHG";
            } else {
                return code.replaceAll("\\..*", "") + ".XSHE";
            }
        }
    }

    public static String convertCodeToInsight(String code) {
        if(code.endsWith(".XSHG")) {
            return code.replaceAll("\\..*", "") + ".SH";
        } else if(code.endsWith("XSHE")) {
            return code.replaceAll("\\..*", "") + ".SZ";
        } else {
            if(code.startsWith("6")) {
                return code.replaceAll("\\..*", "") + ".SH";
            } else {
                return code.replaceAll("\\..*", "") + ".SZ";
            }
        }
    }

    public static Double getDoubleValue(BigDecimal bigDecimal) {
        if(null == bigDecimal) {
            return null;
        }
        return bigDecimal.doubleValue();
    }

    public static LimitThreshold parseLimitThreshold(String securityId, int dateKey, int lastUnLimitDay) {
        //新股上市首日、科创板/创业板上市前5个交易日不设置涨跌停
        if(dateKey <= lastUnLimitDay) {
            return LimitThreshold.NO_LIMIT;
        }

        if(securityId.startsWith("68") && dateKey >= 20190729) {
            return LimitThreshold.PERCENT_TWENTY;
        } else if(securityId.startsWith("30") && dateKey >= 20200824) {
            return LimitThreshold.PERCENT_TWENTY;
        } else {
            return LimitThreshold.PERCENT_TEN;
        }
    }

    public static boolean checkPriceOverLimit(BigDecimal actual, BigDecimal expect, LimitThreshold threshold) {
//        round(Close*adj *90%) <= price*adj  <= round(Close*adj *110%)
        BigDecimal lowLimit = expect.multiply(BigDecimal.ONE.subtract(threshold.getValue())).setScale(2, RoundingMode.HALF_DOWN);
        BigDecimal highLimit = expect.multiply(BigDecimal.ONE.add(threshold.getValue())).setScale(2, RoundingMode.HALF_DOWN);
        return lowLimit.compareTo(actual) <= 0 && actual.compareTo(highLimit) <= 0;
    }
    public static double convertBigDemicalToDouble(BigDecimal a)
    {
        if (a != null) { return a.doubleValue();}
        else {
            Double ab=Double.NaN;
            return ab;
        }
    }
}
