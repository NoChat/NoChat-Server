package controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import controllers.secured.PhoneSecured;
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
        return ok("chats : " + id);
    }
    @Security.Authenticated(PhoneSecured.class)
    public static Result create(){
        //Form<Chat> chatForm = new Form<>(Chat.class).bindFromRequest();
        User user = Users.currentUser();
        Logger.info(user.phoneNumber);
        Chat chat = new Chat();
        chat.sendUser = user;
        chat.state = ChatState.RECEIVED;
        chat.created = new Date();
        chat.save();



        ObjectNode result = Json.newObject();
        result.put("code", "0");
        result.put("msg", "");
        result.put("data", Json.toJson(chat));

        return ok(result);
    }
}
