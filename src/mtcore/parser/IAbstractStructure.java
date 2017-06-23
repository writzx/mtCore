package mtcore.parser;

import java.nio.ByteBuffer;

public interface IAbstractStructure {
    void read(ByteBuffer bfr);
    void write(ByteBuffer bfr);
    int getLength();
}
