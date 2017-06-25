package mtcore.parser.block;

import mtcore.parser.metadata.CMetadata;

import java.nio.ByteBuffer;

public class CMetadataBlock extends CBlock {
    short metadataLength;
    CMetadata metadata;

    @Override
    public void read(ByteBuffer bfr) throws CorruptedBlockException {
        super.read(bfr);
        metadataLength = bfr.getShort();
        metadata = CMetadata.factory(bfr);
        metadata.read(bfr);
    }

    @Override
    public void write(ByteBuffer bfr) throws CorruptedBlockException {
        super.write(bfr);
        metadataLength = (short) metadata.getLength();
        bfr.putShort(metadataLength);
        metadata.write(bfr);
    }

    @Override
    public int getLength() {
        return super.getLength() + Short.BYTES + metadata.getLength();
    }
}
