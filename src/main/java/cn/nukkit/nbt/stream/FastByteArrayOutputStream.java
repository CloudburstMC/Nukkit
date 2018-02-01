package cn.nukkit.nbt.stream;


/*
 * fastutil: Fast & compact type-specific collections for Java
 *
 * Copyright (C) 2003-2011 Sebastiano Vigna
 *
 *  This library is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as published by the Free
 *  Software Foundation; either version 2.1 of the License, or (at your option)
 *  any later version.
 *
 *  This library is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 *  for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 */

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/** Simple, fast byte-array output stream that exposes the backing array.
 *
 * <P>{@link java.io.ByteArrayOutputStream} is nice, but to get its content you
 * must generate each time a new object. This doesn't happen here.
 *
 * <P>This class will automatically enlarge the backing array, doubling its
 * size whenever new space is needed. The {@link #reset()} method will
 * mark the content as empty, but will not decrease the capacity
 *
 * @author Sebastiano Vigna
 */

public class FastByteArrayOutputStream extends OutputStream {

    public static final long ONEOVERPHI = 106039;

    /** The array backing the output stream. */
    public final static int DEFAULT_INITIAL_CAPACITY = 16;

    /** The array backing the output stream. */
    public byte[] array;

    /** The number of valid bytes in {@link #array}. */
    public int length;

    /** The current writing position. */
    private int position;

    /** Creates a new array output stream with an initial capacity of {@link #DEFAULT_INITIAL_CAPACITY} bytes. */
    public FastByteArrayOutputStream() {
        this( DEFAULT_INITIAL_CAPACITY );
    }

    /** Creates a new array output stream with a given initial capacity.
     *
     * @param initialCapacity the initial length of the backing array.
     */
    public FastByteArrayOutputStream( final int initialCapacity ) {
        array = new byte[ initialCapacity ];
    }

    /** Creates a new array output stream wrapping a given byte array.
     *
     * @param a the byte array to wrap.
     */
    public FastByteArrayOutputStream( final byte[] a ) {
        array = a;
    }

    /** Marks this array output stream as empty. */
    public void reset() {
        length = 0;
        position = 0;
    }

    public void write( final int b ) {
        if ( position == length ) {
            length++;
            if ( position == array.length ) array = grow( array, length );
        }
        array[ position++ ] = (byte)b;
    }

    public static void ensureOffsetLength( final int arrayLength, final int offset, final int length ) {
        if ( offset < 0 ) throw new ArrayIndexOutOfBoundsException( "Offset (" + offset + ") is negative" );
        if ( length < 0 ) throw new IllegalArgumentException( "Length (" + length + ") is negative" );
        if ( offset + length > arrayLength ) throw new ArrayIndexOutOfBoundsException( "Last index (" + ( offset + length ) + ") is greater than array length (" + arrayLength + ")" );
    }

    public static byte[] grow( final byte[] array, final int length ) {
        if ( length > array.length ) {
            final int newLength = (int)Math.min( Math.max( ( ONEOVERPHI * array.length ) >>> 16, length ), Integer.MAX_VALUE );
            final byte t[] =
                    new byte[ newLength ];
            System.arraycopy( array, 0, t, 0, array.length );
            return t;
        }
        return array;
    }

    public static byte[] grow( final byte[] array, final int length, final int preserve ) {
        if ( length > array.length ) {
            final int newLength = (int)Math.min( Math.max( ( ONEOVERPHI * array.length ) >>> 16, length ), Integer.MAX_VALUE );
            final byte t[] =
                    new byte[ newLength ];
            System.arraycopy( array, 0, t, 0, preserve );
            return t;
        }
        return array;
    }

    public void write( final byte[] b, final int off, final int len ) throws IOException {
        if ( position + len > array.length ) array = grow( array, position + len, position );
        System.arraycopy( b, off, array, position, len );
        if ( position + len > length ) length = position += len;
    }

    public void position( long newPosition ) {
        if ( position > Integer.MAX_VALUE ) throw new IllegalArgumentException( "Position too large: " + newPosition );
        position = (int)newPosition;
    }

    public long position() {
        return position;
    }

    public long length() throws IOException {
        return length;
    }

    public byte[] toByteArray() {
        if (position == array.length) return array;
        return Arrays.copyOfRange(array, 0, position);
    }
}