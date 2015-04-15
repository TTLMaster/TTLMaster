package ru.antiyotazapret.yotatetherttl;

import java.io.BufferedReader;
import java.io.InputStreamReader;

class ShellExecutor {

    public String execute(String command) {
        StringBuilder output = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            try {
                process.waitFor();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append('\n');
                    }
                } finally {
                    reader.close();
                }
            } finally {
                process.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

}