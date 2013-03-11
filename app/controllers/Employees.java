package controllers;


import play.*;
import play.mvc.*;
import play.data.*;

import static play.data.Form.*;

import models.*;
import views.html.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.GetMethod;

@Security.Authenticated(Secured.class)
public class Employees extends Controller {

    static Form<Employee> employeeForm = form(Employee.class);
    private static Configuration conf = Play.application().configuration();

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
            if(!chargeUser(Http.Context.current().request().username(),Application.createUserFee)){
                return ok(views.html.noCredits.render(User.findByEmail(Http.Context.current().request().username())));
            }
            Employee.create(filledForm.get());

            Logger.info("Employee "+filledForm.data().get("email")+ " added");
            return redirect(routes.Employees.employees());
        }
    }

    private static boolean chargeUser(String email, long fee){
        HttpClient client = new HttpClient();
        client.getParams().setParameter("http.useragent", "App Client");
        BufferedReader br = null;

        GetMethod method = new GetMethod(conf.getString("ACCOUNTING_URL") +"/"+email+"/"+fee);
        boolean result =false;
        try{
            int returnCode = client.executeMethod(method);

            if(returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
                System.err.println("The Get method is not implemented by this URI");
                // still consume the response body
                method.getResponseBodyAsString();
            } else {
                br = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
                String readLine = br.readLine();
                if("true".equals(readLine.trim())){
                    System.err.println("true");
                    result = true;
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            method.releaseConnection();
            if(br != null) try { br.close(); } catch (Exception fe) {System.err.println("conn not closed");}
        }
      return result;
    }
}
