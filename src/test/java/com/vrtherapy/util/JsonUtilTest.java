package com.vrtherapy.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class JsonUtilTest {

    @Test
    @DisplayName("Should serialize object to JSON and parse back to Map")
    void testSerializationAndDeserialization() {
        Map<String, Object> source = Map.of(
                "environment", "CALM_BEACH",
                "intensity", 3,
                "guidedBreathing", true
        );

        String json = JsonUtil.toJson(source);
        assertNotNull(json);
        assertTrue(json.contains("\"environment\":\"CALM_BEACH\""));

        Map<String, Object> parsed = JsonUtil.fromJsonToMap(json);
        assertNotNull(parsed);
        assertEquals("CALM_BEACH", parsed.get("environment"));
        assertEquals(3, ((Number) parsed.get("intensity")).intValue());
        assertEquals(true, parsed.get("guidedBreathing"));
    }

    @Test
    @DisplayName("Should handle null and empty inputs safely")
    void testNullSafeHandling() {
        assertEquals("{}", JsonUtil.toJson(null));
        assertTrue(JsonUtil.fromJsonToMap(null).isEmpty());
        assertTrue(JsonUtil.fromJsonToMap("").isEmpty());
    }
}
