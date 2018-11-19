package com.revolut.testtask.unit;

import com.revolut.testtask.ApplicationContext;
import com.revolut.testtask.model.Account;
import com.revolut.testtask.model.Transaction;
import com.revolut.testtask.services.Repository;
import com.revolut.testtask.services.TransferOperationService;
import com.revolut.testtask.services.exceptions.EntityNotExistException;
import com.revolut.testtask.services.exceptions.InvalidOperationException;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNull;

public class TransferServiceTest {

    public static EntityManagerFactory entityManagerFactory;
    public static TransferOperationService transferOperationService;
    public static Repository repository;

    @Before
    public void setUp() {
        ApplicationContext context = ApplicationContext.getInstance();
        context.initBeans();
        entityManagerFactory = context.getEntityManagerFactory();
        repository = context.getRepository();
        transferOperationService = context.getTransferService();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(new Account(100));
        entityManager.persist(new Account(100));
        entityManager.persist(new Account(100));
        entityManager.persist(new Account(100));
        entityManager.getTransaction().commit();
    }

    @After
    public void tearDown() throws Exception {
        ApplicationContext.getInstance().shutdown();
    }

    @Test
    public void doTransferShouldReturnValidTransaction(){
        Transaction transaction = transferOperationService.transfer(1,2,10);
        assertEquals(new Integer(1), transaction.getFromId());
        assertEquals(new Integer(2), transaction.getToId());
        assertEquals(new Integer(10), transaction.getAmount());
        assertNotNull(transaction.getId());
        assertNotNull(transaction.getDate());

        assertEquals(repository.getAccountById(1).getBalance(), new Integer(90));
        assertEquals(repository.getAccountById(2).getBalance(), new Integer(110));
    }

    @Test(expected = EntityNotExistException.class)
    public void doTransferFromNotExist(){
        transferOperationService.transfer(100,2,10);
    }

    @Test(expected = EntityNotExistException.class)
    public void doTransferToNotExist(){
        transferOperationService.transfer(2,100,10);
    }

    @Test(expected = EntityNotExistException.class)
    public void doTransferFromAndToNotExist(){
        transferOperationService.transfer(100,200,10);
    }

    @Test(expected = InvalidOperationException.class)
    public void doTransferSameAccount(){
        transferOperationService.transfer(2,2,10);
    }

    @Test(expected = InvalidOperationException.class)
    public void doTransferNegativeAmount(){
        transferOperationService.transfer(1,2,-10);
    }

    @Test(expected = InvalidOperationException.class)
    public void doTransferNotEnoughMoney(){
        transferOperationService.transfer(1,2,1000);
    }

    @Test
    public void doDepositShouldReturnValidTransaction(){
        Transaction transaction = transferOperationService.depositing(3,10);
        assertNull(transaction.getFromId());
        assertEquals(new Integer(3), transaction.getToId());
        assertEquals(new Integer(10), transaction.getAmount());
        assertNotNull(transaction.getId());
        assertNotNull(transaction.getDate());

        assertEquals(repository.getAccountById(3).getBalance(), new Integer(110));
        }

    @Test(expected = EntityNotExistException.class)
    public void doDepositAccountNotExist(){
        transferOperationService.depositing(100,10);
    }

    @Test(expected = InvalidOperationException.class)
    public void doDepositNegativeAmount(){
        transferOperationService.depositing(3,-10);
    }

    @Test
    public void doWithdrawShouldReturnValidTransaction(){
        Transaction transaction = transferOperationService.withdraw(4,10);
        assertNull(transaction.getToId());
        assertEquals(new Integer(4), transaction.getFromId());
        assertEquals(new Integer(10), transaction.getAmount());
        assertNotNull(transaction.getId());
        assertNotNull(transaction.getDate());

        assertEquals(repository.getAccountById(4).getBalance(), new Integer(90));
    }

    @Test(expected = EntityNotExistException.class)
    public void doWithdrawAccountNotExist(){
        transferOperationService.withdraw(100,10);
    }

    @Test(expected = InvalidOperationException.class)
    public void doWithdrawNegativeAmount(){
        transferOperationService.withdraw(4,-10);
    }


}