package nl.mohinilatha.emv;

/** Thrown when TLV data is malformed, for example cut off or with a wrong length. */
public class TlvException extends RuntimeException {
    public TlvException(String message) {
        super(message);
    }
}
