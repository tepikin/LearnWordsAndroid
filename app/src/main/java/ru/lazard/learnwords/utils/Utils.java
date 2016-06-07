package ru.lazard.learnwords.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by Egor on 06.06.2016.
 */
public class Utils {

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
}
