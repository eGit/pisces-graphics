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
 * The backend blender is {@link Blit}.  This front end to the blender
 * computes an alpha plane (from Renderer field rowAA to Blit argument
 * alphaData) between a sink (source) and a surface (target) for
 * subpixel blending (antialiasing).
 * 
 * @see Pisces
 * @see Blit
 */
public final class Renderer
    extends RendererBase
{
    /*
     * Initial edge list size
     * IMPL_NOTE - restore size after growth
     */
    public static final int INITIAL_EDGES = 1000;
    /*
     * Recommended maximum scratchpad sizes.  The arrays will grow
     * larger if needed, but when finished() is called they will be released
     * if they have grown larger than these sizes.
     */
    public static final int DEFAULT_INDICES_SIZE = 8192;
    public static final int DEFAULT_CROSSINGS_SIZE = 32*1024;

    public static final int NUM_ALPHA_ROWS = 8;

    private static final int PAINT_FLAT_COLOR = 0;
    private static final int PAINT_TEXTURE = 1;

    private static final int MIN_QUAD_OPT_WIDTH = ToFixed(100);

    /*
     * Antialiasing (supersample coordinate space definition).
     */
    private int SUBPIXEL_LG_POSITIONS_X;
    private int SUBPIXEL_LG_POSITIONS_Y;
    private int SUBPIXEL_MASK_X;
    private int SUBPIXEL_MASK_Y;
    private int SUBPIXEL_POSITIONS_X;
    private int SUBPIXEL_POSITIONS_Y;
    private int MAX_AA_ALPHA;
    private int MAX_AA_ALPHA_DENOM;
    private int HALF_MAX_AA_ALPHA_DENOM;
    private int XSHIFT;
    private int YSHIFT;
    private int YSTEP;
    private int HYSTEP;
    private int YMASK;

    /*
     * Cache to store RLE-encoded coverage mask of the current primitive
     */
    PiscesCache cache = null;
    /*
     * Drawing region in S15.16 space
     */
    private int boundsMinX, boundsMinY, boundsMaxX, boundsMaxY;
    /*
     * Current primitive in subsample space
     */
    private int rasterMinX, rasterMaxX, rasterMinY, rasterMaxY;
    /*
     * Current primitive in nominal user space
     */
    private int bboxX0, bboxY0, bboxX1, bboxY1;
    /*
     * Current winding rule
     */
    private int windingRule = WIND_NON_ZERO;
    /*
     * Current drawing position (i.e., final point of last segment) in S15.16 space
     */
    private int x0, y0; 
    /*
     * Position of most recent 'moveTo' command
     */
    private int sx0, sy0;
    /*
     * Buffer to be filled with one row's worth of alpha values
     */
    private byte[] rowAA = null;

    private int[] paintBuffer = null;
    private int paintBufferOffset;
    private int paintBufferStride;
    /*
     * Image information
     */
    private int width, height;

    private Object imageData;
    private int imageOffset;
    private int imageScanlineStride;
    private int imagePixelStride;

    private int paintMode = PAINT_FLAT_COLOR;
    private int cred, cgreen, cblue, calpha;

    Paint paint;
    /*
     * Map from [0, MAX_AA_ALPHA] to [0, 256]
     * Used to normalize alpha processing to 8 bits
     */
    private int[] alphaMap = new int[16*16 + 1];

    private int compositeRule = Blit.COMPOSITE_SRC_OVER;
    /*
     * Track the number of vertical extrema of the incoming edge list
     * in order to determine the maximum number of crossings of a
     * scanline
     */
    private int firstOrientation = 0;
    private int lastOrientation = 0;
    private int flips = 0;
    /*
     * Parameters for emitRow
     */
    private int rowAAOffset;
    private int rowNum;
    private int alphaWidth;
    private int[] minTouched = new int[NUM_ALPHA_ROWS];
    private int[] maxTouched = new int[NUM_ALPHA_ROWS];
    private int[] rowOffsets = new int[NUM_ALPHA_ROWS];
    private int currX, currY;
    private int currImageOffset;
    /*
     * Edge data is in S15.16 format
     */
    private int[] edges = new int[5*INITIAL_EDGES];
    private int edgeIdx = 0;
    private int edgeMinY = Integer.MAX_VALUE;
    private int edgeMaxY = Integer.MIN_VALUE;
    /*
     * Crossing list data
     */
    private int[] crossingIndices;
    private int[] crossings;
    private int crossingMinY;
    private int crossingMaxY;
    private int crossingMinX = Integer.MAX_VALUE;
    private int crossingMaxX = Integer.MIN_VALUE;
    private int crossingMaxXEntries;
    private int numCrossings = 0;
    private boolean crossingsSorted = false;

    private int crossingY;
    private int crossingRowCount;
    private int crossingRowOffset;
    private int crossingRowIndex;


    public Renderer(Object imageData, int width, int height,
                    int imageOffset,
                    int imageScanlineStride,
                    int imagePixelStride,
                    int imageType)
    {
        super(imageType);
        this.imageData = imageData;
        this.width = width;
        this.height = height;
        this.imageOffset = imageOffset;
        this.imageScanlineStride = imageScanlineStride;
        this.imagePixelStride = imagePixelStride;

        setAntialiasing(DEFAULT_SUBPIXEL_LG_POSITIONS_X,
                        DEFAULT_SUBPIXEL_LG_POSITIONS_Y);
    }


    public void dispose(){
        this.cache = null;
        this.paint = null;
    }
    public void setAntialiasing(int subpixelLgPositionsX,
                                int subpixelLgPositionsY)
    {
        this.SUBPIXEL_LG_POSITIONS_X = subpixelLgPositionsX;
        this.SUBPIXEL_LG_POSITIONS_Y = subpixelLgPositionsY;

        this.SUBPIXEL_MASK_X =
            (1 << (SUBPIXEL_LG_POSITIONS_X)) - 1;
        this.SUBPIXEL_MASK_Y =
            (1 << (SUBPIXEL_LG_POSITIONS_Y)) - 1;
        this.SUBPIXEL_POSITIONS_X =
            1 << (SUBPIXEL_LG_POSITIONS_X);
        this.SUBPIXEL_POSITIONS_Y =
            1 << (SUBPIXEL_LG_POSITIONS_Y);
        this.MAX_AA_ALPHA =
            (SUBPIXEL_POSITIONS_X*SUBPIXEL_POSITIONS_Y);
        this.MAX_AA_ALPHA_DENOM = 255*MAX_AA_ALPHA;
        this.HALF_MAX_AA_ALPHA_DENOM = MAX_AA_ALPHA_DENOM/2;
        this.XSHIFT = 16 - SUBPIXEL_LG_POSITIONS_X;
        this.YSHIFT = 16 - SUBPIXEL_LG_POSITIONS_Y;
        this.YSTEP = 1 << YSHIFT;
        this.HYSTEP = 1 << (YSHIFT - 1);
        this.YMASK = ~(YSTEP - 1);

        createAlphaMap(this.calpha);
    }

    public int getSubpixelLgPositionsX() {
        return SUBPIXEL_LG_POSITIONS_X;
    }

    public int getSubpixelLgPositionsY() {
        return SUBPIXEL_LG_POSITIONS_Y;
    }

    public void setColor(int red, int green, int blue, int alpha) {
        switch(imageType){
        case TYPE_INT_RGB:
        case TYPE_INT_ARGB:
        case TYPE_BYTE_GRAY:
            this.cred = red;
            this.cgreen = green;
            this.cblue = blue;
            break;
        case TYPE_USHORT_565_RGB:
            this.cred = Blit.convert8To5[red];
            this.cgreen = Blit.convert8To6[green];
            this.cblue = Blit.convert8To5[blue];
            break;
        }

        this.calpha = alpha;
        this.createAlphaMap(alpha);

        this.paint = null;
        this.paintMode = PAINT_FLAT_COLOR;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
        this.createAlphaMap(255);
        this.paintMode = PAINT_TEXTURE;
    }
    public void getBoundingBox(double[] bbox) {
        bbox[0] = bboxX0;
        bbox[1] = bboxY0;
        bbox[2] = (bboxX1 - bboxX0);
        bbox[3] = (bboxY1 - bboxY0);
    }
    public void beginRendering(double boundsX, double boundsY,
                               double boundsWidth, double boundsHeight,
                               int windingRule)
    {
        lastOrientation = 0;
        flips = 0;

        resetEdges();

        this.bboxX0 = (int)boundsX; 
        this.bboxY0 = (int)boundsY;
        this.bboxX1 = (int)(boundsX + boundsWidth);
        this.bboxY1 = (int)(boundsY + boundsHeight);
        this.boundsMinX = ToFixed(boundsX); 
        this.boundsMinY = ToFixed(boundsY);
        this.boundsMaxX = ToFixed(boundsX + boundsWidth);
        this.boundsMaxY = ToFixed(boundsY + boundsHeight);
        this.windingRule = windingRule;
    }
    public void moveTo(double x0, double y0) {

        this.sx0 = this.x0 = ToFixed(x0);
        this.sy0 = this.y0 = ToFixed(y0);
        this.lastOrientation = 0;
    }
    public void lineTo(double x1, double y1) {
        final int _x1 = ToFixed(x1);
        final int _y1 = ToFixed(y1);
        /*
         * Ignore horizontal lines
         * Next line will count flip
         */
        if (y0 == _y1){
            this.x0 = _x1;
            return;
        }
        else {

            int orientation = (y0 < _y1) ? 1 : -1;
            if (lastOrientation == 0) {
                firstOrientation = orientation;
            }
            else if (orientation != lastOrientation) {
                ++flips;
            }
            lastOrientation = orientation;
            /*
             * Bias Y by 1 ULP so endpoints never lie on a scanline
             */
            this.addEdge(x0, (y0 | 0x1), _x1, (_y1 | 0x1));

            this.x0 = _x1;
            this.y0 = _y1;
        }
    }

    public void clearRect(double x, double y, double w, double h) {
        Blit.clearRect(imageData, imageType, 
                       imageOffset, imageScanlineStride, imagePixelStride,
                       (int)x, (int)y, (int)w, (int)h, 
                       calpha, cred, cgreen, cblue);
    }
    public void close() {

        int orientation = lastOrientation;
        if (y0 != sy0) {
            orientation = (y0 < sy0) ? 1 : -1;
        }
        if (orientation != firstOrientation) {
            ++flips;
        }
        lineTo(ToFloat(this.sx0), ToFloat(this.sy0));
    }

    public void endRendering() {
        _endRendering();

        if (cache != null) {
            cache.bboxX0 = bboxX0;
            cache.bboxY0 = bboxY0;
            cache.bboxX1 = bboxX1;
            cache.bboxY1 = bboxY1;

            cache.isValid = true;
        }
    }

    public void setCache(PiscesCache cache) {
        this.cache = cache;
    }

    public void renderFromCache(PiscesCache cache) {
        this.alphaWidth = cache.alphaWidth;
        int alphaHeight = cache.alphaHeight;

        int bufLen = NUM_ALPHA_ROWS*alphaWidth;
        if (this.rowAA == null || this.rowAA.length < bufLen) {
            this.rowAA = new byte[bufLen];
        }

        // Decode run-length encoded alpha mask data
        // The data for row j begins at cache.rowOffsetsRLE[j]
        // and is encoded as a set of 2-byte pairs (val, runLen)
        // terminated by a (0, 0) pair.

        int currX = cache.bboxX0;
        int currY = cache.bboxY0;
        currImageOffset = imageOffset +
            currY*imageScanlineStride +
            currX*imagePixelStride;
        
        int idx = 0;
        for (int j = 0; j < alphaHeight; j++) {
            int jj = j & (NUM_ALPHA_ROWS - 1);

            rowOffsets[jj] = idx - cache.minTouched[j];

            int pos = cache.rowOffsetsRLE[j];
            int len = 0;

            while (true) {
                byte val = cache.rowAARLE[pos];
                int runLen = cache.rowAARLE[pos + 1] & 0xff;
                if (runLen == 0) {
                    break;
                }
                for (int i = 0; i < runLen; i++) {
                    rowAA[idx++] = val;
                }
                len += runLen;
                pos += 2;
            }

            minTouched[jj] = cache.minTouched[j];
            if (len == 0) {
                // Empty rows have minX = Integer.MAX_VALUE,
                // maxX = Integer.MIN_VALUE
                maxTouched[jj] = Integer.MIN_VALUE;
            } else {
                maxTouched[jj] = cache.minTouched[j] + len - 1;
            }

            // Perform blitting after NUM_ALPHA_ROWS rows have
            // been decoded, or when we reach the last row
            if ((jj == NUM_ALPHA_ROWS - 1) || (j == alphaHeight - 1)) {
                emitRows(jj + 1);
                currImageOffset += (jj + 1)*imageScanlineStride;
                idx = 0;
            }

        }

        // Update the bounding box for possible retrieval via getBoundingBox
        this.bboxX0 = cache.bboxX0;
        this.bboxY0 = cache.bboxY0;
        this.bboxX1 = cache.bboxX1;
        this.bboxY1 = cache.bboxY1;
    }
    public Renderer clone(){
        return (Renderer)super.clone();
    }
    /*
     * Create a lookup table indexed from 0 to MAX_AA_ALPHA, inclusive,
     * containing normalized values for (index*alpha).  Normalization
     * maps a value from 0 to MAX_AA_ALPHA*alpha into the range 0 to 256.
     */
    private void createAlphaMap(int alpha) {

        alpha *= 256;

        for (int index = 0; index <= MAX_AA_ALPHA; index++) {

            alphaMap[index] = ((index*alpha + HALF_MAX_AA_ALPHA_DENOM)/
                               MAX_AA_ALPHA_DENOM);
        }
    }
    /*
     * Scan convert a single edge
     */
    private void computeCrossingsForEdge(int index,
                                         int boundsMinY, int boundsMaxY)
    {
        int iy0 = edges[index + 1];
        int iy1 = edges[index + 3];

        // Clip to valid Y range
        int clipy0 = (iy0 > boundsMinY) ? iy0 : boundsMinY;
        int clipy1 = (iy1 < boundsMaxY) ? iy1 : boundsMaxY;

        int minY = ((clipy0 + HYSTEP) & YMASK) + HYSTEP;
        int maxY = ((clipy1 - HYSTEP) & YMASK) + HYSTEP;
        /*
         * IMPL_NOTE - If line falls outside the valid X range, could
         * draw a vertical line instead
         */
        /*
         * Exit if no scanlines are crossed
         */
        if (minY > maxY) {
            return;
        }
        else {
            /*
             * Scan convert line using a DDA approach
             */
            int ix0 = edges[index];
            int ix1 = edges[index + 2];
            int dx = ix1 - ix0;
            int dy = iy1 - iy0;
            /*
             * Compute first crossing point at y = minY
             */
            int orientation = edges[index + 4];
            int y = minY;
            long lx = (long)(y - iy0)*dx/dy + ix0;
            this.addCrossing(y >> YSHIFT, (int)(lx >> XSHIFT), orientation);
            /*
             * Advance y to next scanline, exit if past endpoint
             */
            y += YSTEP;
            if (y > maxY) {
                return;
            }
            else {
                /*
                 * Compute xstep only if additional scanlines are crossed
                 * For each scanline, add xstep to lx and YSTEP to y and
                 * emit the new crossing
                 */
                long xstep = ((long)YSTEP*dx)/dy;
                for (; y <= maxY; y += YSTEP) {
                    lx += xstep;
                    this.addCrossing(y >> YSHIFT, (int)(lx >> XSHIFT), orientation);
                }
            }
        }
    }

    private void computeBounds() {
        rasterMinX = crossingMinX & ~SUBPIXEL_MASK_X;
        rasterMaxX = crossingMaxX | SUBPIXEL_MASK_X;
        rasterMinY = crossingMinY & ~SUBPIXEL_MASK_Y;
        rasterMaxY = crossingMaxY | SUBPIXEL_MASK_Y;
        /*
         * If nothing was drawn, we have:
         * minX = Integer.MAX_VALUE and maxX = Integer.MIN_VALUE
         * so nothing to render
         */
        if (rasterMinX > rasterMaxX || rasterMinY > rasterMaxY) {
            rasterMinX = 0;
            rasterMaxX = -1;
            rasterMinY = 0;
            rasterMaxY = -1;
            return;
        }
        else {
            if (rasterMinX < boundsMinX >> XSHIFT) {
                rasterMinX = boundsMinX >> XSHIFT;
            }
            if (rasterMinY < boundsMinY >> YSHIFT) {
                rasterMinY = boundsMinY >> YSHIFT;
            }
            if (rasterMaxX > boundsMaxX >> XSHIFT) {
                rasterMaxX = boundsMaxX >> XSHIFT;
            }
            if (rasterMaxY > boundsMaxY >> YSHIFT) {
                rasterMaxY = boundsMaxY >> YSHIFT;
            }
        }
    }

    private void _endRendering() {
        if (flips == 0) {
            bboxX0 = bboxY0 = 0;
            bboxX1 = bboxY1 = -1;
            return;
        }
        /*
         * Special case for filling a single rect with a flat, opaque color
         */
        if (paintMode == PAINT_FLAT_COLOR &&
            calpha == 255 &&
            edgeIdx == 10 &&
            edges[0] == edges[2] &&
            edges[1] == edges[6] &&
            edges[3] == edges[8] &&
            edges[5] == edges[7] &&
            Math.abs(edges[0] - edges[5]) > MIN_QUAD_OPT_WIDTH)
        {

            int x0 = edges[0] >> XSHIFT;
            int y0 = edges[1] >> YSHIFT;
            int x1 = edges[5] >> XSHIFT;
            int y1 = edges[3] >> YSHIFT;
            
            if (x0 > x1) {
                int tmp = x0;
                x0 = x1;
                x1 = tmp;
            }
            if (y0 > y1) {
                int tmp = y0;
                y0 = y1;
                y1 = tmp;
            }

            int bMinX = this.boundsMinX >> XSHIFT;
            int bMinY = this.boundsMinY >> YSHIFT;
            int bMaxX = this.boundsMaxX >> XSHIFT;
            int bMaxY = this.boundsMaxY >> YSHIFT;
            /*
             * Clip to image bounds in supersampled coordinates
             */
            x0 = Clamp(x0, bMinX, bMaxX);
            x1 = Clamp(x1, bMinX, bMaxX);
            y0 = Clamp(y0, bMinY, bMaxY);
            y1 = Clamp(y1, bMinY, bMaxY);

            Blit.fillRectSrcOver(this,
                                 imageData, imageType,
                                 imageOffset,
                                 imageScanlineStride, imagePixelStride,
                                 width, height,
                                 x0, y0, x1, y1,
                                 cred, cgreen, cblue);

            bboxX0 = x0 >> SUBPIXEL_LG_POSITIONS_X;
            bboxY0 = y0 >> SUBPIXEL_LG_POSITIONS_Y;
            bboxX1 = ((x1 + SUBPIXEL_POSITIONS_X - 1)
                      >> SUBPIXEL_LG_POSITIONS_X);
            bboxY1 = ((y1 + SUBPIXEL_POSITIONS_Y - 1)
                      >> SUBPIXEL_LG_POSITIONS_Y);

            return;
        }

        int minY = (edgeMinY > boundsMinY) ? edgeMinY : boundsMinY;
        int maxY = (edgeMaxY < boundsMaxY) ? edgeMaxY : boundsMaxY;

        // Check for empty intersection of primitive with the drawing area
        if (minY > maxY) {
            bboxX0 = bboxY0 = 0;
            bboxX1 = bboxY1 = -1;
            return;
        }
        else {
            // Compute Y extent in subpixel coordinates
            int iminY = (minY >> YSHIFT) & ~SUBPIXEL_MASK_Y;
            int imaxY = (maxY >> YSHIFT) | SUBPIXEL_MASK_Y;
            int yextent = (imaxY - iminY) + 1;

            // Maximum number of crossings 
            int size = flips*yextent;

            int bmax = (boundsMaxY >> YSHIFT) - 1;
            if (imaxY > bmax) {
                imaxY = bmax;
            }

            // Initialize X bounds, will be refined for each strip
            bboxX0 = Integer.MAX_VALUE;
            bboxX1 = Integer.MIN_VALUE;

            // Set Y bounds
            bboxY0 = iminY >> SUBPIXEL_LG_POSITIONS_Y;
            bboxY1 = (imaxY + SUBPIXEL_POSITIONS_Y - 1) >> SUBPIXEL_LG_POSITIONS_Y;

            // Compute number of rows that can be processing using
            // a crossings table no larger than DEFAULT_CROSSINGS_SIZE.
            // However, we must process at least one row, so we grow the table
            // temporarily if needed.  This would require an object with a
            // huge number of flips.
            int rows = DEFAULT_CROSSINGS_SIZE/(flips*SUBPIXEL_POSITIONS_Y);
            rows = Math.min(rows, yextent);
            rows = Math.max(rows, 1);
            for (int i = iminY; i <= imaxY; i += rows*SUBPIXEL_POSITIONS_Y) {
                // Compute index of last scanline to be processed in this pass
                int last = Math.min(i + rows*SUBPIXEL_POSITIONS_Y - 1, imaxY);
                setCrossingsExtents(i, last, flips);

                int bminY = i << YSHIFT;
                int bmaxY = (last << YSHIFT) | ~YMASK;

                // Process edges from the edge list
                int maxIdx = edgeIdx;
                for (int index = 0; index < maxIdx; index += 5) {
                    // Test y1 < min:
                    //
                    // If edge lies entirely above current strip,
                    // discard it
                    if (edges[index + 3] < bminY) {
                        // Overwrite the edge with the last edge
                        edgeIdx -= 5;
                        int fidx = edgeIdx;
                        int tidx = index;
                        edges[tidx++] = edges[fidx++];
                        edges[tidx++] = edges[fidx++];
                        edges[tidx++] = edges[fidx++];
                        edges[tidx++] = edges[fidx++];
                        edges[tidx  ] = edges[fidx  ];

                        maxIdx -= 5;
                        index -= 5;
                        continue;
                    }

                    // Test y0 > max:
                    //
                    // If edge lies entirely below current strip,
                    // skip it for now
                    if (edges[index + 1] > bmaxY) {
                        continue;
                    }

                    computeCrossingsForEdge(index, bminY, bmaxY);
                }

                computeBounds();
                if (rasterMaxX < rasterMinX) {
                    continue;
                }

                bboxX0 = Math.min(bboxX0,
                                  rasterMinX >> SUBPIXEL_LG_POSITIONS_X);
                bboxX1 = Math.max(bboxX1,
                                  (rasterMaxX + SUBPIXEL_POSITIONS_X - 1)
                                  >> SUBPIXEL_LG_POSITIONS_X);
                renderStrip();
            }

            // Free up any unusually large scratchpad memory used by the
            // preceding primitive
            crossingListFinished();
        }
    }
    private void renderStrip() {
        // Grow rowAA according to the raster width
        int width = (rasterMaxX - rasterMinX + 1) >> SUBPIXEL_LG_POSITIONS_X;
        alphaWidth = width;

        // Allocate one extra entry in rowAA to avoid a conditional in
        // the rendering loop
        int bufLen = NUM_ALPHA_ROWS*width + 1;
        if (this.rowAA == null || this.rowAA.length < bufLen) {
            this.rowAA = new byte[bufLen];

            this.paintBuffer = new int[bufLen];
            this.paintBufferOffset = 0;
            this.paintBufferStride = width;
        }

        // Mask to determine the relevant bit of the crossing sum
        // 0x1 if EVEN_ODD, all bits if NON_ZERO
        int mask = (windingRule == WIND_EVEN_ODD) ? 0x1 : ~0x0;
	
        int y = 0;
        int prevY = rasterMinY - 1;

        this.currY = rasterMinY >> SUBPIXEL_LG_POSITIONS_Y;
        this.currX = rasterMinX >> SUBPIXEL_LG_POSITIONS_X;
        this.currImageOffset = imageOffset +
            currY*imageScanlineStride +
            currX*imagePixelStride;
        this.rowAAOffset = 0;
        this.rowNum = 0;

        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;

        iterateCrossings();
        while (hasMoreCrossingRows()) {
            y = crossingY;

            // Emit any skipped rows
            for (int j = prevY + 1; j < y; j++) {
                if (((j & SUBPIXEL_MASK_Y) == SUBPIXEL_MASK_Y) ||
                    (j == rasterMaxY)) {
                    emitRow(0, -1, false);
                }
            }
            prevY = y;

            if (crossingRowIndex < crossingRowCount) {
                int lx = crossings[crossingRowOffset + crossingRowIndex];
                lx >>= 1;
                int hx = crossings[crossingRowOffset + crossingRowCount - 1];
                hx >>= 1;
                int x0 = lx > rasterMinX ? lx : rasterMinX;
                int x1 = hx < rasterMaxX ? hx : rasterMaxX;
                x0 -= rasterMinX;
                x1 -= rasterMinX;

                minX = Math.min(minX, x0 >> SUBPIXEL_LG_POSITIONS_X);
                maxX = Math.max(maxX, x1 >> SUBPIXEL_LG_POSITIONS_X);
            }

            int sum = 0;
            int prev = rasterMinX;
            while (crossingRowIndex < crossingRowCount) {
                int crxo = crossings[crossingRowOffset + crossingRowIndex];
                crossingRowIndex++;

                int crx = crxo >> 1;
                int crorientation = ((crxo & 0x1) == 0x1) ? 1 : -1;	

                if ((sum & mask) != 0) {
                    // Clip to active X range, if x1 < x0 loop will
                    // have no effect
                    int x0 = prev > rasterMinX ? prev : rasterMinX;
                    int x1 =  crx < rasterMaxX ?  crx : rasterMaxX;

                    // Empty spans
                    if (x1 > x0) {
                        x0 -= rasterMinX;
                        x1 -= rasterMinX;

                        // Accumulate alpha, equivalent to:
                        //   for (int x = x0; x < x1; x++) {
                        //       ++rowAA[x >> SUBPIXEL_LG_POSITIONS_X];
                        //   }
                        //
                        // In the middle of the span, we can update a full
                        // pixel at a time (i.e., SUBPIXEL_POSITIONS_X
                        // subpixels)
                        
                        int x = x0 >> SUBPIXEL_LG_POSITIONS_X;
                        int xmaxm1 = (x1 - 1) >> SUBPIXEL_LG_POSITIONS_X;
                        if (x == xmaxm1) {
                            // Start and end in same pixel
                            rowAA[x + rowAAOffset] += x1 - x0;
                        } else {
                            // Start and end in different pixels
                            rowAA[x++ + rowAAOffset] += SUBPIXEL_POSITIONS_X -
                                (x0 & SUBPIXEL_MASK_X);
                            int xmax = x1 >> SUBPIXEL_LG_POSITIONS_X;
                            while (x < xmax) {
                                rowAA[x++ + rowAAOffset] +=
                                    SUBPIXEL_POSITIONS_X;
                            }
                            // Note - at this point it is possible that
                            // x == width, which implies that
                            // x1 & SUBPIXEL_MASK_X == 0.  We allocate
                            // one extra entry in rowAA so this
                            // assignment will be harmless.  The alternative
                            // is an extra conditional here, or some other
                            // scheme to deal with the last pixel better.
                            rowAA[x + rowAAOffset] += x1 & SUBPIXEL_MASK_X;
                        }
                    }
                }
                sum += crorientation;
                prev = crx;
            }

            // Every SUBPIXEL_POSITIONS rows, output an antialiased row
            if (((y & SUBPIXEL_MASK_Y) == SUBPIXEL_MASK_Y) ||
                (y == rasterMaxY)) {
                emitRow(minX, maxX, false);
                minX = Integer.MAX_VALUE;
                maxX = Integer.MIN_VALUE;
            }
        }

        // Emit final row
        for (int j = prevY + 1; j <= rasterMaxY; j++) {
            if (((j & SUBPIXEL_MASK_Y) == SUBPIXEL_MASK_Y) ||
                (j == rasterMaxY)) {
                emitRow(minX, maxX, false);
                minX = Integer.MAX_VALUE;
                maxX = Integer.MIN_VALUE;
            }
        }

        // Emit last bunch of rows
        if (rowAAOffset != 0) {
            emitRow(minX, maxX, true);
        }
    }

    private void clearAlpha(byte[] alpha,
                            int alphaOffset,
                            int width, int height,
                            int[] minTouched, int[] maxTouched,
                            int[] rowOffsets)
    {
        for (int j = 0; j < height; j++) {
            int minX = minTouched[j];
            int maxX = maxTouched[j];
            if (maxX >= minX) {
                int w = maxX - minX + 1;
                if (w + minX > width) {
                    w = width - minX;
                }
                
                int aidx = alphaOffset + rowOffsets[j] + minX;
                for (int i = 0; i < w; i++, aidx++) {
                    alpha[aidx] = (byte)0;
                }
            }
        }
    }
    
    private void emitRow(int minX, int maxX, boolean forceOutput) {
        // Copy rowAA data into the cache if one is present
        if (cache != null) {
            if (cache.alphaWidth == 0) {
                cache.alphaWidth = alphaWidth;
            } else {
                if (alphaWidth != cache.alphaWidth) {
                    throw new IllegalStateException("alphaWidth changed!");
                }
            }

            int len = -1;
            int dstIdx = cache.alphaRLELength;
            if (maxX >= minX) {
                int srcIdx = rowAAOffset + minX;
                len = maxX - minX + 1;

                // Perform run-length encoding
                // and store results in the cache
                byte startVal = rowAA[srcIdx];
                int runLen = 1;
                for (int x = 1; x < len; x++) {
                    byte nextVal = rowAA[srcIdx + x];
                    if (nextVal == startVal && runLen < 255) {
                        ++runLen;
                    } else {
                        cache.addRLERun(startVal, runLen);

                        runLen = 1;
                        startVal = nextVal;
                    }                    
                }
                if (runLen > 0) {
                    cache.addRLERun(startVal, runLen);
                }
                cache.addRLERun((byte)0, 0);
            }

            cache.addRow(minX, dstIdx);
        }

        // Record values for later blitting

        this.minTouched[rowNum] = minX;
        this.maxTouched[rowNum] = maxX;
        this.rowOffsets[rowNum] = rowAAOffset;

        rowAAOffset += alphaWidth;
        rowNum++;
        if (forceOutput || rowNum == NUM_ALPHA_ROWS) {
            emitRows(rowNum);
            clearAlpha(rowAA, 0,
                       alphaWidth, rowNum,
                       minTouched, maxTouched, rowOffsets);

            currY += rowNum;
            currImageOffset += rowNum*imageScanlineStride;
            rowAAOffset = 0;
            rowNum = 0;
        }
    }

    private void emitRows(int alphaHeight) {
        if (paintMode == PAINT_FLAT_COLOR) {
            Blit.blit(imageData, imageType,
                      currImageOffset, imageScanlineStride, imagePixelStride,
                      rowAA, 0,
                      alphaWidth, alphaHeight,
                      minTouched, maxTouched, rowOffsets,
                      compositeRule,
                      cred, cgreen, cblue, calpha, alphaMap);
        }
        else {
            paint.paint(currX, currY, alphaWidth, alphaHeight,
                        minTouched, maxTouched,
                        paintBuffer, paintBufferOffset, paintBufferStride);
            
            Blit.blit(imageData, imageType,
                      currImageOffset, imageScanlineStride, imagePixelStride,
                      rowAA, 0,
                      alphaWidth, alphaHeight,
                      minTouched, maxTouched, rowOffsets,
                      compositeRule,
                      paintBuffer, paintBufferOffset, paintBufferStride,
                      alphaMap);
        }
    }

    private void addEdge(int x0, int y0, int x1, int y1) {
        int newLen = edgeIdx + 5;
        if (edges.length < newLen) {
            int[] tmp = new int[Math.max(11*edges.length/10, newLen)];
            System.arraycopy(edges, 0, tmp, 0, edgeIdx);
            this.edges = tmp;
        }

        int orientation = 1;
        if (y0 > y1) {
            int tmp = y0;
            y0 = y1;
            y1 = tmp;

            orientation = -1;
        }

        // Skip edges that don't cross a subsampled scanline
        int eminY = ((y0 + HYSTEP) & YMASK);
        int emaxY = ((y1 - HYSTEP) & YMASK);
        if (eminY > emaxY) {
            return;
        }
        else {
            if (orientation == -1) {
                int tmp = x0;
                x0 = x1;
                x1 = tmp;
            }

            edges[edgeIdx++] = x0;
            edges[edgeIdx++] = y0;
            edges[edgeIdx++] = x1;
            edges[edgeIdx++] = y1;
            edges[edgeIdx++] = orientation;
        
            // Update Y bounds of primitive
            if (y0 < edgeMinY) {
                edgeMinY = y0;
            }
            if (y1 > edgeMaxY) {
                edgeMaxY = y1;
            }
        }
    }

    private void resetEdges() {
        this.edgeIdx = 0;
        this.edgeMinY = Integer.MAX_VALUE;
        this.edgeMaxY = Integer.MIN_VALUE;
    }

    private void setCrossingsExtents(int minY, int maxY, int maxXEntries) {
        int yextent = maxY - minY + 1;

        // Grow indices array as needed
        if (crossingIndices == null || crossingIndices.length < yextent) {
            this.crossingIndices =
                new int[Math.max(yextent, DEFAULT_INDICES_SIZE)];
        }
        // Grow crossings array as needed
        if (crossings == null || crossings.length < yextent*maxXEntries) {
            this.crossings = new int[Math.max(yextent*maxXEntries,
                                              DEFAULT_CROSSINGS_SIZE)];
        }
        this.crossingMinY = minY;
        this.crossingMaxY = maxY;
        this.crossingMaxXEntries = maxXEntries;
        resetCrossings();
    }

    private void resetCrossings() {
        int yextent = crossingMaxY - crossingMinY + 1;
        int start = 0;
        for (int i = 0; i < yextent; i++) {
            crossingIndices[i] = start;
            start += crossingMaxXEntries;
        }
        crossingMinX = Integer.MAX_VALUE;
        crossingMaxX = Integer.MIN_VALUE;
        numCrossings = 0;
        crossingsSorted = false;
    }

    // Free sorting arrays if larger than maximum size
    private void crossingListFinished() {
        if (crossings.length > DEFAULT_CROSSINGS_SIZE) {
            crossings = new int[DEFAULT_CROSSINGS_SIZE];
        }
        if (crossingIndices.length > DEFAULT_INDICES_SIZE) {
            crossingIndices = new int[DEFAULT_INDICES_SIZE];
        }
    }

    private void sortCrossings(int[] x, int off, int len) {
        for (int i = off + 1; i < off + len; i++) {
            int j = i;
            int xj = x[j];
            int xjm1;

            while (j > off && (xjm1 = x[j - 1]) > xj) {
                x[j] = xjm1;
                x[j - 1] = xj;                    
                j--;
            }
        }
    }

    private void sortCrossings() {
        int start = 0;
        for (int i = 0; i <= crossingMaxY - crossingMinY; i++) {
            sortCrossings(crossings, start, crossingIndices[i] - start);
            start += crossingMaxXEntries;
        }
    }

    private void addCrossing(int y, int x, int orientation) {
        if (x < crossingMinX) {
            crossingMinX = x;
        }
        if (x > crossingMaxX) {
            crossingMaxX = x;
        }

        int index = crossingIndices[y - crossingMinY]++;
        x <<= 1;
        crossings[index] = (orientation == 1) ? (x | 0x1) : x;

        ++numCrossings;
    }

    private void iterateCrossings() {
        if (!crossingsSorted) {
            sortCrossings();
            crossingsSorted = true;
        }
        crossingY = crossingMinY - 1;
        crossingRowOffset = -crossingMaxXEntries;
    }

    private boolean hasMoreCrossingRows() {
        if (++crossingY <= crossingMaxY) {
            crossingRowOffset += crossingMaxXEntries;
            int y = crossingY - crossingMinY;
            crossingRowCount = crossingIndices[y] - y*crossingMaxXEntries;
            crossingRowIndex = 0;
            return true;
        } else {
            return false;
        }
    }

}

