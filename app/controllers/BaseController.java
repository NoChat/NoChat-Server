package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;


/**
 * Created by Rangken on 15. 2. 25..
 */
public class BaseController extends Controller {
    static protected Result renderOk(JsonNode data){
        ObjectNode result = Json.newObject();
        result.put("code", "0");
        result.put("msg", "");
        result.put("data", data);
        return ok(result);
    }
    static protected Result renderOk(String str){
        ObjectNode result = Json.newObject();
        result.put("code", "0");
        result.put("msg", "");
        result.put("data", str);
        return ok(result);
    }
    public static Result renderFail(String code, String msg){
        ObjectNode result = Json.newObject();
        result.put("code", code);
        result.put("msg", msg);
        return ok(result);
    }
}
