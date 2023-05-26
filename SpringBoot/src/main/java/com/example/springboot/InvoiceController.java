package com.example.springboot;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InvoiceController {

    @PostMapping("invoices/{customerId")
    public String generateInvoice(@PathVariable String customerId){
        System.out.println(customerId);

        return "got it";
    }

}
