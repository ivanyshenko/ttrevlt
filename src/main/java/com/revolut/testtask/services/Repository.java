package com.revolut.testtask.services;

import com.revolut.testtask.model.Account;
import com.revolut.testtask.model.Transaction;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class Repository {
    private EntityManagerFactory entityManagerFactory;

    public Repository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public Transaction getTransactionById(Integer id){
        Transaction transaction = entityManagerFactory.createEntityManager().find(Transaction.class, id);
        return transaction;
    }

    public Account getAccountById(Integer id){
        return entityManagerFactory.createEntityManager().find(Account.class, id);
    }

    public Account createEmptyAccount(){
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Account account = new Account(0);
        entityManager.persist(account);
        entityManager.getTransaction().commit();
        entityManager.close();
        return account;
    }

}
