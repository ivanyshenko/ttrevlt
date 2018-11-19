package com.revolut.testtask.controllers;

import com.revolut.testtask.ApplicationContext;
import com.revolut.testtask.model.Transaction;
import com.revolut.testtask.services.Repository;
import com.revolut.testtask.services.TransferOperationService;
import com.revolut.testtask.services.exceptions.InvalidOperationException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class TransferOperationController {

    private TransferOperationService transferService;
    private Repository repository;

    public TransferOperationController() {
        this.transferService = ApplicationContext.getInstance().getTransferService();
        this.repository = ApplicationContext.getInstance().getRepository();
    }

    @POST
    @Path("transfer")
    @Produces(MediaType.APPLICATION_JSON)
    public Response transfer(@FormParam("from") Integer from,
                             @FormParam("to") Integer to,
                             @FormParam("amount") Integer amount) {
        if(from == null || to == null || amount == null){
            throw new InvalidOperationException();
        }
        Transaction transaction = transferService.transfer(from, to, amount);
        return Response.ok(transaction).build();
    }

    @POST
    @Path("depositing")
    @Produces(MediaType.APPLICATION_JSON)
    public Response depositing(@FormParam("id") Integer id,
                               @FormParam("amount") Integer amount) {
        if(id == null || amount == null){
            throw new InvalidOperationException();
        }
        Transaction transaction = transferService.depositing(id, amount);
        return Response.ok(transaction).build();
    }

    @POST
    @Path("withdraw")
    @Produces(MediaType.APPLICATION_JSON)
    public Response withdraw(@FormParam("id") Integer id,
                             @FormParam("amount") Integer amount) {
        if(id == null || amount == null){
            throw new InvalidOperationException();
        }
        Transaction transaction = transferService.withdraw(id, amount);
        return Response.ok(transaction).build();
    }
}

