

package wos.mobile.util;

import static java.util.UUID.randomUUID;

import org.apache.commons.lang3.ArrayUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil extends org.apache.commons.lang3.StringUtils  {
    public static final String CHARSET_UTF8 = "UTF-8";
    public static final String PATTERN_MOBILE_STR = "^[1][3,4,5,7,8][0-9]{9}$";
    public static final String PATTERN_EMAIL_STR = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    public static final String PATTERN_CAMEL_STR = "[A-Z]([a-z\\d]+)?";
    public static final String PATTERN_UNDERLINE_2_CAMEL_STR = "([A-Za-z\\d]+)(_)?";
    public static final String EMPTY = "";

    public static List<String> stringCastList(String str, String regex) {
        if (isEmpty(str)) {
            return new ArrayList<>();
        }

        List<String> lists = new ArrayList<>();
        String[] strings = str.split(regex);
        for (String tmp : strings) {
            lists.add(tmp);
        }
        return lists;
    }

    public static String stringEncode(String str, String charset) {
        String result = "";
        try {
            result = URLEncoder.encode(str, charset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Long objectToLong(Object object) {
        if (isEmpty((String) object)) {
            return null;
        } else {
            return Long.valueOf(object.toString());
        }
    }

    public static Integer objectToInteger(Object object) {
        if (isEmpty((String) object)) {
            return null;
        } else {
            return Integer.valueOf(object.toString());
        }
    }

    public static Float objectToFloat(Object object) {
        if (isEmpty((String) object)) {
            return null;
        } else {
            return Float.valueOf(object.toString());
        }
    }

    public static Boolean objectToBoolean(Object object) {
        if (isEmpty((String) object)) {
            return null;
        } else {
            String flag = object.toString();
            return "1".equals(flag) || "true".equals(flag);
        }
    }

    public static BigDecimal objectToBigDecimal(Object object) {
        if (isEmpty((String) object)) {
            return null;
        } else {
            return new BigDecimal(object.toString()).compareTo(BigDecimal.ZERO) <= 0 ? new BigDecimal(0) : new BigDecimal(object.toString());
        }
    }

    public static String objectToString(Object object) {
        if (isEmpty((String) object)) {
            return null;
        } else {
            return object.toString();
        }
    }

    public static boolean isMobile(String str) {
        final Pattern patternMobile = Pattern.compile(PATTERN_MOBILE_STR);
        Matcher m = patternMobile.matcher(str);
        return m.matches();
    }

    public static boolean isEmail(String str) {
        final Pattern patternEmail = Pattern.compile(PATTERN_EMAIL_STR);
        Matcher m = patternEmail.matcher(str);
        return m.matches();
    }

    public static String nullToStr(Object object) {
        if (object == null) {
            return "";
        }
        return object.toString();
    }

    public static Integer booleanToInteger(Boolean flag) {
        if (flag) {
            return 1;
        } else {
            return 0;
        }
    }

    public static String getExceptionStack(Exception e) {
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        StringBuilder result = new StringBuilder();
        result.append(e.toString() + "\n");
        for (int index = stackTraceElements.length - 1; index >= 0; --index) {
            result.append("at [" + stackTraceElements[index].getClassName() + ",");
            result.append(stackTraceElements[index].getFileName() + ",");
            result.append(stackTraceElements[index].getMethodName() + ",");
            result.append(stackTraceElements[index].getLineNumber() + "]\n");
        }
        return result.toString();
    }

    public static void fileToLocal(String saveFilePath, String imgurl) {
        String filepath = saveFilePath;
        OutputStream bos = null;
        BufferedInputStream bis = null;

        try {
            // 实例化url
            URL url = new URL(imgurl);
            // 载入图片到输入流
            bis = new BufferedInputStream(url.openStream());
            // 实例化存储字节数组
            byte[] bytes = new byte[100];
            // 设置写入路径以及图片名称
            bos = new FileOutputStream(new File(filepath));
            int len;
            while ((len = bis.read(bytes)) > 0) {
                bos.write(bytes, 0, len);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // 关闭输出流
            if (bos != null) {
                bos.flush();
                bos.close();
            }

            // 关闭输入流
            if (bis != null) {
                bis.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下划线转驼峰法
     * 
     * @param line       源字符串
     * @param smallCamel 大小驼峰,是否为小驼峰
     * @return 转换后的字符串
     */
    public static String underline2Camel(final String line, boolean smallCamel) {
        if (isEmpty(line)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        Pattern pattern = Pattern.compile(PATTERN_UNDERLINE_2_CAMEL_STR);
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            String word = matcher.group();
            sb.append(smallCamel && matcher.start() == 0 ? Character.toLowerCase(word.charAt(0)) : Character.toUpperCase(word.charAt(0)));
            int index = word.lastIndexOf('_');
            if (index > 0) {
                sb.append(word.substring(1, index).toLowerCase());
            } else {
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    /**
     * 驼峰法转下划线
     * 
     * @param line 源字符串
     * @return 转换后的字符串
     */
    public static String camel2Underline(final String line) {
        if (isEmpty(line)) {
            return "";
        }
        String newLine = String.valueOf(line.charAt(0)).toUpperCase().concat(line.substring(1));
        StringBuilder sb = new StringBuilder();
        Pattern pattern = Pattern.compile(PATTERN_CAMEL_STR);
        Matcher matcher = pattern.matcher(newLine);
        while (matcher.find()) {
            String word = matcher.group();
            sb.append(word.toUpperCase());
            sb.append(matcher.end() == newLine.length() ? "" : "_");
        }
        return sb.toString();
    }

    public static void escapeXmlTag(Map<String, Object> data) {
        if (data == null) {
            return;
        }

        Iterator<String> iter = data.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            Object obj = data.get(key);
            if (obj != null && obj instanceof String) {
                String value = replaceXmlTag((String) obj);
                data.put(key, value);
            }
        }
    }

    public static String replaceXmlTag(String s) {
        if (s == null) {
            return "";
        } else {
            return s.replace("&", "&amp;").replace("'", "&apos;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
        }
    }

    public static boolean isEmpty(String s) {
        if (s == null || s.trim().length() < 1 || "null".equals(s)) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(String str) {
        return org.apache.commons.lang3.StringUtils.isNotEmpty(str);
    }

    public static boolean isNotNull(String s) {
        if (s == null || s.trim().length() < 1) {
            return false;
        }
        return true;
    }

    public static String replaceIllegalChar(final String s) {
        String str = s == null ? "" : s;
        str = str.replaceAll("\'", "\'\'");
        str = str.replaceAll("_", "\\_");
        str = str.replaceAll("%", "\\%");
        return str;
    }

    public static String[] getMonthArray(int x) {
        String[] str = new String[x];
        for (int i = 0; i < str.length; i++) {
            str[i] = String.valueOf(0);
        }
        return str;
    }

    public static String[] getQuarter() {
        return new String[] { "第一季度", "第二季度", "第三季度", "第四季度" };
    }

    /**
     * 方法名称:transMapToString 传入参数:map 返回值:String 形如 username'chenziwen^password'1234
     */
    @SuppressWarnings("rawtypes")
    public static String transMapToString(Map map) {
        Map.Entry entry;
        StringBuilder sb = new StringBuilder();
        for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
            entry = (Map.Entry) iterator.next();
            sb.append(entry.getKey().toString()).append(":").append(null == entry.getValue() ? "" : entry.getValue().toString()).append(iterator.hasNext() ? "," : "");
        }
        return sb.toString();
    }

    /**
     * 在str的前后都加上指定字符串
     * 
     * @param str
     * @param warpper
     * @return
     */
    public static String warpper(String str, String warpper) {
        String result = "";
        if (isNotEmpty(str) && isNotEmpty(warpper)) {
            result = warpper + str + warpper;
        }
        return result;
    }

    /**
     * 统计子字符串出现次数
     * 
     * @param str
     * @param subStr
     * @return
     */
    public static int statStrCount(String str, String subStr) {
        int count = 0;
        while (str.indexOf(subStr) >= 0) {
            str = str.substring(str.indexOf(subStr) + subStr.length());
            count++;
        }
        return count;
//        System.out.println("指定字符串在原字符串中出现："+count+"次");
    }

    /**
     * 拼接字符串
     * 
     * @param list
     * @return
     */
//    public static String join(List<?> list) {
//        return org.apache.commons.lang3.StringUtils.join(list, PropertiesUtils.getProperty("DEFAULT_SEPARATOR"));
//    }
    /**
     * 拼接字符串
     * 
     * @param list
     * @param separator
     * @return
     */
    public static String join(List<?> list, String separator) {
        return org.apache.commons.lang3.StringUtils.join(list, separator);
    }

    /**
     * 拼接字符串
     * 
     * @param list
     * @param separator
     * @param warpper
     * @return
     */
    public static String join(List<?> list, String separator, String warpper) {
        return join(list.toArray(), separator, warpper);
    }

    /**
     * 拼接字符串
     * 
     * @param list
     * @param separator 连接符
     * @return
     */
    public static String join(Object[] list, String separator) {
        return org.apache.commons.lang3.StringUtils.join(list, separator);
    }

    /**
     * 拼接字符串
     * 
     * @param list
     * @param separator 分割符
     * @param warpper   包裹符
     * @return
     */
    public static String join(Object[] list, String separator, String warpper) {
        List<String> warpperList = new ArrayList<>();
        for (Object o : list) {
            warpperList.add(warpper + o + warpper);
        }
        return org.apache.commons.lang3.StringUtils.join(warpperList, separator);
    }

    public static String fmtMicrometer(String text) {
        DecimalFormat df;
        if (text.indexOf('.') >= 0) {
            if (text.length() - text.indexOf('.') - 1 == 0) {
                df = new DecimalFormat("###,##0.");
            } else if (text.length() - text.indexOf('.') - 1 == 1) {
                df = new DecimalFormat("###,##0.0");
            } else {
                df = new DecimalFormat("###,##0.00");
            }
        } else {
            df = new DecimalFormat("###,##0");
        }

        double number = 0.0;
        try {
            number = Double.parseDouble(text);
        } catch (Exception e) {
            e.printStackTrace();
            number = 0.0;
        }
        return df.format(number);
    }

    /**
     * 字符串置右，不足位数前置补指定字符
     * 
     * @param src 源字符串
     * @param len 指定长度
     * @param ch  补充的字符串
     * @return
     */
    public static String padRight(String src, int len, char ch) {
        int diff = len - src.length();
        if (diff <= 0) {
            return src;
        }

        char[] charr = new char[len];
        System.arraycopy(src.toCharArray(), 0, charr, 0, src.length());
        for (int i = src.length(); i < len; i++) {
            charr[i] = ch;
        }
        return new String(charr);
    }

    /**
     * 字符串置左，不足位数前置补指定字符
     * 
     * @param src 源字符串
     * @param len 指定长度
     * @param ch  补充的字符串
     * @return
     */
    public static String padLeft(String src, int len, char ch) {
        int diff = len - src.length();
        if (diff <= 0) {
            return src;
        }

        char[] charr = new char[len];
        System.arraycopy(src.toCharArray(), 0, charr, diff, src.length());
        for (int i = 0; i < diff; i++) {
            charr[i] = ch;
        }
        return new String(charr);
    }

    /**
     * 求string的各个char的int之和
     * @param str
     * @return
     */
    public static int SumStrAscii(String str) {
        byte[] bytestr = str.getBytes();
        int sum = 0;
        for (int i = 0; i < bytestr.length; i++) {
            sum += bytestr[i];
        }
        return sum;
    }

    /** 
     * ASCII转AIS
     * @param p
     * @return
     */
    public static String ascii2ais(String p) {
        StringBuilder sb = new StringBuilder();
        char[] chars = p.toCharArray();
        for (char c : chars) {
            int i = SumStrAscii(String.valueOf(c));
            i -= 48;
            if (i > 40) {
                i -= 8;
            }
            String sixBit = padLeft(Integer.toBinaryString(i), 6, '0');
//            sb.append(sixBit + " ");
            sb.append(sixBit);
        }

        return sb.toString();
    }

    /**
     * Performs the logic for the {@code split} and
     * {@code splitPreserveAllTokens} methods that return a maximum array
     * length.
     *
     * @param str  the String to parse, may be {@code null}
     * @param separatorChars the separate character
     * @param max  the maximum number of elements to include in the
     *  array. A zero or negative value implies no limit.
     * @param preserveAllTokens if {@code true}, adjacent separators are
     * treated as empty token separators; if {@code false}, adjacent
     * separators are treated as one separator.
     * @return an array of parsed Strings, {@code null} if null String input
     */
    private static String[] splitWorker(final String str, final String separatorChars, final int max, final boolean preserveAllTokens) {
        // Performance tuned for 2.0 (JDK1.4)
        // Direct code is quicker than StringTokenizer.
        // Also, StringTokenizer uses isSpace() not isWhitespace()

        if (str == null) {
            return null;
        }
        final int len = str.length();
        if (len == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        final List<String> list = new ArrayList<>();
        int sizePlus1 = 1;
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars == null) {
            // Null separator means use whitespace
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else if (separatorChars.length() == 1) {
            // Optimise 1 character case
            final char sep = separatorChars.charAt(0);
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else {
            // standard case
            while (i < len) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        }
        if (match || preserveAllTokens && lastMatch) {
            list.add(str.substring(start, i));
        }
        return list.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public static String[] split(final String str, final String separatorChars, final int max) {
        return splitWorker(str, separatorChars, max, false);
    }

    public static boolean equals(final CharSequence cs1, final CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        if (cs1.length() != cs2.length()) {
            return false;
        }
        if (cs1 instanceof String && cs2 instanceof String) {
            return cs1.equals(cs2);
        }
        // Step-wise comparison
        final int length = cs1.length();
        for (int i = 0; i < length; i++) {
            if (cs1.charAt(i) != cs2.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public static boolean equalsAny(CharSequence string, CharSequence... searchStrings) {
        if (ArrayUtils.isNotEmpty(searchStrings)) {
            CharSequence[] var2 = searchStrings;
            int var3 = searchStrings.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                CharSequence next = var2[var4];
                if (equals(string, next)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean equalsAnyIgnoreCase(CharSequence string, CharSequence... searchStrings) {
        if (ArrayUtils.isNotEmpty(searchStrings)) {
            CharSequence[] var2 = searchStrings;
            int var3 = searchStrings.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                CharSequence next = var2[var4];
                if (equalsIgnoreCase(string, next)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean equalsIgnoreCase(CharSequence cs1, CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        } else if (cs1 != null && cs2 != null) {
            return cs1.length() != cs2.length() ? false : regionMatches(cs1, true, 0, cs2, 0, cs1.length());
        } else {
            return false;
        }
    }

    private static boolean regionMatches(CharSequence cs, boolean ignoreCase, int thisStart, CharSequence substring, int start, int length) {
        if (cs instanceof String && substring instanceof String) {
            return ((String)cs).regionMatches(ignoreCase, thisStart, (String)substring, start, length);
        } else {
            int index1 = thisStart;
            int index2 = start;
            int tmpLen = length;
            int srcLen = cs.length() - thisStart;
            int otherLen = substring.length() - start;
            if (thisStart >= 0 && start >= 0 && length >= 0) {
                if (srcLen >= length && otherLen >= length) {
                    while(tmpLen-- > 0) {
                        char c1 = cs.charAt(index1++);
                        char c2 = substring.charAt(index2++);
                        if (c1 != c2) {
                            if (!ignoreCase) {
                                return false;
                            }

                            char u1 = Character.toUpperCase(c1);
                            char u2 = Character.toUpperCase(c2);
                            if (u1 != u2 && Character.toLowerCase(u1) != Character.toLowerCase(u2)) {
                                return false;
                            }
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    public static String defaultString(final String str) {
        return defaultString(str, EMPTY);
    }

    public static String defaultString(final String str, final String defaultStr) {
        return str == null ? defaultStr : str;
    }

    /**
     * 获取GUID
     *
     * @author Tyr Tao
     * @return
     */
    private static String getGuid() {
        UUID guid = randomUUID();
        return guid.toString();
    }

    /**
     * 获取UUID
     * @return
     */
    public static String getUUID() {
        return getGuid().replace("-", "");
    }
}
