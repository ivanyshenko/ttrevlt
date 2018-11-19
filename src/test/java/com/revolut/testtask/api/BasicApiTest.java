package com.revolut.testtask.api;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.revolut.testtask.ApplicationContext;
import com.revolut.testtask.model.Account;
import com.revolut.testtask.model.Transaction;
import org.junit.*;

import javax.persistence.EntityManager;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BasicApiTest {
    public static WebTarget target;
    public static ApplicationContext context = ApplicationContext.getInstance();

    @BeforeClass
    public static void init() throws Exception {
        context.init();
        context.getJettyServer().start();

        target = ClientBuilder.newClient()
                .register(JacksonJaxbJsonProvider.class)
                .target("http://localhost:" + context.JETTY_PORT);

        EntityManager entityManager = context.getEntityManagerFactory().createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(new Account(50));
        entityManager.persist(new Account(100));
        entityManager.persist(new Account(150));
        entityManager.persist(new Transaction(2,3,50));
        entityManager.getTransaction().commit();
    }

    @AfterClass
    public static void shutdown() throws Exception{
        context.shutdown();
    }

    @Test
    public void createValidAccountShouldReturnValidAccount(){
        Account response = target
                .path("account")
                .request()
                .post(Entity.json(null))
                .readEntity(Account.class);
        EntityManager entityManager = context.getEntityManagerFactory().createEntityManager();
        assertEquals(entityManager.find(Account.class, response.getId()), response);
    }

    @Test
    public void getAccountShouldReturnValidAccount(){
        Account response = target
                .path("account")
                .path("1")
                .request()
                .get()
                .readEntity(Account.class);
        assertNotNull(response.getId());
        assertEquals(new Integer(50), response.getBalance());
    }

    @Test
    public void getAccountShouldReturn404(){
        Response response = target
                .path("account")
                .path("100")
                .request()
                .get();
        assertEquals(404, response.getStatus());
    }

    @Test
    public void getTransactionShouldReturnValidTransaction(){
        Transaction response = target
                .path("transaction")
                .path("1")
                .request()
                .get()
                .readEntity(Transaction.class);
        assertNotNull(response.getId());
        assertEquals(new Integer(2), response.getFromId());
        assertEquals(new Integer(3), response.getToId());
        assertEquals(new Integer(50), response.getAmount());
        assertNotNull(response.getDate());
    }

    @Test
    public void getTransactionShouldReturn404(){
        Response response = target
                .path("transaction")
                .path("100")
                .request()
                .get();
        assertEquals(404, response.getStatus());
    }
}
