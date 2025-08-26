import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/Bus_Booking";
        String user = "root"; // check this in your mysql
        String password = ""; // enter your mysql password

        try (Connection con = DriverManager.getConnection(url, user, password);
             Scanner sc = new Scanner(System.in)) {

            // 1. Show all buses
            System.out.println("\nAvailable Buses:");
            String busQuery = "SELECT bus_id, starting_point, stopping_point, capacity, isAvailable FROM Bus";
            try (PreparedStatement ps = con.prepareStatement(busQuery);
                 ResultSet rs = ps.executeQuery()) {

                System.out.printf("%-5s %-15s %-15s %-10s %-12s%n", "ID", "Starting", "Stopping", "Capacity", "Available");
                while (rs.next()) {
                    System.out.printf("%-5d %-15s %-15s %-10d %-12s%n",
                            rs.getInt("bus_id"),
                            rs.getString("starting_point"),
                            rs.getString("stopping_point"),
                            rs.getInt("capacity"),
                            rs.getBoolean("isAvailable") ? "Yes" : "No");
                }
            }

            // 2. Ask user for boarding & destination
            System.out.print("\nEnter Boarding Point: ");
            String boardingPoint = sc.nextLine();
            System.out.print("Enter Destination Point: ");
            String destinationPoint = sc.nextLine();

            // 3. Find bus automatically
            String findBus = "SELECT * FROM Bus WHERE starting_point = ? AND stopping_point = ? AND isAvailable = TRUE";
            try (PreparedStatement psFind = con.prepareStatement(findBus)) {
                psFind.setString(1, boardingPoint);
                psFind.setString(2, destinationPoint);

                try (ResultSet rs = psFind.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("No bus available for this route.");
                        return;
                    }

                    int busId = rs.getInt("bus_id");
                    int capacity = rs.getInt("capacity");

                    if (capacity <= 0) {
                        System.out.println("Bus found but it is full.");
                        return;
                    }

                    // 4. Create ticket with required values
                    System.out.print("Enter Passenger Name: ");
                    String passengerName = sc.nextLine();
                    System.out.print("Enter Ticket Amount: ");
                    double ticketAmount = sc.nextDouble();

                    String insertTicket = "INSERT INTO Passenger (name, bus_id, boarding_point, destination_point, ticket_amount) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement psInsert = con.prepareStatement(insertTicket)) {
                        psInsert.setString(1, passengerName);
                        psInsert.setInt(2, busId);
                        psInsert.setString(3, boardingPoint);
                        psInsert.setString(4, destinationPoint);
                        psInsert.setDouble(5, ticketAmount);

                        int rows = psInsert.executeUpdate();
                        if (rows > 0) {
                            System.out.println("Ticket booked successfully for Bus ID: " + busId);

                            // Reduce capacity
                            String updateCapacity = "UPDATE Bus SET capacity = capacity - 1 WHERE bus_id = ?";
                            try (PreparedStatement psUpdate = con.prepareStatement(updateCapacity)) {
                                psUpdate.setInt(1, busId);
                                psUpdate.executeUpdate();
                            }
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

