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
 * An INT ARGB {@link Surface$Sink}.
 */
public class NativeSurface 
    extends Object
    implements Surface.Sink.Int
{

    
    private final int width;
    private final int height;
    private final int[] data;


    public NativeSurface(int width, int height) {
        this(null, width, height);
    }
    public NativeSurface(int[] data, int width, int height) {
        super();
        this.data = (data != null) ? data : new int[width * height];
        this.width = width;
        this.height = height;
    }

    

    /**
     * @see Surface
     */
    public final int getWidth() {
        return width;
    }
    /**
     * @see Surface
     */
    public final int getHeight() {
        return height;
    }
    /**
     * @see Surface$Sink
     */
    public int getDataType(){

        return TYPE_INT_ARGB;
    }    
    /**
     * @see Surface$Sink
     */
    public int[] getData() {

        return data;
    }
    /**
     * @see Surface
     */
    public final void getRGB(int[] argb, int offset, int scanLength, 
                             int x, int y, int width, int height)
    {
        if ((argb == data) &&
            (offset == 0) &&
            (scanLength == this.width) &&
            (x == 0) && (y == 0) && 
            (width == this.width) &&
            (height == this.height))
        {
            return;
        }
        else {
            int dstX = 0;
            int dstY = 0;
        
            if (x < 0) {
                dstX -= x;
                width += x;
                x = 0;
            }

            if (y < 0) {
                dstY -= y;
                height += y;
                y = 0;
            }

            if ((x + width) > this.width) {
                width = this.width - x;
            }

            if ((y + height) > this.height) {
                height = this.height - y;
            }

            if ((width > 0) && (height > 0)) {

                offset += dstY * scanLength + dstX;    

                Copy(argb, offset, scanLength, 
                     data, y * this.width + x, this.width, 
                     width, height);
            }
        }
    }
    /**
     * @see Surface
     */
    public final void setRGB(int[] argb, int offset, int scanLength, 
                             int x, int y, int width, int height)
    {
        if ((argb == data) &&
            (offset == 0) &&
            (scanLength == this.width) &&
            (x == 0) && (y == 0) &&
            (width == this.width) &&
            (height == this.height))
        {
            return;
        }
        else {
            int srcX = 0;
            int srcY = 0;
        
            if (x < 0) {
                srcX -= x;
                width += x;
                x = 0;
            }

            if (y < 0) {
                srcY -= y;
                height += y;
                y = 0;
            }

            if ((x + width) > this.width) {
                width = this.width - x;
            }

            if ((y + height) > this.height) {
                height = this.height - y;
            }

            if ((width > 0) && (height > 0)) {

                offset += srcY * scanLength + srcX;    

                Copy(data, y * this.width + x, this.width,
                     argb, offset, scanLength, 
                     width, height);
            }
        }
    }
    /**
     * @see Surface$Sink
     */
    public void blit(Surface ps, 
                     int srcX, int srcY, 
                     int dstX, int dstY, 
                     int width, int height, 
                     float opacity)
    {
        int srcW = ps.getWidth();
        int srcH = ps.getHeight();
        int dstW = getWidth();
        int dstH = getHeight();
            
        if (srcX < 0) {
            dstX -= srcX;
            width += srcX;
            srcX = 0;
        }
            
        if (srcY < 0) {
            dstY -= srcY;
            height += srcY;
            srcY = 0;
        }
            
        if ((srcX + width) > srcW) {
            width = srcW - srcX;
        }
            
        if ((srcY + height) > srcH) {
            height = srcH - srcY;
        }
        /*
         */
        if ((width < 0) || (height < 0) || (opacity == 0))

            return;

        else if (ps instanceof NativeSurface) {

            NativeSurface ns = (NativeSurface)ps;                

            this.blit( ns.getData(), srcY * srcW + srcX, srcW, dstX, dstY, 
                       width, height, opacity);
        }
        else {
            if (dstX < 0) {
                srcX -= dstX;
                width += dstX;
                dstX = 0;
            }
            
            if (dstY < 0) {
                srcY -= dstY;
                height += dstY;
                dstY = 0;
            }

            if ((dstX + width) > dstW) {
                width = dstW - dstX;
            }
            
            if ((dstY + height) > dstH) {
                height = dstH - dstY;
            }
            
            if ((width > 0) && (height > 0)) {
                int size = width * height;
                int[] srcRGB = new int[size];

                ps.getRGB(srcRGB, 0, width, srcX, srcY, width, height);

                this.blit(srcRGB, 0, width, dstX, dstY, width, height, 
                          opacity);
            }
        }
    }
    /**
     * @see Surface$Sink
     */
    public void blit(int[] argb, int offset, int scanLength, 
                     int x, int y, int width, int height, float opacity)
    {
        int srcX = 0;
        int srcY = 0;
        
        if (x < 0) {
            srcX -= x;
            width += x;
            x = 0;
        }

        if (y < 0) {
            srcY -= y;
            height += y;
            y = 0;
        }

        if ((x + width) > this.width) {
            width = this.width - x;
        }

        if ((y + height) > this.height) {
            height = this.height - y;
        }

        if ((width > 0) && (height > 0)) {

            offset += srcY * scanLength + srcX;

            Draw(data, y * this.width + x, this.width,
                 argb, offset, scanLength, 
                 width, height, opacity);
        }
    }
    
    /**
     * Pixel copy procedure
     */
    private static void Copy(int[] dstRGB, int dstOffset, int dstScanLength,
                             int[] srcRGB, int srcOffset, int srcScanLength,
                             int width, int height)
    {
        int srcScanRest = srcScanLength - width;
        int dstScanRest = dstScanLength - width;

        for (; height > 0; --height) {

            for (int w = width; w > 0; --w) {

                dstRGB[dstOffset++] = srcRGB[srcOffset++];
            }
            srcOffset += srcScanRest;
            dstOffset += dstScanRest;
        }
    }
    /**
     * Pixel copy / alpha- blend procedure
     */
    private static void Draw(int[] dstRGB, int dstOffset, int dstScanLength,
                             int[] srcRGB, int srcOffset, int srcScanLength,
                             int width, int height, float opacity)
    {
        int srcScanRest = srcScanLength - width;
        int dstScanRest = dstScanLength - width;

        int op = (int)(0x100 * opacity);

        for (; height > 0; --height) {

            for (int w = width; w > 0; --w) {

                int srcA = ((srcRGB[srcOffset] >> 24) & 0xff) * op;

                if (srcA == 0xff00) {

                    dstRGB[dstOffset++] = srcRGB[srcOffset++];        
                }
                else {
                    int dstVal = dstRGB[dstOffset];
                    int dstA = (dstVal >> 24) & 0xff;

                    int anom = 255 * srcA;
                    int bnom = dstA * (0xff00 - srcA);

                    int denom = anom + bnom;
                    if (denom > 0) {
                        long recip = ((long)1 << 32) / denom;
                        long fa = anom * recip;
                        long fb = bnom * recip;

                        int srcVal = srcRGB[srcOffset];

                        int oalpha = ((257 * denom) >> 24) & 0xff;

                        int ored = (int)((fa * ((srcVal >> 16) & 0xff) + 
                                          fb * ((dstVal >> 16) & 0xff)) >> 32);

                        int ogreen = (int)((fa * ((srcVal >> 8) & 0xff) + 
                                            fb * ((dstVal >> 8) & 0xff)) >> 32);

                        int oblue = (int)((fa * (srcVal & 0xff) + 
                                           fb * (dstVal & 0xff)) >> 32);

                        dstRGB[dstOffset] = ((oalpha << 24) | 
                                             (ored << 16) |
                                             (ogreen << 8) | 
                                             oblue);
                    }
                    ++srcOffset;
                    ++dstOffset;
                }
            }
            srcOffset += srcScanRest;
            dstOffset += dstScanRest;
        }
    }
}
