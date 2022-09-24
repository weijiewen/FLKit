package com.wjw.flkit.unit;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FLRegexInputFilter implements InputFilter {
    private Pattern pattern;
    private String regex;

    static public String VaildMoney = "(^(([1-9]\\d*)|0)(\\.([0-9]{0,2}))?$)";
    static public String VaildNumber = "(^\\d+$)";
    static public String VaildPhone = "(^1[0-9]{0,10}$)";
    static public String VaildEmail = "(^[A-Za-z0-9]([A-Za-z0-9._%+_]?)+(@?[A-Za-z0-9]?)+(\\.[A-Za-z]{0,3})?$)";
    static public String VaildPassword = "(^[A-Za-z0-9`~!@#$%^&*()_+\\-=\\[\\]\\;',./{}|:\"<>\\?]+$)";
    static public String VaildIDCard = "(^[0-9]{15}$)|([0-9]{17}([0-9]|X)$)";
    public FLRegexInputFilter(String regex) {
        this.regex = regex;
        pattern = Pattern.compile(regex);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String sourceText = source.toString();
        String destText = dest.toString();
        //验证删除等按键
        if (TextUtils.isEmpty(sourceText)) {
            return null;
        }
        String allText = destText;
        String replaceStr = destText.substring(dstart, dend);
        if (allText.isEmpty()) {
            allText = sourceText;
        }
        else {
            if (replaceStr.isEmpty()) {
                if (dstart == destText.length()) {
                    allText = destText + sourceText;
                }
                else {
                    StringBuilder builder = new StringBuilder(allText);
                    builder.insert(dstart, sourceText);
                    allText = builder.toString();
                }
            }
            else {
                allText.replace(replaceStr, sourceText);
            }
        }
        if (regex == VaildIDCard) {
            if (allText.length() < 18) {
                Matcher matcher = Pattern.compile(VaildNumber).matcher(sourceText);
                if (!matcher.matches()) {
                    return "";
                }
                else {
                    return null;
                }
            }
        }
        Matcher matcher = pattern.matcher(allText);
        if (!matcher.matches()) {
            return "";
        }
        return null;
    }
}
