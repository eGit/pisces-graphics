/*
 * Pisces Graphics
 * Copyright (C) 2010 John Pritchard
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * CLASSPATH Exception
 * 
 * Linking this library statically or dynamically with other modules
 * is making a combined work based on this library. Thus, the terms
 * and conditions of the GNU General Public License cover the whole
 * combination.
 * 
 * As a special exception, the copyright holders of this library give
 * you permission to link this library with independent modules to
 * produce an executable, regardless of the license terms of these
 * independent modules, and to copy and distribute the resulting
 * executable under terms of your choice, provided that you also meet,
 * for each linked independent module, the terms and conditions of the
 * license of that module. An independent module is a module which is
 * not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the
 * library, but you are not obligated to do so. If you do not wish to
 * do so, delete this exception statement from your version.
 * 
 * The CLASSPATH Exception is discussed briefly at
 * <http://www.gnu.org/software/classpath/license.html>.
 */
/*
 * Copyright 2007 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */
package pisces.d;

public interface Surface
{
    /**
     * Constant indicating 8/8/8 RGB pixel data stored in an
     * <code>int</code> array.
     */
    public static final int TYPE_INT_RGB = 1;

    /**
     * Constant indicating 8/8/8/8 ARGB pixel data stored in an
     * <code>int</code> array.
     */
    public static final int TYPE_INT_ARGB = 2;

    /**
     * Constant indicating 8/8/8/8 ARGB alpha-premultiplied pixel data stored 
     * in a <code>int</code> array.
     */
    public static final int TYPE_INT_ARGB_PRE = 3;

    /**
     * Constant indicating 5/6/5 RGB pixel data stored in an
     * <code>short</code> array.
     */
    public static final int TYPE_USHORT_565_RGB = 8;

    /**
     * Constant indicating 8 bit grayscale pixel data stored in a
     * <code>byte</code> array.
     */
    public static final int TYPE_BYTE_GRAY = 10;


    public interface Sink
        extends Surface
    {
        public int getDataType();

        public Object getData();

        public interface Int
            extends Sink
        {
            public int[] getData();
        }

        public interface Short
            extends Sink
        {
            public short[] getData();
        }

        public interface Byte
            extends Sink
        {
            public byte[] getData();
        }

        public void blit(Surface ps, int srcX, int srcY, 
                         int dstX, int dstY, int width, int height, float opacity);
    
        public void blit(int[] argb, int offset, int scanLength, 
                         int x, int y, int width, int height, float opacity);
    }

    public int getWidth();
    
    public int getHeight();
    
    public void getRGB(int[] argb, int offset, int scanLength, 
            int x, int y, int width, int height);
    
    public void setRGB(int[] argb, int offset, int scanLength, 
            int x, int y, int width, int height);

}
