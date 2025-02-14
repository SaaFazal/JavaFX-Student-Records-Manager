package com.mycompany.jsch;

import com.jcraft.jsch.*;
import java.util.Scanner;

public class ScpTo {
    private static final String REMOTE_HOST = "172.18.0.3";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "ntu-user";
    private static final int REMOTE_PORT = 22;
    private static final int SESSION_TIMEOUT = 10000;
    private static final int CHANNEL_TIMEOUT = 5000;

    public static void main(String[] args) {

        String localFile = "test.txt";
        String remoteFile = "/root/afile.txt";

        Session jschSession = null;

        try {

            JSch jsch = new JSch();
            jsch.setKnownHosts("/home/mkyong/.ssh/known_hosts");

            // Set the StrictHostKeyChecking option to "no" to automatically answer "yes" to the prompt
            jschSession = jsch.getSession(USERNAME, REMOTE_HOST, REMOTE_PORT);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            jschSession.setConfig(config);

            // authenticate using password
            jschSession.setPassword(PASSWORD);

            // 10 seconds session timeout
            jschSession.connect(SESSION_TIMEOUT);

            Channel sftp = jschSession.openChannel("sftp");

            // 5 seconds timeout
            sftp.connect(CHANNEL_TIMEOUT);

            ChannelSftp channelSftp = (ChannelSftp) sftp;

            // transfer file from local to remote server
            channelSftp.put(localFile, remoteFile);

            // Ask the user if they want to delete the remote file
            System.out.print("Do you want to delete the remote file? (y/n): ");
            Scanner scanner = new Scanner(System.in);
            String userInput = scanner.nextLine().trim().toLowerCase();

            if ("y".equals(userInput)) {
                // Delete the remote file
                channelSftp.rm(remoteFile);
                System.out.println("Remote file deleted.");
            } else {
                System.out.println("Remote file not deleted.");
            }

            channelSftp.exit();

        } catch (JSchException | SftpException e) {
            e.printStackTrace();
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }

        System.out.println("Done");
    }
}
