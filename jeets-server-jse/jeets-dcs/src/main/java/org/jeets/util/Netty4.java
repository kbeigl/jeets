package org.jeets.util;

import java.util.Arrays;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

public final class Netty4 {

    private Netty4() { }

    /**
     * Helper to inspect a Netty4 ByteBuf without modifying it.
     * <p>
     * TODO Currently using sysout statements!
     */
    public static void inspectByteBuf(ByteBuf buffer) {
        System.out.println("===== inspect ByteBuf " + buffer.toString());
//      i.e.inspect ByteBuf PooledUnsafeDirectByteBuf(ridx: 0, widx: 51, cap: 1024)
//      traccar.RupProtoDecoder:  UnpooledHeapByteBuf(ridx: 0, widx: 50, cap: 50/50)
        System.out.print("readable - " + buffer.isReadable());
        System.out.print("/ writable - " + buffer.isWritable());
        System.out.println("/ readonly - " + buffer.isReadOnly());
//      display wrong refCnt !? inspect buffer > refCnt
        int referenceCount = buffer.refCnt();
        System.out.println("reference count: " + referenceCount);
        if (referenceCount == 0) {
            System.out.println("this buffer has been deallocated");
        }
//      ReferenceCountUtil refCnt; .. ?
//      get current ridx widx
        int readerIdx = buffer.readerIndex();
        int writerIdx = buffer.writerIndex();
//      readXxx changes pointers (and creates new ByteBuf!?!), change to getXxx ?
//      readBytes javadoc: Transfers this buffer's data to a newly created buffer starting at the current readerIndex
//      and increases the readerIndexby the number of the transferred bytes (= length).
//      The returned buffer's readerIndex and writerIndex are 0 and length respectively.
//        System.out.println("   HEX: " + ByteBufUtil.hexDump(buffer.readBytes(buffer.readableBytes())).toUpperCase());
//      System.out.println("   HEX: " + ByteBufUtil.hexDump(buffer).toUpperCase());
        System.out.println("   HEX: " + ByteBufUtil.hexDump(buffer));
        if (buffer.hasArray()) {
//          System.out.println("This buffer has a backing byte array: ");
            System.out.println(" array: " + Arrays.toString(buffer.array()));
        } else {
            System.out.println("This buffer has no backing byte array!");
//          always print this, or only in 'else'
//          buffer.getCharSequence(index, length, charset)
        }
//      reset ridx widx
        buffer.readerIndex(readerIdx);
        buffer.writerIndex(writerIdx);
        System.out.println("=====");
    }
}
