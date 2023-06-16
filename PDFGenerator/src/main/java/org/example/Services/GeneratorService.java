package org.example.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.example.Models.ChargeInfo;
import org.example.Models.Customer;
import org.example.Models.Database;
import org.example.Queue.Subscriber;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

public class GeneratorService {

    //defined DecimalFormat to display money amounts with 2 decimals
    private static final DecimalFormat dfZero = new DecimalFormat("0.00");


    //gathers Data --> json String is converted to list again, new Customer gets created related to the customer_id
    public void gatherData(String jsonInput) throws JsonProcessingException, DocumentException, FileNotFoundException {

        if (jsonInput.equals("[]")){
            System.out.println("PDF cannot be created (customer does not exist)");
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        List<ChargeInfo> chargeInfoList = mapper.readValue(jsonInput, new TypeReference<>() {});

        int customerId = chargeInfoList.get(0).getCustomer_id();
        Customer customer = getCustomerFromDB(customerId);



        createPDF(chargeInfoList, customer);

    }

    //returns new Customer Object with id from user input (id, first name and last name are set with the help of a DB query)
    public Customer getCustomerFromDB(int id){

        Customer customer = new Customer();
        String query = "SELECT first_name, last_name FROM customer WHERE id = ?";

        try(
            Connection conn = Database.getConnection();
        ){
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                customer.setId(id);
                customer.setFirst_name(rs.getString("first_name"));
                customer.setLast_name(rs.getString("last_name"));

            }

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return customer;
    }

    //PDF is created when information is complete
    public void createPDF(List<ChargeInfo> datalist, Customer customer) throws FileNotFoundException, DocumentException {
        Document document = new Document();

        //path where PDFs are saved
        Path basePath = Paths.get("").toAbsolutePath();
        Path path = basePath.resolve("PDFGenerator").resolve("createdInvoices").resolve(customer.getId()+".pdf");

        PdfWriter.getInstance(document, new FileOutputStream(path.toFile()));

        document.open();

        //set fonts for different paragraphs
        Font font = FontFactory.getFont(FontFactory.COURIER, 22, BaseColor.BLACK);
        Font fontLittle = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
        Font fontBold = FontFactory.getFont(FontFactory.COURIER, 12, Font.BOLD);

        //add heading
        Chunk heading = new Chunk("Invoice - " + customer.getFirst_name() + " " + customer.getLast_name(), font);
        document.add(heading);

        //add line separator
        Chunk linebreak = new Chunk(new LineSeparator());
        Paragraph dottedLine = new Paragraph(linebreak);
        document.add(dottedLine);

        //add empty line
        document.add( Chunk.NEWLINE );

        //add new text paragraph
        Chunk intro = new Chunk("Following tank loads must be paid:", fontLittle);
        Paragraph p1 = new Paragraph(intro);
        document.add(p1);

        document.add( Chunk.NEWLINE );

        //adds table (functions inspired by: https://www.baeldung.com/java-pdf-creation)
        PdfPTable table = new PdfPTable(2);
        addTableHeader(table);
        addRows(table, datalist);
        document.add(table);

        document.add( Chunk.NEWLINE );

        //adds customer information
        Chunk customerId = new Chunk("Customer-ID: " + customer.getId(), fontLittle);
        Paragraph p2 = new Paragraph(customerId);
        document.add(p2);
        Chunk name = new Chunk("Name: " + customer.getFirst_name() + " " + customer.getLast_name(), fontLittle);
        Paragraph p3 = new Paragraph(name);
        document.add(p3);

        document.add( Chunk.NEWLINE );

        //adds money amounts
        Chunk price = new Chunk("Price per kilowatt hour: 0.48 €", fontLittle);
        Paragraph p4 = new Paragraph(price);
        document.add(p4);
        Chunk priceTotal = new Chunk("Total amount: " + getTotalAmount(datalist) + " €", fontBold);
        Paragraph p5 = new Paragraph(priceTotal);
        document.add(p5);

        document.add( Chunk.NEWLINE );
        document.add( Chunk.NEWLINE );

        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        Calendar obj = Calendar.getInstance();
        String dateString = formatter.format(obj.getTime());

        //adds creation date
        Chunk date = new Chunk("Created on: " + dateString, fontLittle);
        Paragraph p6 = new Paragraph(date);
        document.add(p6);


        document.close();
    }

    //adds table header with specific styling (background colour, border, etc.)
    private void addTableHeader(PdfPTable table) {
        Stream.of("Invoice-ID", "Kilowatt-hours")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(1);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    //adds rows with user data (Invoice-Id and kwh)
    private void addRows(PdfPTable table, List<ChargeInfo> list) {

        for(ChargeInfo element : list){
            table.addCell(Integer.toString(element.getId()));
            table.addCell(element.getKwh());
        }
    }

    //calculates total amount for payment
    private String getTotalAmount(List<ChargeInfo> list){

        float sum = 0;
        for(ChargeInfo element : list){
            sum += Float.parseFloat(element.getKwh()) * 0.48;
        }

        return dfZero.format(sum);
    }


}
