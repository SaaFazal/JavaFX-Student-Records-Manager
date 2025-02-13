/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafxapplication1;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class LoadBalancer {

    private static final String[] FILE_SERVERS = {
        "file-server-1", "file-server-2", "file-server-3", "file-server-4"
    };

    // Simulating MySQL database storage of chunk metadata 
    private static List<String> fileMetadataDB = new ArrayList<>();

    public LoadBalancer() {
        // Initialize load balancer, possibly connect to the database
    }

    /**
     * Handles file upload by chunking the file and distributing it across file servers.
     * 
     * @param file The file to be uploaded.
     */
    public void handleFileUpload(File file) {
        // Step 1: Split the file into chunks
        List<File> fileChunks = chunkFile(file);

        // Step 2: Distribute the chunks to available file servers
        for (File chunk : fileChunks) {
            // Apply a scheduling algorithm to decide where to store the chunk
            String assignedServer = distributeChunkToServer(chunk);
            // Store the chunk's metadata in the "database" (for now, just a list)
            saveChunkMetadata(chunk.getName(), assignedServer);
        }

        System.out.println("File upload and distribution completed.");
    }

    /**
     * Splits the file into smaller chunks.
     * 
     * @param file The file to be chunked.
     * @return List of chunks.
     */
    private List<File> chunkFile(File file) {
        List<File> fileChunks = new ArrayList<>();
        try {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            int chunkSize = 1024 * 1024; // 1 MB per chunk
            int chunkCount = (int) Math.ceil((double) fileBytes.length / chunkSize);

            for (int i = 0; i < chunkCount; i++) {
                int start = i * chunkSize;
                int end = Math.min(start + chunkSize, fileBytes.length);
                byte[] chunk = new byte[end - start];
                System.arraycopy(fileBytes, start, chunk, 0, chunk.length);

                // Create chunk file (this is just a placeholder for chunk file creation)
                File chunkFile = new File("chunk_" + file.getName() + "_" + i);
                Files.write(chunkFile.toPath(), chunk);
                fileChunks.add(chunkFile);
            }
        } catch (IOException e) {
            System.err.println("Error chunking the file: " + e.getMessage());
        }

        return fileChunks;
    }

    /**
     * Distributes the chunk to an appropriate file server based on a scheduling algorithm.
     * 
     * @param chunk The file chunk.
     * @return The name of the assigned file server.
     */
    private String distributeChunkToServer(File chunk) {
        // Implement scheduling algorithm, for now, using random distribution
        Random random = new Random();
        return FILE_SERVERS[random.nextInt(FILE_SERVERS.length)];
    }

    /**
     * Saves the chunk metadata (e.g., which server the chunk is stored on).
     * 
     * @param chunkName The name of the chunk file.
     * @param server The server where the chunk is stored.
     */
    private void saveChunkMetadata(String chunkName, String server) {
        String metadata = "Chunk: " + chunkName + " -> Server: " + server;
        fileMetadataDB.add(metadata);  // Simulate saving to a database
        System.out.println("Saved metadata: " + metadata);
    }

    /**
     * Retrieves the chunk metadata (to be used for download, etc.).
     * 
     * @return A list of metadata.
     */
    public List<String> getFileMetadata() {
        return fileMetadataDB;
    }
}
