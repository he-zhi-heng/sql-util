package com.he.sqlutils.utils;

/**
 * @author hemoren
 */
public class HumpUtil {
    private static final String UNDERLINE = "_";
    /**
     * 下划线命名转为驼峰命名
     *
     * @param para
     * @return
     */
    public static String underlineToHump(String para) {
        StringBuilder result = new StringBuilder();
        String[] a = para.split(UNDERLINE);
        for (String s : a) {
            if (!para.contains(UNDERLINE)) {
                result.append(s);
                continue;
            }
            if (result.length() == 0) {
                result.append(s.toLowerCase());
            } else {
                result.append(s.substring(0, 1).toUpperCase());
                result.append(s.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    public static String humpToUnderline(String para) {
        return humpToUnderline(para, false);
    }

    /**
     * 驼峰命名转为下划线命名
     *
     * @param para
     * @param capital
     * @return
     */
    public static String humpToUnderline(String para, boolean capital) {
        StringBuilder sb = new StringBuilder(para);
        int temp = 0;
        String separator = UNDERLINE;
        if (!para.contains(separator)) {
            for (int i = 0; i < para.length(); i++) {
                if (Character.isUpperCase(para.charAt(i))) {
                    sb.insert(i + temp, separator);
                    temp += 1;
                }
            }
        }
        String res = sb.toString().startsWith(separator) ? sb.substring(1) : sb.toString();
        if (capital) {
            return res.toUpperCase();
        } else {
            return res.toLowerCase();
        }
    }
}
