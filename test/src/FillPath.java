
import pisces.Color;
import pisces.Graphics;
import pisces.Polygon;

/**
 * Creates a 300x300 PNG image 
 */
public class FillPath
    extends pisces.Image
{

    public FillPath(){
        super(300,300);

        Graphics g = this.createGraphics();
        g.setAntialiasing(true);

        g.setColor(Color.White);
        g.fillRect(0, 0, 300, 300);

        Polygon.Square square = new Polygon.Square(100,100,100);
        g.setColor(Color.Black);
        g.fillPath(square);
    }


    public static void main(String[] argv){

        if (1 == argv.length){
            try {
                java.io.File out = new java.io.File(argv[0]);

                FillPath img = new FillPath();
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
            System.err.println("Usage: FillPath out-file.png");
            System.exit(1);
        }
    }
}
