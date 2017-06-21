package Parser.Block;

import java.nio.ByteBuffer;

public class CBlockID {
    byte group;
    byte code;

    public void read(ByteBuffer bfr) {
        group = bfr.get();
        code = bfr.get();
    }
    public void write(ByteBuffer bfr) {
        bfr.put(group);
        bfr.put(code);
    }
}
