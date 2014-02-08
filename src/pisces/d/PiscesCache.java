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

/**
 * Fixed point rendering cache used by {@link Renderer}.
 *
 */
public final class PiscesCache {

    private static final float ROWAA_RLE_FACTOR = 1.2f;
    private static final float TOUCHED_FACTOR = 1.2f;
    private static final int MIN_TOUCHED_LEN = 32;


    boolean isValid = false;

    int bboxX0, bboxY0, bboxX1, bboxY1;

    byte[] rowAARLE = null;
    int alphaRLELength = 0;
    int[] rowOffsetsRLE = null;

    int alphaWidth = 0;
    int alphaHeight = 0;

    int[] minTouched = null;

    
    public PiscesCache() {
        super();
    }


    public synchronized boolean isValid() {
        return isValid;
    }

    public synchronized void dispose() {
        alphaWidth = alphaHeight = 0;

        rowAARLE = null;
        alphaRLELength = 0;

        minTouched = null;
        rowOffsetsRLE = null;

        isValid = false;
    }

    void addRLERun(byte val, int runLen) {
        reallocRowAARLE(alphaRLELength + 2);
        rowAARLE[alphaRLELength++] = val;
        rowAARLE[alphaRLELength++] = (byte)runLen;
    }

    void addRow(int minX, int offset) {
        reallocRowInfo(alphaHeight + 1);
        minTouched[alphaHeight] = minX;
        rowOffsetsRLE[alphaHeight] = offset;
        ++alphaHeight;
    }
    private void reallocRowAARLE(int newLength) {
        if (rowAARLE == null) {
            rowAARLE = new byte[newLength];
        } else if (rowAARLE.length < newLength) {
            int len = Math.max(newLength,
                               (int)(rowAARLE.length*ROWAA_RLE_FACTOR));
            byte[] newRowAARLE = new byte[len];
            System.arraycopy(rowAARLE, 0, newRowAARLE, 0, rowAARLE.length);
            rowAARLE = newRowAARLE;
        }
    }
    private void reallocRowInfo(int newHeight) {
        if (minTouched == null) {
            int len = Math.max(newHeight, MIN_TOUCHED_LEN);
            minTouched = new int[len];
            rowOffsetsRLE = new int[len];
        } else if (minTouched.length < newHeight) {
            int len = Math.max(newHeight,
                               (int)(minTouched.length*TOUCHED_FACTOR));
            int[] newMinTouched = new int[len];
            int[] newRowOffsetsRLE = new int[len];
            System.arraycopy(minTouched, 0, newMinTouched, 0,
                             minTouched.length);
            System.arraycopy(rowOffsetsRLE, 0, newRowOffsetsRLE, 0,
                             rowOffsetsRLE.length);
            minTouched = newMinTouched;
            rowOffsetsRLE = newRowOffsetsRLE;
        }
    }
}
