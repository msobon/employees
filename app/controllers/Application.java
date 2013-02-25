package controllers;

import models.User;
import play.*;
import play.data.Form;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static String portalAuthUrl = "http://localhost:9000/accounting";
    public static Long createUserFee = 5l;

    static Form<User> userForm = form(User.class);

    // -- Authentication

    public static class Login {

        public String email;
        public String password;

        public String validate() {
            if (User.authenticate(email, password) == null) {
                return "Invalid user or password";
            }
            return null;
        }

    }

    /**
     * Login page.
     */
    public static Result login() {
        String ssoToken = "";

        if (request().cookies().get("ssoToken") != null) {
            ssoToken = request().cookies().get("ssoToken").value();
        }

        if (ssoToken == "") {
            return redirect("http://localhost:9000?redirectUrl=appUrl")  ; //todo wywalic do propertisow + dodac return redirect
//            return ok(
//                    login.render(form(Login.class))
//            );
        } else {
            return ok("cookie was set, TODO token validation via rest service");
        }
    }

    /**
     * Handle login form submission.
     */
    public static Result authenticate() {
        Form<Login> loginForm = form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session("email", loginForm.get().email);

            return redirect(
                    routes.Employees.employees()
            );

        }
    }

    /**
     * Logout and clean the session.
     */
    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(
                routes.Application.login()
        );
    }

    public static Result register() {
        //TODO validation
        Form<User> filledForm = userForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(
                    views.html.register.render(filledForm)
            );
        } else {
            User.create(filledForm.get());
            return redirect(routes.Application.login());
        }
    }

}