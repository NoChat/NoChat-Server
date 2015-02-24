package controllers;

import models.User;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created by Rangken on 15. 2. 24..
 */
public class Secured extends Security.Authenticator{
    @Override
    public String getUsername(Http.Context ctx) {
        User user = Users.findByApiToken();
        if(user != null) {
            ctx.args.put("current_user", user);
            return user.phoneNumber;
        }
        return null;
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return ok("fail");
    }
}
