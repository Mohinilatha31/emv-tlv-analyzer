package nl.mohinilatha.emv;

import java.util.List;

/**
 * One TLV (Tag-Length-Value) object.
 * A "constructed" tag (for example 6F or 70) contains other TLV objects as its children.
 */
public record Tlv(String tag, int length, byte[] value, List<Tlv> children) {

    public boolean isConstructed() {
        return children != null && !children.isEmpty();
    }

    public String valueHex() {
        return Hex.fromBytes(value);
    }
}
