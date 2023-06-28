package org.awaitility;

import org.awaitility.classes.Asynch;
import org.awaitility.classes.FakeRepository;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

public class UsingCountDownLatchTest {

    private static final long TWO_SECONDS = 2000;
    private static final long ZERO = 0;

    @Before
    public void setUp() {
        Awaitility.reset();
    }

    @Test(timeout = TWO_SECONDS)
    public void usingCountDownLatch() {
        CountDownLatch latch = new CountDownLatch(1);
        new Asynch(new FakeRepositoryWithCountDownLatch(latch)).perform();
        await().untilLatch(latch, is(equalTo(ZERO)));
    }

    @Test(timeout = TWO_SECONDS)
    public void usingCountDownLatchAndTimout() {
        CountDownLatch latch = new CountDownLatch(1);
        assertThrows(ConditionTimeoutException.class, () -> await().atMost(200, TimeUnit.MILLISECONDS).untilLatch(latch, is(equalTo(ZERO))));
    }

    static class FakeRepositoryWithCountDownLatch implements FakeRepository {

        private final CountDownLatch latch;

        public FakeRepositoryWithCountDownLatch(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public int getValue() {
            return Math.toIntExact(latch.getCount());
        }

        @Override
        public void setValue(int value) {
            latch.countDown();
        }
    }
}
