package com.varshavikshith.utilsfunctions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.varshavikshith.utilsfunctios.helper.NetworkUtils;
import com.varshavikshith.utilsfunctios.helper.SuccessResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

public class UtilFunctions {

    public static final int AUTOCOMPLETE_REQUEST_CODE = 1001;

    public static void hideTheKeypad(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void commonFakeGpsDialog(final Activity context, String title, final String subjuct) {

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(subjuct)
                .setPositiveButton("Disable", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                context.finishAffinity();
                            } else {
                                System.exit(0);
                            }
                            context.startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                            dialog.dismiss();
                        } catch (Exception e) {
                            System.exit(0);
                            context.startActivity(new Intent(Settings.ACTION_SETTINGS));
                        }
                    }
                }).show();
    }

    public static void commonDialog(final Context context, String content, final boolean isInternetError,
                                    AppCompatActivity appCompatActivity, final View viewDisplay) {
        if (isInternetError) {
            if (NetworkUtils.getConnectionType(context).equals("NA")) {
                showNoInternetSnackBar(appCompatActivity, context, viewDisplay, "Enable your mobile data or wifi", "Settings");
            } else if (NetworkUtils.getConnectionType(context).equals("W")) {
                showNoInternetSnackBar(appCompatActivity, context, viewDisplay, "Your wifi connection is limited. Try alternatives", "Settings");

            } else if (NetworkUtils.getConnectionType(context).equals("M")) {
                showNoInternetSnackBar(appCompatActivity, context, viewDisplay, "Your mobile data connection is limited. Try alternatives", "Settings");

            }

        } else {
            try {
                Snackbar snackbar1 = Snackbar.make(viewDisplay, content, Snackbar.LENGTH_LONG);
                snackbar1.setDuration(15000);
                snackbar1.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void showNoInternetSnackBar(final AppCompatActivity activity, final Context context,
                                               View displayView, String message, String buttonName) {

        try {
            Snackbar snackbar = Snackbar
                    .make(displayView, message, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent myIntent = new Intent(Settings.ACTION_SETTINGS);
                            context.startActivity(myIntent);
                        }
                    });

            snackbar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void gotoScreenSnackBar(final Activity activity, String buttonText, String subject, View viewDisplay, final Intent intent) {
        try {
            Snackbar snackbar1 = Snackbar.make(viewDisplay, subject, Snackbar.LENGTH_LONG);
            snackbar1.setDuration(Snackbar.LENGTH_INDEFINITE);
            snackbar1.setAction(buttonText, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(intent);
                }
            });
            snackbar1.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static void scroolTo(final ScrollView scrollView, final View view) {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0, view.getBottom());
            }
        });
    }

    public static void fullscroolToTop(final View viewfragment, final int id) {
        ((ScrollView) viewfragment.findViewById(id)).post(new Runnable() {
            @Override
            public void run() {
                ((ScrollView) viewfragment.findViewById(id)).fullScroll(((ScrollView) viewfragment.findViewById(id)).FOCUS_UP);
            }
        });

    }

    public static void fullNestedscroolToTop(final View viewfragment, final int id) {
        ((NestedScrollView) viewfragment.findViewById(id)).post(new Runnable() {
            @Override
            public void run() {
                ((NestedScrollView) viewfragment.findViewById(id)).fullScroll(((NestedScrollView) viewfragment.findViewById(id)).FOCUS_UP);
            }
        });

    }

    public static boolean IsToDateIsLowThanFrom(String fromDate, String toDate) throws ParseException {
        Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(fromDate);
        Date date2 = new SimpleDateFormat("dd/MM/yyyy").parse(toDate);
        int result = date2.compareTo(date1);
        if (result < 0) {
            return true;
        } else {
            return false;
        }
    }

    public static String generateSixDigitOtp(Context context) {
        Random rnd = new Random();
        int n = 100000 + rnd.nextInt(900000);
        return String.valueOf(n);
    }

    public static int generateRamdomNoForAlaram() {
        Random rnd = new Random();
        return 100000 + rnd.nextInt(900000);
    }

    public static String getCurrentTimeDate() {
        Date date = new Date();
        String strDateFormat = "hh:mm a";
        java.text.DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        return dateFormat.format(date);
    }

    public static String getTodayDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static String getTodayMonthAndYear() {
        Calendar c = Calendar.getInstance();
        System.out.println("nnnnnnnnnn--------A--------" + c.get(Calendar.YEAR));
        System.out.println("nnnnnnnnnn-------B---------" + c.get(Calendar.MONTH));
        String year = String.valueOf(c.get(Calendar.YEAR));
        String month = "";

        switch (c.get(Calendar.MONTH)) {
            case 0:
                month = "Jan";
                break;
            case 1:
                month = "Feb";
                break;
            case 2:
                month = "Mar";
                break;
            case 3:
                month = "Apr";
                break;
            case 4:
                month = "May";
                break;
            case 5:
                month = "Jun";
                break;
            case 6:
                month = "Jul";
                break;
            case 7:
                month = "Aug";
                break;
            case 8:
                month = "Sep";
                break;
            case 9:
                month = "Oct";
                break;
            case 10:
                month = "Nov";
                break;
            case 11:
                month = "Dec";
        }
        return month + " " + year;


    }

    public static int getCurrentMonth() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH);


    }

    public static int getCurrentYear() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR);
    }

    public static String decodeMonth(int month) {
        String value = null;
        switch (month) {
            case 0:
                value = "Jan";
                break;
            case 1:
                value = "Feb";
                break;
            case 2:
                value = "Mar";
                break;
            case 3:
                value = "Apr";
                break;
            case 4:
                value = "May";
                break;
            case 5:
                value = "Jun";
                break;
            case 6:
                value = "Jul";
                break;
            case 7:
                value = "Aug";
                break;
            case 8:
                value = "Sep";
                break;
            case 9:
                value = "Oct";
                break;
            case 10:
                value = "Nov";
                break;
            case 11:
                value = "Dec";
        }
        return value;
    }

    public static boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    public static String getTheCurrentAppVersionOfTheDevice(Context context) {
        String versionName = "NA";
        try {
            versionName = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public long getCurrentAppVersionCode(Context context) {
        long versionName = 0;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                versionName = context.getPackageManager()
                        .getPackageInfo(context.getPackageName(), 0).getLongVersionCode();
            } else {
                versionName = context.getPackageManager()
                        .getPackageInfo(context.getPackageName(), 0).versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    //////////////////////////// add the sim info
    @SuppressLint("MissingPermission")
    public static String formTheSimInfoString(Context context) {
        ArrayList<String> stringsSimInfo = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            if ((SubscriptionManager.from(context).getActiveSubscriptionInfoList()) != null) {
                List<SubscriptionInfo> subscriptionInfos = null;
                subscriptionInfos = SubscriptionManager.from(context).getActiveSubscriptionInfoList();
                for (int i = 0; i < subscriptionInfos.size(); i++) {
                    SubscriptionInfo lsuSubscriptionInfo = subscriptionInfos.get(i);
                    stringsSimInfo.add(lsuSubscriptionInfo.getIccId());
                }
                return Arrays.toString(stringsSimInfo.toArray());
            } else {
                return "NO_SIM_CARD";
            }
        } else {
            return "BELLOW_KITKAT";
        }


    }

    /////////////// get device total ram size
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static String totalRamMemorySize(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long availableMegs = mi.totalMem / 1048576L;
        double v = availableMegs / 1024.0;
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        return decimalFormat.format(v) + " GB";
    }

    //// convert bytes to mb


    public static String convertBytesToReadableFormat(long trafficPrint) {

        if ((trafficPrint / 1000000000) > 0) {

            return (trafficPrint / 1000000000 + " GB");

        } else if ((trafficPrint / 1000000) > 0) {

            return (trafficPrint / 1000000 + " MB");

        } else if ((trafficPrint / 1000) > 0) {

            return (trafficPrint / 1000 + " kB");

        } else {

            return (trafficPrint + " byte");

        }

    }


    //////////////////// get the device battery capacity
    public static String getTotalBatteryCapacity(Context context) {
        Object mPowerProfile_ = null;

        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class).newInstance(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            double batteryCapacity = (Double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getAveragePower", String.class)
                    .invoke(mPowerProfile_, "battery.capacity");

            return (int) batteryCapacity + " mah";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NA";
    }

    public static String convertTimestampToDate(String dateInMilliseconds) {
        return DateFormat.format("dd/MM/yyyy HH:mm:ss", Long.parseLong(dateInMilliseconds)).toString();
    }

    public static String convertTimestampToDates(String dateInMilliseconds) {
        return DateFormat.format("dd/MM/yyyy", Long.parseLong(dateInMilliseconds)).toString();
    }

    ///// convert sec to hhmmss
    public static String getDurationString(long millis) {
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        return hms;
    }

    /// get current system time in milisec
    public static long getCurrentSystemTimeStamp() {
        Calendar calendar = new GregorianCalendar();
        return calendar.getTimeInMillis();
    }

    public static ArrayList<String> getLaunchApps(Context context) {
        ArrayList<String> stringsPackages = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            if (pm.getLaunchIntentForPackage(packageInfo.packageName) != null) {
                String currAppName = pm.getApplicationLabel(packageInfo).toString();
                //This app is a non-system app
                stringsPackages.add(packageInfo.packageName);
            }
        }
        return stringsPackages;
    }

    /////// check activity is present
    public static boolean isActivityPresent(AppCompatActivity appCompatActivity) {
        if (appCompatActivity != null)
            return !(appCompatActivity).isFinishing();
        else
            return false;
    }

    public static boolean isServiceRunning(Context context, String canonicalName) {
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            if (manager != null) {
                for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                    if (service.service.getClassName().equals(canonicalName)) {
                        return true;
                    }
                }
            } else {
                return true;
            }
            return false;
        } catch (Exception e) {
            Toast.makeText(context, "Error:-" + e.getMessage(), Toast.LENGTH_SHORT).show();
            return true;

        }

    }

    //////////////////////////// get the country code. . .
    public static String GetCountryZipCode(Context context) {
        String CountryID = "";
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = context.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        return "+" + CountryZipCode;
    }

    private static boolean isTimeAutomaticTime(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(c.getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
        } else {
            return Settings.System.getInt(c.getContentResolver(), Settings.System.AUTO_TIME, 0) == 1;
        }
    }

    private static boolean isTimeAutomaticTimeZone(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(c.getContentResolver(), Settings.Global.AUTO_TIME_ZONE, 0) == 1;
        } else {
            return Settings.System.getInt(c.getContentResolver(), Settings.System.AUTO_TIME_ZONE, 0) == 1;
        }
    }

    public static boolean isAutomaticTimeAndZoneEnabled(Context context) {
        return isTimeAutomaticTime(context) && isTimeAutomaticTimeZone(context);
    }

    public static void OpenDateTimeSettingsScreen(Context context) {
        final Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void CreateCommNotificationChannel(Context context,
                                                     String notificationId, String notificationName,
                                                     int notificationImportance, boolean isHighBuzzer) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(notificationId,
                    notificationName,
                    notificationImportance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            if (isHighBuzzer)
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            else
                notificationChannel.setVibrationPattern(new long[]{100});
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public static boolean checkBatteryOptimized(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return !((PowerManager) context.getSystemService(Context.POWER_SERVICE))
                    .isIgnoringBatteryOptimizations(context.getPackageName());
        }
        return false;
    }

    private static Map<String, ?> getAllSharedPreference(Context context, String sharedPreferenceName) {

        SharedPreferences preferences = context.getSharedPreferences(sharedPreferenceName, 0);

        return preferences.getAll();
    }

    public static void sendAppData(Context context, String fileAuthorityName,
                                   String databaseName, String[] emailTo, String subject) throws JSONException {

        File prefsdir = new File(context.getApplicationInfo().dataDir, "shared_prefs");
        String[] list = new String[]{};
        if (prefsdir.exists() && prefsdir.isDirectory()) {
            list = prefsdir.list();
        }

        JSONArray jsonArrayMain = new JSONArray();
        /////////////////  DYNAMIC SHARED PREFERENCETO GET/////////////////////
        for (int i = 0; i < list.length; i++) {
            String preffile = list[i].substring(0, list[i].length() - 4);

            JSONObject jsonObjectShared = new JSONObject();
            jsonObjectShared.put("Shared Preference Name", preffile);
            Map<String, ?> allPreferenceValueKeys = getAllSharedPreference(context, preffile);
            for (Map.Entry<String, ?> entry : allPreferenceValueKeys.entrySet()) {
                jsonObjectShared.put(entry.getKey() + "", entry.getValue().toString() + "");
            }
            jsonArrayMain.put(jsonObjectShared);

        }

        String encededString = Base64.encodeToString(jsonArrayMain.toString().getBytes(), Base64.DEFAULT);

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_EMAIL, emailTo);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.putExtra(Intent.EXTRA_TEXT, encededString);
        sendIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
        File file2 = new File("data/data/" + context.getPackageName() + "/databases/" + databaseName + ".db");
        if (file2.exists()) {
            final Uri path = FileProvider.getUriForFile(context,
                    fileAuthorityName, file2);
            sendIntent.putExtra(Intent.EXTRA_STREAM, path);
        }
        sendIntent.setType("application/octet-stream");
        context.startActivity(Intent.createChooser(sendIntent, "Email:"));
    }

    public static int getLocationMode(Context context) {
        try {
            return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            return 2;
        }
    }

    public static boolean isGpsOn(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean status = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return status;
    }

    public static void gotoLocationSettings(final Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title);

        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle(title);
        alert.show();
    }

    public static String getDateAfterDays(String dt, int days) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, days);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
        return sdf1.format(c.getTime());
    }

    public static long getDaysBetweenDates(String start, String end) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date startDate, endDate;
        long numberOfDays = 0;
        try {
            startDate = dateFormat.parse(start);
            endDate = dateFormat.parse(end);
            numberOfDays = getUnitBetweenDates(startDate, endDate, TimeUnit.DAYS);
        } catch (ParseException e) {
            System.out.println("------cccccccc------" + e.getMessage());
            e.printStackTrace();
        }
        return numberOfDays;
    }

    private static long getUnitBetweenDates(Date startDate, Date endDate, TimeUnit unit) {
        long timeDiff = endDate.getTime() - startDate.getTime();
        return unit.convert(timeDiff, TimeUnit.MILLISECONDS);
    }

    public static void initializePlaces(Context context, String GOOGLE_API_KEY) {
        if (!Places.isInitialized()) {
            Places.initialize(context, GOOGLE_API_KEY);
        }
        PlacesClient placesClient = Places.createClient(context);
    }

    public static void openAutoCompleteOverlay(Activity context, String countryCode, List<Place.Field> fields) {
        if (fields == null)
            fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);

        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .setTypeFilter(TypeFilter.ADDRESS)
                .setCountry(countryCode != null ? countryCode : "IN")
                .build(context);
        context.startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    public static void selectDateDialog(Context context,
                                        final SuccessResultListener dateResultListener) {
        SimpleDateFormat mDF = new SimpleDateFormat("yyyy-mm-dd");
        Date today = new Date();
        mDF.format(today);
        final Calendar c = Calendar.getInstance();
        c.setTime(today);
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String formattedDay = (String.valueOf(dayOfMonth));
                        int m = monthOfYear + 1;
                        String formattedMonth = (String.valueOf(m));
                        if (dayOfMonth < 10) {
                            formattedDay = "0" + dayOfMonth;
                        }
                        if (m < 10) {
                            formattedMonth = "0" + m;
                        }
                        dateResultListener.successResponse(formattedDay + "/" + (formattedMonth) + "/" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        datePickerDialog.show();
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
    }

    public static long getDateToTimeStamp(String convertDate) {
        java.text.DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = (Date) formatter.parse(convertDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return getCurrentSystemTimeStamp();
        }
        return date.getTime();
    }

    /////////// return with required pattern
    public static String getFormattedDate(long time, String pattern) {
        Date date = new Date(time); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat(pattern); // the format of your date
        return sdf.format(date);
    }

}
