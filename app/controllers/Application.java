package controllers;


import play.*;
import play.libs.F;
import play.libs.WS;
import play.mvc.*;
import play.data.*;

import static play.data.Form.*;

import models.*;
import views.html.*;

public class Application extends Controller {

    public static String portalAuthUrl = "http://localhost:9000/accounting";
    public static Long createUserFee = 5l;

    static Form<User> userForm = form(User.class);
    public static final String SSO_VALIDATION_URL = "http://localhost:9000/sso/validate/";

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
            return async(WS.url(SSO_VALIDATION_URL + ssoToken).get().map(
                    new F.Function<WS.Response, Result>() {
                        public Result apply(WS.Response response) {

                            Logger.debug("Token validation result: " + response.getBody());
                            if ("err".equals(response.getBody())) {
                                return ok("Token validation failed");
                            } else {
                                 return setSession(response.getBody());
                                //return ok("auth correct");
//                                return redirect(routes.Employees.employees());
                            }
                           // return ok("ok "+response.getBody());
                        }
                    }));
        }
        if (ssoToken == "") {
            return redirect("http://localhost:9000?redirectUrl=appUrl"); //todo wywalic do propertisow + dodac return redirect
//            return ok(
//                    login.render(form(Login.class))
//            );
        } else {


            return ok("cookie was set, TODO token validation via rest service");
        }
    }

    public static Result setSession(String email){
       // session("email", email);  wymaga upgraed'u play'a do 2.1
        return ok("session set");
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