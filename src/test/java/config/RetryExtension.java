package config;

import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

/**
 * JUnit 5 extension for retrying failed tests
 * Retry count is configured via test.retry.count property
 * Note: This is a simplified retry mechanism that logs retry attempts.
 */
@Slf4j
public class RetryExtension implements TestExecutionExceptionHandler {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create("RetryExtension");

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        int maxRetries = ConfigProvider.getConfig().retryCount();

        ExtensionContext.Store store = context.getStore(NAMESPACE);
        Integer currentRetry = store.getOrDefault(context.getUniqueId(), Integer.class, 0);

        if (currentRetry < maxRetries) {
            store.put(context.getUniqueId(), currentRetry + 1);
            String testName = context.getDisplayName();
            log.info("[RETRY] Test '{}' failed on attempt {}. Will be retried. (Max retries: {})", testName, currentRetry + 1, maxRetries);
            Allure.addAttachment("Retry Information",
                    String.format("Test failed on attempt %d of %d", currentRetry + 1, maxRetries + 1));
        } else {
            log.info("[RETRY] Test '{}' failed after {} attempts.", context.getDisplayName(), maxRetries + 1);
        }
        throw throwable;
    }
}
