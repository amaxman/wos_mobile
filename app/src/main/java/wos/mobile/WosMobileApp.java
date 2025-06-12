package wos.mobile;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

import wos.mobile.entity.Constants;
import wos.mobile.entity.Property;
import wos.mobile.service.BasicRestService;
import wos.mobile.util.CommonFunc;
import wos.mobile.util.CrashHandler;


public class WosMobileApp extends Application {

    private static WosMobileApp app;

    public CrashHandler mCrashHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        alarmManager();
        mCrashHandler = CrashHandler.getInstance();
        mCrashHandler.init(getApplicationContext());
        init();
        BasicRestService.restServer= Constants.RestConfig.restServer;
    }

    public void init() {
        WosMobileApp.app = this;
    }

    public void exit() {
        Property.sessionId = "";
        Property.staffName = "";
        Property.compName = "";

        SharedPreferences sp = getSharedPreferences(CommonFunc.CONFIG, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(CommonFunc.Config_SessionId);
        editor.remove(CommonFunc.Config_StaffName);
        editor.apply();
        System.exit(0);
    }

    public static WosMobileApp getInstance() {
        return WosMobileApp.app;
    }

    public CrashHandler getCrashHandler() {
        return mCrashHandler;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private void alarmManager() {
        try {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent intent = new Intent();
            intent.setAction("xftime.wolf.intentservice");
            PendingIntent send = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 23 * 60 * 60 * 1000, AlarmManager.INTERVAL_DAY, send);
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
    }

    //#region 语言
    private Locale getCurrentLocale(Context context) {
        Configuration config = context.getResources().getConfiguration();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return config.getLocales().get(0);
        } else {
            return config.locale;
        }
    }
    //#endregion
}
