package nl.mohinilatha.emv;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses BER-TLV data, the format EMV uses for card responses and for ISO 8583 field 55.
 *
 * Rules implemented (EMV Book 3, Annex B):
 * - Tag: if the lowest 5 bits of the first byte are all 1 (0x1F), the tag continues in the next byte.
 *   Each next byte with its top bit set (0x80) means "one more tag byte follows".
 * - Tag: bit 6 of the first byte (0x20) set means the tag is constructed (its value is more TLVs).
 * - Length: below 0x80 the byte is the length itself. 0x81 means "length is in the next 1 byte",
 *   0x82 means "length is in the next 2 bytes", and so on.
 */
public final class TlvParser {

    public List<Tlv> parse(String hex) {
        return parse(Hex.toBytes(hex));
    }

    public List<Tlv> parse(byte[] data) {
        return parse(data, 0, data.length);
    }

    private List<Tlv> parse(byte[] data, int start, int end) {
        List<Tlv> result = new ArrayList<>();
        int pos = start;
        while (pos < end) {
            // Padding bytes 00 or FF can appear between TLV objects; skip them.
            int b = data[pos] & 0xFF;
            if (b == 0x00 || b == 0xFF) {
                pos++;
                continue;
            }

            // 1. Read the tag
            int tagStart = pos;
            boolean constructed = (b & 0x20) != 0;
            pos++;
            if ((b & 0x1F) == 0x1F) {
                do {
                    if (pos >= end) {
                        throw new TlvException("Tag starting at byte " + tagStart + " is cut off");
                    }
                } while ((data[pos++] & 0x80) != 0);
            }
            String tag = Hex.fromBytes(copy(data, tagStart, pos));

            // 2. Read the length
            if (pos >= end) {
                throw new TlvException("Tag " + tag + " has no length byte");
            }
            int lenByte = data[pos++] & 0xFF;
            int length;
            if (lenByte < 0x80) {
                length = lenByte;
            } else {
                int numBytes = lenByte & 0x7F;
                if (numBytes == 0 || numBytes > 3) {
                    throw new TlvException("Tag " + tag + " has an unsupported length format 0x"
                            + Integer.toHexString(lenByte).toUpperCase());
                }
                if (pos + numBytes > end) {
                    throw new TlvException("Length of tag " + tag + " is cut off");
                }
                length = 0;
                for (int i = 0; i < numBytes; i++) {
                    length = (length << 8) | (data[pos++] & 0xFF);
                }
            }

            // 3. Read the value
            if (pos + length > end) {
                throw new TlvException("Tag " + tag + " says length " + length
                        + " but only " + (end - pos) + " bytes are left");
            }
            byte[] value = copy(data, pos, pos + length);
            List<Tlv> children = constructed ? parse(data, pos, pos + length) : List.of();
            result.add(new Tlv(tag, length, value, children));
            pos += length;
        }
        return result;
    }

    /** Finds the first tag with this name anywhere in the tree, or null. */
    public static Tlv find(List<Tlv> tlvs, String tag) {
        for (Tlv t : tlvs) {
            if (t.tag().equalsIgnoreCase(tag)) {
                return t;
            }
            Tlv inChild = find(t.children(), tag);
            if (inChild != null) {
                return inChild;
            }
        }
        return null;
    }

    private static byte[] copy(byte[] data, int from, int to) {
        byte[] out = new byte[to - from];
        System.arraycopy(data, from, out, 0, out.length);
        return out;
    }
}
