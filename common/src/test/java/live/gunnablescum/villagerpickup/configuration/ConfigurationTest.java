package live.gunnablescum.villagerpickup.configuration;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigurationTest {

    @TempDir
    Path tempDir;

    private Path writeOldConfigFile(Map<String, Boolean> values) throws IOException {
        Gson gson = new Gson();
        Map<String, List<?>> object = Map.of("sections", List.of(Map.of("values", values)));
        Path configFile = tempDir.resolve("villager-pickup.json");
        try(FileWriter writer = new FileWriter(configFile.toFile())) {
            writer.write(gson.toJson(object));
        }
        return configFile;
    }

    @Test
    void loadOldConfigFormat() throws IOException {
        Path configFile = writeOldConfigFile(Map.of(
                "enable_villager_pickup", false,
                "allow_villager_rename_with_anvil", true
        ));

        Configuration configuration = new Configuration(configFile.toFile());

        // Check that the values remain
        assertFalse(configuration.settings.get(ConfigurationElement.ENABLE_VILLAGER_PICKUP));
        assertTrue(configuration.settings.get(ConfigurationElement.ALLOW_VILLAGER_ANVIL_RENAME));

        String savedConfig = Files.readString(configFile);
        assertTrue(savedConfig.contains("\"enable_villager_pickup\""));
        assertTrue(savedConfig.contains("\"allow_villager_anvil_rename\""));

        // Check that neither sections nor old keys appear
        assertFalse(savedConfig.contains("\"allow_villager_rename_with_anvil\""));
        assertFalse(savedConfig.contains("\"sections\""));
   }
}
