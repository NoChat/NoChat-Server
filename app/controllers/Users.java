package controllers;

import controllers.secured.ApiTokenSecured;
import models.User;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import play.Logger;
import play.data.Form;
import play.data.validation.ValidationError;
import play.db.ebean.Transactional;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import services.sms.SMS;

import java.util.List;
import java.util.Map;

import static play.data.Form.form;

/**
 * Created by Rangken on 15. 2. 22..
 */
public class Users extends BaseController {
    private static final int HASH_ITERATIONS = 1024;

    public static Result show(Long id) {
        Form<User> userForm = Form.form(User.class);
        User user = userForm.bindFromRequest().get();

        return ok("user");
    }

    public static User currentUser() {
        return (User) Http.Context.current().args.get("current_user");
    }

    public static User findByApiToken() {
        Http.Request req = Http.Context.current().request();
        Map<String, String[]> headerMap = req.headers();
        String[] tokens = headerMap.get("apiToken");
        if (tokens != null && tokens[0] != null) {
            User user = User.findByApiToken(tokens[0]);
            if (user != null) {
                Logger.info("token : " + tokens[0] + " userId : " + user.id);
            }
            return user;
        }
        return null;
    }

    /**
     * 사용자 가입 처리
     * 입력된 데이터 유효성 검증에 실패하면 bad request 응답
     * 사용자 정보를 저장, 로그인 쿠기 생성 후 메인 페이지로 이동
     * 시스템 설정에서 가입승인 기능이 활성화되어 있다면 사용자의 계정 상태를 잠금으로 설정하여 저장, 로그인 쿠키 생성 안됨
     *
     * @return
     */
    @Transactional
    public static Result newUser() {
        Form<User> newUserForm = form(User.class).bindFromRequest();
        if (newUserForm.field("loginId").value().trim().isEmpty()) {
            return renderFail("10001", Messages.get("user.wrongloginId.alert"));
        }

        if (newUserForm.field("loginId").value().contains(" ")) {
            return renderFail("10001", Messages.get("user.wrongloginId.alert"));
        }

        // password가 빈 값이 들어오면 안된다.
        if (newUserForm.field("password").value().trim().isEmpty()) {
            return renderFail("10001", Messages.get("user.wrongPassword.alert"));
        }

        // 중복된 loginId로 가입할 수 없다.
        if (User.isLoginIdExist(newUserForm.field("loginId").value())) {
            return renderFail("10001", Messages.get("user.loginId.duplicate"));
        }
        User user = createNewUser(newUserForm.get());

        return renderOk(Json.toJson(user));
        /*
        validate(newUserForm);
        if (newUserForm.hasErrors()) {
            String errorMsg = "";
            java.util.Map<String, List<ValidationError>> errorsAll = newUserForm.errors();
            for (String field : errorsAll.keySet()) {
                //errorMsg += field + " ";
                for (ValidationError error : errorsAll.get(field)) {
                    errorMsg += Messages.get(error.message());
                    break;
                }
                break;
            }
            return renderFail("10001", errorMsg);
        } else {
            User user = createNewUser(newUserForm.get());

            return renderOk(Json.toJson(user));
        }
        */
    }

    private static User createNewUser(User user) {
        RandomNumberGenerator rng = new SecureRandomNumberGenerator();
        user.passwordSalt = rng.nextBytes().toBase64();
        user.password = hashedPassword(user.password, user.passwordSalt);
        User.create(user);

        return user;
    }

    /**
     * 비밀번호 hash 값 생성
     *
     * @param plainTextPassword plain text
     * @param passwordSalt      hash salt
     * @return hashed password
     */
    public static String hashedPassword(String plainTextPassword, String passwordSalt) {
        if (plainTextPassword == null || passwordSalt == null) {
            throw new IllegalArgumentException("Bad password or passwordSalt!");
        }
        return new Sha256Hash(plainTextPassword, org.apache.shiro.util.ByteSource.Util.bytes(passwordSalt), HASH_ITERATIONS).toBase64();
    }

    @Transactional
    public static Result signIn() {
        final Map<String, String[]> values = request().body().asFormUrlEncoded();
        if (values.get("loginId") == null || values.get("password") == null) {
            return renderFail("90001", Messages.get("common.wrongParameter.alert"));
        }
        String loginId = values.get("loginId")[0];
        String password = values.get("password")[0];
        User user = User.findByLoginId(loginId);
        Logger.info(user + "");
        if (user != null && isValidPassword(user, password)) {
            // 유저 로그인 성공
            if (user.phoneNumber == null) {
                return renderFail("12001", Messages.get("user.phoneNumberNotYet.alert"));
            } else {
                return renderOk(Json.toJson(user));
            }
        }
        return renderFail("12000", Messages.get("user.loginFail.alert"));
    }

    /**
     * 비밀번호 검증
     * 사용자 객체의 hash 된 비밀번호 값과 입력 받은 비밀번호의 hash 값이 같은지 검사한다
     *
     * @param currentUser 사용자 객체
     * @param password    입력받은 비밀번호
     * @return
     */
    public static boolean isValidPassword(User currentUser, String password) {
        String hashedOldPassword = hashedPassword(password, currentUser.passwordSalt);
        return currentUser.password.equals(hashedOldPassword);
    }

    /*
     * 사용자 가입 입력 폼 유효성 체크
     */
    private static void validate(Form<User> newUserForm) {
        // loginId가 빈 값이 들어오면 안된다.
        if (newUserForm.field("loginId").value().trim().isEmpty()) {
            newUserForm.reject("loginId", "user.wrongloginId.alert");
        }

        if (newUserForm.field("loginId").value().contains(" ")) {
            newUserForm.reject("loginId", "user.wrongloginId.alert");
        }

        // password가 빈 값이 들어오면 안된다.
        if (newUserForm.field("password").value().trim().isEmpty()) {
            newUserForm.reject("password", "user.wrongPassword.alert");
        }

        // 중복된 loginId로 가입할 수 없다.
        if (User.isLoginIdExist(newUserForm.field("loginId").value())) {
            newUserForm.reject("loginId", "user.loginId.duplicate");
        }

        // 중복된 phoneNumber로 가입할 수 없다.
        /*
        if (User.isPhoneNumberExist(newUserForm.field("phoneNumber").value())) {
            newUserForm.reject("phoneNumber", "user.phoneNumber.duplicate");
        }
        */
    }

    @Transactional
    @Security.Authenticated(ApiTokenSecured.class)
    public static Result sendPhoneNumberToken() {
        User user = Users.currentUser();
        final Map<String, String[]> values = request().body().asFormUrlEncoded();
        String phoneNumber = "";
        String phoneLocale = "";
        if (values.get("phoneNumber") == null) {
            return renderFail("90000", Messages.get("common.wrongParameter.alert"));
        }
        phoneNumber = values.get("phoneNumber")[0];
        if (values.get("phoneLocale") == null) {
            phoneLocale = "KR";
        } else {
            phoneLocale = values.get("phoneLocale")[0];
        }
        // TODO : 폰 나라에 따라 지역코드를 다르게 해야함
        user = User.createPhoneNumberToken(user);
        SMS.send(phoneNumber, user.phoneNumberToken, phoneLocale);
        return renderOk(Json.toJson(user));
    }

    @Transactional
    @Security.Authenticated(ApiTokenSecured.class)
    public static Result authPhone() {
        User user = Users.currentUser();
        final Map<String, String[]> values = request().body().asFormUrlEncoded();
        if (values.get("phoneNumber") == null || values.get("phoneNumberToken") == null) {
            return renderFail("90001", Messages.get("common.wrongParameter.alert"));
        }
        String phoneNum = values.get("phoneNumber")[0];
        String phoneNumToken = values.get("phoneNumberToken")[0];
        // 전화 번호 형식이 잘못 됬다면
        if (phoneNum.trim().isEmpty() || phoneNum.contains(" ")) {
            return renderFail("10002", Messages.get("user.wrongPhoneNumber.alert"));
        }

        if (user.phoneNumberToken.equals(phoneNumToken)) {
            User.resetUsersPhoneNumber(phoneNum);
            user = User.savePhoneNumber(user, phoneNum);
        } else {
            return renderFail("11000", Messages.get("user.phoneNumberAuthFail.alert"));
        }

        return renderOk(Json.toJson(user));
    }

    @Transactional
    @Security.Authenticated(ApiTokenSecured.class)
    public static Result getFriends() {
        User user = Users.currentUser();
        final Map<String, String[]> values = request().body().asFormUrlEncoded();
        if (values.get("phoneNumbers") == null) {
            return renderFail("90000", Messages.get("common.wrongParameter.alert"));
        }
        String friendsPhoneNums = values.get("phoneNumbers")[0];
        List<User> friends = User.getFriends(friendsPhoneNums);
        return renderOk(Json.toJson(friends));
    }
}
