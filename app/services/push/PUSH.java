package services.push;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import controllers.Config;
import models.User;
import models.enumeration.OS;

/**
 * Created by Rangken on 15. 2. 25..
 */
public class PUSH {
    static ApnsService service;
    static {
        if(Config.PUSH_DEV){
            service =
                    APNS.newService()
                            .withCert("certs/dev/nochat_push.p12", "nochat")
                            .withSandboxDestination()
                            .build();
        }else{
            service =
                    APNS.newService()
                            .withCert("certs/pro/nochat_push.p12", "nochat")
                            .build();
        }
    }

    public static void sendPush(User user){
        if(user.os == OS.IOS){
            String payload = APNS.newPayload().alertBody("Can't be simpler than this!").build();
            String token = user.deviceToken;
            service.push(token, payload);
        }else if(user.os == OS.Android){

        }
    }
}
