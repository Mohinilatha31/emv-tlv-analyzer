package nl.mohinilatha.emv;

import java.util.ArrayList;
import java.util.List;

/**
 * Decodes tag 9B, Transaction Status Information (TSI): 2 bytes showing which steps
 * of the transaction were actually performed (EMV Book 3, Annex C6). Byte 2 is reserved.
 */
public final class TsiDecoder {

    private static final String[] BYTE1 = {
            "Offline data authentication was performed",
            "Cardholder verification was performed",
            "Card risk management was performed",
            "Issuer authentication was performed",
            "Terminal risk management was performed",
            "Script processing was performed",
            null, null};

    public static List<String> decode(byte[] tsi) {
        if (tsi.length != 2) {
            throw new IllegalArgumentException("TSI must be 2 bytes, got " + tsi.length);
        }
        List<String> steps = new ArrayList<>();
        for (int bitIdx = 0; bitIdx < 8; bitIdx++) {
            if ((tsi[0] & (0x80 >> bitIdx)) != 0 && BYTE1[bitIdx] != null) {
                steps.add(BYTE1[bitIdx]);
            }
        }
        return steps;
    }

    public static List<String> decode(String hex) {
        return decode(Hex.toBytes(hex));
    }
}
