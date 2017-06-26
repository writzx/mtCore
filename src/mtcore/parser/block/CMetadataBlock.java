package mtcore.parser.block;

import mtcore.Constants;

import java.nio.ByteBuffer;

public class CMetadataBlock extends CBlock {
    short metadataBlockLength;
    byte[] metadataBlock;
    // CMetadata metadata;

    @Override
    public void read(ByteBuffer bfr) throws CorruptedBlockException {
        super.read(bfr);
        metadataBlockLength = bfr.getShort();
        if (metadataBlockLength > Constants.MAX_DATA_LENGTH || metadataBlockLength > bfr.remaining()) {
            throw new CorruptedBlockException(method, metadataBlockLength);
        }
        metadataBlock = new byte[metadataBlockLength];
        bfr.get(metadataBlock);
        // metadata = CMetadata.factory(bfr);
        // metadata.read(bfr);
    }

    @Override
    public void write(ByteBuffer bfr) throws CorruptedBlockException {
        metadataBlockLength = (short) metadataBlock.length;
        if (metadataBlockLength > Constants.MAX_DATA_LENGTH || metadataBlockLength > bfr.remaining()) {
            throw new CorruptedBlockException();
        }
        super.write(bfr);
        bfr.putShort(metadataBlockLength);
        bfr.put(metadataBlock);
        // metadata.write(bfr);
    }

    @Override
    public int getLength() {
        return super.getLength() + Short.BYTES + metadataBlock.length;
    }
}
