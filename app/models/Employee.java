package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="employee")
public class Employee extends Model {

    @Id
    public Long id;

    @Constraints.Required
    public String firstName;

    @Constraints.Required
    public String lastName;

    @Constraints.Required
    public String homeAddress;

    public String mailAddress;

    @Constraints.Required
    public Date employmentDate;

    @Constraints.Required
    public int salary;

    public String comments;

    public static Finder<Long,Employee> find = new Finder(
            Long.class, Employee.class
    );

    public static List<Employee> all() {
        return find.all();
    }

    public static void create(Employee employeee) {
        employeee.save();
    }

    public static void delete(Long id) {
        find.ref(id).delete();
    }


}
