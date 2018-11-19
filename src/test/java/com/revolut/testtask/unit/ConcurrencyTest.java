package com.revolut.testtask.unit;

import com.revolut.testtask.ApplicationContext;
import com.revolut.testtask.services.Repository;
import com.revolut.testtask.services.TransferOperationService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


import static junit.framework.TestCase.assertEquals;

public class ConcurrencyTest {

    public static TransferOperationService transferOperationService;
    public static Repository repository;
    public static AtomicInteger activeThreadsNum = new AtomicInteger();
    public static List<Thread> threads = new ArrayList<>();


    @Before
    public void setUp() {
        ApplicationContext context = ApplicationContext.getInstance();
        context.initBeans();
        repository = context.getRepository();
        transferOperationService = context.getTransferService();
    }

    @After
    public void tearDown() throws Exception {
        ApplicationContext.getInstance().shutdown();
        for (Thread thread : threads) {
            if (thread.getState() != Thread.State.TERMINATED){
                thread.interrupt();
            }
        }
    }

    @Test
    public void concurrencyTest() throws InterruptedException {
        repository.createEmptyAccount();
        repository.createEmptyAccount();
        transferOperationService.depositing(1, 1000);
        transferOperationService.depositing(2, 1000);

        List<Thread> threads = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        for (int i = 0; i < 32; i++) {
            int finalI = i;
            Thread thread = (new Thread(() -> {
                try {
                    countDownLatch.await();
                    for (int j = 0; j < 100; j++) {
                        if (finalI %2 == 0){
                            transferOperationService.depositing(1, 1);
                            transferOperationService.transfer(1, 2, 1);
                            transferOperationService.withdraw(2, 1);
                        } else {
                            transferOperationService.depositing(2, 1);
                            transferOperationService.transfer(2, 1, 1);
                            transferOperationService.withdraw(1, 1);
                        }
                    }
                    activeThreadsNum.decrementAndGet();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }));
            threads.add(thread);
            thread.start();
        }
        //check deadlocks
        Thread deadlockCheckerThread = new Thread(() -> {
            ThreadMXBean bean = ManagementFactory.getThreadMXBean();
            while (activeThreadsNum.get() > 0){
                try {
                    //checking is very expensive operation
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException();
                }
                long[] threadIds = bean.findDeadlockedThreads();
                if (threadIds != null && threadIds.length > 0){ //we got deadlock
                    return;
                }

            }
        });
        activeThreadsNum.set(threads.size());
        deadlockCheckerThread.start();
        //wait until all threads are finished
        countDownLatch.countDown();
        deadlockCheckerThread.join();

        assertEquals(0, activeThreadsNum.get());
        assertEquals(new Integer(1000), repository.getAccountById(1).getBalance());
        assertEquals(new Integer(1000), repository.getAccountById(2).getBalance());
    }
}
