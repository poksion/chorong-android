package net.poksion.chorong.android.samples.test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.support.test.internal.runner.RunnerArgs;
import android.support.test.internal.runner.TestExecutor;
import android.support.test.internal.runner.TestRequest;
import android.support.test.internal.runner.TestRequestBuilder;
import android.support.test.internal.runner.listener.ActivityFinisherRunListener;
import android.support.test.internal.runner.listener.CoverageListener;
import android.support.test.internal.runner.listener.DelayInjector;
import android.support.test.internal.runner.listener.InstrumentationResultPrinter;
import android.support.test.internal.runner.listener.LogRunListener;
import android.support.test.internal.runner.listener.SuiteAssignmentPrinter;
import android.support.test.runner.MonitoringInstrumentation;
import android.support.test.runner.lifecycle.ApplicationLifecycleCallback;
import android.support.test.runner.lifecycle.ApplicationLifecycleMonitorRegistry;
import android.util.Log;
import cucumber.api.android.CucumberInstrumentationCore;
import org.junit.runner.notification.RunListener;

@SuppressLint("NewApi")
public class Instrumentation extends MonitoringInstrumentation {

    private final CucumberInstrumentationCore instrumentationCore = new CucumberInstrumentationCore(this);

    // to run as junit runner
    private boolean isJUnitTest = false;

    // CAUTION
    // This instrumentation can be running as AndroidJUnitRunner mode.
    // Below code is copied from AndroidJunitRunner
    private static final String LOG_TAG = "AndroidJUnitRunner";

    private Bundle mArguments;
    private InstrumentationResultPrinter mInstrumentationResultPrinter = null;
    private RunnerArgs mRunnerArgs;

    @Override
    public void onCreate(final Bundle bundle) {
        if(bundle.getString("class", null) != null || bundle.getString("package", null) != null) {
            isJUnitTest = true;
            onCreateForJUnit(bundle);
        } else {
            super.onCreate(bundle);
            instrumentationCore.create(bundle);
            start();
        }
    }

    @Override
    public void onStart() {
        if(isJUnitTest) {
            onStartForJUnit();
        } else {
            waitForIdleSync();
            instrumentationCore.start();
        }
    }

    private void onCreateForJUnit(Bundle arguments) {
        mArguments = arguments;
        parseRunnerArgs(mArguments);

        if (mRunnerArgs.debug) {
            Log.i(LOG_TAG, "Waiting for debugger to connect...");
            Debug.waitForDebugger();
            Log.i(LOG_TAG, "Debugger connected.");
        }

        super.onCreate(arguments);

        for (ApplicationLifecycleCallback listener : mRunnerArgs.appListeners) {
            ApplicationLifecycleMonitorRegistry.getInstance().addLifecycleCallback(listener);
        }

        start();
    }

    private void parseRunnerArgs(Bundle arguments) {
        mRunnerArgs = new RunnerArgs.Builder()
                .fromManifest(this)
                .fromBundle(arguments)
                .build();
    }

    private Bundle getArguments(){
        return mArguments;
    }

    private InstrumentationResultPrinter getInstrumentationResultPrinter() {
        return mInstrumentationResultPrinter;
    }

    private void onStartForJUnit() {
        super.onStart();

        if (mRunnerArgs.idle) {
            // TODO this is just proof of concept. A more detailed implementation will follow
            Log.i(LOG_TAG, "Runner is idle...");
            return;
        }

        Bundle results = new Bundle();
        try {
            TestExecutor.Builder executorBuilder = new TestExecutor.Builder(this);

            addListeners(mRunnerArgs, executorBuilder);

            TestRequest testRequest = buildRequest(mRunnerArgs, getArguments());

            results = executorBuilder.build().execute(testRequest);

        } catch (RuntimeException e) {
            final String msg = "Fatal exception when running tests";
            Log.e(LOG_TAG, msg, e);
            // report the exception to instrumentation out
            results.putString(Instrumentation.REPORT_KEY_STREAMRESULT,
                    msg + "\n" + Log.getStackTraceString(e));
        }
        finish(Activity.RESULT_OK, results);
    }

    private void addListeners(RunnerArgs args, TestExecutor.Builder builder) {
        if (args.suiteAssignment) {
            builder.addRunListener(new SuiteAssignmentPrinter());
        } else {
            builder.addRunListener(new LogRunListener());
            mInstrumentationResultPrinter = new InstrumentationResultPrinter();
            builder.addRunListener(mInstrumentationResultPrinter);
            builder.addRunListener(new ActivityFinisherRunListener(this,
                    new MonitoringInstrumentation.ActivityFinisher()));
            addDelayListener(args, builder);
            addCoverageListener(args, builder);
        }

        addListenersFromArg(args, builder);
    }

    private void addCoverageListener(RunnerArgs args, TestExecutor.Builder builder) {
        if (args.codeCoverage) {
            builder.addRunListener(new CoverageListener(args.codeCoveragePath));
        }
    }

    private void addDelayListener(RunnerArgs args, TestExecutor.Builder builder) {
        if (args.delayInMillis > 0) {
            builder.addRunListener(new DelayInjector(args.delayInMillis));
        } else if (args.logOnly && Build.VERSION.SDK_INT < 16) {
            // On older platforms, collecting tests can fail for large volume of tests.
            // Insert a small delay between each test to prevent this
            builder.addRunListener(new DelayInjector(15 /* msec */));
        }
    }

    private void addListenersFromArg(RunnerArgs args, TestExecutor.Builder builder) {
        for (RunListener listener : args.listeners) {
            builder.addRunListener(listener);
        }
    }

    @Override
    public boolean onException(Object obj, Throwable e) {
        InstrumentationResultPrinter instResultPrinter = getInstrumentationResultPrinter();
        if (instResultPrinter != null) {
            // report better error message back to Instrumentation results.
            instResultPrinter.reportProcessCrash(e);
        }
        return super.onException(obj, e);
    }

    private TestRequest buildRequest(RunnerArgs runnerArgs, Bundle bundleArgs) {

        TestRequestBuilder builder = createTestRequestBuilder(this, bundleArgs);

        // only scan for tests for current apk aka testContext
        // Note that this represents a change from InstrumentationTestRunner where
        // getTargetContext().getPackageCodePath() aka app under test was also scanned
        builder.addApkToScan(getContext().getPackageCodePath());

        builder.addFromRunnerArgs(runnerArgs);

        return builder.build();
    }

    private TestRequestBuilder createTestRequestBuilder(Instrumentation instr, Bundle arguments) {
        return new TestRequestBuilder(instr, arguments);
    }
}
