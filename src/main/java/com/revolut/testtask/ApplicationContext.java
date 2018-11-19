package com.revolut.testtask;

import com.revolut.testtask.services.Repository;
import com.revolut.testtask.services.TransferOperationService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class ApplicationContext {
    private static ApplicationContext ourInstance = new ApplicationContext();
    public static ApplicationContext getInstance() {
        return ourInstance;
    }
    private ApplicationContext() { }

    public static int JETTY_PORT = 8080;

    private Server jettyServer;

    private EntityManagerFactory entityManagerFactory;

    private TransferOperationService transferService;

    private Repository repository;


    public void init(){
        initBeans();
        initServer();
    }

    public void shutdown() throws Exception {
        if (entityManagerFactory != null){
            entityManagerFactory.close();
        }
        if (jettyServer != null){
            jettyServer.stop();
            jettyServer.destroy(); }
    }

    public void initServer(){
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        jettyServer = new Server(JETTY_PORT);
        jettyServer.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);
        jerseyServlet.setInitParameter(
                "jersey.config.server.provider.packages",
                "com.revolut.testtask.controllers");
    }

    private void initHibernate(){
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory( "com.revolut.testtask.jpa" );
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public void initBeans(){
        initHibernate();
        transferService = new TransferOperationService(entityManagerFactory);
        repository = new Repository(entityManagerFactory);
    }

    public Repository getRepository() {
        return repository;
    }

    public TransferOperationService getTransferService() {
        return transferService;
    }

    public Server getJettyServer() {
        return jettyServer;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

}
