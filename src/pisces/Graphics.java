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

import pisces.d.NativeSurface;
import pisces.d.PiscesRenderer;
import pisces.m.Matrix;
import pisces.png.Encoder;

/**
 * Pisces user interface, similar to Graphics2D and friends.  Produces
 * PNG images.
 * 
 * @see Path
 * @see Polygon
 */
public class Graphics
    extends Object
    implements Cloneable
{

    public final int width, height;

    protected final NativeSurface surface;

    private PiscesRenderer renderer;


    public Graphics(int w, int h){
        super();
        if (0 < w && 0 < h){
            this.width = w;
            this.height = h;
            this.surface = new NativeSurface(w,h);
            this.renderer = new PiscesRenderer(this.surface);
        }
        else
            throw new IllegalArgumentException();
    }
    public Graphics(Image img){
        super();
        if (null != img){
            this.width = img.getWidth();
            this.height = img.getHeight();
            this.surface = img;
            this.renderer = new PiscesRenderer(img);
        }
        else
            throw new IllegalArgumentException();
    }


    public final byte[] toPNG(){

        Encoder png = new Encoder(this.surface);
        return png.encode();
    }
    public final byte[] toPNG(boolean alpha){

        Encoder png = new Encoder(this.surface,alpha);
        return png.encode();
    }
    public final byte[] toPNG(boolean alpha, int compression){

        Encoder png = new Encoder(this.surface,alpha,Encoder.FILTER_NONE,compression);
        return png.encode();
    }
    public final Graphics create(){
        return this.clone();
    }
    public final Graphics create(double x, double y, double w, double h){
        return this.create().setClip(x,y,w,h);
    }
    public final Graphics setAntialiasing(boolean antialiasingOn) {
        this.renderer.setAntialiasing(antialiasingOn);
        return this;
    }
    public final boolean getAntialiasing() {
        return this.renderer.getAntialiasing();
    }
    public final Graphics setColor(Color color){
        this.renderer.setColor(color.red, color.green, color.blue, color.alpha);
        return this;
    }
    public final Graphics setColor(int argb){
        int a = (argb >>> 24) & 0xff;
        if (0 == a)
            a = 255;
        int r = (argb >>> 16) & 0xff;
        int g = (argb >>> 8) & 0xff;
        int b = (argb & 0xff);
        this.setColor(r,g,b,a);
        return this;
    }
    public final Graphics setColor(int red, int green, int blue, int alpha) {
        this.renderer.setColor(red, green, blue, alpha);
        return this;
    }
    public final Graphics setColor(int red, int green, int blue) {
        this.renderer.setColor(red, green, blue);
        return this;
    }
    public final Graphics setTransform(Matrix transform) {
        this.renderer.setTransform(transform);
        return this;
    }
    public final Matrix getTransform() {
        return this.renderer.getTransform();
    }
    public final Graphics setClip(double minX, double minY, double width, double height) {
        this.renderer.setClip(minX, minY, width, height);
        return this;
    }
    public final Graphics resetClip() {
        this.renderer.resetClip();
        return this;
    }
    public final Graphics setStroke(){
        this.renderer.setStroke();
        return this;
    }
    public final Graphics setFill(){
        this.renderer.setFill();
        return this;
    }
    public final Graphics moveTo(double x0, double y0) {
        this.renderer.moveTo(x0, y0);
        return this;
    }
    public final Graphics lineTo(double x1, double y1) {
        this.renderer.lineTo(x1, y1);
        return this;
    }
    public final Graphics lineJoin() {
        this.renderer.lineJoin();
        return this;
    }
    public final Graphics quadTo(double x1, double y1, double x2, double y2) {
        this.renderer.quadTo(x1, y1, x2, y2);
        return this;
    }
    public final Graphics cubicTo(double x1, double y1, double x2, double y2, double x3, double y3) {
        this.renderer.cubicTo(x1, y1, x2, y2, x3, y3);
        return this;
    }
    public final Graphics getBoundingBox(double[] bbox) {
        this.renderer.getBoundingBox(bbox);
        return this;
    }
    public final Graphics drawPath(Path p){
        if (null != p){
            this.setStroke();
            this.renderer.beginRendering(p.windingRule);
            p.produce(this.renderer);
            this.renderer.endRendering();
            return this;
        }
        else
            throw new IllegalArgumentException();
    }
    public final Graphics fillPath(Path p){
        if (null != p){
            this.setFill();
            this.renderer.beginRendering(p.windingRule);
            p.produce(this.renderer);
            this.renderer.endRendering();
            return this;
        }
        else
            throw new IllegalArgumentException();
    }
    public final Graphics drawLine(double x0, double y0, double x1, double y1) {
        this.renderer.drawLine(x0, y0, x1, y1);
        return this;
    }
    public final Graphics fillRect(double x, double y, double w, double h) {
        this.renderer.fillRect(x, y, w, h);
        return this;
    }
    public final Graphics drawRect(double x, double y, double w, double h) {
        this.renderer.drawRect(x, y, w, h);
        return this;
    }
    public final Graphics drawOval(double x, double y, double w, double h) {
        this.renderer.drawOval(x, y, w, h);
        return this;
    }
    public final Graphics fillOval(double x, double y, double w, double h) {
        this.renderer.fillOval(x, y, w, h);
        return this;
    }
    public final Graphics drawArc(double x, double y, double width, double height,
                              double startAngle, double arcAngle, int arcType)
    {
        this.renderer.drawArc(x, y, width, height,
                              startAngle, arcAngle, arcType);
        return this;
    }
    public final Graphics fillArc(double x, double y, double width, double height,
                              double startAngle, double arcAngle, int arcType)
    {
        this.renderer.fillArc(x, y, width, height,
                              startAngle, arcAngle, arcType);
        return this;
    }
    public final Graphics fillOrDrawArc(double x, double y, double width, double height,
                                    double startAngle, double arcAngle, int arcType,
                                    boolean stroke)
    {
        this.renderer.fillOrDrawArc(x, y, width, height,
                                    startAngle, arcAngle, arcType,
                                    stroke);
        return this;
    }
    public final Graphics clearRect(double x, double y, double w, double h) {
        this.renderer.clearRect(x, y, w, h);
        return this;
    }
    protected Graphics clone(){
        try {
            Graphics clone = (Graphics)super.clone();
            clone.renderer = this.renderer.clone();
            return clone;
        }
        catch (CloneNotSupportedException err){
            throw new InternalError();
        }
    }
}
