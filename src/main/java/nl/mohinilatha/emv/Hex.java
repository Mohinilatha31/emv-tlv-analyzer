package nl.mohinilatha.emv;

/** Small helpers to convert between hex strings and bytes. */
public final class Hex {

    private Hex() {
    }

    /** Turns "9F 26 08" or "9f2608" into bytes. Spaces are ignored. */
    public static byte[] toBytes(String hex) {
        if (hex == null) {
            throw new IllegalArgumentException("Hex input is null");
        }
        String clean = hex.replaceAll("\\s+", "").toUpperCase();
        if (clean.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex input must have an even number of characters");
        }
        if (!clean.matches("[0-9A-F]*")) {
            throw new IllegalArgumentException("Hex input contains characters that are not 0-9 or A-F");
        }
        byte[] out = new byte[clean.length() / 2];
        for (int i = 0; i < out.length; i++) {
            out[i] = (byte) Integer.parseInt(clean.substring(2 * i, 2 * i + 2), 16);
        }
        return out;
    }

    public static String fromBytes(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b & 0xFF));
        }
        return sb.toString();
    }
}
