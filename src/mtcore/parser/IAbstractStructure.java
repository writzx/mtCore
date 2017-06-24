package mtcore.parser;

import mtcore.parser.block.CorruptedBlockException;
import mtcore.parser.block.UnknownBlockException;

import java.nio.ByteBuffer;

public interface IAbstractStructure {
    void read(ByteBuffer bfr) throws UnknownBlockException, CorruptedBlockException;
    void write(ByteBuffer bfr) throws UnknownBlockException, CorruptedBlockException;
    int getLength();
    int getMaxLength();
}
