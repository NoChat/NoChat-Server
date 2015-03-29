package controllers;

import models.enumeration.OS;
import play.i18n.Messages;
import play.mvc.Result;
import services.push.PUSH;

import java.util.Map;

/**
 * Created by young on 2015. 3. 29..
 */
public class Test extends BaseController {
    public static Result push() {
        final Map<String, String[]> values = request().body().asFormUrlEncoded();
        String token = values.get("token")[0];
        String osStr = values.get("os")[0];
        String content = values.get("content")[0];
        OS os = OS.iOS;
        if(osStr.equals("iOS")){
            os = OS.iOS;
        }else if(osStr.equals("Android")){
            os = OS.Android;
        }

        PUSH.testPush(token, content, os);
        return ok("ok");
    }
}
