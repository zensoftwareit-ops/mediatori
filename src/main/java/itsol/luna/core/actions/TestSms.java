package itsol.luna.core.actions;



import java.util.Optional;

import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.MyActionSupport;
import pl.smsapi.OAuthClient;
import pl.smsapi.api.SmsFactory;
import pl.smsapi.api.action.sms.SMSSend;
import pl.smsapi.api.response.MessageResponse;
import pl.smsapi.api.response.StatusResponse;
import pl.smsapi.exception.SmsapiException;
import pl.smsapi.proxy.ProxyNative;

public class TestSms extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1076974187795700758L;
	
	private String token;
	

	public String execute() {
        try {
            String oauthToken = getToken();
            OAuthClient client = new OAuthClient(oauthToken);
            ProxyNative proxy = new ProxyNative("https://api.smsapi.com/");

            SmsFactory smsApi = new SmsFactory(client, proxy);

            SMSSend action = smsApi.actionSend()
                    .setTo("+393423838219")
                    .setText("test");

            StatusResponse result = action.execute();

            Optional<MessageResponse> status = result.getList().stream().findFirst();
            if (status.isEmpty()) {
                throw new RuntimeException();
            }

            System.out.println("Phone number: " + status.get().getNumber());
            System.out.println("Shipment id: " + status.get().getId());
            System.out.println("Shipment status: " + status.get().getStatus());

        } catch (SmsapiException e) {
            System.out.println("Exception: " + e.getMessage());
        }

		return SUCCESS;
	}


	public String getToken() {
		return token;
	}


	public void setToken(String token) {
		this.token = token;
	}

}
