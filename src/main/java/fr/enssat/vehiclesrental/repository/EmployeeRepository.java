package fr.enssat.vehiclesrental.repository;

import fr.enssat.vehiclesrental.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    List<Employee> findByFirstname(String firstname);
    List<Employee> findByLastname(String lastname);
    Optional<Employee> findByEmail(String email);
    @Query("SELECT count(*) FROM Employee")
    Integer countAll();
}
