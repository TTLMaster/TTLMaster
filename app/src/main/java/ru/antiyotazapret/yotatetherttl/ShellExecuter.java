package ru.antiyotazapret.yotatetherttl;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ShellExecuter {

    public String Executer(String command) {
        StringBuilder output = new StringBuilder();
        Process p;
        try {
            p = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append('\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

}