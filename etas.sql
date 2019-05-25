create database etas

use etas

create table employee(
	id BIGINT NOT NULL AUTO_INCREMENT,
	full_name VARCHAR(50) NOT NULL,
	designation VARCHAR(20) NOT NULL,
	joining_date TIMESTAMP NOT NULL,
	email VARCHAR(100) NOT NULL,
	phone VARCHAR(20) NOT NULL,
	address VARCHAR(200),
	PRIMARY KEY (id),
	CONSTRAINT UC_Employee_Email UNIQUE (email),
	CONSTRAINT UC_Employee_Phone UNIQUE (phone)
	);

create table cab(
	id BIGINT NOT NULL AUTO_INCREMENT,
	registration_number VARCHAR(50) NOT NULL,
	driver_id BIGINT NOT NULL,
	status BOOLEAN NOT NULL,
	comments VARCHAR(500),
	varancy INT NOT NULL,
	PRIMARY KEY (id),
	CONSTRAINT UC_Cab_RegistrationNumber UNIQUE (registration_number),
	CONSTRAINT FK_EmployeeCab FOREIGN KEY (driver_id) REFERENCES employee(id)
	);

create table booking(
	id BIGINT NOT NULL AUTO_INCREMENT,
	source_location VARCHAR(50) NOT NULL,
	datetime_journey  TIMESTAMP NOT NULL,
	employee_id BIGINT NOT NULL,
	status BOOLEAN NOT NULL,
	PRIMARY KEY (id),
    CONSTRAINT FK_EmployeeBooking FOREIGN KEY (employee_id) REFERENCES employee(id)
	);

create table request(
	id BIGINT NOT NULL AUTO_INCREMENT,
	status VARCHAR(15) NOT NULL,
	comments VARCHAR(500),
	booking_id BIGINT,
	creation_date TIMESTAMP NOT NULL,
	generator BIGINT NOT NULL,
	PRIMARY KEY (id),
	CONSTRAINT FK_EmployeeRequest FOREIGN KEY (generator) REFERENCES employee(id),
	CONSTRAINT FK_BookingRequest FOREIGN KEY (booking_id) REFERENCES booking(id)
	);
