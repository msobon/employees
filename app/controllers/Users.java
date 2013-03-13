package controllers;


import play.*;
import play.mvc.*;
import play.data.*;

import static play.data.Form.*;

import models.*;
import views.html.*;


@Security.Authenticated(Secured.class)
public class Users extends Controller {


    static Form<User> userForm = form(User.class);

    public static Result users() {
        User performer = User.findByEmail(Http.Context.current().request().username());
        if (performer.isAdmin)
            return ok(
                    views.html.users.render(User.all(), userForm, performer)
            );
        else
            return Results.forbidden();
    }

    public static Result userDetails(String email) {
        User performer = User.findByEmail(Http.Context.current().request().username());
        if (performer.isAdmin) {
            User user = User.findByEmail(email);
            return ok(views.html.userDetails.render(performer, user));
        } else return Results.forbidden();
    }

    public static Result deleteUser(Long id) {
        User performer = User.findByEmail(Http.Context.current().request().username());
        if (performer.isAdmin) {
            User.delete(id);
            return redirect(routes.Users.users());
        } else
            return Results.forbidden();
    }

    public static Result addSSOUser(String email, String name) {
        User u = new User();
        u.email = email;
        u.name = name;
        u.password = "";
        User.create(u);

        return ok("user added");
    }

    public static Result createUser() {
        Form<User> filledForm = userForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(
                    views.html.users.render(User.all(), filledForm, User.findByEmail(Http.Context.current().request().username()))
            );
        } else {
            User.create(filledForm.get());
            return redirect(routes.Users.users());
        }
    }
}
