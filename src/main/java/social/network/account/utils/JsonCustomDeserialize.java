package social.network.account.utils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class JsonCustomDeserialize extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext ctxt)
            throws IOException {

        String birthDateStr = jsonParser.getText().trim();
        LocalDate birthDate;
        if (birthDateStr.startsWith("none") || birthDateStr.isBlank()) {
            birthDate = null;
        } else {
           Instant instant = Instant.parse(birthDateStr);
           birthDate = LocalDate.ofInstant(instant, ZoneId.systemDefault());
        }
        return birthDate;
    }
}
