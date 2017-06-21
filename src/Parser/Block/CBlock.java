package Parser.Block;

import java.nio.ByteBuffer;

public abstract class CBlock {
    EBlockType blockType;
    CBlockID id;
    EMethodType method;
    short checksum;

    public void read(ByteBuffer bfr) {
        blockType = EBlockType.parse(bfr.get());
        id = new CBlockID();
        id.read(bfr);
        method = EMethodType.parse(bfr.get());
        checksum = bfr.getShort();
    }

    public void write(ByteBuffer bfr) {
        bfr.put(blockType.v());
        id.write(bfr);
        bfr.put(method.v());
        bfr.putShort(checksum);
    }

    private static CBlock factory(EBlockType bType) throws UnknownBlockException {
        switch (bType) {
            case Authorization: return new CAuthorizationBlock();
            case Pairing: return new CPairingBlock();
            case Message: return new CMessageBlock();
            case Metadata: return new CMetadataBlock();
            case Data: return new CDataBlock();
            case Unknown: default: throw new UnknownBlockException("Unknown Block", bType); // handle this in code
        }
    }

    public static CBlock factory(ByteBuffer bfr) throws UnknownBlockException {
        bfr.mark();
        byte b = bfr.get();
        bfr.reset();
        return factory(EBlockType.parse(b));
    }
}
