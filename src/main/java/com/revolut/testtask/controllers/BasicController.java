package com.revolut.testtask.controllers;

import com.revolut.testtask.ApplicationContext;
import com.revolut.testtask.model.Account;
import com.revolut.testtask.model.Transaction;
import com.revolut.testtask.services.Repository;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class BasicController {

    private Repository repository;

    public BasicController() {
        repository = ApplicationContext.getInstance().getRepository();
    }

    @GET
    @Path(value = "account/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountById(@PathParam("id") int id) {
        Account account = repository.getAccountById(id);
        if (account != null){
            return Response.ok(account).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("Account not found for ID: " + id).build();
    }

    @POST
    @Path(value = "account")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount() {
        Account account = repository.createEmptyAccount();
        return Response.ok(account).build();
    }

    @GET
    @Path(value = "transaction/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransactionById(@PathParam("id") Integer id) {
        Transaction transaction = repository.getTransactionById(id);
        if (transaction != null){
            return Response.ok(transaction).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("Transaction not found for ID: " + id).build();
    }
}
