package ru.ifmo.lang.ketov.t10;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    private static class ClearbackgroundHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            URL hp = new URL(host);
            System.out.println("Protocol: " + hp.getProtocol());
            System.out.println("Port: " + hp.getPort());
            System.out.println("Host: " + hp.getHost());

            URLConnection hpCon = hp.openConnection();
            System.out.println("Date: " + hpCon.getDate());
            System.out.println("Type: " + hpCon.getContentType());


            BufferedImage image = ImageIO.read(hpCon.getInputStream());

            String response = exchange.getRequestURI().getQuery();

            String cl = "";
            int clInt = 0;
            if (response.length() >= 44) {
                cl = response.substring(47);
                clInt = Integer.parseInt(cl);
                System.out.println(response.substring(41));
            }


            for (int i = 0; i < image.getHeight(); i++) {
                for (int j = 0; j < image.getWidth(); j++) {
                    if (clInt != 0 && image.getRGB(i, j) == clInt) {
                        image.setRGB(i, j, new Color(238, 231, 11, 100).getRGB());
                    } else {
                        if (image.getRGB(i, j) == Color.WHITE.getRGB()) {
                            image.setRGB(i, j, new Color(255, 0, 0, 0).getRGB());
                        }
                    }
                }
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            System.out.println(response.substring(7));
            exchange.sendResponseHeaders(200, outputStream.toByteArray().length);
            final OutputStream responseBody = exchange.getResponseBody();
            ImageIO.write(image, "png", exchange.getResponseBody());

            responseBody.write(response.getBytes());

            responseBody.close();
            exchange.close();
        }

    }

    static final String host = "http://mrgr.net/static/grumpy.png";

    public static void main(String[] args) {
        try {

            final HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/clear_background", new ClearbackgroundHandler());
            server.start();

        } catch (MalformedURLException e) {
            System.out.println("Error: " + e);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }
}