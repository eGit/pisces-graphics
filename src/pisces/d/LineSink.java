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
 * The {@link LineSink} interface accepts a series of line
 * drawing commands: <code>moveTo</code>, <code>lineTo</code>,
 * <code>close</code> (equivalent to a <code>lineTo</code> command
 * with an argument equal to the argument of the last
 * <code>moveTo</code> command), and <code>end</code>.
 *
 * <p> A {@link Flattener} may be used to connect a general path
 * source to a {@link LineSink}.
 *
 * <p> The {@link Renderer} class implements the
 * {@link LineSink} interface.
 *
 */
public abstract class LineSink
    extends FXMath
    implements Cloneable
{
    /**
     * Moves the current drawing position to the point (x0, y0).
     *
     * @param x0 the X coordinate 
     * @param y0 the Y coordinate 
     */
    public abstract void moveTo(double x0, double y0);
    /**
     * Provides a hint that the current segment should be joined to
     * the following segment using an explicit miter or round join if
     * required.
     *
     * <p> An application-generated path will generally have no need
     * to contain calls to this method; they are typically introduced
     * by a {@link Flattener} to mark segment divisions that appear in
     * its input, and consumed by a {@link Stroker} that is
     * responsible for emitting the miter or round join segments.
     *
     * <p> Other {@link LineSink} classes should simply pass this hint
     * to their output sink as needed.
     */
    public abstract  void lineJoin();
    /**
     * Draws a line from the current drawing position to the point
     * <code>(x1, y1)</code> and sets the current drawing position to
     * <code>(x1, y1)</code>.
     *
     * @param x1 the X coordinate 
     * @param y1 the Y coordinate 
     */
    public abstract  void lineTo(double x1, double y1);
    /**
     * Closes the current path by drawing a line from the current
     * drawing position to the point specified by the most recent
     * <code>moveTo</code> command.
     */
    public abstract void close();
    /**
     * Ends the current path.  It may be necessary to end a path in
     * order to allow end caps to be drawn.
     */
    public abstract void end();

    public abstract void dispose();

    public LineSink clone(){
        try {
            return (LineSink)super.clone();
        }
        catch (CloneNotSupportedException err){
            throw new InternalError();
        }
    }
}

