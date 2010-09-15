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

public class Transform6 extends Transform4 {

    public double m02, m12;

    public Transform6() {
        this( 1, 0, 0, 1, 0, 0);
    }
    public Transform6(Transform6 t) {
        this(t.m00, t.m01, t.m10, t.m11, t.m02, t.m12);
    }
    public Transform6(double m00, double m01,
                      double m10, double m11,
                      double m02, double m12)
    {
        super(m00, m01, m10, m11);
        this.m02 = m02;
        this.m12 = m12;
    }


    public void postMultiply(Transform6 t) {

        this.m00 = Z1(m00*t.m00 + m01*t.m10);
        this.m01 = Z1(m00*t.m01 + m01*t.m11);
        this.m02 = Z1(m02 + m00*t.m02 + m01*t.m12);
        this.m10 = Z1(m10*t.m00 + m11*t.m10);
        this.m11 = Z1(m10*t.m01 + m11*t.m11);
        this.m12 = Z1(m12 + m10*t.m02 + m11*t.m12);
    }
    public Transform6 inverse() {

        double det = m00*m11 - m01*m10;

        double a00 = Z1( m11/det);
        double a01 = Z1(-m01/det);
        double a10 = Z1(-m10/det);
        double a11 = Z1( m00/det);
        double a02 = Z1((m01*m12 - m02*m11)/det);
        double a12 = Z1((m02*m10 - m00*m12)/det);

        return new Transform6(a00, a01, a10, a11, a02, a12);
    }
    public boolean isIdentity() {
        return (m00 == 1.0 && m01 == 0.0 &&
                m10 == 0.0 && m11 == 1.0 &&
                m02 == 0.0 && m12 == 0.0);
    }
    public String toString() {
        return "Transform6[" + 
            "m00=" + (m00) + ", " +
            "m01=" + (m01) + ", " +
            "m02=" + (m02) + ", " +
            "m10=" + (m10) + ", " +
            "m11=" + (m11) + ", " +
            "m12=" + (m12) + "]";
    }
}
