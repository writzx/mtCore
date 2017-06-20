package Parser.Block;

import java.nio.ByteBuffer;

public abstract class CBlock {
    EBlockType BlockType;

    public void read(ByteBuffer bfr) {
        BlockType = EBlockType.parse(bfr.get());
    }

    public void write(ByteBuffer bfr) {
        bfr.put(BlockType.v());
    }

    public static CBlock factory(EBlockType bType) throws UnknownBlockException {
        switch (bType) {
            case Authorization: return new CAuthorizationBlock();
            case Pairing: return new CPairingBlock();
            case Message: return new CMessageBlock();
            case Metadata: return new CMetadataBlock();
            case Data: return new CDataBlock();
            case Unknown: default: throw new UnknownBlockException("Unknown Block", bType); // handle this in code
        }
    }

    public static CBlock initBlock(ByteBuffer bfr) throws UnknownBlockException {
        bfr.mark();
        byte b = bfr.get();
        bfr.reset();
        return factory(EBlockType.parse(b));
    }
}
