package nl.mohinilatha.emv;

/**
 * Decodes tag 9F27, Cryptogram Information Data (CID): bits 8-7 say which cryptogram
 * the card returned to GENERATE AC (EMV Book 3, section 6.5.5.4).
 */
public final class CidDecoder {

    public static String decode(byte cid) {
        return switch ((cid & 0xC0) >> 6) {
            case 0 -> "AAC - Application Authentication Cryptogram: the card declined";
            case 1 -> "TC - Transaction Certificate: the card approved offline";
            case 2 -> "ARQC - Authorisation Request Cryptogram: the card asks to go online to the issuer";
            default -> "RFU - reserved value, not valid";
        };
    }

    public static String decode(String hex) {
        byte[] bytes = Hex.toBytes(hex);
        if (bytes.length != 1) {
            throw new IllegalArgumentException("CID must be 1 byte, got " + bytes.length);
        }
        return decode(bytes[0]);
    }
}
