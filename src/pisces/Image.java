/*
 * Pisces User
 * Copyright (C) 2009 John Pritchard
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.  The copyright
 * holders designate particular file as subject to the "Classpath"
 * exception as provided in the LICENSE file that accompanied this
 * code.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */
package pisces;

import pisces.png.Encoder;

/**
 * 
 * 
 * @see Graphics
 */
public class Image
    extends pisces.d.NativeSurface
    implements Cloneable
{

    public Image(int width, int height){
        super(width,height);
    }


    public Graphics createGraphics(){

        return new Graphics(this);
    }
    public final byte[] toPNG(){

        Encoder png = new Encoder(this);
        return png.encode();
    }
    public final byte[] toPNG(boolean alpha){

        Encoder png = new Encoder(this,alpha);
        return png.encode();
    }
    public final byte[] toPNG(boolean alpha, int compression){

        Encoder png = new Encoder(this,alpha,Encoder.FILTER_NONE,compression);
        return png.encode();
    }
}
