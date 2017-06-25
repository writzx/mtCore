package mtcore.parser.metadata;

import mtcore.parser.block.CBlockID;
import mtcore.parser.block.CorruptedBlockException;
import mtcore.parser.block.UnknownBlockException;
import mtcore.parser.message.CLink;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class CMultipartMetadata extends CMetadata {
    CBlockID nextBlockID; // next MultiPartMetadata BlockID, 0x0000 if all the parts fit in this block.
    short partLength; // array length: parts.length * part.getLength()
    CPart[] parts;

    @Override
    public void read(ByteBuffer bfr) throws UnknownBlockException, CorruptedBlockException {
        super.read(bfr);
        nextBlockID = new CBlockID();
        nextBlockID.read(bfr);

        CPart part;
        List<CPart> partL = new ArrayList<>();
        for (short off = 0; off < partLength; off += part.getLength()) {
            part = new CPart();
            part.read(bfr);
            partL.add(part);
        }
        parts = partL.toArray(parts);
    }

    @Override
    public void write(ByteBuffer bfr) throws UnknownBlockException, CorruptedBlockException {
        super.write(bfr);
        nextBlockID.write(bfr);
        if (parts.length > 0) {
            partLength = (short) (parts.length + parts[0].getLength());
        } else {
            partLength = 0;
        }
        bfr.putShort(partLength);
        for (CPart part : parts) {
            part.write(bfr);
        }
    }

    @Override
    public int getLength() {
        short plen;
        if (parts.length > 0) {
            plen = (short) (parts.length + parts[0].getLength());
        } else {
            plen = 0;
        }
        return super.getLength() + nextBlockID.getLength() + Short.BYTES + plen;
    }
}
