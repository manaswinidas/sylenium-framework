package io.github.symonk.testcases;

import com.codeborne.selenide.Selenide;
import io.github.symonk.configurations.selenide.CustomListener;
import io.github.symonk.configurations.selenide.CustomSelenideLogger;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

@Slf4j
public class TestBaseTemplate {

    private static final String TEST_NAME = "test_name";
    private static final CustomListener listener = new CustomListener().withPageSource(true).withScreenshot(true).withTestLog(true);

    @BeforeMethod(alwaysRun = true, description = "Setup logger for test")
    public void initiateLogger(final Method method) {
        startTestLogging(method.getName());
        log.info("Executing: + " + method.getName());
        CustomSelenideLogger.addListener("CustomListener", listener.setCurrentLog(method.getName()));
    }

    @AfterMethod(description = "Unload the test threads logger")
    public void finalizeLogger(final Method method) {
        stopTestLogging();
        CustomSelenideLogger.setListenerLogFile(method.getName());
    }

    @AfterMethod(description = "Prevent browser session leakage")
    public void preventBrowserSessionLeakage() {
        Selenide.clearBrowserLocalStorage();
        Selenide.clearBrowserCookies();
        Selenide.close();
    }

    @AfterMethod(description = "Unregister test listeners")
    public void unregisterTestListeners() {
        CustomSelenideLogger.removeAllListeners();
    }

    private void startTestLogging(String name) {
        log.info("Multi threaded logger initialized for test: " + name);
        MDC.put(TEST_NAME, name);
    }

    private String stopTestLogging() {
        String name = MDC.get(TEST_NAME);
        MDC.remove(TEST_NAME);
        return name;
    }


}