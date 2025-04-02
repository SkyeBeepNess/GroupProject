package courseSelection.src.courseselection;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class BookingHandler {

    public static Map<String, Appointment> loadBookings(String filePath) 
    {
        Map<String, Appointment> bookings = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) 
        {
            String line;
            while ((line = br.readLine()) != null) 
            {
                String[] parts = line.split(",");
                if(parts.length < 5) continue;  // skip incomplete lines clearly

                try 
                {
                    LocalDate examDate = LocalDate.parse(parts[3]);
                    LocalDate interviewDate = LocalDate.parse(parts[4]);
                    bookings.put(parts[0], new Appointment(parts[0], new Course(parts[1], parts[2], "", ""),
                            examDate, interviewDate));
                } catch (DateTimeParseException e) 
                {
                    System.out.println("Corrupted date format in booking ID " + parts[0] + ", skipped.");
                    continue;  // skip corrupted dates clearly
                }
            }
        } catch (IOException e) 
       		{
        		e.printStackTrace();
       		}
        		return bookings;
    }
    public static void saveBookings(Map<String, Appointment> bookings, String filePath) 
    {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) 
        {
            for (Appointment app : bookings.values()) 
            {
                bw.write(app.getBookingId() + "," + app.getCourse().getId() + "," + app.getCourse().getName() + ","
                        + app.getExamDate() + "," + app.getInterviewDate());
                bw.newLine();
            }
        } catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
}
