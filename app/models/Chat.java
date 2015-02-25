package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import models.enumeration.ChatState;
import play.data.format.Formats;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Rangken on 15. 2. 22..
 */
@Entity
public class Chat extends Model {
    private static final long serialVersionUID = 1L;

    public static Finder<Long,Chat> find = new Finder<Long,Chat>(
            Long.class, Chat.class
    );

    @Id
    public Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonBackReference
    @JoinColumn(name = "send_user_id")
    public User sendUser;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonBackReference
    @JoinColumn(name = "received_user_id")
    public User receivedUser;

    @OneToOne(cascade = CascadeType.ALL)
    public ChatType chatType;

    @Enumerated(EnumType.ORDINAL)
    public ChatState state;

    @Formats.DateTime(pattern = "YYYY/MM/DD/hh/mm/ss")
    public Date created;
    //post.createdDate = JodaDateUtil.now();

    public Chat(){
        state = ChatState.SENDING;
    }
    @Override
    public String toString(){
        return id+"";
    }
}
