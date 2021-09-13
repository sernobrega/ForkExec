package com.forkexec.hub.domain;

import com.forkexec.hub.domain.exception.NotEnoughBalanceFault;
import com.forkexec.hub.domain.exception.NotEnoughBalanceFault_Exception;
import com.forkexec.pts.ws.*;
import com.forkexec.pts.ws.cli.*;

import javax.xml.ws.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

public class PointsFrontEnd {

    private HashMap<String, PointsClient> ptsClients;

    private final AtomicLong tag_id = new AtomicLong(1);

    private Map<String, AccountView> cache = new WeakHashMap<>();

    public PointsFrontEnd(HashMap<String, PointsClient> ptsClients) {
        this.ptsClients = ptsClients;
    }

    // remote invocation methods ----------------------------------------------

    public void activateUser(String userEmail) throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
        System.out.println("Request received");
        int n_responses = 0;
        int n_exceptions = 0;
        HashMap<PointsClient, Response<ActivateUserResponse>> responsesMap = new HashMap<>();
        HashMap<Throwable, Integer> exceptions = new HashMap<>();

        for(PointsClient cli: ptsClients.values())
            responsesMap.put(cli, cli.activateUserAsync(userEmail));

        while(n_responses + n_exceptions < ptsClients.size()/2 + 1) {
            for(PointsClient cli: ptsClients.values()) {
                Response<ActivateUserResponse> response = responsesMap.get(cli);
                if(response.isDone()) {
                    responsesMap.remove(cli);
                    try {
                        response.get();
                        n_responses++;
                        System.out.println("Response from " + cli.getWsURL());
                    } catch(ExecutionException e) {
                        if(e.getCause() instanceof InvalidEmailFault_Exception || e.getCause() instanceof EmailAlreadyExistsFault_Exception)
                        {
                            n_exceptions++;
                            if(exceptions.containsKey(e.getCause())) {
                                exceptions.put(e.getCause(), exceptions.get(e.getCause()) + 1);
                            } else {
                                exceptions.put(e.getCause(), 1);
                            }
                            System.out.println("Exception from " + cli.getWsURL());
                        }
                        else {
                            responsesMap.put(cli, cli.activateUserAsync(userEmail));
                        }
                    } catch(InterruptedException e) {
                        responsesMap.put(cli, cli.activateUserAsync(userEmail));
                    }
                }
            }

            try {
                Thread.sleep(10 /* milliseconds */); //Possível fazer response.wait()?
                /* while waiting for responses do something else... */
                System.out.print(".");
                System.out.flush();
            } catch(InterruptedException e) {
                //Ignore exception
            }
        }
        System.out.println("Quorum has been met");

        int max = n_responses;
        Throwable cause = null;

        for(Throwable t: exceptions.keySet()) {
            if(exceptions.get(t) > max) {
                max = exceptions.get(t);
                cause = t;
            }
        }

        if(cause instanceof InvalidEmailFault_Exception) {
            throwInvalidEmailFault(cause.getMessage());
        }
        else if(cause instanceof EmailAlreadyExistsFault_Exception) {
            throwEmailAlreadyExistsFault(cause.getMessage());
        }
    }

    public int spendPoints(String userEmail, int pointsToSpend)
            throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, NotEnoughBalanceFault_Exception {
        int userBalance = pointsBalance(userEmail);
        if(userBalance - pointsToSpend < 0)
            throwNotEnoughBalanceFault("Not enough balance in User account");
        AccountView acc = setBalance(userEmail, userBalance - pointsToSpend, incTag());
        incTag();
        return acc.getBalance();
    }

    public int addPoints(String userEmail, int pointsToAdd)
            throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
        int userBalance = pointsBalance(userEmail);
        AccountView acc = setBalance(userEmail, userBalance + pointsToAdd, incTag());
        incTag();
        return acc.getBalance();
    }

    private long incTag() {
        return tag_id.addAndGet(1);
    }

    public int pointsBalance(String userEmail) throws InvalidEmailFault_Exception {
        return getBalance(userEmail).getBalance();

    }

    // auxiliary methods ------------------------------------------------------

    private AccountView getBalance(String userEmail) throws InvalidEmailFault_Exception {

        if(cache.containsKey(userEmail))
            return cache.get(userEmail);

        int n_responses = 0;
        int n_exceptions = 0;
        Throwable cause = null;
        HashMap<PointsClient, Response<GetBalanceResponse>> responsesMap = new HashMap<>();
        ArrayList<AccountView> accountViews = new ArrayList<>();

        for(PointsClient cli: ptsClients.values())
            responsesMap.put(cli, cli.getBalanceAsync(userEmail));

        while(n_responses + n_exceptions < ptsClients.size()/2 + 1) {
            for(PointsClient cli: ptsClients.values()) {
                Response<GetBalanceResponse> response = responsesMap.get(cli);
                if(response.isDone()) {
                    responsesMap.remove(cli);
                    try {
                        accountViews.add(response.get().getReturn());
                        n_responses++;
                        System.out.println("Response from " + cli.getWsURL());
                    } catch(ExecutionException e) {
                        System.out.println("Exception...");
                        if(e.getCause() instanceof InvalidEmailFault_Exception)
                        {
                            n_exceptions++;
                            cause = e.getCause();
                            System.out.println("Exception from " + cli.getWsURL());
                        }
                        else {
                            responsesMap.put(cli, cli.getBalanceAsync(userEmail));
                        }
                    } catch(InterruptedException e) {
                        responsesMap.put(cli, cli.getBalanceAsync(userEmail));
                    }
                }
            }

            try {
                Thread.sleep(10 /* milliseconds */); //Possível fazer response.wait()?
                /* while waiting for responses do something else... */
                System.out.print(".");
                System.out.flush();
            } catch(InterruptedException e) {
                //Ignore exception
            }
        }
        System.out.println("Quorum has been met");

        if(n_exceptions > n_responses)
            throwInvalidEmailFault(cause.getMessage());

        AccountView recent = null;
        for(AccountView acc: accountViews) {
            if(recent == null || acc.getSeq() > recent.getSeq())
                recent = acc;
        }

        cache.replace(userEmail, recent);

        return recent;
    }

    private AccountView setBalance(String userEmail, int points, long tag) throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
        int n_responses = 0;
        int n_exceptions = 0;
        HashMap<Throwable, Integer> exceptions = new HashMap<>();
        HashMap<PointsClient, Response<SetBalanceResponse>> responsesMap = new HashMap<>();
        ArrayList<AccountView> accountViews = new ArrayList<>();

        for(PointsClient cli: ptsClients.values())
            responsesMap.put(cli, cli.setBalanceAsync(userEmail, points, tag));

        while(n_responses + n_exceptions < ptsClients.size()/2 + 1) {
            for(PointsClient cli: ptsClients.values()) {
                Response<SetBalanceResponse> response = responsesMap.get(cli);
                if(response.isDone()) {
                    responsesMap.remove(cli);
                    try {
                        accountViews.add(response.get().getReturn());
                        n_responses++;
                        System.out.println("Response from " + cli.getWsURL());
                    } catch(ExecutionException e) {
                        if(e.getCause() instanceof InvalidEmailFault_Exception)
                        {
                            n_exceptions++;
                            exceptions.put(e.getCause(), exceptions.get(e.getCause()) + 1);
                            System.out.println("Exception from " + cli.getWsURL());
                        }
                        else {
                            responsesMap.put(cli, cli.setBalanceAsync(userEmail, points, tag));
                        }
                    } catch(InterruptedException e) {
                        responsesMap.put(cli, cli.setBalanceAsync(userEmail, points, tag));
                    }
                }
            }

            try {
                Thread.sleep(10 /* milliseconds */); //Possível fazer response.wait()?
                /* while waiting for responses do something else... */
                System.out.print(".");
                System.out.flush();
            } catch(InterruptedException e) {
                //Ignore exception
            }
        }
        System.out.println("Quorum has been met");

        AccountView recent = null;
        for(AccountView acc: accountViews) {
            if(recent == null || acc.getSeq() > recent.getSeq())
                recent = acc;
        }

        int max = n_responses;
        Throwable cause = null;

        for(Throwable t: exceptions.keySet()) {
            if(exceptions.get(t) > max) {
                max = exceptions.get(t);
                cause = t;
            }
        }

        if(cause instanceof InvalidEmailFault_Exception) {
            throwInvalidEmailFault(cause.getMessage());
        }
        else if(cause instanceof InvalidPointsFault_Exception) {
            throwInvalidPointsFault(cause.getMessage());
        }

        return recent;
    }

    // control operations -----------------------------------------------------

    public String ctrlPing(String inputMessage) {
        HashMap<PointsClient, Response<CtrlPingResponse>> responsesMap = new HashMap<>();
        String output = null;

        for(PointsClient cli: ptsClients.values())
            responsesMap.put(cli, cli.ctrlPingAsync(inputMessage));

        int i = 0;
        while(i < 3) {
            for(PointsClient cli: ptsClients.values()) {
                Response<CtrlPingResponse> response = responsesMap.get(cli);
                if(response.isDone()) {
                    responsesMap.remove(cli);
                    try {
                        output = response.get().getReturn();
                        System.out.println("Response from " + cli.getWsURL());
                    } catch(ExecutionException | InterruptedException e) {
                        responsesMap.put(cli, cli.ctrlPingAsync(inputMessage));
                    }
                }
            }

            try {
                Thread.sleep(10 /* milliseconds */); //Possível fazer response.wait()?
                /* while waiting for responses do something else... */
                System.out.print(".");
                System.out.flush();
            } catch(InterruptedException e) {
                //Ignore exception
            }
            i++;
        }

        return output;
    }

    public void ctrlClear() {
        for(PointsClient cli: ptsClients.values()) {
            cli.ctrlClearAsync();
        }

    }

    public void ctrlInit(int startPoints) throws BadInitFault_Exception {
        HashMap<PointsClient, Response<CtrlInitResponse>> responsesMap = new HashMap<>();
        Throwable cause = null;

        for(PointsClient cli: ptsClients.values())
            responsesMap.put(cli, cli.ctrlInitAsync(startPoints));

        int i = 0;
        while(i < 3) {
            for(PointsClient cli: ptsClients.values()) {
                Response<CtrlInitResponse> response = responsesMap.get(cli);
                if(response.isDone()) {
                    responsesMap.remove(cli);
                    try {
                        response.get();
                        System.out.println("Response from " + cli.getWsURL());
                    } catch (ExecutionException e) {
                        if(!(e.getCause() instanceof BadInitFault_Exception))
                            responsesMap.put(cli, cli.ctrlInitAsync(startPoints));
                        else
                            cause = e.getCause();
                    } catch(InterruptedException e) {
                        responsesMap.put(cli, cli.ctrlInitAsync(startPoints));
                    }
                }
            }

            try {
                Thread.sleep(10 /* milliseconds */); //Possível fazer response.wait()?
                /* while waiting for responses do something else... */
                System.out.print(".");
                System.out.flush();
            } catch(InterruptedException e) {
                //Ignore exception
            }
            i++;
        }

        if(cause != null)
            throwBadInit(cause.getMessage());
    }

    // Exception helpers -----------------------------------------------------

    /** Helper to throw a new BadInit exception. */
    private void throwBadInit(final String message) throws BadInitFault_Exception {
        final BadInitFault faultInfo = new BadInitFault();
        faultInfo.setMessage(message);
        throw new BadInitFault_Exception(message, faultInfo);
    }

    /** Helper to throw a new EmailAlreadyExistsFault exception. */
    private void throwEmailAlreadyExistsFault(final String message) throws EmailAlreadyExistsFault_Exception {
        final EmailAlreadyExistsFault faultInfo = new EmailAlreadyExistsFault();
        faultInfo.setMessage(message);
        throw new EmailAlreadyExistsFault_Exception(message, faultInfo);
    }

    /** Helper to throw a new InvalidEmailFault exception. */
    private void throwInvalidEmailFault(final String message) throws InvalidEmailFault_Exception {
        final InvalidEmailFault faultInfo = new InvalidEmailFault();
        faultInfo.setMessage(message);
        throw new InvalidEmailFault_Exception(message, faultInfo);
    }

    /** Helper to throw a new InvalidPointsFault exception. */
    private void throwInvalidPointsFault(final String message) throws InvalidPointsFault_Exception {
        final InvalidPointsFault faultInfo = new InvalidPointsFault();
        faultInfo.setMessage(message);
        throw new InvalidPointsFault_Exception(message, faultInfo);
    }

    /** Helper to throw a new NotEnoughPointsFault exception. */
    private void throwNotEnoughBalanceFault(final String message) throws NotEnoughBalanceFault_Exception {
        final com.forkexec.hub.domain.exception.NotEnoughBalanceFault faultInfo = new NotEnoughBalanceFault();
        faultInfo.setMessage(message);
        throw new NotEnoughBalanceFault_Exception(message, faultInfo);
    }
}
