package com.forkexec.pts.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.Map;
import java.util.concurrent.Future;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Response;

import com.forkexec.pts.ws.*;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

/**
 * Client port wrapper.
 *
 * Adds easier end point address configuration to the Port generated by
 * wsimport.
 */
public class PointsClient implements PointsPortType {

	/** WS service */
	PointsService service = null;

	/** WS port (port type is the interface, port is the implementation) */
	PointsPortType port = null;

	/** UDDI server URL */
	private String uddiURL = null;

	/** WS name */
	private String wsName = null;

	/** WS end point address */
	private String wsURL = null; // default value is defined inside WSDL

	public String getWsURL() {
		return wsURL;
	}

	/** output option **/
	private boolean verbose = false;

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/** constructor with provided web service URL */
	public PointsClient(String wsURL) throws PointsClientException {
		this.wsURL = wsURL;
		createStub();
	}

	/** constructor with provided UDDI location and name */
	public PointsClient(String uddiURL, String wsName) throws PointsClientException {
		this.uddiURL = uddiURL;
		this.wsName = wsName;
		uddiLookup();
		createStub();
	}

	/** UDDI lookup */
	private void uddiLookup() throws PointsClientException {
		try {
			if (verbose)
				System.out.printf("Contacting UDDI at %s%n", uddiURL);
			UDDINaming uddiNaming = new UDDINaming(uddiURL);

			if (verbose)
				System.out.printf("Looking for '%s'%n", wsName);
			wsURL = uddiNaming.lookup(wsName);

		} catch (Exception e) {
			String msg = String.format("Client failed lookup on UDDI at %s!", uddiURL);
			throw new PointsClientException(msg, e);
		}

		if (wsURL == null) {
			String msg = String.format("Service with name %s not found on UDDI at %s", wsName, uddiURL);
			throw new PointsClientException(msg);
		}
	}

	/** Stub creation and configuration */
	private void createStub() {
		if (verbose)
			System.out.println("Creating stub ...");
		service = new PointsService();
		port = service.getPointsPort();

		if (wsURL != null) {
			if (verbose)
				System.out.println("Setting endpoint address ...");
			BindingProvider bindingProvider = (BindingProvider) port;
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(ENDPOINT_ADDRESS_PROPERTY, wsURL);
		}
	}

	// async operations  ------------------------------------------------------

	@Override
	public Response<CtrlClearResponse> ctrlClearAsync() {
		return port.ctrlClearAsync();
	}

	@Override
	public Future<?> ctrlClearAsync(AsyncHandler<CtrlClearResponse> asyncHandler) {
		return port.ctrlClearAsync(asyncHandler);
	}

	@Override
	public Response<CtrlPingResponse> ctrlPingAsync(String inputMessage) {
		return port.ctrlPingAsync(inputMessage);
	}

	@Override
	public Future<?> ctrlPingAsync(String inputMessage, AsyncHandler<CtrlPingResponse> asyncHandler) {
		return port.ctrlPingAsync(inputMessage, asyncHandler);
	}

	@Override
	public Response<CtrlInitResponse> ctrlInitAsync(int startPoints) {
		return port.ctrlInitAsync(startPoints);
	}

	@Override
	public Future<?> ctrlInitAsync(int startPoints, AsyncHandler<CtrlInitResponse> asyncHandler) {
		return port.ctrlInitAsync(startPoints, asyncHandler);
	}

	@Override
	public Response<ActivateUserResponse> activateUserAsync(String userEmail) {
		return port.activateUserAsync(userEmail);
	}

	@Override
	public Future<?> activateUserAsync(String userEmail, AsyncHandler<ActivateUserResponse> asyncHandler) {
		return port.activateUserAsync(userEmail, asyncHandler);
	}

	@Override
	public Response<SetBalanceResponse> setBalanceAsync(String userEmail, int points, long seq) {
		return port.setBalanceAsync(userEmail, points, seq);
	}

	@Override
	public Future<?> setBalanceAsync(String userEmail, int points, long seq, AsyncHandler<SetBalanceResponse> asyncHandler) {
		return port.setBalanceAsync(userEmail, points, seq, asyncHandler);
	}

	@Override
	public Response<GetBalanceResponse> getBalanceAsync(String userEmail) {
		return port.getBalanceAsync(userEmail);
	}

	@Override
	public Future<?> getBalanceAsync(String userEmail, AsyncHandler<GetBalanceResponse> asyncHandler) {
		return port.getBalanceAsync(userEmail, asyncHandler);
	}

	// remote invocation methods ----------------------------------------------

	@Override
	public void activateUser(String userEmail) throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		port.activateUser(userEmail);
	}

	@Override
	public AccountView getBalance(String userEmail) throws InvalidEmailFault_Exception {
		return port.getBalance(userEmail);
	}

	@Override
	public AccountView setBalance(String userEmail, int pointsToAdd, long tag)
			throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
		return port.setBalance(userEmail, pointsToAdd, tag);
	}

	// control operations -----------------------------------------------------

	@Override
	public String ctrlPing(String inputMessage) {
		return port.ctrlPing(inputMessage);
	}

	@Override
	public void ctrlClear() {
		port.ctrlClear();
	}

	@Override
	public void ctrlInit(int startPoints) throws BadInitFault_Exception {
		port.ctrlInit(startPoints);
	}

}