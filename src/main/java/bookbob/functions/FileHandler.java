package bookbob.functions;

import bookbob.entity.Patient;
import bookbob.entity.Records;
import bookbob.entity.AppointmentRecord;
import bookbob.entity.Appointment;
import bookbob.entity.Visit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileHandler {

    private static final Logger logger = Logger.getLogger(FileHandler.class.getName());
    private static String filePath = "data" + File.separator + "bookbob_data.txt";
    private static String appointmentFilePath = "data" + File.separator + "bookbob_appointment.txt";

    public static void initFile(Records records){
        try {
            String directoryName = "data";
            String currentDirectory = System.getProperty("user.dir");
            String directory = currentDirectory + File.separator + directoryName;
            File directoryFile = new File(directory);

            if (directoryFile.mkdirs()) {           //directory was not created
                File file = new File(filePath);
                file.createNewFile();              //create new data file
            } else {                               //directory already created
                logger.log(Level.INFO, "Directory exists");
                File file = new File(filePath);
                if(file.createNewFile()) {         //file was not created
                    logger.log(Level.INFO, "Directory exists, creating new file");
                } else {
                    retrieveData(records);
                }
            }
        } catch(Exception e){
            logger.log(Level.WARNING, "Error initialising file", e);
            e.printStackTrace();
        }
    }

    public static void initFile(AppointmentRecord appointmentRecord){
        try {
            String directoryName = "data";
            String currentDirectory = System.getProperty("user.dir");
            String directory = currentDirectory + File.separator + directoryName;
            File directoryFile = new File(directory);

            if(directoryFile.mkdirs()) {           //directory was not created
                File file = new File(filePath);
                file.createNewFile();              //create new data file
            } else {                               //directory already created
                logger.log(Level.INFO, "Directory for appointments exists");
                File file = new File(appointmentFilePath);
                if (file.createNewFile()) {         //file was not created
                    logger.log(Level.INFO, "Directory for appointments exists, creating new file");
                } else {
                    retrieveData(appointmentRecord);
                }
            }
        } catch(Exception e){
            logger.log(Level.WARNING, "Error initializing file", e);
            e.printStackTrace();
        }
    }

    public static String convertPatientToOutputText(Patient patient) {
        String output = "";
        output += "Name: " + patient.getName() + " | " + "NRIC: " + patient.getNric() + " | "
                + "Phone Number: " + patient.getPhoneNumber() + " | " + "Date_Of_Birth: " + patient.getDateOfBirth()
                + " | " + "Home Address: " + patient.getHomeAddress() + " | " + "Allergy: " + patient.getAllergies()
                + " | " + "Sex: " + patient.getSex() + " | " + "Medical History: " + patient.getMedicalHistories()
                + " | " + "Visit: " + patient.getVisits() + ";";
        return output;
    }


    public static String convertPatientToOutputText(Appointment appointment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String patientName = appointment.getPatientName();
        String patientNric = appointment.getPatientNric();
        String date = appointment.getDate().format(formatter);
        String time = appointment.getTime().toString();
        String output = "";
        output += "Name: " + patientName + "|" + "NRIC: " + patientNric + "|"
                + "Date: " + date  + "|" + "Time: " + time;

        return output;
    }

    public static void autosave(Records records) throws IOException {
        ArrayList<Patient> patients = records.getPatients();
        FileWriter fw = new FileWriter(filePath);
        for (Patient currPatient : patients) {
            String toWrite = convertPatientToOutputText(currPatient);
            fw.write(toWrite + "\n");
        }
        fw.close();
        logger.log(Level.INFO, "Autosaved successfully");
    }

    public static void autosave(AppointmentRecord appointmentRecord) throws IOException {
        List<Appointment> appointments = appointmentRecord.getAppointments();
        FileWriter fw = new FileWriter(appointmentFilePath);
        for (Appointment appointment : appointments) {
            String toWrite = convertPatientToOutputText(appointment);
            fw.write(toWrite + "\n");
        }
        fw.close();
        logger.log(Level.INFO, "Autosaved appointments successfully");
    }

    /*
    public static void retrieveData(Records records){
        try {
            File file = new File(filePath);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String[] data = line.split("\\|");
                String name = data[0].substring(6).trim();
                String nric = data[1].substring(6).trim();
                String phoneNumber = data[2].substring(15).trim();
                String dateOfBirth = data[3].substring(16).trim();
                String homeAddress = data[4].substring(15).trim();
                String sex = data[6].substring(5).trim();
                String visitDetails = data[8].trim();

                ArrayList<String> allergies = parseList(data[5].substring(9).trim());
                ArrayList<String> medicalHistories = parseList(data[7].substring(17).trim());

                // Parse the visit information
                ArrayList<Visit> visits = new ArrayList<>();
                Visit visit = parseVisitInputString(visitDetails);
                visits.add(visit);
                Patient patient = new Patient(name, nric, phoneNumber, dateOfBirth, homeAddress, allergies,
                        sex, medicalHistories, visits);
                records.addPatient(patient);
            }
            logger.log(Level.INFO, "Data retrieved successfully");
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, "File not found", e);
            throw new RuntimeException(e);
        }
    }*/

    //@@author kaboomzxc
    public static void retrieveData(Records records) {
        try {
            File file = new File(filePath);
            Scanner reader = new Scanner(file);

            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String[] data = line.split("\\|");

                if (data.length < 9) {
                    continue;
                }

                // Parse basic patient information
                String name = data[0].substring(6).trim();
                String nric = data[1].substring(6).trim();
                String phoneNumber = data[2].substring(15).trim();
                String dateOfBirth = data[3].substring(16).trim();
                String homeAddress = data[4].substring(15).trim();
                String sex = data[6].substring(5).trim();
                ArrayList<String> allergies = parseList(data[5].substring(9).trim());
                ArrayList<String> medicalHistories = parseList(data[7].substring(17).trim());

                // Parse visits
                ArrayList<Visit> visits = new ArrayList<>();
                String visitsString = data[8].trim();

                // Extract the content between the outer-most brackets
                int firstBracket = visitsString.indexOf("[");
                int lastBracket = visitsString.lastIndexOf("]");

                if (firstBracket != -1 && lastBracket != -1) {
                    visitsString = visitsString.substring(firstBracket + 1, lastBracket);

                    // Split into individual visits by looking for date pattern
                    String[] visitParts = visitsString.split("(?<=\\]),\\s*(?=\\d{2}-\\d{2}-\\d{4})");

                    for (String visitPart : visitParts) {
                        Visit visit = parseVisitInputString("[" + visitPart + "]");
                        if (visit != null) {
                            visits.add(visit);
                        }
                    }
                }

                Patient patient = new Patient(name, nric, phoneNumber, dateOfBirth, homeAddress,
                        allergies, sex, medicalHistories, visits);
                records.addPatient(patient);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void retrieveData(AppointmentRecord appointmentRecord){
        try {
            File file = new File(appointmentFilePath);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String[] data = line.split("\\|");
                String name = data[0].substring(6).trim();
                String nric = data[1].substring(6).trim();
                String date = data[2].substring(6).trim();
                String time = data[3].substring(6).trim();
                Appointment appointment = new Appointment(name, nric, date, time);
                appointmentRecord.addAppointment(appointment);
            }
            logger.log(Level.INFO, "Retrieved successfully");
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, "File not found", e);
            throw new RuntimeException(e);
        }
    }
    /*
    //@@author yentheng0110
    private static ArrayList<String> parseList(String input) {
        ArrayList<String> list = new ArrayList<>();
        if (!input.isEmpty()) {
            // Split the input by commas and trim spaces around each element
            String[] items = input.split(",");
            for (String item : items) {
                list.add(item.trim());
            }
        }
        return list;
    }*/

    //@@author kaboomzxc
    private static ArrayList<String> parseList(String input) {
        ArrayList<String> list = new ArrayList<>();
        // Remove any number of surrounding brackets
        String content = input.replaceAll("^\\[+|\\]+$", "");
        if (!content.isEmpty()) {
            String[] items = content.split(",\\s*");
            list.addAll(Arrays.asList(items));
        }
        return list;
    }

    /*
    //@@author coraleaf0602
    // Parses string with visit details and creates visit object
    public static Visit parseVisitInputString(String visitString) {
        int visitStartIndex = visitString.indexOf("[") + 1;
        int visitEndIndex = visitString.lastIndexOf("]");
        String visitDetails = visitString.substring(visitStartIndex, visitEndIndex).trim();

        // Split visit details into individual components
        String[] components = visitDetails.split(", Diagnosis: \\[|\\], Medications: \\[|\\]");

        // Parse date and time
        String dateTimeString = components[0].trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime visitDateTime = LocalDateTime.parse(dateTimeString, formatter);

        ArrayList<String> diagnosisList = new ArrayList<>();
        ArrayList<String> medicationsList = new ArrayList<>();

        // Check if diagnosis and medication are included in visit details
        if(components.length > 1) {
            // Parse diagnosis
            String diagnosisString = components[1].trim();
            diagnosisList.addAll(Arrays.asList(diagnosisString.split(",\\s*")));

            // Parse medications
            String medicationsString = components[2].trim();
            medicationsList.addAll(Arrays.asList(medicationsString.split(",\\s*")));
        }

        return new Visit(visitDateTime, diagnosisList, medicationsList);
    }*/

    //@@author kaboomzxc
    public static Visit parseVisitInputString(String visitString) {
        try {
            int visitStartIndex = visitString.indexOf("[");
            int visitEndIndex = visitString.lastIndexOf("]");

            if (visitStartIndex == -1 || visitEndIndex == -1) {
                return null;
            }

            String visitDetails = visitString.substring(visitStartIndex + 1, visitEndIndex).trim();

            // Parse date time
            String dateTimeString;
            if (visitDetails.contains("Diagnosis:")) {
                dateTimeString = visitDetails.substring(0, visitDetails.indexOf("Diagnosis:")).trim();
            } else {
                dateTimeString = visitDetails;
            }
            if (dateTimeString.endsWith(",")) {
                dateTimeString = dateTimeString.substring(0, dateTimeString.length() - 1);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            LocalDateTime visitDateTime = LocalDateTime.parse(dateTimeString, formatter);

            // Parse diagnoses and medications
            ArrayList<String> diagnosisList = new ArrayList<>();
            ArrayList<String> medicationsList = new ArrayList<>();

            if (visitDetails.contains("Diagnosis:")) {
                // Find the diagnosis content between brackets and clean it
                int diagStart = visitDetails.indexOf("Diagnosis: [") + 11;
                int diagEnd = visitDetails.indexOf("]", diagStart);
                if (diagStart != -1 && diagEnd != -1) {
                    String diagContent = visitDetails.substring(diagStart, diagEnd);
                    // Remove any extra square brackets
                    diagContent = diagContent.replaceAll("^\\[+|\\]+$", "").trim();
                    if (!diagContent.isEmpty()) {
                        diagnosisList.addAll(Arrays.asList(diagContent.split(",\\s*")));
                    }
                }

                if (visitDetails.contains("Medications:")) {
                    // Find the medications content between brackets and clean it
                    int medStart = visitDetails.indexOf("Medications: [") + 13;
                    int medEnd = visitDetails.indexOf("]", medStart);
                    if (medStart != -1 && medEnd != -1) {
                        String medContent = visitDetails.substring(medStart, medEnd);
                        // Remove any extra square brackets
                        medContent = medContent.replaceAll("^\\[+|\\]+$", "").trim();
                        if (!medContent.isEmpty()) {
                            medicationsList.addAll(Arrays.asList(medContent.split(",\\s*")));
                        }
                    }
                }
            }


            return new Visit(visitDateTime, diagnosisList, medicationsList);
        } catch (Exception e) {
            return null;
        }
    }
}


