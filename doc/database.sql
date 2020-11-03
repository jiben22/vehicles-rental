CREATE TABLE employee (
    id INT(29) PRIMARY KEY,
    lastname VARCHAR(45) NOT NULL,
    firstname VARCHAR(45) NOT NULL,
    birthdate DATE NOT NULL,
    street VARCHAR(128) NOT NULL,
    zipcode VARCHAR(16) NOT NULL,
    city VARCHAR(128) NOT NULL,
    country VARCHAR(128) NOT NULL,
    email VARCHAR(128) NOT NULL UNIQUE,
    position INT(1) NOT NULL,
    password TEXT NOT NULL
);


/*==== INSERT ====*/

INSERT INTO employee (id, lastname, firstname, birthdate, street, zipcode, city, country, email, position, password) VALUES
(157314099170601, 'Stark', 'Tony', '1978-05-03', '9 rue du chene germain', '22700', 'Lannion', 'France', 'tony.stark@marvel.com', 0, '$2a$10$TCdrvG6zWUwrryQ.7Id6Qe9lJCWXaykTFH1vfRyrSRkmF5KK0DoCa'),
(157314099170602, 'Odinson', 'Thor', '1945-05-03', '5 avenue Asgardian ', '22700', 'Lannion', 'France', 'thor@marvel.com', 1, '$2a$10$i/8ECxz8OTwlvVxHwKzEaOq7kWYy/OWNDVo8JcOQsb5yk.Je.9gFW'),
(157314099170603, 'Jonathan', 'Henry', '1958-05-03', 'rue', '0000', 'Nebraska', 'USA', 'antman@marvel.com', 1, '$2a$10$P0OMU1.5XRSCab/ovZ11JuhBdPsYLaL54CrV9rqVGAAIC23lWvlo2'),
(157314099170604, 'Banner', 'Bruce', '1975-05-03', '9 rue du labo', '58695', 'Secret', 'USA', 'hulk@marvel.com', 2, '$2a$10$3RqwnPGFwpiBfp.0XTWy5ubcoqWlNugLtDlicYmFG4PFhHvMyZwZS'),
(157314099170605, 'Rogers', 'Steve', '1985-05-03', '0 rue du pole nord', '0000', 'PoleNord', 'Danemark', 'captain@marvel.com', 2, '$2a$10$DEdu0Y32xX3vOqpJZnQPVOzC/4FhpcgNzQmhVzDtyAJBUOe6ajYKe'),
(157314099170606, 'Barton', 'Clinton', '1947-05-03', '5 branche', '124578', 'Arbre', 'Terre', 'hawkeye@marvel.com', 3, '$2a$10$YP8uoFzmus3jmKBXssoQUuunhmzsLbrTqm0UlLMQS0J/40hb9RIEm'),
(157314099170607, 'Challa', 'T', '1987-05-03', '1 tour', '1234', 'Wakanda', 'Africa', 'blackpanther@marvel.com', 3, '$2a$10$PnNN.7lBHi7RBzyb8F2k6e4A8LIofAy5fEa.lfv/a5ALnkyi6aaFa'),
(157314099170608, 'Romanoff', 'Natasha', '1954-05-03', '1 rue du fantome', '4569', 'Moscou', 'Russie', 'blackwidow@marvel.com', 4, '$2a$10$yaAMX.0ubVOhUarMTTlWGOM2k3dyEseqj4VYzQuZuRRqrxkHK4EUC'),
(157314099170609, 'Rambeau', 'Monica', '1963-05-03', 'partout', '00', 'dans la', 'Galaxie', 'captainmarvel@marvel.com', 4, '$2a$10$mPKK/CRsEA9Wz5K2mw3JGOJVysJV8CM0QSbm6ZLKZWKjhk6oxjOzu'),
(157314099170610, 'Parker', 'Peter', '1969-05-03', 'quelque part', '000', 'New York', 'USA', 'spiderman@marvel.com', 4, '$2a$10$9xb3/9UAmHG4pbUI8ipsH.cx912tRPr8ybg32Ocr538tURvHoZ7gC');

