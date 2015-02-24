package controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import models.Chat;
import models.User;
import models.enumeration.ChatState;
import play.Logger;
import play.libs.Json;
import play.mvc.*;

import java.util.Date;

/**
 * Created by Rangken on 15. 2. 22..
 */
public class Chats extends Controller {
    @BodyParser.Of(value = BodyParser.Text.class, maxLength = 10 * 1024)
    public static Result show(Long id){
        if (!request().accepts("application/json")) {
            return status(Http.Status.NOT_ACCEPTABLE);
        }
        response().setContentType("application/json; charset=utf-8");
        Chat chat = Chat.find.byId(id);
        Logger.debug(chat+"");
        return ok("chats : " + id);
    }
    @Security.Authenticated(Secured.class)
    public static Result create(){
        //Form<Chat> chatForm = new Form<>(Chat.class).bindFromRequest();
        User user = Users.currentUser();
        Logger.info(user.phoneNumber);
        Chat chat = new Chat();
        chat.sendUser = user;
        chat.state = ChatState.RECEIVED;
        chat.created = new Date();
        chat.save();


        ApnsService service =
                APNS.newService()
                        .withCert("certs/dev/nochat_push.p12", "nochat")
                        .withSandboxDestination()
                        .build();

        String payload = APNS.newPayload().alertBody("Can't be simpler than this!").build();
        String token = user.deviceToken;
        service.push(token, payload);

        ObjectNode result = Json.newObject();
        result.put("code", "0");
        result.put("msg", "");
        result.put("data", Json.toJson(chat));

        return ok(result);
    }
}
