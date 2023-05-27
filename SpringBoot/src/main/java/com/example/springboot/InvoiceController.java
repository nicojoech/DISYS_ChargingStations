package com.example.springboot;

import org.springframework.web.bind.annotation.*;

@RestController
public class InvoiceController {

    @PostMapping("/invoices")
    public Long generateInvoice(@RequestBody Invoice invoice) {
        Long customerId = invoice.getCustomerId();

        //Invoice generieren mit anderen Services

        return customerId;
    }
    @GetMapping("/invoices/{customerId}")
    public Long getInvoice(@PathVariable Long customerId) {

        System.out.println("Invoice abfragen für " + customerId);

        //Invoice abfragen

        return customerId;
    }


}
