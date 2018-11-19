package com.revolut.testtask.api;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.revolut.testtask.ApplicationContext;
import com.revolut.testtask.model.Account;
import com.revolut.testtask.model.Transaction;
import com.revolut.testtask.services.Repository;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TransferOperationsApiTest {
    public static WebTarget target;
    public static ApplicationContext context = ApplicationContext.getInstance();
    public static Repository repository;

    @BeforeClass
    public static void init() throws Exception {
        context.init();
        context.getJettyServer().start();

        target = ClientBuilder.newClient()
                .register(JacksonJaxbJsonProvider.class)
                .target("http://localhost:" + context.JETTY_PORT);
        repository = context.getRepository();

        EntityManager entityManager = context.getEntityManagerFactory().createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(new Account(100));
        entityManager.persist(new Account(100));
        entityManager.persist(new Account(100));
        entityManager.persist(new Account(100));
        entityManager.getTransaction().commit();
    }

    @AfterClass
    public static void shutdown() throws Exception{
        context.shutdown();
    }

    @Test
    public void doTransferShouldReturnValidTransaction() {
        MultivaluedMap<String, String> params = new MultivaluedStringMap();
        params.add("from", "1");
        params.add("to", "2");
        params.add("amount", "10");
        Transaction response = target
                .path("transfer")
                .request()
                .post(Entity.form(params))
                .readEntity(Transaction.class);
        assertEquals(new Integer(1), response.getFromId());
        assertEquals(new Integer(2), response.getToId());
        assertEquals(new Integer(10), response.getAmount());
        assertNotNull(response.getDate());
        assertNotNull(response.getId());
    }

    @Test
    public void doTransferAccountNotExistShouldReturn404(){
        MultivaluedMap<String, String> params = new MultivaluedStringMap();
        params.add("from", "100");
        params.add("to", "2");
        params.add("amount", "10");
        Response response = target
                .path("transfer")
                .request()
                .post(Entity.form(params));
        assertEquals(404, response.getStatus());
    }

    @Test
    public void doTransferNegativeAmountFromShouldReturn400(){
        MultivaluedMap<String, String> params = new MultivaluedStringMap();
        params.add("to", "2");
        params.add("amount", "-10");
        Response response = target
                .path("transfer")
                .request()
                .post(Entity.form(params));
        assertEquals(400, response.getStatus());
    }

    @Test
    public void doTransferNullParamShouldReturn400(){
        MultivaluedMap<String, String> params = new MultivaluedStringMap();
        params.add("to", "2");
        params.add("amount", "10");
        Response response = target
                .path("transfer")
                .request()
                .post(Entity.form(params));
        assertEquals(400, response.getStatus());
    }

    @Test
    public void doDepositingShouldReturnValidTransaction(){
        MultivaluedMap<String, String> params = new MultivaluedStringMap();
        params.add("id", "3");
        params.add("amount", "10");
        Transaction response = target
                .path("depositing")
                .request()
                .post(Entity.form(params))
                .readEntity(Transaction.class);
        assertNull(response.getFromId());
        assertEquals(new Integer(3), response.getToId());
        assertEquals(new Integer(10), response.getAmount());
        assertNotNull(response.getDate());
        assertNotNull(response.getId());
    }

    @Test
    public void doDepositingAccountNotExistShouldReturn404(){
        MultivaluedMap<String, String> params = new MultivaluedStringMap();
        params.add("id", "100");
        params.add("amount", "10");
        Response response = target
                .path("depositing")
                .request()
                .post(Entity.form(params));
        assertEquals(404, response.getStatus());
    }

    @Test
    public void doDepositingNegativeAmountFromShouldReturn400(){
        MultivaluedMap<String, String> params = new MultivaluedStringMap();
        params.add("id", "3");
        params.add("amount", "-10");
        Response response = target
                .path("depositing")
                .request()
                .post(Entity.form(params));
        assertEquals(400, response.getStatus());
    }

    @Test
    public void doDepositingNullParamShouldReturn400(){
        MultivaluedMap<String, String> params = new MultivaluedStringMap();
        params.add("amount", "10");
        Response response = target
                .path("depositing")
                .request()
                .post(Entity.form(params));
        assertEquals(400, response.getStatus());
    }


    @Test
    public void doWithdrawShouldReturnValidTransaction(){
        MultivaluedMap<String, String> params = new MultivaluedStringMap();
        params.add("id", "4");
        params.add("amount", "10");
        Transaction response = target
                .path("withdraw")
                .request()
                .post(Entity.form(params))
                .readEntity(Transaction.class);
        assertEquals(new Integer(4), response.getFromId());
        assertNull(response.getToId());
        assertEquals(new Integer(10), response.getAmount());
        assertNotNull(response.getDate());
        assertNotNull(response.getId());
    }

    @Test
    public void doWithdrawAccountNotExistShouldReturn404(){
        MultivaluedMap<String, String> params = new MultivaluedStringMap();
        params.add("id", "100");
        params.add("amount", "10");
        Response response = target
                .path("withdraw")
                .request()
                .post(Entity.form(params));
        assertEquals(404, response.getStatus());
    }

    @Test
    public void doWithdrawNegativeAmountFromShouldReturn400(){
        MultivaluedMap<String, String> params = new MultivaluedStringMap();
        params.add("id", "4");
        params.add("amount", "-10");
        Response response = target
                .path("withdraw")
                .request()
                .post(Entity.form(params));
        assertEquals(400, response.getStatus());
    }

    @Test
    public void doWithdrawNullParamShouldReturn400(){
        MultivaluedMap<String, String> params = new MultivaluedStringMap();
        params.add("amount", "10");
        Response response = target
                .path("withdraw")
                .request()
                .post(Entity.form(params));
        assertEquals(400, response.getStatus());
    }
}
