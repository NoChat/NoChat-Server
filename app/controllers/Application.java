package controllers;

import controllers.actions.VerboseAction;
import play.Logger;
import play.cache.Cached;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;

public class Application extends Controller {

    public static Result index() {
        return movedPermanently("/test"); // 301 move permanently
        //return redirect("/test"); // 303 SEE OTHER REDIRECT
        //return temporaryRedirect("/test"); // 371 temporary redirect
        //return ok("Got request " + request() + "!");
        //return ok(index.render("Your new application is ready."));
    }
    @With(VerboseAction.class)
    //@Security.Authenticated
    //@Cached(key = "index.result")
    public static Result test() {
        Logger.debug("log test");
        return ok("Got request test " + request() + "!");
        //return ok(index.render("Your new application is ready."));
    }
}
