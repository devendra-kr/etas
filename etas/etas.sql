create database etas

use etas

create table employee(
	id BIGINT NOT NULL,
	full_name VARCHAR(50) NOT NULL,
	designation VARCHAR(20) NOT NULL,
	joining_date TIMESTAMP NOT NULL,
	email VARCHAR(100) NOT NULL,
	phone VARCHAR(20) NOT NULL,
	address VARCHAR(200)
	);

create table cab(
	id BIGINT NOT NULL,
	registration_number VARCHAR(50) NOT NULL,
	driver_id BIGINT NOT NULL,
	status BOOLEAN NOT NULL,
	comments VARCHAR(500),
	varancy INT NOT NULL
	);

create table booking(
	id BIGINT NOT NULL,
	source_location VARCHAR(50) NOT NULL,
	datetime_journey  TIMESTAMP NOT NULL,
	employee_id BIGINT NOT NULL,
	status BOOLEAN NOT NULL
	);

create table request(
	id BIGINT NOT NULL,
	status VARCHAR(15) NOT NULL,
	comments VARCHAR(500),
	booking_id BIGINT,
	creation_date TIMESTAMP NOT NULL,
	generator VARCHAR(50) NOT NULL
	);
