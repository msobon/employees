package controllers;


import models.Employee;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

public class Employees extends Controller {

    static Form<Employee> employeeForm = form(Employee.class);

    public static Result employees() {
        return ok(
                views.html.employees.render(Employee.all(), employeeForm )
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
                    views.html.employees.render(Employee.all(), filledForm)
            );
        } else {
            Employee.create(filledForm.get());
            return redirect(routes.Employees.employees());
        }
    }
}
