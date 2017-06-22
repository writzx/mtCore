package Parser.Block;

import java.nio.ByteBuffer;

public abstract class CBlock {
    EBlockType blockType;
    EMethodType method;
    CBlockID id;
    short checksum;

    public void read(ByteBuffer bfr) {
        blockType = EBlockType.parse(bfr.get());
        method = EMethodType.parse(bfr.get());
        id = new CBlockID();
        id.read(bfr);
        checksum = bfr.getShort();
    }

    public void write(ByteBuffer bfr) {
        bfr.put(blockType.v());
        bfr.put(method.v());
        id.write(bfr);
        bfr.putShort(checksum);
    }

    private static CBlock factory(ByteBuffer bfr, EBlockType bType) throws UnknownBlockException {
        switch (bType) {
            case Authorization: CAuthorizationBlock.factory(bfr);
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
        return factory(bfr, EBlockType.parse(b));
    }
}
