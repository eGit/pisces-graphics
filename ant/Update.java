/*
 * Gap Data
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Accept a file path source, and a target path expression to copy the
 * source file to.
 * @author jdp
 */
public class Update {

    public static void usage(){
        System.err.println("Usage");
        System.err.println();
        System.err.println("  Update source[':'source] [target[':'target]*]");
        System.err.println();
        System.err.println("Description");
        System.err.println();
        System.err.println("  Copy one or more source files into the");
        System.err.println("  target directories.");
        System.err.println();
        System.err.println("  For no targets found on the cmd line, exit");
        System.err.println("  silently.");
        System.err.println();
    }
    public static void main(String[] argv){
        if (1 < argv.length){

            File[] sources = List(argv[0]);
            if (null != sources){
                for (File src: sources){
                    File[] targets = List(src,argv);
                    if (null != targets){
                        final long srclen = src.length();
                        try {
                            FileChannel source = new FileInputStream(src).getChannel();
                            try {
                                for (File tgt: targets){

                                    FileChannel target = new FileOutputStream(tgt).getChannel();
                                    try {
                                        source.transferTo(0L,srclen,target);
                                    }
                                    finally {
                                        target.close();
                                    }
                                    System.out.printf("Updated %s\n",tgt.getPath());
                                }
                            }
                            finally {
                                source.close();
                            }
                        }
                        catch (IOException exc){
                            exc.printStackTrace();
                            System.exit(1);
                        }
                    }
                }
                System.exit(0);
            }
            else {
                System.err.printf("Source file(s) not found in '%s'\n",argv[0]);
                System.exit(1);
            }
        }
        else {
            usage();
            System.exit(1);
        }
    }

    private final static File[] List(String path){
        File[] list = null;
        String[] files = path.split(":");
        for (int ccc = 0, ccz = files.length; ccc < ccz; ccc++){

            File tgt = new File(files[ccc]);

            if (tgt.isFile()){
                list = Add(list,tgt);
            }
        }
        return list;
    }
    private final static File[] List(File src, String[] argv){
        File[] list = null;
        for (int cc = 1, count = argv.length; cc < count; cc++){
            String[] files = argv[cc].split(":");
            for (int ccc = 0, ccz = files.length; ccc < ccz; ccc++){

                File tgt = new File(files[ccc]);

                if (tgt.isDirectory()){
                    tgt = new File(tgt,src.getName());
                }

                list = Add(list,tgt);
            }
        }
        return list;
    }
    public final static File[] Add(File[] list, File item){
        if (null == item)
            return list;
        else if (null == list)
            return new File[]{item};
        else {
            int len = list.length;
            File[] copier = new File[len+1];
            System.arraycopy(list,0,copier,0,len);
            copier[len] = item;
            return copier;
        }
    }
}
