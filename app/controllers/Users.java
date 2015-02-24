package controllers;

import models.User;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.util.Map;

/**
 * Created by Rangken on 15. 2. 22..
 */
public class Users extends Controller {

    public static Result show(Long id){
        Form<User> userForm = Form.form(User.class);
        User user = userForm.bindFromRequest().get();

        return ok("user");
    }

    public static User currentUser() {
        return (User)Http.Context.current().args.get("current_user");
    }

    public static User findByApiToken() {
        Http.Request req = Http.Context.current().request();
        Map<String, String[]> headerMap = req.headers();
        String[] tokens = headerMap.get("api-token");
        if(tokens != null && tokens[0] != null){
            Logger.info(tokens[0]);
            User user = User.findByApiToken(tokens[0]);
            if(user != null) {
                Logger.info("token : " + tokens[0]+ " userId : " + user.id);
            }
            return user;
        }
        return null;
    }
}
