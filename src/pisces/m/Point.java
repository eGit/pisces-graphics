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
 * A 3 element point that is represented by double precision floating point 
 * x,y,z coordinates.
 *
 */
public class Point
    extends Tuple
{


    public Point(double x, double y, double z)
    {
        super(x,y,z);
    }
    public Point(double[] p)
    {
        super(p);
    }
    public Point(Point p1)
    {
        super(p1);
    }
    public Point(Tuple t1)
    {
        super(t1);
    }
    public Point()
    {
        super();
    }


    /**
     * Returns the square of the distance between this point and point p1.
     * @param p1 the other point 
     * @return the square of the distance
     */
    public final double distanceSquared(Point p1)
    {
        double dx, dy, dz;

        dx = this.x-p1.x;
        dy = this.y-p1.y;
        dz = this.z-p1.z;
        return (dx*dx+dy*dy+dz*dz);
    }
    /**
     * Returns the distance between this point and point p1.
     * @param p1 the other point
     * @return the distance 
     */
    public final double distance(Point p1)
    {
        double dx, dy, dz;

        dx = this.x-p1.x;
        dy = this.y-p1.y;
        dz = this.z-p1.z;
        return Math.sqrt(dx*dx+dy*dy+dz*dz);
    }
    /**
     * Computes the L-1 (Manhattan) distance between this point and
     * point p1.  The L-1 distance is equal to:
     *  abs(x1-x2) + abs(y1-y2) + abs(z1-z2).
     * @param p1 the other point
     * @return  the L-1 distance
     */
    public final double distanceL1(Point p1) {
        return Math.abs(this.x-p1.x) + Math.abs(this.y-p1.y) +
            Math.abs(this.z-p1.z);
    }
    /**
     * Computes the L-infinite distance between this point and
     * point p1.  The L-infinite distance is equal to
     * MAX[abs(x1-x2), abs(y1-y2), abs(z1-z2)].
     * @param p1 the other point
     * @return  the L-infinite distance
     */
    public final double distanceLinf(Point p1) {
        double tmp;
        tmp = Math.max( Math.abs(this.x-p1.x), Math.abs(this.y-p1.y));

        return Math.max(tmp,Math.abs(this.z-p1.z));
    }
    public Point clone(){
        return (Point)super.clone();
    }
}
