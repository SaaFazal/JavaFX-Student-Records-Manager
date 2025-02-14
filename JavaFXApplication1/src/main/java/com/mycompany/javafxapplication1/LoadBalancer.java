/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafxapplication1;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadBalancer {

    private static List<String> FILE_SERVERS = new ArrayList<>();
    private static List<String> fileMetadataDB = new ArrayList<>();
    private static final int CHUNK_SIZE = 1024 * 1024; // 1MB per chunk
    private static final ConcurrentHashMap<String, AtomicInteger> requestCount = new ConcurrentHashMap<>();
    private static SecretKey secretKey = generateKey();

    public LoadBalancer() {
        FILE_SERVERS = detectAvailableContainers();
        System.out.println("Available File Servers: " + FILE_SERVERS);
    }

    /**
     * Handles file upload by chunking, encrypting, and distributing the file across servers.
     */
    public void handleFileUpload(File file) {
        List<File> fileChunks = chunkFile(file);

        for (File chunk : fileChunks) {
            simulateArtificialDelay();  // ✅ Simulate network latency
            String assignedServer = distributeChunkToServer(chunk);
            saveChunkMetadata(chunk.getName(), assignedServer);
        }

        System.out.println("File upload and distribution completed.");
    }

    /**
     * Splits and encrypts file into chunks.
     */
    private List<File> chunkFile(File file) {
        List<File> fileChunks = new ArrayList<>();
        try {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            int chunkCount = (int) Math.ceil((double) fileBytes.length / CHUNK_SIZE);

            for (int i = 0; i < chunkCount; i++) {
                int start = i * CHUNK_SIZE;
                int end = Math.min(start + CHUNK_SIZE, fileBytes.length);
                byte[] chunk = new byte[end - start];
                System.arraycopy(fileBytes, start, chunk, 0, chunk.length);

                // **Encrypt the chunk**
                byte[] encryptedChunk = encrypt(chunk);

                File chunkFile = new File("chunk_" + file.getName() + "_" + i);
                Files.write(chunkFile.toPath(), encryptedChunk);
                fileChunks.add(chunkFile);
            }
        } catch (IOException e) {
            System.err.println("Error chunking the file: " + e.getMessage());
        }
        return fileChunks;
    }

    /**
     * Distributes the chunk to the least busy server.
     */
    private String distributeChunkToServer(File chunk) {
        if (FILE_SERVERS.isEmpty()) {
            throw new RuntimeException("No available file servers detected!");
        }

        String selectedServer = FILE_SERVERS.get(0);
        int minRequests = Integer.MAX_VALUE;

        for (String server : FILE_SERVERS) {
            requestCount.putIfAbsent(server, new AtomicInteger(0));
            int currentRequests = requestCount.get(server).get();

            if (currentRequests < minRequests) {
                minRequests = currentRequests;
                selectedServer = server;
            }
        }

        requestCount.get(selectedServer).incrementAndGet();
        System.out.println("Assigned " + chunk.getName() + " to " + selectedServer);
        return selectedServer;
    }

    /**
     * Saves file metadata in the simulated database.
     */
    private void saveChunkMetadata(String chunkName, String server) {
        String metadata = "Chunk: " + chunkName + " -> Server: " + server;
        fileMetadataDB.add(metadata);
        System.out.println("Saved metadata: " + metadata);
    }

    /**
     * Introduces artificial delay (30-90 seconds) to simulate real-world network latency.
     */
    private void simulateArtificialDelay() {
        Random random = new Random();
        int delay = 30 + random.nextInt(61);
        System.out.println("Simulating network delay of " + delay + " seconds...");

        try {
            Thread.sleep(delay * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Detects available file storage containers dynamically.
     */
    private List<String> detectAvailableContainers() {
        List<String> servers = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec("docker ps --format '{{.Names}}'");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("file-server")) {
                    servers.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return servers;
    }

    /**
     * AES encryption utility.
     */
    private static SecretKey generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            return keyGenerator.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("Error generating encryption key", e);
        }
    }

    private static byte[] encrypt(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    private static byte[] decrypt(byte[] encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(encryptedData);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting data", e);
        }
    }

    /**
     * Retrieves the metadata of stored files.
     */
    public List<String> getFileMetadata() {
        return fileMetadataDB;
    }

    public void requestCompleted(String server) {
        requestCount.get(server).decrementAndGet();
    }
}
