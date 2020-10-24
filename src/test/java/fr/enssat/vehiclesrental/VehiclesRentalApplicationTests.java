package fr.enssat.vehiclesrental;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes={VehiclesRentalApplication.class})
@EnableJpaRepositories
@Profile("test")
class VehiclesRentalApplicationTests {

	@Test
	void contextLoads() {
	}

}
