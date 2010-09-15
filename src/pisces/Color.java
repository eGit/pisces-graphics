/*
 * Pisces User
 * Copyright (C) 2009 John Pritchard
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
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

/**
 * 
 * 
 * @see Graphics
 */
public class Color
    extends Object
    implements Cloneable
{

    public final static Color White     = new Color(255, 255, 255);
    public final static Color LightGray = new Color(192, 192, 192);
    public final static Color Gray      = new Color(128, 128, 128);
    public final static Color DarkGray  = new Color( 64,  64,  64);
    public final static Color Black     = new Color(  0,   0,   0);
    public final static Color Red       = new Color(255,   0,   0);
    public final static Color Pink      = new Color(255, 175, 175);
    public final static Color Orange    = new Color(255, 200,   0);
    public final static Color Yellow    = new Color(255, 255,   0);
    public final static Color Green     = new Color(  0, 255,   0);
    public final static Color Magenta   = new Color(255,   0, 255);
    public final static Color Cyan      = new Color(  0, 255, 255);
    public final static Color Blue      = new Color(  0,   0, 255);


    public final int alpha, red, green, blue;



    public Color(int argb){
        super();

        int a = (argb >>> 24) & 0xff;
        if (0 == a)
            this.alpha = 255;
        else
            this.alpha = a;

        this.red = (argb >>> 16) & 0xff;
        this.green = (argb >>> 8) & 0xff;
        this.blue = (argb & 0xff);
    }
    public Color(int r, int g, int b){
        this(0xff,r,g,b);
    }
    public Color(int a, int r, int g, int b){
        super();
        this.alpha = a;
        this.red = r;
        this.green = g;
        this.blue = b;
    }


    public Color clone(){
        try {
            return (Color)super.clone();
        }
        catch (CloneNotSupportedException err){
            throw new InternalError();
        }
    }
}
