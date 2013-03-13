package models;

import com.avaje.ebean.Ebean;
import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Transactional;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * User entity managed by Ebean
 */
@Entity
@Table(name = "account")
public class User extends Model {

    @Id
    public Long id;

    @Constraints.Required
    @Formats.NonEmpty
    public String email;

    @Constraints.Required
    public String name;

    @Constraints.Required
    public String password;

    public Long usedCredits = 0l;

    public boolean isAdmin = false;


    // -- Queries

    public static Finder<String, User> find = new Finder(String.class, User.class);

    public static Finder<Long, User> findById = new Finder(Long.class, User.class);

    /**
     * Retrieve all users.
     */
    public static List<User> all() {
        return find.all();
    }

    /**
     * Retrieve a User from email.
     */
    public static User findByEmail(String email) {
        return find.where().eq("email", email).findUnique();
    }

    /**
     * Authenticate a User.
     */
    public static User authenticate(String email, String password) {
        return find.where()
                .eq("email", email)
                .eq("password", password)
                .findUnique();
    }

    public static void create(User user) {
        Logger.debug("Adding user: " + user);
        user.save();
    }

    public static void delete(Long id) {
        findById.ref(id).delete();
    }

    // --

    public String toString() {
        return "User(" + email + ")";
    }

}

