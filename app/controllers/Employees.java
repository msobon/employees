package controllers;


import models.Employee;
import models.User;
import play.data.Form;
import play.mvc.*;

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
            Employee.create(filledForm.get());
            return redirect(routes.Employees.employees());
        }
    }
}
