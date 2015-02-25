package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import models.enumeration.OS;
import org.joda.time.LocalDate;
import play.data.format.Formats;
import play.data.validation.Constraints.Pattern;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import javax.persistence.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by Rangken on 15. 2. 22..
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"login_id"}))
@JsonIgnoreProperties({ "password", "passwordSalt", "deviceToken", "phoneNumberToken" })
public class User extends Model {
    /**
     * 로그인ID 패턴
     */
    public static final String LOGIN_ID_PATTERN = "[a-zA-Z0-9-]+([_.][a-zA-Z0-9-]+)*";
    public static final String PHONE_NUMBER_PATTERN = "[0-9]{8,}";
    public static final String PHONE_NUMBER_TOKEN_PATTERN = "[0-9]{4}";
    private static SecureRandom random = new SecureRandom();

    @Id
    public Long id;

    @Pattern(value = "^" + LOGIN_ID_PATTERN + "$", message = "user.wrongloginId.alert")
    @Required
    public String loginId;

    public String password;

    public String passwordSalt;

    public String apiToken;

    public String deviceToken;
    // 09d26515ee7ea107cc7427fc2ceacc231423c708d87af97c98473f5abf9917db
    public String locale;

    @Enumerated(EnumType.STRING)
    public OS os;

    @Pattern(value = "^" + PHONE_NUMBER_PATTERN + "$", message = "user.wrongPhoneNumber.alert")
    public String phoneNumber;

    @Pattern(value = "^" + PHONE_NUMBER_TOKEN_PATTERN + "$", message = "user.wrongPhoneNumberToken.alert")
    public String phoneNumberToken;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "chat", joinColumns = @JoinColumn(name = "send_user_id"))
    @OrderBy("created DESC")
    public List<Chat> sendChats;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "chat", joinColumns = @JoinColumn(name = "received_user_id"))
    @OrderBy("created DESC")
    public List<Chat> receivedChats;

    @Formats.DateTime(pattern = "yyyy-MM-dd")
    public Date updatedAt;

    public static Finder<Long,User> find = new Finder<Long,User>(
            Long.class, User.class
    );

    public static Long create(User user) {
        user.updatedAt = LocalDate.now().toDate();
        user.apiToken = new BigInteger(130, random).toString(32);
        //DateTime.now().toDate();
        user.save();
        return user.id;
    }
    public static User createPhoneNumberToken(User user){
        Random random = new Random();
        String token = random.nextInt(10000)+"";
        while(true){
            if(token.length() == 4){
                break;
            }
            token = random.nextInt(10000)+"";
        }
        user.phoneNumberToken = token;
        user.save();
        return user;
    }
    public static User savePhoneNumber(User user, String phoneNumber){
        user.phoneNumber = phoneNumber;
        user.save();
        return user;
    }
    /**
     * 기존에 존재하는 loginId인지 확인한다.
     *
     * {@link loginId}에 검증된 보조 이메일로 존재하는지 확인한다.
     *
     * @param loginId
     * @return loginId이 있으면 true / 없으면 false
     */
    public static boolean isLoginIdExist(String loginId) {
        User user = find.where().ieq("loginId", loginId).findUnique();
        return user != null;
    }

    public static User findByApiToken(String token){
        return find.where()
                .eq("apiToken", token)
                .findUnique();
    }
    public static User findByLoginId(String loginId){
        return find.where()
                .eq("loginId", loginId)
                .findUnique();
    }
}
