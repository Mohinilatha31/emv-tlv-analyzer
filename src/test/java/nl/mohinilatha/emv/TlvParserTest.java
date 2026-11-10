package nl.mohinilatha.emv;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TlvParserTest {

    private final TlvParser parser = new TlvParser();

    @Test
    @DisplayName("Parses a single primitive tag")
    void parsesPrimitiveTag() {
        List<Tlv> tlvs = parser.parse("9F270180");
        assertEquals(1, tlvs.size());
        assertEquals("9F27", tlvs.get(0).tag());
        assertEquals(1, tlvs.get(0).length());
        assertEquals("80", tlvs.get(0).valueHex());
    }

    @Test
    @DisplayName("Parses a PSE response with nested (constructed) tags")
    void parsesNestedPseResponse() {
        // SELECT 1PAY.SYS.DDF01 response
        List<Tlv> tlvs = parser.parse("6F1A840E315041592E5359532E4444463031A5088801025F2D02656E");
        Tlv fci = tlvs.get(0);
        assertEquals("6F", fci.tag());
        assertTrue(fci.isConstructed());
        assertEquals("1PAY.SYS.DDF01",
                new String(TlvParser.find(tlvs, "84").value()));
        assertEquals("02", TlvParser.find(tlvs, "88").valueHex());
        assertEquals("en", new String(TlvParser.find(tlvs, "5F2D").value()));
    }

    @Test
    @DisplayName("Finds the AID and label inside a contactless PPSE response")
    void parsesPpseResponse() {
        List<Tlv> tlvs = parser.parse(
                "6F2F840E325041592E5359532E4444463031A51DBF0C1A61184F07A0000000031010500A56495341204445424954870101");
        assertEquals("A0000000031010", TlvParser.find(tlvs, "4F").valueHex());
        assertEquals("VISA DEBIT", new String(TlvParser.find(tlvs, "50").value()));
        assertEquals("BF0C", TlvParser.find(tlvs, "BF0C").tag());
    }

    @Test
    @DisplayName("Ignores spaces and lower case in the input")
    void acceptsSpacesAndLowerCase() {
        List<Tlv> tlvs = parser.parse("9f 27 01 80");
        assertEquals("9F27", tlvs.get(0).tag());
    }

    @Test
    @DisplayName("Reads long-form lengths (0x81 xx)")
    void readsLongFormLength() {
        String value = "AB".repeat(130);
        List<Tlv> tlvs = parser.parse("9F1081" + "82" + value);
        assertEquals(130, tlvs.get(0).length());
    }

    @Test
    @DisplayName("Rejects data where the length is longer than the bytes left")
    void rejectsTruncatedValue() {
        TlvException e = assertThrows(TlvException.class, () -> parser.parse("9F260811223344"));
        assertTrue(e.getMessage().contains("9F26"));
    }

    @Test
    @DisplayName("Rejects a tag with no length byte")
    void rejectsMissingLength() {
        assertThrows(TlvException.class, () -> parser.parse("9F27"));
    }

    @Test
    @DisplayName("Rejects hex with an odd number of characters")
    void rejectsOddHex() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse("9F2701801"));
    }

    @Test
    @DisplayName("Rejects characters that are not hex")
    void rejectsNonHex() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse("9F27ZZ80"));
    }
}
