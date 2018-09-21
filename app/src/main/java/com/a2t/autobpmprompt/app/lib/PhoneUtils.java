package com.a2t.autobpmprompt.app.lib;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;
import android.telephony.TelephonyManager;

//import com.google.android.gms.ads.identifier.AdvertisingIdClient;
//import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
//import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;
import java.util.Locale;

public class PhoneUtils {
    public static String myPhoneNumber(Context ctx) {
        TelephonyManager tMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        return tMgr.getLine1Number();
    }

    private static String getMy9DigitPhoneNumber(Context ctx) {
        String s = myPhoneNumber(ctx);
        return s != null && s.length() > 2 ? s.substring(2) : null;
    }

    /**
     * Get ISO 3166-1 alpha-2 country code for this device (or null if not available)
     *
     * @param context Context reference to get the TelephonyManager instance from
     * @return country code or null
     */
    public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                } else{
                    String country = context.getResources().getConfiguration().locale.getCountry();
                    if (StringUtils.isNotEmpty(country)) {
                        return country;
                    } else {
                        return context.getResources().getConfiguration().locale.getLanguage();
                    }
                }
            } else {
                //No code available, get the current locale config
                String country = context.getResources().getConfiguration().locale.getCountry();
                if (StringUtils.isNotEmpty(country)) {
                    return country;
                } else {
                    return context.getResources().getConfiguration().locale.getLanguage();
                }
            }
        } catch (Exception e) {
            LogUtils.e("CountryCode", "Error getting country code", e);
        }
        return null;
    }

    /**
     * Get ISO 3166-1 alpha-2 country code for this device (or null if not available)
     *
     * @param context Context reference to get the TelephonyManager instance from
     * @return country code or null
     */
    public static String getSIMSerialNumber(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSimSerialNumber();
        } catch (Exception e) {
            LogUtils.e("SerialNumber", "Error getting SIM serial number", e);
        }
        return null;
    }

    public static String getDeviceId(Context context) {
        try {
            String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            return deviceId;
        } catch (Exception e) {
            LogUtils.e("SerialNumber", "Error getting device ID", e);
        }
        return null;
    }

//    public static void getAdvertisingID(final Context ctx, final ArgumentCallback<String> cb) {
//        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
//            @Override
//            protected String doInBackground(Void... params) {
//                AdvertisingIdClient.Info idInfo = null;
//                try {
//                    idInfo = AdvertisingIdClient.getAdvertisingIdInfo(ctx);
//                } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException | IOException e) {
//                    e.printStackTrace();
//                    cb.error("Google play services not found", e);
//                }
//
//                String advertId = null;
//                if (idInfo != null) {
//                    advertId = idInfo.getId();
//                }
//
//                return advertId;
//            }
//
//            @Override
//            protected void onPostExecute(String advertId) {
//                if (StringUtils.isEmpty(advertId)) {
//                    cb.error("Advertising id empty", new Exception("Advertising id empty"));
//                } else {
//                    cb.done(advertId);
//                }
//            }
//        };
//        task.execute();
//    }
}
