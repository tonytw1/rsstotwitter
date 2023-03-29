package nz.gen.wellington.rsstotwitter.twitter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class TwitterServiceTest {

    @Test
    public void canMapTwitterCreatedAtToDate() {
        LocalDateTime createdAt = LocalDateTime.of(2023, 2, 3, 1, 1, 1);
        new DateTime(createdAt.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli(), DateTimeZone.UTC);
    }

}
