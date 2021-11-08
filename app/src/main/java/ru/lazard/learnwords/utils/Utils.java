package ru.lazard.learnwords.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.annotation.StringRes;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Egor on 06.06.2016.
 */
public class Utils {
    public static float dpToPx(float dp, Context context) {
        float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
        return size;
    }
    public static void showKeyboard(View view  ){
        if (view == null )return;
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }
    public static boolean equals(String s1,String s2){
        if (s1==null&&s2==null)return true;
        if (s1==null&&s2!=null)return false;
        if (s1!=null&&s2==null)return false;
        return s1.equals(s2);
    }
    public static boolean equalsIgnoreCase(String s1,String s2){
        if (s1==null&&s2==null)return true;
        if (s1==null&&s2!=null)return false;
        if (s1!=null&&s2==null)return false;
        return s1.equalsIgnoreCase(s2);
    }
    public static void hideKeyboard(View view){
        if (view == null )return;
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static final String getApplicationName(Context context) {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(context.getPackageName(), 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        final String applicationName = (String) (ai != null ? pm
                .getApplicationLabel(ai) : "(unknown)");
        return applicationName;
    }

    public static String getApplicationVersion(Context context) {
        String version = "uncnown";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }
    /**
     * Random value from [0, maxValue)
     *
     * @param maxValue
     * @return
     */
    public static int randomInt(int maxValue) {
        return (int) (Math.random() * maxValue);
    }
    public static int getApplicationVersionCode(Context context) {
        int versionCode = -1;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }
    public static void sendEmail(String recipient, String subject,
                                 String content, Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        context.startActivity(Intent.createChooser(intent, null));
    }
    public static final void showMarketPage(Context context, @StringRes int market_not_exist_resId) {
        showMarketPage(context.getPackageName(), context, context.getString(market_not_exist_resId));
    }

    public static final void showMarketPage(Context context, String market_not_exist_text) {
        showMarketPage(context.getPackageName(), context, market_not_exist_text);
    }

    public static final void showMarketPage(String packageName, Context context, String market_not_exist_text) {
        if (context == null) return;
        if (TextUtils.isEmpty(packageName)) return;
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            if (!TextUtils.isEmpty(market_not_exist_text)) {
                Toast.makeText(context, market_not_exist_text,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public static boolean isTextEn(String text){
        if (TextUtils.isEmpty(text)) return false;

        char[] array= text.toCharArray();
        String  englishChars = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
        String  rusChars = "йцукенгшщзхъфывапролджэячсмитьбюЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ";
        int enCharsNum = 0;
        int ruCharsNum = 0;
        for (int i = 0; i < array.length; i++) {
            if (englishChars.contains(""+array[i]) ){
                enCharsNum++;
            }
            if (rusChars.contains(""+array[i]) ){
                ruCharsNum++;
            }
        }
        return enCharsNum>ruCharsNum;
    }
    public static final String md5(final String s) {
        if (TextUtils.isEmpty(s)) {
            return "";
        }
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2) {
                    h = "0" + h;
                }
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
