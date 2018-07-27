package net.eliahrebstock;

import fr.novapost.lib.test.ResourceLoader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit test for constructing a HAProxyRecord object from a CSV stream
 */
class HAProxyRecordTest {

    @Test
    void constuctorTest() {
        InputStream csv = ResourceLoader.getStream("test.csv");
        try {
            long test = csv.skip(2); // skip first #
            if (test < 2) {
                throw new IOException();
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        InputStreamReader in = new InputStreamReader(csv);
        Iterable<CSVRecord> records = null;

        try {
            records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }

        ArrayList<HAProxyRecord> pojoRecords = new ArrayList<>();
        for (CSVRecord record : records) {
            HAProxyRecord rec = new HAProxyRecord(record);
            pojoRecords.add(rec);
        }
        System.out.println(pojoRecords.toString());
    }
}
