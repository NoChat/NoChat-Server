package controllers;


import controllers.secured.PhoneSecured;
import models.Chat;
import models.ChatType;
import models.User;
import models.enumeration.ChatState;
import play.Logger;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import services.push.PUSH;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Rangken on 15. 2. 22..
 */
public class Chats extends BaseController {
    @BodyParser.Of(value = BodyParser.Text.class, maxLength = 10 * 1024)
    public static Result show(Long id){
        if (!request().accepts("application/json")) {
            return status(Http.Status.NOT_ACCEPTABLE);
        }
        response().setContentType("application/json; charset=utf-8");
        Chat chat = Chat.find.byId(id);
        return ok("chats : " + id);
    }

    public static Result showTypes(){
        List<ChatType> chatTypes = ChatType.find.all();
        return renderOk(Json.toJson(chatTypes));
    }
    @Security.Authenticated(PhoneSecured.class)
    public static Result create(){
        //Form<Chat> chatForm = new Form<>(Chat.class).bindFromRequest();
        User currentUser = Users.currentUser();
        final Map<String, String[]> values = request().body().asFormUrlEncoded();
        if(values.get("userId") == null || values.get("chatTypeId") == null){
            return renderFail("90002", Messages.get("common.wrongParameter.alert"));
        }
        String userId = values.get("userId")[0];
        String chatTypeId = values.get("chatTypeId")[0];

        User receivedUser = User.find.byId(Long.parseLong(userId));
        Chat chat = new Chat();
        chat.sendUser = currentUser;
        chat.receivedUser = receivedUser;
        chat.state = ChatState.RECEIVED;
        chat.created = new Date();
        ChatType chatType = ChatType.find.byId(Long.parseLong(chatTypeId));
        chat.chatType = chatType;
        chat.save();

        PUSH.sendChatPush(currentUser, receivedUser, chat);

        return renderOk(Json.toJson(chat));
    }
    @Security.Authenticated(PhoneSecured.class)
    public static Result reply(String chatId, String reply){
        User currentUser = Users.currentUser();
        Logger.info(reply);
        Logger.info(chatId);
        Chat chat = Chat.find.byId(Long.parseLong(chatId));
        // user Id 값으로 정보를 가져오게 하기 위함
        chat.sendUser.refresh();
        Logger.info(chat.chatType.name);
        if(reply.equals("ok")){
            chat.state = ChatState.ACCEPT;
            PUSH.sendReplyPush(currentUser, chat.sendUser, chat);
        }else{
            chat.state = ChatState.REJECT;
            PUSH.sendReplyPush(currentUser, chat.sendUser, chat);
        }
        chat.save();


        return renderOk(Json.toJson(chat));
    }
}
