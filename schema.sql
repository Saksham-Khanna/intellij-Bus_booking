DROP DATABASE IF EXISTS Bus_Booking;
CREATE DATABASE Bus_Booking;
USE Bus_Booking;

CREATE TABLE Bus (
    bus_id INT PRIMARY KEY AUTO_INCREMENT,
    date_booked DATE NOT NULL,
    capacity INT NOT NULL,
    isAvailable BOOLEAN NOT NULL,
    starting_point VARCHAR(255) NOT NULL,
    stopping_point VARCHAR(255) NOT NULL
);

CREATE TABLE Passenger (
    passenger_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    boarding_point VARCHAR(255) NOT NULL,
    destination_point VARCHAR(255) NOT NULL,
    ticket_amount DECIMAL(10, 2) NOT NULL,
    bus_id INT,
    FOREIGN KEY (bus_id) REFERENCES Bus(bus_id)
);

-- Insert sample data

SELECT * FROM Bus;
SELECT * FROM Passenger;
