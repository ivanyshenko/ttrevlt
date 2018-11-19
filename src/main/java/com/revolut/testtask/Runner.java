package com.revolut.testtask;

public class Runner
{
    public static void main( String[] args ) throws Exception {
        ApplicationContext applicationContext = ApplicationContext.getInstance();

        applicationContext.init();
        try {
            applicationContext.getJettyServer().start();
            applicationContext.getJettyServer().join();
        } finally {
            applicationContext.shutdown();
        }
    }
}
