package controllers.secured;

import controllers.BaseController;
import controllers.Users;
import models.User;
import play.i18n.Messages;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created by Rangken on 15. 2. 25..
 */
public class PhoneSecured extends Security.Authenticator{
    @Override
    public String getUsername(Http.Context ctx) {
        User user = Users.findByApiToken();
        if(user != null && user.phoneNumber != null) {
            ctx.args.put("current_user", user);
            return user.loginId;
        }
        return null;
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return BaseController.renderFail("10000", Messages.get("user.wrongToken.alert"));
    }
}
