package com.example.demo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SimpleClient {

    public static void main(String[] args) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String baseUrl = "http://localhost:8080/api/persons";

            // 1. Eine neue Person erstellen (POST)
            String jsonPayload = "{\"name\": \"Max Mustermann\", \"age\": 30}";
            
            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            System.out.println("Erstelle neue Person...");
            HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status (POST): " + postResponse.statusCode());
            System.out.println("Antwort: " + postResponse.body());

            // 2. Alle Personen abrufen (GET)
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .GET()
                    .build();

            System.out.println("\nRufe alle Personen ab...");
            HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
            
            if (getResponse.statusCode() == 200) {
                System.out.println("Antwort vom Server (GET):");
                System.out.println(getResponse.body());
            } else {
                System.out.println("Fehler beim Abrufen: " + getResponse.statusCode());
            }

        } catch (Exception e) {
            System.err.println("Fehler bei der Kommunikation: " + e.getMessage());
            System.err.println("Stelle sicher, dass die DemoApplication auf Port 8080 läuft.");
        }
    }
}
