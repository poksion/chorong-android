package net.poksion.chorong.android.samples.test.fixtures;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.Until;

@SuppressLint("NewApi")
public class AppFixture {

    private static AppFixture sAppFixture;
    private AppFixture() {}

    private UiDevice uiDevice;
    private long timeout = 5000;
    private boolean hangOn = false;

    public static void startApp() {
        sAppFixture = new AppFixture();

        sAppFixture.parseArguments(InstrumentationRegistry.getArguments());
        sAppFixture.startApp("net.poksion.chorong.android.samples");
    }

    public static void onThen() {

        sAppFixture.hangOnIf();
    }

    private void parseArguments(Bundle arguments) {
        // TODO
        // -e xxx yyy
        // key : xxx
        // value : yyy
        // the type of value is always string

        try {
            hangOn = Boolean.parseBoolean(arguments.getString("hangon", "false"));
        } catch(Exception e) {
            e.printStackTrace();
            hangOn = false;
        }
    }

    private void startApp(String packageName) {
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        uiDevice.pressHome();

        final String launcherPackage = uiDevice.getLauncherPackageName();
        uiDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), timeout * 2);

        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        uiDevice.waitForWindowUpdate(null, timeout);
    }

    private void hangOnIf() {
        while(hangOn) {
            sleepQuietly(10000);
        }
    }

    private void sleepQuietly(long ms) {
        try {
            Thread.sleep(ms);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
