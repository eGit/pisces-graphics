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
package pisces.m;

import java.lang.Math;

/**
 * A generic 3-element tuple that is represented by double-precision 
 * floating point x,y,z coordinates.
 *
 */
public abstract class Tuple
    extends pisces.d.FPMath
    implements Cloneable
{

    public	double	x;
    public	double	y;
    public	double	z;


    public Tuple(double x, double y, double z)
    {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Tuple(double[] t)
    {
        super();
        this.x = t[0];
        this.y = t[1];
        this.z = t[2];
    }
    public Tuple(Tuple t1)
    {
        super();
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
    }
    public Tuple()
    {
        super();
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
    }


    public final Tuple set(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }
    public final Tuple set(double[] t)
    {
        this.x = t[0];
        this.y = t[1];
        this.z = t[2];
        return this;
    }
    public final Tuple set(Tuple t1)
    {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
        return this;
    }
    /**
     * Copies the x,y,z coordinates of this tuple into the array t
     * of length 3.
     * @param t  the target array 
     */
    public final double[] get(double[] t)
    {
        t[0] = this.x;
        t[1] = this.y;
        t[2] = this.z;
        return t;
    }
    public final Tuple get(Tuple t)
    {
        t.x = this.x;
        t.y = this.y;
        t.z = this.z;
        return t;
    }
    public final Tuple add(Tuple t1, Tuple t2)
    {
        this.x = t1.x + t2.x;
        this.y = t1.y + t2.y;
        this.z = t1.z + t2.z;
        return this;
    }
    public final Tuple add(Tuple t1)
    { 
        this.x += t1.x;
        this.y += t1.y;
        this.z += t1.z;
        return this;
    }
    public final Tuple sub(Tuple t1, Tuple t2)
    {
        this.x = t1.x - t2.x;
        this.y = t1.y - t2.y;
        this.z = t1.z - t2.z;
        return this;
    }
    public final Tuple sub(Tuple t1)
    { 
        this.x -= t1.x;
        this.y -= t1.y;
        this.z -= t1.z;
        return this;
    }
    public final Tuple negate(Tuple t1)
    {
        this.x = -t1.x;
        this.y = -t1.y;
        this.z = -t1.z;
        return this;
    }
    public final Tuple negate()
    {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }
    public final Tuple scale(double s, Tuple t1)
    {
        this.x = s*t1.x;
        this.y = s*t1.y;
        this.z = s*t1.z;
        return this;
    }
    public final Tuple scale(double s)
    {
        this.x *= s;
        this.y *= s;
        this.z *= s;
        return this;
    }
    /**
     * Sets the value of this tuple to the scalar multiplication
     * of tuple t1 and then adds tuple t2 (this = s*t1 + t2).
     * @param s the scalar value
     * @param t1 the tuple to be multipled
     * @param t2 the tuple to be added
     */
    public final Tuple scaleAdd(double s, Tuple t1, Tuple t2)
    {
        this.x = s*t1.x + t2.x;
        this.y = s*t1.y + t2.y;
        this.z = s*t1.z + t2.z;
        return this;
    }
    /**
     * Sets the value of this tuple to the scalar multiplication
     * of itself and then adds tuple t1 (this = s*this + t1).
     * @param s the scalar value
     * @param t1 the tuple to be added
     */  
    public final Tuple scaleAdd(double s, Tuple t1) {
        this.x = s*this.x + t1.x;
        this.y = s*this.y + t1.y;
        this.z = s*this.z + t1.z;
        return this;
    }
    /**
     * Returns a string that contains the values of this Tuple.
     * The form is (x,y,z).
     * @return the String representation
     */  
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }


    /**
     * Returns a hash code value based on the data values in this
     * object.  Two different Tuple objects with identical data values
     * (i.e., Tuple.equals returns true) will return the same hash
     * code value.  Two objects with different data members may return the
     * same hash value, although this is not likely.
     * @return the integer hash code value
     */  
    public int hashCode() {
        long bits = 1L;
        bits = 31L * bits + Double.doubleToLongBits(x);
        bits = 31L * bits + Double.doubleToLongBits(y);
        bits = 31L * bits + Double.doubleToLongBits(z);
        return (int) (bits ^ (bits >> 32));
    }
    /**
     * Returns true if all of the data members of Tuple t1 are
     * equal to the corresponding data members in this Tuple.
     * @param t1  the tuple with which the comparison is made
     * @return  true or false
     */  
    public boolean equals(Tuple t1)
    {
        if (this == t1)
            return true;
        else if (null == t1)
            return false;
        else {
            return (EEQ(this.x, t1.x) && EEQ(this.y, t1.y) && EEQ(this.z, t1.z));
        }
    }
    /**
     * Returns true if the Object t1 is of type Tuple and all of the
     * data members of t1 are equal to the corresponding data members in
     * this Tuple.
     * @param t1  the Object with which the comparison is made
     * @return  true or false
     */  
    public boolean equals(Object t1)
    {
        try {
            Tuple t2 = (Tuple) t1;
            return this.equals(t2);
        }
        catch (ClassCastException e1) {
            return false;
        }
    }
    /**
     *  Clamps the tuple parameter to the range [low, high] and 
     *  places the values into this tuple.  
     *  @param min   the lowest value in the tuple after clamping
     *  @param max  the highest value in the tuple after clamping 
     *  @param t   the source tuple, which will not be modified
     */
    public final Tuple clamp(double min, double max, Tuple t) {
        if ( t.x > max ) {
            x = max;
        } else if ( t.x < min ){
            x = min;
        } else {
            x = t.x;
        }
 
        if ( t.y > max ) {
            y = max;
        } else if ( t.y < min ){
            y = min;
        } else {
            y = t.y;
        }
 
        if ( t.z > max ) {
            z = max;
        } else if ( t.z < min ){
            z = min;
        } else {
            z = t.z;
        }
        return this;
    }
    /** 
     *  Clamps the minimum value of the tuple parameter to the min 
     *  parameter and places the values into this tuple.
     *  @param min   the lowest value in the tuple after clamping 
     *  @param t   the source tuple, which will not be modified
     */   
    public final Tuple clampMin(double min, Tuple t) { 
        if ( t.x < min ) {
            x = min;
        } else {
            x = t.x;
        }
 
        if ( t.y < min ) {
            y = min;
        } else {
            y = t.y;
        }
 
        if ( t.z < min ) {
            z = min;
        } else {
            z = t.z;
        }
        return this;
    } 
    /**  
     *  Clamps the maximum value of the tuple parameter to the max 
     *  parameter and places the values into this tuple.
     *  @param max the highest value in the tuple after clamping  
     *  @param t   the source tuple, which will not be modified
     */    
    public final Tuple clampMax(double max, Tuple t) {  
        if ( t.x > max ) {
            x = max;
        } else {
            x = t.x;
        }
 
        if ( t.y > max ) {
            y = max;
        } else {
            y = t.y;
        }
 
        if ( t.z > max ) {
            z = max;
        } else {
            z = t.z;
        }
        return this;
    } 
    /**  
     *  Sets each component of the tuple parameter to its absolute 
     *  value and places the modified values into this tuple.
     *  @param t   the source tuple, which will not be modified
     */    
    public final Tuple absolute(Tuple t)
    {
        this.x = Math.abs(t.x);
        this.y = Math.abs(t.y);
        this.z = Math.abs(t.z);
        return this;
    } 
    /**
     *  Clamps this tuple to the range [low, high].
     *  @param min  the lowest value in this tuple after clamping
     *  @param max  the highest value in this tuple after clamping
     */
    public final Tuple clamp(double min, double max) {
        if ( x > max ) {
            x = max;
        } else if ( x < min ){
            x = min;
        }
 
        if ( y > max ) {
            y = max;
        } else if ( y < min ){
            y = min;
        }
 
        if ( z > max ) {
            z = max;
        } else if ( z < min ){
            z = min;
        }
        return this;
    }
    /**
     *  Clamps the minimum value of this tuple to the min parameter.
     *  @param min   the lowest value in this tuple after clamping
     */
    public final Tuple clampMin(double min) { 
        if ( x < min ) 
            this.x=min;
        if ( y < min ) 
            this.y=min;
        if ( z < min ) 
            this.z=min;
        return this;
    } 
    /**
     *  Clamps the maximum value of this tuple to the max parameter.
     *  @param max   the highest value in the tuple after clamping
     */
    public final Tuple clampMax(double max) { 
        if ( x > max ) 
            this.x=max;
        if ( y > max ) 
            this.y=max;
        if ( z > max ) 
            this.z=max;
        return this;
    }
    /**
     *  Sets each component of this tuple to its absolute value.
     */
    public final Tuple absolute()
    {
        this.x = Math.abs(x);
        this.y = Math.abs(y);
        this.z = Math.abs(z);
        return this;
    }
    /**
     *  Linearly interpolates between tuples t1 and t2 and places the 
     *  result into this tuple:  this = (1-alpha)*t1 + alpha*t2.
     *  @param t1  the first tuple
     *  @param t2  the second tuple  
     *  @param alpha  the alpha interpolation parameter  
     */   
    public final Tuple interpolate(Tuple t1, Tuple t2, double alpha) {
        this.x = (1-alpha)*t1.x + alpha*t2.x;
        this.y = (1-alpha)*t1.y + alpha*t2.y;
        this.z = (1-alpha)*t1.z + alpha*t2.z;
        return this;
    }
    /**   
     *  Linearly interpolates between this tuple and tuple t1 and 
     *  places the result into this tuple:  this = (1-alpha)*this + alpha*t1. 
     *  @param t1  the first tuple 
     *  @param alpha  the alpha interpolation parameter   
     */    
    public final Tuple interpolate(Tuple t1, double alpha) {
        this.x = (1-alpha)*this.x + alpha*t1.x;
        this.y = (1-alpha)*this.y + alpha*t1.y;
        this.z = (1-alpha)*this.z + alpha*t1.z;
        return this;
    } 
    public Tuple clone() {
        try {
            return (Tuple)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
	public final double getX() {
		return x;
	}
	public final Tuple setX(double x) {
		this.x = x;
        return this;
	}
	public final double getY() {
		return y;
	}
	public final Tuple setY(double y) {
		this.y = y;
        return this;
	}
	public final double getZ() {
		return z;
	}
	public final Tuple setZ(double z) {
		this.z = z;
        return this;
	}
}
