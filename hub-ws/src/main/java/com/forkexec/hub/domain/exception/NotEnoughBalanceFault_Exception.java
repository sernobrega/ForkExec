package com.forkexec.hub.domain.exception;

public class NotEnoughBalanceFault_Exception
        extends Exception
{
    private NotEnoughBalanceFault faultInfo;

    public NotEnoughBalanceFault_Exception(String message, NotEnoughBalanceFault faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    public NotEnoughBalanceFault_Exception(String message, NotEnoughBalanceFault faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    public NotEnoughBalanceFault getFaultInfo() {
        return faultInfo;
    }

}
