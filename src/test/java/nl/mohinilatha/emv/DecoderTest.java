package nl.mohinilatha.emv;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DecoderTest {

    @Test
    @DisplayName("TVR with no bits set means all checks passed")
    void tvrAllClear() {
        assertTrue(TvrDecoder.decode("0000000000").isEmpty());
    }

    @Test
    @DisplayName("TVR flags 'floor limit exceeded' (byte 4 bit 8)")
    void tvrFloorLimit() {
        List<String> findings = TvrDecoder.decode("0000008000");
        assertEquals(1, findings.size());
        assertTrue(findings.get(0).startsWith("Transaction exceeds floor limit"));
    }

    @Test
    @DisplayName("TVR finds several problems at once")
    void tvrSeveralBits() {
        // byte 1 bit 8: ODA not performed; byte 2 bit 7: expired; byte 3 bit 6: PIN try limit exceeded
        List<String> findings = TvrDecoder.decode("8040200000");
        assertEquals(3, findings.size());
        assertTrue(findings.get(0).startsWith("Offline data authentication was not performed"));
        assertTrue(findings.get(1).startsWith("Expired application"));
        assertTrue(findings.get(2).startsWith("PIN Try Limit exceeded"));
    }

    @Test
    @DisplayName("TVR must be exactly 5 bytes")
    void tvrWrongLength() {
        assertThrows(IllegalArgumentException.class, () -> TvrDecoder.decode("0000"));
    }

    @Test
    @DisplayName("TSI E800 lists the four steps performed")
    void tsiSteps() {
        List<String> steps = TsiDecoder.decode("E800");
        assertEquals(List.of(
                "Offline data authentication was performed",
                "Cardholder verification was performed",
                "Card risk management was performed",
                "Terminal risk management was performed"), steps);
    }

    @ParameterizedTest(name = "CID {0} -> {1}")
    @CsvSource({
            "00, AAC",
            "40, TC",
            "80, ARQC",
            "C0, RFU"
    })
    void cidTypes(String cid, String expectedStart) {
        assertTrue(CidDecoder.decode(cid).startsWith(expectedStart));
    }

    @Test
    @DisplayName("Report for the built-in example explains why it went online")
    void analyzerReport() {
        String report = new Analyzer().analyze(Main.EXAMPLE);
        assertTrue(report.contains("ARQC"));
        assertTrue(report.contains("Transaction exceeds floor limit"));
        assertTrue(report.contains("Terminal risk management was performed"));
    }
}
