package com.vrtherapy.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

/**
 * Utility wrapper around Google Gson for JSON serialization and parsing.
 * Supports Java 8+ LocalDateTime via custom TypeAdapters.
 */
public final class JsonUtil {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                    LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .create();

    private JsonUtil() {
        // Utility constructor
    }

    /**
     * Serializes any Java object to a JSON string.
     */
    public static String toJson(Object object) {
        if (object == null) {
            return "{}";
        }
        try {
            return GSON.toJson(object);
        } catch (Exception e) {
            logger.error("Failed to serialize object to JSON: {}", e.getMessage(), e);
            return "{}";
        }
    }

    /**
     * Deserializes a JSON string into a Map of properties.
     */
    public static Map<String, Object> fromJsonToMap(String json) {
        if (json == null || json.trim().isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> map = GSON.fromJson(json, type);
            return map != null ? map : Collections.emptyMap();
        } catch (Exception e) {
            logger.error("Failed to parse JSON string to Map: {}", e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    /**
     * Deserializes a JSON string into a specific target class.
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return GSON.fromJson(json, clazz);
        } catch (Exception e) {
            logger.error("Failed to parse JSON into class {}: {}", clazz.getSimpleName(), e.getMessage(), e);
            return null;
        }
    }

    public static Gson getGson() {
        return GSON;
    }
}
