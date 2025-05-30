package com.cPlace.pixel.dto;

import com.cPlace.pixel.domain.Pixel;
import java.nio.ByteBuffer;

public record PixelMedia(short x, short y, byte color, int timeStamp) {

    public static PixelMedia from(Pixel pixel) {
        return new PixelMedia(
                (short) pixel.x(),
                (short) pixel.y(),
                (byte) pixel.color().ordinal(),
                (int) pixel.timeStamp()
        );
    }

    public byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.putShort(x);
        buffer.putShort(y);
        buffer.put(color);
        buffer.put((byte) 0);   // padding
        buffer.putInt(timeStamp);

        return buffer.array();
    }
}
