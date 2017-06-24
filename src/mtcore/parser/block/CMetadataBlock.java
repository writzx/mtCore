package mtcore.parser.block;

import mtcore.parser.metadata.CMetadata;

import java.nio.ByteBuffer;

public class CMetadataBlock extends CBlock {
    short metadataLength;
    CMetadata metadata;
    // todo add block address struct array

    @Override
    public void read(ByteBuffer bfr) throws CorruptedBlockException {
        super.read(bfr);
    }

    @Override
    public void write(ByteBuffer bfr) throws CorruptedBlockException {
        super.write(bfr);
    }

    @Override
    public int getLength() {
        return super.getLength() + Short.BYTES + metadata.getLength();
    }

    @Override
    public int getMaxDataLength() {
        return 0;
    }
}
