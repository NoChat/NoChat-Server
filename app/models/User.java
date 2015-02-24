package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Rangken on 15. 2. 22..
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"phone_number"}))
public class User extends Model {
    @Id
    public Long id;
    public String apiToken;
    public String deviceToken;
    // 09d26515ee7ea107cc7427fc2ceacc231423c708d87af97c98473f5abf9917db
    public String locale;
    public Integer os;

    @Constraints.Required
    public String phoneNumber;

    //@Constraints.Email(message = "user.wrongEmail.alert")
    public String email;

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
    public static User findByApiToken(String token){
        return find.where()
                .eq("apiToken", token)
                .findUnique();
    }
    public static User findByDeviceToken(String token){
        return find.where()
                .eq("deviceToken", token)
                .findUnique();
    }
}
