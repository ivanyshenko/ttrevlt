package com.revolut.testtask.services;

import com.revolut.testtask.model.Account;
import com.revolut.testtask.model.Transaction;
import com.revolut.testtask.services.exceptions.EntityNotExistException;
import com.revolut.testtask.services.exceptions.InvalidOperationException;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TransferOperationService {

    private EntityManagerFactory entityManagerFactory;

    public TransferOperationService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    private Map<Integer, Lock> lockMap = new ConcurrentHashMap<>();
    private final Lock lockCreationLock = new ReentrantLock();


    public Transaction transfer(Integer fromId, Integer toId, Integer amount){
        if (amount <= 0){
            throw new InvalidOperationException();
        }

        Lock firstOne;
        Lock secondOne;
        if (fromId > toId){
            firstOne = getLockForAccountId(toId);
            secondOne = getLockForAccountId(fromId);
        } else if (fromId < toId){
            firstOne = getLockForAccountId(fromId);
            secondOne = getLockForAccountId(toId);
        } else {
            //transfer to the same account doesn't make sense, but can cause deadlock
            throw new InvalidOperationException();
        }
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try{
            firstOne.lock();
            secondOne.lock();
            Account fromAccount = entityManager.find(Account.class, fromId);
            Account toAccount = entityManager.find(Account.class, toId);
            if (fromAccount == null || toAccount == null){
                throw new EntityNotExistException();
            }
            Integer newFromBalance = fromAccount.getBalance() - amount;
            Integer newToBalance = toAccount.getBalance() + amount;
            if (newFromBalance < 0){
                throw new InvalidOperationException();
            } else {
                fromAccount.setBalance(newFromBalance);
                toAccount.setBalance(newToBalance);
                Transaction transaction = new Transaction(fromId, toId, amount);
                entityManager.getTransaction().begin();
                entityManager.persist(transaction);
                entityManager.persist(fromAccount);
                entityManager.persist(toAccount);
                entityManager.getTransaction().commit();
                return transaction;
            }
        } finally {
            entityManager.close();
            firstOne.unlock();
            secondOne.unlock();
        }
    }

    public Transaction depositing(Integer id, Integer amount){
        if (amount <= 0){
            throw new InvalidOperationException();
        }

        Lock accountLock = getLockForAccountId(id);
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try{
            accountLock.lock();
            Account account = entityManager.find(Account.class, id);
            if (account == null){
                throw new EntityNotExistException();
            }
            Integer newBalance = account.getBalance() + amount;
            account.setBalance(newBalance);
            Transaction transaction = new Transaction(null, id, amount);
            entityManager.getTransaction().begin();
            entityManager.persist(account);
            entityManager.persist(transaction);
            entityManager.getTransaction().commit();
            return transaction;
        } finally {
            entityManager.close();
            accountLock.unlock();
        }
    }

    public Transaction withdraw(Integer id, Integer amount){
        if (amount <= 0){
            throw new InvalidOperationException();
        }

        Lock accountLock = getLockForAccountId(id);
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try{
            accountLock.lock();
            Account account = entityManager.find(Account.class, id);
            if (account == null){
                throw new EntityNotExistException();
            }
            Integer newBalance = account.getBalance() - amount;
            if (newBalance < 0){
                throw new InvalidOperationException();
            }
            account.setBalance(newBalance);
            Transaction transaction = new Transaction(id, null, amount);
            entityManager.getTransaction().begin();
            entityManager.persist(account);
            entityManager.persist(transaction);
            entityManager.getTransaction().commit();
            return transaction;
        } finally {
            entityManager.close();
            accountLock.unlock();
        }
    }

    private Lock getLockForAccountId(Integer id){
        Lock lock = lockMap.get(id);
        if (lock == null){
            //synchronize creation of a new lock.
            try{
                lockCreationLock.lock();
                lock = lockMap.computeIfAbsent(id, integer -> new ReentrantLock());
            } finally {
                lockCreationLock.unlock();
            }
        }
        return lock;
    }
}
