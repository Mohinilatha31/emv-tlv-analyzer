package nl.mohinilatha.emv;

import java.util.List;

/** Turns parsed TLV data into a readable report, decoding TVR, TSI and CID where present. */
public final class Analyzer {

    private final TlvParser parser = new TlvParser();

    public String analyze(String hex) {
        List<Tlv> tlvs = parser.parse(hex);
        StringBuilder out = new StringBuilder();
        out.append("TLV structure\n");
        out.append("-------------\n");
        for (Tlv t : tlvs) {
            print(t, 0, out);
        }

        appendDecoded(out, tlvs);
        return out.toString();
    }

    private void print(Tlv t, int depth, StringBuilder out) {
        String indent = "  ".repeat(depth);
        out.append(indent).append(t.tag()).append(" (").append(EmvTags.name(t.tag())).append(")")
                .append(" len=").append(t.length());
        if (t.isConstructed()) {
            out.append("\n");
            for (Tlv child : t.children()) {
                print(child, depth + 1, out);
            }
        } else {
            out.append(" value=").append(t.valueHex());
            String text = asText(t.value());
            if (text != null) {
                out.append(" \"").append(text).append("\"");
            }
            out.append("\n");
        }
    }

    private void appendDecoded(StringBuilder out, List<Tlv> tlvs) {
        Tlv tvr = TlvParser.find(tlvs, "95");
        Tlv tsi = TlvParser.find(tlvs, "9B");
        Tlv cid = TlvParser.find(tlvs, "9F27");
        if (tvr == null && tsi == null && cid == null) {
            return;
        }
        out.append("\nDecoded\n");
        out.append("-------\n");
        if (cid != null && cid.length() == 1) {
            out.append("Cryptogram (9F27): ").append(CidDecoder.decode(cid.value()[0])).append("\n");
        }
        if (tvr != null && tvr.length() == 5) {
            List<String> findings = TvrDecoder.decode(tvr.value());
            out.append("TVR (95): ");
            out.append(findings.isEmpty() ? "all checks passed (no bits set)\n" : "\n");
            findings.forEach(f -> out.append("  - ").append(f).append("\n"));
        }
        if (tsi != null && tsi.length() == 2) {
            out.append("TSI (9B) steps performed:\n");
            TsiDecoder.decode(tsi.value()).forEach(s -> out.append("  - ").append(s).append("\n"));
        }
    }

    /** Shows the value as text when it is printable ASCII, e.g. "VISA DEBIT" in tag 50. */
    private static String asText(byte[] value) {
        if (value.length < 2) {
            return null;
        }
        for (byte b : value) {
            if (b < 0x20 || b > 0x7E) {
                return null;
            }
        }
        return new String(value, java.nio.charset.StandardCharsets.US_ASCII);
    }
}
