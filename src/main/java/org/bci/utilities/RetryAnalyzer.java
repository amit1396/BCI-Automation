package org.bci.utilities;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

	private static final int MAX_RETRY = 2;

	private final ThreadLocal<Integer> retryCount =
			ThreadLocal.withInitial(() -> 0);

	@Override
	public boolean retry(ITestResult result) {

		int currentRetryCount = retryCount.get();

		if (currentRetryCount < MAX_RETRY) {

			currentRetryCount++;

			retryCount.set(currentRetryCount);

			result.setAttribute(
					"retryAttempt",
					currentRetryCount
			);

			System.out.println(
					"Retrying: ["
							+ result.getName()
							+ "] | Attempt: "
							+ currentRetryCount
							+ "/"
							+ MAX_RETRY
			);

			return true;
		}

		retryCount.remove();

		return false;
	}
}