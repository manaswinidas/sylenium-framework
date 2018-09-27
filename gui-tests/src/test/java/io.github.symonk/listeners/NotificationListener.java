package io.github.symonk.listeners;

import io.github.symonk.common.helpers.slack.SlackHelper;
import io.github.symonk.configurations.properties.FrameworkProperties;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.aeonbits.owner.ConfigFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class NotificationListener implements ITestListener {

  public static final AtomicInteger testsPassed = new AtomicInteger(0);
  public static final AtomicInteger testsFailed = new AtomicInteger(0);
  private final SlackHelper slackNotifier = new SlackHelper(ConfigFactory.create(FrameworkProperties.class));
  public final Map<Integer, Integer> mapOfTestsWithResults = new HashMap<>();


  @Override
  public void onTestStart(final ITestResult iTestResult) {
    slackNotifier.notify("Test run started");
    final Method method = iTestResult.getMethod().getConstructorOrMethod().getMethod();
    String value =  method.getAnnotation(TmsLink.class).value();
    if(!value.isEmpty()) {
      mapOfTestsWithResults.put(Integer.valueOf(value), iTestResult.getStatus());
    }
  }

  @Override
  public void onTestSuccess(final ITestResult iTestResult) {
    final Method method = iTestResult.getMethod().getConstructorOrMethod().getMethod();
    String value =  method.getAnnotation(TmsLink.class).value();
    if(!value.isEmpty()) {
      mapOfTestsWithResults.put(Integer.valueOf(value), iTestResult.getStatus());
    }
  }

  @Override
  public void onTestFailure(final ITestResult iTestResult) {
    slackNotifier.notify("Test Failure! " + iTestResult.getName());
    testsFailed.incrementAndGet();
  }

  @Override
  public void onTestSkipped(final ITestResult iTestResult) {
    //
  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(final ITestResult iTestResult) {
    //
  }

  @Override
  public void onStart(final ITestContext iTestContext) {
    //
  }

  @Override
  public void onFinish(final ITestContext iTestContext) {
    slackNotifier.notify("Test run finished!");
  }
}
