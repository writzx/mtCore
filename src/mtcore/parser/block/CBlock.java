package mtcore.parser.block;

import java.nio.ByteBuffer;

public abstract class CBlock {
    public EBlockType blockType;
    public EMethodType method;
    public CBlockID id;
    public short checksum;

    public void read(ByteBuffer bfr) throws CorruptedBlockException {
        blockType = EBlockType.parse(bfr.get());
        method = EMethodType.parse(bfr.get());
        id = new CBlockID();
        id.read(bfr);
        checksum = bfr.getShort();
    }

    public void write(ByteBuffer bfr) throws CorruptedBlockException {
        bfr.put(blockType.v());
        bfr.put(method.v());
        id.write(bfr);
        bfr.putShort(checksum);
    }

    private static CBlock factory(ByteBuffer bfr, EBlockType bType) throws UnknownBlockException, CorruptedBlockException {
        switch (bType) {
            case Authorization: return CAuthorizationBlock.factory(bfr);
            case Pairing: return CPairingBlock.factory(bfr);
            case Message: return new CMessageBlock();
            case Metadata: return new CMetadataBlock();
            case Data: return new CDataBlock();
            case Information: return new CInformationBlock();
            case Unknown: default: throw new UnknownBlockException("Unknown block", bType); // handle this in code
        }
    }

    public static CBlock factory(ByteBuffer bfr) throws UnknownBlockException, CorruptedBlockException {
        bfr.mark();
        byte b = bfr.get();
        bfr.reset();
        return factory(bfr, EBlockType.parse(b));
    }
}
