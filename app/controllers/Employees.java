package controllers;


import models.Employee;
import models.User;
import play.Logger;
import play.data.Form;
import play.mvc.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;

@Security.Authenticated(Secured.class)
public class Employees extends Controller {

    static Form<Employee> employeeForm = form(Employee.class);

    public static Result employees() {
        return ok(
                views.html.employees.render(Employee.all(), employeeForm, User.findByEmail(Http.Context.current().request().username()))
        );
    }

    public static Result employeeDetails(Long id) {
        return ok(
                views.html.employeeDetails.render(Employee.find.byId(id), User.findByEmail(Http.Context.current().request().username()))
        );
    }

    public static Result deleteEmployee(Long id) {
        Employee.find.ref(id).delete();
        return redirect(routes.Employees.employees());
    }

    public static Result createEmployee() {
        Form<Employee> filledForm = employeeForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(
                    views.html.employees.render(Employee.all(), filledForm, User.findByEmail(Http.Context.current().request().username()))
            );
        } else {
            chargeUser(Http.Context.current().request().username(),100);
            Employee.create(filledForm.get());

            Logger.info("Employee "+filledForm.data().get("email")+ " added");
            return redirect(routes.Employees.employees());
        }
    }

    private static void chargeUser(String email, long fee){
        HttpClient client = new HttpClient();
        client.getParams().setParameter("http.useragent", "Test Client");

        BufferedReader br = null;

        PostMethod method = new PostMethod(Application.portalAuthUrl);
        method.addParameter("email", email);
        method.addParameter("fee", String.valueOf(fee));

        try{
            int returnCode = client.executeMethod(method);

            if(returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
                System.err.println("The Post method is not implemented by this URI");
                // still consume the response body
                method.getResponseBodyAsString();
            } else {
                br = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
                String readLine;
                while(((readLine = br.readLine()) != null)) {
                    System.err.println(readLine);
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            method.releaseConnection();
            if(br != null) try { br.close(); } catch (Exception fe) {}
        }

    }
}
