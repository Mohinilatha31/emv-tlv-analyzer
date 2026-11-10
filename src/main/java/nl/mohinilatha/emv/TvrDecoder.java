package nl.mohinilatha.emv;

import java.util.ArrayList;
import java.util.List;

/**
 * Decodes tag 95, Terminal Verification Results (TVR): 5 bytes, where each bit set to 1
 * means a check during the transaction failed or needs attention (EMV Book 3, Annex C5).
 * In testing, the TVR is usually the first place to look when a transaction is declined.
 */
public final class TvrDecoder {

    // MEANINGS[byte][bit], bit index 0 = bit 8 (0x80) ... 7 = bit 1 (0x01). null = RFU (not used).
    private static final String[][] MEANINGS = {
            { // Byte 1 - offline data authentication
                    "Offline data authentication was not performed",
                    "SDA failed",
                    "ICC data missing",
                    "Card appears on terminal exception file",
                    "DDA failed",
                    "CDA failed",
                    "SDA selected",
                    null},
            { // Byte 2 - processing restrictions
                    "ICC and terminal have different application versions",
                    "Expired application",
                    "Application not yet effective",
                    "Requested service not allowed for card product",
                    "New card",
                    null, null, null},
            { // Byte 3 - cardholder verification
                    "Cardholder verification was not successful",
                    "Unrecognised CVM",
                    "PIN Try Limit exceeded",
                    "PIN entry required and PIN pad not present or not working",
                    "PIN entry required, PIN pad present, but PIN was not entered",
                    "Online PIN entered",
                    null, null},
            { // Byte 4 - terminal risk management
                    "Transaction exceeds floor limit",
                    "Lower consecutive offline limit exceeded",
                    "Upper consecutive offline limit exceeded",
                    "Transaction selected randomly for online processing",
                    "Merchant forced transaction online",
                    null, null, null},
            { // Byte 5 - issuer authentication and scripts
                    "Default TDOL used",
                    "Issuer authentication failed",
                    "Script processing failed before final GENERATE AC",
                    "Script processing failed after final GENERATE AC",
                    null, null, null, null}
    };

    public static List<String> decode(byte[] tvr) {
        if (tvr.length != 5) {
            throw new IllegalArgumentException("TVR must be 5 bytes, got " + tvr.length);
        }
        List<String> findings = new ArrayList<>();
        for (int byteIdx = 0; byteIdx < 5; byteIdx++) {
            for (int bitIdx = 0; bitIdx < 8; bitIdx++) {
                int mask = 0x80 >> bitIdx;
                if ((tvr[byteIdx] & mask) != 0) {
                    String meaning = MEANINGS[byteIdx][bitIdx];
                    String where = "byte " + (byteIdx + 1) + " bit " + (8 - bitIdx);
                    findings.add(meaning != null ? meaning + " (" + where + ")" : "RFU bit set (" + where + ")");
                }
            }
        }
        return findings;
    }

    public static List<String> decode(String hex) {
        return decode(Hex.toBytes(hex));
    }
}
