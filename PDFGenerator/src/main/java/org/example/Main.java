package org.example;

import com.itextpdf.text.DocumentException;
import org.example.Queue.Subscriber;
import org.example.Services.GeneratorService;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) throws IOException, TimeoutException, DocumentException {

        GeneratorService generatorService = new GeneratorService();
        Subscriber.receive(generatorService);

    }
}