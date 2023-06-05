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

    private static final DecimalFormat dfZero = new DecimalFormat("0.00");

    public void gatherData(String jsonInput) throws JsonProcessingException, DocumentException, FileNotFoundException {

        ObjectMapper mapper = new ObjectMapper();
        List<ChargeInfo> chargeInfoList = mapper.readValue(jsonInput, new TypeReference<>() {});

        int customerId = chargeInfoList.get(0).getCustomer_id();
        Customer customer = getCustomerFromDB(customerId);

        createPDF(chargeInfoList, customer);

    }

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

    public void createPDF(List<ChargeInfo> datalist, Customer customer) throws FileNotFoundException, DocumentException {
        Document document = new Document();

        Path basePath = Paths.get("").toAbsolutePath();
        Path path = basePath.resolve("PDFGenerator").resolve("createdInvoices").resolve(customer.getId()+".pdf");

        PdfWriter.getInstance(document, new FileOutputStream(path.toFile()));

        document.open();
        Font font = FontFactory.getFont(FontFactory.COURIER, 22, BaseColor.BLACK);
        Font fontLittle = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
        Font fontBold = FontFactory.getFont(FontFactory.COURIER, 12, Font.BOLD);

        Chunk heading = new Chunk("Invoice - " + customer.getFirst_name() + " " + customer.getLast_name(), font);
        document.add(heading);

        Chunk linebreak = new Chunk(new LineSeparator());
        Paragraph dottedLine = new Paragraph(linebreak);
        document.add(dottedLine);

        document.add( Chunk.NEWLINE );

        Chunk intro = new Chunk("Following tank loads must be paid:", fontLittle);
        Paragraph p1 = new Paragraph(intro);
        document.add(p1);

        document.add( Chunk.NEWLINE );

        PdfPTable table = new PdfPTable(2);
        addTableHeader(table);
        addRows(table, datalist);
        document.add(table);

        document.add( Chunk.NEWLINE );

        Chunk customerId = new Chunk("Customer-ID: " + customer.getId(), fontLittle);
        Paragraph p2 = new Paragraph(customerId);
        document.add(p2);

        Chunk name = new Chunk("Name: " + customer.getFirst_name() + " " + customer.getLast_name(), fontLittle);
        Paragraph p3 = new Paragraph(name);
        document.add(p3);

        document.add( Chunk.NEWLINE );

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

        Chunk date = new Chunk("Created on: " + dateString, fontLittle);
        Paragraph p6 = new Paragraph(date);
        document.add(p6);


        document.close();
    }

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

    private void addRows(PdfPTable table, List<ChargeInfo> list) {

        for(ChargeInfo element : list){
            table.addCell(Integer.toString(element.getId()));
            table.addCell(element.getKwh());
        }
    }

    private String getTotalAmount(List<ChargeInfo> list){



        float sum = 0;
        for(ChargeInfo element : list){
            sum += Float.parseFloat(element.getKwh()) * 0.48;
        }

        return dfZero.format(sum);

    }


}
