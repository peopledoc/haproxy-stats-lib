package net.eliahrebstock;

import de.vandermeer.asciitable.AsciiTable;
import fr.novapost.lib.test.ResourceLoader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit test for the Checker.
 */
class CheckerTest {

    @Test
    void test() {
        // Test using apache commons csv for parsing
        InputStream csv = ResourceLoader.getStream("test.csv");
        InputStreamReader in = new InputStreamReader(csv);
        Iterable<CSVRecord> records = null;
        try {
            //records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
            records = CSVFormat.RFC4180.parse(in);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }

        AsciiTable at = new AsciiTable();

        for (CSVRecord record : records) {
            at.addRule();
            List<String> cols = new ArrayList<>();
            for (String s : record) {
                cols.add(s);
            }
            at.addRow(cols);
        }
        at.addRule();
        String s = at.render(1000);
        System.out.println(s);
    }
}
