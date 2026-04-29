package org.example.sport;

import com.google.gson.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Main extends Application {

    private static JsonElement jsonData;
    private TreeView<String> treeView;
    private TextArea detailsArea;

    @Override
    public void start(Stage stage) {

        // 🔍 Suchfeld
        TextField searchField = new TextField();
        searchField.setPromptText("Team suchen...");

        // 🔄 Reload Button
        Button reloadButton = new Button("Neu laden");

        // 🔥 Beste Quote Button
        Button bestOddsButton = new Button("🔥 Beste Quote finden");

        // 🐶 Underdog Button
        Button underdogButton = new Button("🐶 Underdogs finden");

        // 📄 Detailanzeige
        detailsArea = new TextArea();
        detailsArea.setEditable(false);
        detailsArea.setPrefHeight(150);

        // 🌳 TreeView
        treeView = new TreeView<>();
        treeView.setShowRoot(false);

        buildTree(jsonData);

        // 👉 Klick auf Tree → Details anzeigen
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                detailsArea.setText(newVal.getValue());
            }
        });

        // 🔍 Suche
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterTree(newVal.toLowerCase());
        });

        // 🔄 Reload
        reloadButton.setOnAction(e -> {
            loadData();
            buildTree(jsonData);
        });

        // 🔥 Beste Quote
        bestOddsButton.setOnAction(e -> {
            String result = findBestOdds();
            detailsArea.setText(result);
        });

        // 🐶 Underdogs
        underdogButton.setOnAction(e -> {
            String result = findUnderdogs();
            detailsArea.setText(result);
        });

        VBox root = new VBox(10, searchField, reloadButton, bestOddsButton, underdogButton, treeView, detailsArea);
        root.setPadding(new Insets(10));

        VBox.setVgrow(treeView, Priority.ALWAYS);

        Scene scene = new Scene(root, 900, 650);
        stage.setScene(scene);
        stage.setTitle("API Odds Viewer");
        stage.show();
    }

    @Override
    public void init() {
        loadData();
    }

    // 🌐 API laden
    private void loadData() {
        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://odds.p.rapidapi.com/v4/sports/upcoming/odds?regions=us&oddsFormat=decimal&markets=h2h%2Cspreads&dateFormat=iso")
                    .get()
                    .addHeader("x-rapidapi-key", "abe0c70d46msh50190eeb79a8928p1d3348jsn08a45e2bcf57")
                    .addHeader("x-rapidapi-host", "odds.p.rapidapi.com")
                    .addHeader("Content-Type", "application/json")
                    .build();


            try (Response response = client.newCall(request).execute()) {
                String jsonString = response.body().string();
                jsonData = JsonParser.parseString(jsonString);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🌳 Tree bauen
    private void buildTree(JsonElement element) {
        TreeItem<String> rootItem = createTree(element, "JSON");
        treeView.setRoot(rootItem);
    }

    // 🔍 Filter
    private void filterTree(String keyword) {
        if (keyword.isEmpty()) {
            buildTree(jsonData);
            return;
        }

        TreeItem<String> filteredRoot = filterNode(createTree(jsonData, "JSON"), keyword);
        treeView.setRoot(filteredRoot);
    }

    private TreeItem<String> filterNode(TreeItem<String> item, String keyword) {
        TreeItem<String> result = new TreeItem<>(item.getValue());

        for (TreeItem<String> child : item.getChildren()) {
            TreeItem<String> filteredChild = filterNode(child, keyword);

            if (!filteredChild.getChildren().isEmpty() ||
                    filteredChild.getValue().toLowerCase().contains(keyword)) {
                result.getChildren().add(filteredChild);
            }
        }

        return result;
    }

    // 🔥 Beste Quote finden
    private String findBestOdds() {
        double bestOdds = 0;
        String bestMatch = "";
        String bestTeam = "";

        JsonArray events = jsonData.getAsJsonArray();

        for (JsonElement event : events) {
            JsonObject obj = event.getAsJsonObject();

            String home = obj.get("home_team").getAsString();
            String away = obj.get("away_team").getAsString();

            JsonArray bookmakers = obj.getAsJsonArray("bookmakers");

            for (JsonElement bookmaker : bookmakers) {
                JsonObject bm = bookmaker.getAsJsonObject();
                JsonArray markets = bm.getAsJsonArray("markets");

                for (JsonElement market : markets) {
                    JsonObject m = market.getAsJsonObject();

                    if (m.get("key").getAsString().equals("h2h")) {

                        JsonArray outcomes = m.getAsJsonArray("outcomes");

                        for (JsonElement outcome : outcomes) {
                            JsonObject o = outcome.getAsJsonObject();

                            double price = o.get("price").getAsDouble();
                            String team = o.get("name").getAsString();

                            if (price > bestOdds) {
                                bestOdds = price;
                                bestTeam = team;
                                bestMatch = home + " vs " + away;
                            }
                        }
                    }
                }
            }
        }

        return "🔥 BESTE QUOTE 🔥\n\n"
                + "Match: " + bestMatch + "\n"
                + "Tipp: " + bestTeam + "\n"
                + "Quote: " + bestOdds;
    }

    // 🐶 Underdogs finden
    private String findUnderdogs() {
        StringBuilder result = new StringBuilder("🐶 UNDERDOGS (hohe Quoten)\n\n");

        JsonArray events = jsonData.getAsJsonArray();

        for (JsonElement event : events) {
            JsonObject obj = event.getAsJsonObject();

            String home = obj.get("home_team").getAsString();
            String away = obj.get("away_team").getAsString();

            JsonArray bookmakers = obj.getAsJsonArray("bookmakers");

            for (JsonElement bookmaker : bookmakers) {
                JsonObject bm = bookmaker.getAsJsonObject();
                JsonArray markets = bm.getAsJsonArray("markets");

                for (JsonElement market : markets) {
                    JsonObject m = market.getAsJsonObject();

                    if (m.get("key").getAsString().equals("h2h")) {

                        JsonArray outcomes = m.getAsJsonArray("outcomes");

                        for (JsonElement outcome : outcomes) {
                            JsonObject o = outcome.getAsJsonObject();

                            double price = o.get("price").getAsDouble();
                            String team = o.get("name").getAsString();

                            if (price >= 2.5) {
                                result.append("Match: ")
                                        .append(home).append(" vs ").append(away).append("\n")
                                        .append("Underdog: ").append(team).append("\n")
                                        .append("Quote: ").append(price).append("\n\n");
                            }
                        }
                    }
                }
            }
        }

        if (result.toString().equals("🐶 UNDERDOGS (hohe Quoten)\n\n")) {
            return "Keine Underdogs gefunden 😅";
        }

        return result.toString();
    }

    // 🌳 JSON → Tree
    private TreeItem<String> createTree(JsonElement element, String key) {
        TreeItem<String> item;

        if (element.isJsonObject()) {
            item = new TreeItem<>(key + " { }");

            element.getAsJsonObject().entrySet().forEach(entry -> {
                item.getChildren().add(createTree(entry.getValue(), entry.getKey()));
            });

        } else if (element.isJsonArray()) {
            item = new TreeItem<>(key + " [ ]");

            int index = 0;
            for (JsonElement el : element.getAsJsonArray()) {
                item.getChildren().add(createTree(el, "[" + index++ + "]"));
            }

        } else {
            item = new TreeItem<>(key + ": " + element.getAsString());
        }

        return item;
    }

    public static void main(String[] args) {
        launch(args);
    }
}