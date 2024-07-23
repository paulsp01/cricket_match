package com.cricket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Iterator;

public class Main {
    public static void main(String[] args) {
        String url = "https://api.cuvora.com/car/partner/cricket-data";
        String apiKey = "test-creds@2320";

        try {
            String jsonResponse = getJsonResponse(url, apiKey);
            if (jsonResponse != null) {
                computeCricketData(jsonResponse);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getJsonResponse(String url, String apiKey) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);
        request.addHeader("apiKey", apiKey);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            return EntityUtils.toString(response.getEntity());
        }
    }

    private static void computeCricketData(String jsonResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        Iterator<JsonNode> elements = rootNode.elements();

        int highestScore = 0;
        String highestScoreTeam = "";
        int matchesWith300PlusScore = 0;

        while (elements.hasNext()) {
            JsonNode match = elements.next();
            String t1 = match.get("t1").asText();
            String t2 = match.get("t2").asText();
            int t1s = parseScore(match.get("t1s").asText());
            int t2s = parseScore(match.get("t2s").asText());

            if (t1s > highestScore) {
                highestScore = t1s;
                highestScoreTeam = t1;
            }

            if (t2s > highestScore) {
                highestScore = t2s;
                highestScoreTeam = t2;
            }

            if (t1s + t2s > 300) {
                matchesWith300PlusScore++;
            }
        }

        System.out.println("Highest Score : " + highestScore + " and Team Name is : " + highestScoreTeam);
        System.out.println("Number Of Matches with total 300 Plus Score : " + matchesWith300PlusScore);
    }

    private static int parseScore(String score) {
        if (score == null || score.isEmpty()) {
            return 0;
        }

        String[] parts = score.split("/");
        return Integer.parseInt(parts[0]);
    }
}
