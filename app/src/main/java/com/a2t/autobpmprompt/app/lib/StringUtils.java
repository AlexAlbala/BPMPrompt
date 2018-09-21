package com.a2t.autobpmprompt.app.lib;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alex on 23/11/15.
 */
public class StringUtils {
    public static boolean isEmpty(String s) {
        return (s == null) || (s.equals(""));
    }

    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    public static String join(String s, String ch) {
        return join(s, ch, false, false);
    }

    public static String join(String s, String ch, boolean start) {
        return join(s, ch, start, false);
    }

    public static String join(String s, String ch, boolean start, boolean end) {
        String ret = "";
        if (start) {
            ret = ch;
        }

        for (int i = 0; i < s.length(); i++) {
            ret += s.charAt(i) + ch;
        }

        if (!end) {
            ret = ret.substring(0, s.length() - ch.length());
        }

        return ret;
    }

    public static String join(List<String> list, String ch) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));

            if (i < list.size() - 1) {
                sb.append(ch);
            }
        }

        return sb.toString();
    }

    public static boolean isEmail(String email) {
        if(isEmpty(email)) return false;
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches())
            return true;
        else
            return false;
    }

    public static boolean isTypicalEmail(String email) {
        String regex =
                ".*@(((g|hot)?mail)|yahoo|outlook|live|gmx)\\.(com|com\\.au|com\\.tw|ca|co\\.nz|co\\.uk|de|fr|it|ru|net|org|edu|gov|jp|nl|kr|se|eu|ie|co\\.il|us|at|be|dk|hk|es|gr|ch|no|cz|net|net\\.au|info|biz|mil|co\\.jp|sg|hu|uk|in)";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(email);
        if (matcher.matches())
            return true;
        else
            return false;
    }

    public static boolean isNumber(String email) {
        String regExpn = "^([+-]?\\d*\\.?\\d*)$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches())
            return true;
        else
            return false;
    }

    public static String capitalize(String input){
        return input.substring(0,1).toUpperCase() + input.substring(1);
    }
}
