package Parser.Block;

import java.nio.ByteBuffer;

public abstract class CBlock {
    EBlockType blockType;
    CBlockID id;
    EMethodType method;
    short checksum;
    short dataLength; // less than 0x2000 as of now
    byte[] data;      // length = dataLength


    public void read(ByteBuffer bfr) {
        blockType = EBlockType.parse(bfr.get());
        id = new CBlockID();
        id.read(bfr);
        method = EMethodType.parse(bfr.get());
        checksum = bfr.getShort();
        dataLength = bfr.getShort(); // check here for max value and throw exception (using bfr.remaining())
                                     // here one corrupted block will mean the remaining buffer is corrupted
                                     // handle that in exception catch block
        data = new byte[dataLength];
        bfr.get(data);               // no possibility error since already checked above
    }

    public void write(ByteBuffer bfr) {
        // check here if the buffer can hold the block by comparing (dataLength + header_size) and bfr.remaining() and throw exception
        // in catch block exception should be handled in such a way that this block is placed in the next buffer
        bfr.put(blockType.v());
        id.write(bfr);
        bfr.put(method.v());
        bfr.putShort(checksum);
        dataLength = (short) data.length;
        bfr.putShort(dataLength);
        bfr.put(data);
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
