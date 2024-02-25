import java.sql.*;
import java.util.Scanner;
public class HospitalManagementSystem {
	
	private static final String username= "root";
	private static final String url= "jdbc:mysql://localhost:3306/hospital";
	private static final String password="root@localhost#";
	
	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		}
		
		Scanner scanner= new Scanner(System.in);
		
		try {
			Connection connection= DriverManager.getConnection(url, username, password);
			
			Patient patient = new Patient(connection, scanner);
			Doctor doctor= new Doctor(connection);
			
			while(true) {
				System.out.println();
				System.out.println();
				System.out.println("HOSPITAL MANAGEMENT SYSTEM");
				System.out.println("1. Add Patient");
				System.out.println("2. View Patienst");
				System.out.println("3. View Doctors");
				System.out.println("4. Book Appointment");
				System.out.println("5. Exit");
				System.out.println();
				
				System.out.println("Enter Your Choice: ");
				int choice= scanner.nextInt();
				
				switch(choice) {
				case 1:
					patient.addPatient();
					break;
				case 2:
					patient.viewPatient();
					break;
				case 3:
					doctor.viewDoctor();
					break;
				case 4:
					bookAppointment(connection, scanner, patient, doctor);
					System.out.println();
					break;
				case 5:
					return;
				default:
					System.out.println("Invalid Dhoice");
					break;
					
				}
				
				
				
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	public static void bookAppointment(Connection connection, Scanner scanner,Patient patient, Doctor doctor) {
		System.out.println("Enter Patient Id: ");
		int patientId= scanner.nextInt();
		
		System.out.println("Enter Doctor Id: ");
		int doctorId= scanner.nextInt();
		
		System.out.println("Enter appointment date(YYYY-MM-DD)");
		String appointmentDate= scanner.next();
		
		if(patient.getPatientById(patientId)&& doctor.getDoctorById(doctorId)) {
			
			if(checkDoctorAvailability(doctorId, appointmentDate, connection)) {
				String appointmenQuery="INSERT INTO appointments(patient_id,doctors_id,appointment_date)VALUES(?,?,?);";
				
				try {
					PreparedStatement preparedStatement = connection.prepareStatement(appointmenQuery);
					preparedStatement.setInt(1, patientId);
					preparedStatement.setInt(2, doctorId);
					preparedStatement.setString(3, appointmentDate);
					
					int rowsAffected= preparedStatement.executeUpdate();
					
					if(rowsAffected>0) {
						System.out.println("Appointment Booked!");
					}else {
						System.out.println("Booking Failed!!");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}else {
				System.out.println("Doctor not Available on this Date ");
			}
			
		}else {
			System.out.println("Either patient or Doctor doesn't exist!! ");
		}
	}

	private static boolean checkDoctorAvailability(int doctorId, String appointmentDate,Connection connection) {
		String query="SELECT COUNT(*) FROM appointments WHERE doctors_id = ? AND appointment_date = ?; ";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, doctorId);
			preparedStatement.setString(2, appointmentDate);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) {
				int count = resultSet.getInt(1);
				if(count==0) {
					return true;
				}else {
					return false;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	
	

}
