package fr.enssat.vehiclesrental.service;

import fr.enssat.vehiclesrental.model.Employee;
import fr.enssat.vehiclesrental.repository.EmployeeRepository;
import fr.enssat.vehiclesrental.service.exception.alreadyexists.EmployeeAlreadyExistException;
import fr.enssat.vehiclesrental.service.exception.notfound.EmployeeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EmployeeService implements IEmployeeService {

    private final EmployeeRepository repository;

    @Override
    public boolean exists(Integer id) {
        return repository.existsById(id);
    }

    @Override
    public Employee getEmployee(Integer id) {
        return repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(String.valueOf(id)));
    }

    @Override
    public Employee getEmployeeByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(() -> new EmployeeNotFoundException(email));
    }

    @Override
    public List<Employee> getEmployeeByFirstname(String firstname) {
        return repository.findByFirstname(firstname);
    }

    @Override
    public List<Employee> getEmployeeByLastname(String lastname) {
        return repository.findByLastname(lastname);
    }

    @Override
    public List<Employee> getEmployees() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "lastname"));
    }

    @Override
    public Employee addEmployee(Employee employee) {
        if (repository.existsById(Math.toIntExact(employee.getId())))
            throw new EmployeeAlreadyExistException(employee);
        return repository.saveAndFlush(employee);
    }

    @Override
    public Employee editEmployee(Employee employee) {
        if (!repository.existsById(Math.toIntExact(employee.getId())))
            throw new EmployeeNotFoundException(String.valueOf(employee.getId()));
        return repository.saveAndFlush(employee);
    }

    @Override
    public void deleteEmployee(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<Employee> employee = repository.findByEmail(email);
        if (employee.isEmpty()) {
            throw new UsernameNotFoundException("User mail " + email + " was not found in the database");
        }

        System.out.println("Found mail: " + email);
        return new User(employee.get().getEmail(), employee.get().getPassword(), mapRolesToAuthorities(Collections.singletonList(employee.get().getPosition().label)));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<String> positions) {
        return positions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}