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

    public static final String SSO_VALIDATION_URL = "http://localhost:9000/sso/validate/";
    public static final String SSO_lOGOUT_URL = "http://localhost:9000/sso/logout/";
    public static final String ACCOUNTING_URL = "http://localhost:9000/accounting";
    public static final String PORTAL_AUTH_URL = "http://localhost:9000";
    public static final String APP_URL = "http://localhost:9001";     //TODO move to app_cofig

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
            return async(WS.url(SSO_VALIDATION_URL + ssoToken).get().map(
                    new F.Function<WS.Response, Result>() {
                        public Result apply(WS.Response response) {

                            Logger.debug("Token validation result: " + response.getBody());
                            if ("err".equals(response.getBody())) {
                                return ok("Token validation failed");
                            } else {
                                return setSession(response.getBody(), "/employees");
                            }
                        }
                    }));
        } else {
            Logger.debug("No token found, redirecting to portal");
            response().setCookie("redirectUrl", APP_URL);

            return redirect(PORTAL_AUTH_URL);
        }
    }

    public static Result setSession(String email, String redirectUrl) {
        session("email", email);
        Logger.debug("Session set for user: " + email);
        return redirect(redirectUrl);
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

            return redirect(routes.Employees.employees());
        }
    }

    /**
     * Logout and clean the session.
     */
    public static Result logout() {
        WS.url(SSO_lOGOUT_URL + "/" + request().username()).get().map(
                new F.Function<WS.Response, Result>() {
                    public Result apply(WS.Response response) {
                        Logger.debug("SSO session has been destroyed");

                        return ok();
                    }
                });

        session().clear();
        response().discardCookies("ssoToken");
        flash("success", "You've been logged out");
        return redirect(routes.Application.login());
    }

    public static Result register() {
        //TODO validation
        Form<User> filledForm = userForm.bindFromRequest();
        if (filledForm.hasErrors()) {

            return badRequest(views.html.register.render(filledForm));
        } else {
            User.create(filledForm.get());
            return redirect(routes.Application.login());
        }
    }

}