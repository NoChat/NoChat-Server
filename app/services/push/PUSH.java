package services.push;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import controllers.Config;
import models.Chat;
import models.User;
import models.enumeration.ChatState;
import models.enumeration.OS;
import play.Logger;

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

    public static void sendChatPush(User sendUser, User receiveUser, Chat chat){
        if(receiveUser.os == OS.iOS){
            String payload = APNS.newPayload()
                    .customField("pushType", "0")
                    .customField("userId", sendUser.id + "") // 푸시 보내는 사용자 아이디
                    .customField("chatId", chat.id + "") // 보내는 챗 아이디
                    .alertBody(chat.chatType.name+" ㄱㄱ?")
                    .build();
            String token = receiveUser.deviceToken;
            service.push(token, payload);
        }else if(receiveUser.os == OS.Android){

        }
    }

    public static void sendReplyPush(User sendUser, User receiveUser, Chat chat){
        Logger.info("PUSH : " + receiveUser.deviceToken);
        StringBuilder message = new StringBuilder(chat.chatType.name);
        if(chat.state == ChatState.ACCEPT){
            message.append(" 좋아요!");
        }else{
            message.append(" 싫어요!");
        }
        if(receiveUser.os == OS.iOS){
            String payload = APNS.newPayload()
                    .customField("pushType", "1")
                    .customField("userId", sendUser.id + "") // 푸시 보내는 사용자 아이디
                    .customField("chatId", chat.id + "") // 보내는 챗 아이디
                    .alertBody(message.toString())
                    .build();

            String token = receiveUser.deviceToken;
            Logger.info("push : " + token);
            service.push(token, payload);
        }else if(receiveUser.os == OS.Android){

        }
    }
}
