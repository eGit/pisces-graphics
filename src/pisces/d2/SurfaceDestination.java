/*
 * Copyright (C) 2010 John Pritchard
 * Copyright  1990-2008 Sun Microsystems, Inc. All Rights Reserved. 
 *  
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License version 
 * 2 only, as published by the Free Software Foundation. 
 *  
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License version 2 for more details (a copy is 
 * included at /legal/license.txt). 
 *  
 * You should have received a copy of the GNU General Public License 
 * version 2 along with this work; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 
 * 02110-1301 USA 
 *  
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa 
 * Clara, CA 95054 or visit www.sun.com if you need additional 
 * information or have any questions.
 */
package pisces.d2;

public interface SurfaceDestination {
    public void drawSurface(Surface ps, int srcX, int srcY, 
            int dstX, int dstY, int width, int height, float opacity);
    
    public void drawRGB(int[] argb, int offset, int scanLength, 
            int x, int y, int width, int height, float opacity);
}
