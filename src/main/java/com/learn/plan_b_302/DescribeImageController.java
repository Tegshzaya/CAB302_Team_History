package com.learn.plan_b_302;

import com.google.gson.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Base64;
import java.util.Set;

public class DescribeImageController {

    @FXML private ImageView preview;
    @FXML private TextArea resultArea;
    @FXML private Button analyzeBtn;
    @FXML private CheckBox useBase64;
    @FXML private TextField urlField;  // Make sure your FXML has fx:id="urlField" and a "Use URL" button wired to #handleUseUrl

    private File chosenFile;

    // CONFIG
    private static final String MODEL = "gpt-4o-mini";   // vision-capable model

    private static final String OPENAI_API_KEY = "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";

    private static final String UA = "PlanB302-ArtApp/1.0 (+https://example.com; contact: you@example.com)";

    // OkHttp client w/ redirects + reasonable timeouts
    private static final OkHttpClient http = new OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .callTimeout(Duration.ofSeconds(60))
            .connectTimeout(Duration.ofSeconds(20))
            .readTimeout(Duration.ofSeconds(60))
            .build();

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // Allowed image types per API
    private static final Set<String> ALLOWED_MIME =
            Set.of("image/jpeg", "image/png", "image/gif", "image/webp");

    @FXML
    private void initialize() {
        analyzeBtn.setDisable(true);
        if (useBase64 != null) useBase64.setSelected(true); // base64 is best for local files
    }

    // UI HANDLERS

    @FXML
    public void handleChooseImage() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose an image");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.webp", "*.gif", "*.bmp")
        );
        File f = fc.showOpenDialog(preview.getScene().getWindow());
        if (f != null) {
            chosenFile = f;
            if (preview != null) preview.setImage(new Image(f.toURI().toString(), true));
            analyzeBtn.setDisable(false);
            if (resultArea != null) resultArea.clear();
            if (urlField != null) urlField.clear(); // clear URL if user picked a file
        }
    }

    @FXML
    public void handleUseUrl() {
        String url = urlField != null ? urlField.getText().trim() : "";
        if (!url.isBlank()) {
            chosenFile = null; // prefer URL path
            if (preview != null) preview.setImage(new Image(url, true));
            analyzeBtn.setDisable(false);
            if (resultArea != null) resultArea.clear();
        }
    }

    @FXML
    public void handleAnalyze() {
        try {
            // Guard: API key
            if (OPENAI_API_KEY == null || OPENAI_API_KEY.isBlank() || OPENAI_API_KEY.contains("REPLACE_ME")) {
                resultArea.setText("API key is missing. Temporarily hardcode it here or set OPENAI_API_KEY.");
                return;
            }

            // Determine source: URL or local file (then build data URI)
            String imageRef = null;
            String urlText = (urlField != null) ? urlField.getText().trim() : "";

            if (!urlText.isBlank()) {
                // Download URL -> validate -> convert to base64 data URI (works even if server blocks hotlinking)
                try {
                    byte[] bytes = fetchBytes(urlText);
                    String sniffed = sniffMimeFromBytes(bytes);
                    if (sniffed == null || !ALLOWED_MIME.contains(sniffed)) {
                        resultArea.setText(
                                "The link didn’t return a supported image (jpeg/png/gif/webp).\n" +
                                        "Tip: open the image in a new tab and copy the direct image URL, or upload the file."
                        );
                        return;
                    }
                    imageRef = toDataUri(sniffed, bytes);
                } catch (IOException ex) {
                    resultArea.setText(
                            "Could not download image from the URL.\n" +
                                    "Make sure it’s a public direct image, or upload the file.\n" + ex.getMessage()
                    );
                    return;
                }
            } else if (chosenFile != null) {
                if (useBase64 != null && useBase64.isSelected()) {
                    String mime = detectMime(chosenFile.getName());
                    byte[] bytes = Files.readAllBytes(chosenFile.toPath());
                    imageRef = toDataUri(mime, bytes);
                } else {
                    resultArea.setText("For local files, enable “Send as Base64” or paste an HTTPS image URL.");
                    return;
                }
            } else {
                resultArea.setText("Provide a file or paste an image URL, then click Analyze.");
                return;
            }

            JsonObject req = buildResponsesRequest(imageRef);

            RequestBody body = RequestBody.create(
                    gson.toJson(req), MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/responses")
                    .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response resp = http.newCall(request).execute()) {
                String respBody = (resp.body() != null) ? resp.body().string() : "";
                if (!resp.isSuccessful()) {
                    resultArea.setText("HTTP " + resp.code() + ":\n" + respBody);
                    return;
                }

                resultArea.setText(pretty(respBody));

                // Optional: try to surface the structured JSON (depends on model output)
                try {
                    JsonObject root = JsonParser.parseString(respBody).getAsJsonObject();
                    JsonArray outputs = root.getAsJsonArray("output");
                    if (outputs != null && outputs.size() > 0) {
                        String extracted = tryExtractStructured(outputs);
                        if (extracted != null) {
                            resultArea.appendText("\n\nParsed result:\n" + pretty(extracted));
                        }
                    }
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            resultArea.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // HELPERS

    // Simple filename-based detection (used for local files)
    private String detectMime(String name) {
        String lower = name.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".bmp")) return "image/bmp"; // not accepted by API; we’ll relabel to jpeg below
        return "image/jpeg";
    }

    // Convert raw bytes to data URI (relabel BMP as JPEG to avoid server rejection)
    private String toDataUri(String mime, byte[] bytes) {
        if ("image/bmp".equals(mime)) mime = "image/jpeg";
        String b64 = Base64.getEncoder().encodeToString(bytes);
        return "data:" + mime + ";base64," + b64;
    }

    // Fetch URL with headers so CDNs like Wikimedia don’t 403 us
    private byte[] fetchBytes(String url) throws IOException {
        Request req = new Request.Builder()
                .url(url)
                .get()
                .addHeader("User-Agent", UA)
                .addHeader("Accept", "image/*")
                .build();

        try (Response r = http.newCall(req).execute()) {
            if (!r.isSuccessful() || r.body() == null) {
                throw new IOException("GET failed: HTTP " + r.code() + " " + r.message());
            }
            return r.body().bytes();
        }
    }

    // Very light signature check for jpeg/png/gif/webp
    private String sniffMimeFromBytes(byte[] bytes) {
        if (bytes.length >= 3 && (bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8) return "image/jpeg"; // JPEG
        if (bytes.length >= 8 && bytes[0]==(byte)0x89 && bytes[1]=='P' && bytes[2]=='N' && bytes[3]=='G') return "image/png"; // PNG
        if (bytes.length >= 6 && bytes[0]=='G' && bytes[1]=='I' && bytes[2]=='F') return "image/gif"; // GIF
        if (bytes.length >= 12 && bytes[0]=='R' && bytes[1]=='I' && bytes[2]=='F' && bytes[3]=='F'
                && bytes[8]=='W' && bytes[9]=='E' && bytes[10]=='B' && bytes[11]=='P') return "image/webp"; // WEBP
        return null;
    }

    private String pretty(String json) {
        try {
            return gson.toJson(JsonParser.parseString(json));
        } catch (Exception e) {
            return json;
        }
    }

    private String tryExtractStructured(JsonArray outputs) {
        for (JsonElement el : outputs) {
            String s = el.toString();
            if (s.contains("\"description\"") && s.contains("\"year_guess\"") && s.contains("\"location_guess\"")) {
                return s;
            }
        }
        return null;
    }

    /**
     * Build the Responses API request:
     * - content[0]: input_text instruction
     * - content[1]: input_image with image_url STRING (either https URL or data: URI)
     * - text.format: json_schema (with additionalProperties=false)
     */
    private JsonObject buildResponsesRequest(String imageRef) {
        // ---- JSON Schema ----
        JsonObject schema = new JsonObject();
        schema.addProperty("type", "object");
        schema.addProperty("additionalProperties", false); // required

        JsonObject props = new JsonObject();
        props.add("description", prop("string", "Concise description of the image"));
        props.add("year_guess", prop("string", "Estimated year or range, e.g., \"~1889\" or \"unknown\""));
        props.add("location_guess", prop("string", "Likely place (city, museum or country) or \"unknown\""));

        JsonObject confidence = new JsonObject();
        confidence.addProperty("type", "number");
        confidence.addProperty("description", "0..1 confidence in the year/location guess");
        confidence.addProperty("minimum", 0);
        confidence.addProperty("maximum", 1);
        props.add("confidence", confidence);

        schema.add("properties", props);

        JsonArray required = new JsonArray();
        required.add(new JsonPrimitive("description"));
        required.add(new JsonPrimitive("year_guess"));
        required.add(new JsonPrimitive("location_guess"));
        required.add(new JsonPrimitive("confidence"));
        schema.add("required", required);

        // ---- message content: instruction + image ----
        JsonArray content = new JsonArray();

        JsonObject instruction = new JsonObject();
        instruction.addProperty("type", "input_text");
        instruction.addProperty("text",
                "You are an art/location describer. Given the image, return JSON with: " +
                        "description, year_guess (estimated), location_guess (likely place), confidence (0..1). " +
                        "If uncertain, say \"unknown\" and lower confidence.");
        content.add(instruction);

        JsonObject img = new JsonObject();
        img.addProperty("type", "input_image");
        img.addProperty("image_url", imageRef); // string: https://... or data:image/...;base64,...
        content.add(img);

        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.add("content", content);

        JsonArray input = new JsonArray();
        input.add(userMsg);

        // text.format at top level
        JsonObject format = new JsonObject();
        format.addProperty("type", "json_schema");
        format.addProperty("name", "ImageDescription");
        format.add("schema", schema);

        JsonObject text = new JsonObject();
        text.add("format", format);

        // final request
        JsonObject request = new JsonObject();
        request.addProperty("model", MODEL);
        request.add("input", input);
        request.add("text", text);

        // Debug print (optional): see exactly what we send
        System.out.println(gson.toJson(request));
        return request;
    }

    private JsonObject prop(String type, String desc) {
        JsonObject p = new JsonObject();
        p.addProperty("type", type);
        p.addProperty("description", desc);
        return p;
    }
}
