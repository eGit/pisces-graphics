
import pisces.Color;
import pisces.Graphics;
import pisces.Font;
import pisces.Polygon;
import pisces.m.Matrix;

/**
 * Creates a 300x300 PNG image 
 */
public class ABC
    extends pisces.Image
{

    public ABC()
        throws java.io.IOException
    {
        super(300,300);

        Graphics g = this.createGraphics();
        g.setAntialiasing(true);

        g.setColor(Color.White);
        g.fillRect(0, 0, 300, 300);

        Font font = new Font("sun12x22.psfu");

        g.setFont(font);
        g.setColor(Color.Black);

        g.blit("ABC",20,20,1.0f);

    }


    public static void main(String[] argv){

        if (1 == argv.length){
            try {
                java.io.File out = new java.io.File(argv[0]);

                ABC img = new ABC();
                byte[] png = img.toPNG();
                java.io.OutputStream os = new java.io.FileOutputStream(out);
                try {
                    os.write(png,0,png.length);
                    os.flush();
                }
                finally {
                    os.close();
                }
                System.out.println(out.getPath());
                System.exit(0);
            }
            catch (Exception any){
                any.printStackTrace();
                System.exit(1);
            }
        }
        else {
            System.err.println("Usage: ABC out-file.png");
            System.exit(1);
        }
    }
}
