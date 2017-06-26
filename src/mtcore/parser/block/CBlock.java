package mtcore.parser.block;

import mtcore.parser.IAbstractStructure;

import java.nio.ByteBuffer;

public abstract class CBlock implements IAbstractStructure {
    public EBlockType blockType;
    public EMethodType method;
    public CBlockID id;
    public short checksum; // checksum of DECRYPTED DATA

    @Override
    public int getLength() {
        return Byte.BYTES + Byte.BYTES + id.getLength() + Short.BYTES;
    }

    @Override
    public void read(ByteBuffer bfr) throws CorruptedBlockException {
        blockType = EBlockType.parse(bfr.get());
        method = EMethodType.parse(bfr.get());
        id = new CBlockID();
        id.read(bfr);
        checksum = bfr.getShort();
    }

    @Override
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
            case Log: return new CLogBlock();
            case Unknown: default: throw new UnknownBlockException("Unknown block", bType); // handle this in code
        }
    }

    public static CBlock factory(ByteBuffer bfr) throws UnknownBlockException, CorruptedBlockException {
        bfr.mark();
        byte b = bfr.get();
        CBlock block = factory(bfr, EBlockType.parse(b));
        bfr.reset();
        return block;
    }
}
