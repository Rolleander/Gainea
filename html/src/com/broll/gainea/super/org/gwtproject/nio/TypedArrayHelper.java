/*******************************************************************************
 * Compatibility adapter between protobuf-gwt's Elemental2 buffers and
 * libGDX's GWT java.nio emulation.
 ******************************************************************************/
package org.gwtproject.nio;

import elemental2.core.ArrayBuffer;
import elemental2.core.ArrayBufferView;
import elemental2.core.Uint8Array;
import java.nio.ByteBuffer;
import java.nio.HasArrayBufferView;
import jsinterop.base.Js;

public final class TypedArrayHelper {

    private TypedArrayHelper() {
    }

    public static ByteBuffer wrap(ArrayBuffer source) {
        return copy(new Uint8Array(source));
    }

    public static ByteBuffer wrap(ArrayBufferView source) {
        return copy(new Uint8Array(source.buffer, source.byteOffset, source.byteLength));
    }

    public static ArrayBufferView unwrap(ByteBuffer source) {
        if (source instanceof HasArrayBufferView) {
            return Js.uncheckedCast(((HasArrayBufferView) source).getTypedArray());
        }

        ByteBuffer copy = source.duplicate();
        copy.clear();
        Uint8Array target = new Uint8Array(copy.remaining());
        for (int i = 0; copy.hasRemaining(); i++) {
            target.setAt(i, (double) (copy.get() & 0xff));
        }
        return target;
    }

    private static ByteBuffer copy(Uint8Array source) {
        ByteBuffer target = ByteBuffer.allocateDirect(source.length);
        for (int i = 0; i < source.length; i++) {
            target.put((byte) source.getAt(i).intValue());
        }
        target.flip();
        return target;
    }

    /** Retained for gwt-nio API compatibility. */
    public interface Wrapper {
        @Deprecated
        ByteBuffer wrap(ArrayBuffer arrayBuffer);
    }
}
