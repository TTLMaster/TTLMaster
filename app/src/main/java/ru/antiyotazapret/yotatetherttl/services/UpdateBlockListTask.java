package ru.antiyotazapret.yotatetherttl.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import ru.antiyotazapret.yotatetherttl.TtlApplication;

public class UpdateBlockListTask extends Task<String,Set<String>> {

    @Override
    Set<String> action(String p) {

        Set<String> out = new HashSet<>();
        URL url;

        try {
            url = new URL(p);
        } catch (MalformedURLException e) {
            TtlApplication.Loge(e.toString());
            setException(e);
            return null;
        }

        try {

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            url.openStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                try {
                    InetAddress[] addresses = InetAddress.getAllByName(inputLine);
                    for (InetAddress addr : addresses) {
                        out.add(addr.getHostAddress());
                        TtlApplication.Logi(String.format("%s -> %s", inputLine, addr.getHostAddress()));
                    }
                } catch (UnknownHostException e) {
                    TtlApplication.Logi(e.toString());
                }
            }
            in.close();

        } catch (IOException e) {
            TtlApplication.Logi(e.toString());
            setException(e);
            return null;

        }

        return out;
    }
}
