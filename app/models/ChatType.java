package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by Rangken on 15. 2. 24..
 */
@Entity
public class ChatType extends Model {
    @Id
    public Long id;

    public String name;

    public boolean isVisible;

    public static Finder<Long,ChatType> find = new Finder<Long,ChatType>(
            Long.class, ChatType.class
    );

    @Override
    public String toString(){
        return name;
    }
}
