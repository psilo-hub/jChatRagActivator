package free.svoss.tools.jChatRagActivator;

import com.msiops.ground.crockford32.Crockford32;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.io.Console;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        AnsiConsole.systemInstall();
        Runtime.getRuntime().addShutdownHook(new Thread(AnsiConsole::systemUninstall));


        System.out.println(Ansi.ansi().eraseScreen().fgBrightGreen().a( "--- ").fgBrightBlue().a("Activation code generator").fgBrightGreen().a(" ---\n\n").reset() );
        System.out.println(Ansi.ansi().fgBrightMagenta().a( "Enter password :").reset() );



        Console cnsl = System.console();

        // Read password
        String pw = new String(cnsl.readPassword( "> "));

        byte[] pwHash = Hashing.getSha256(pw.getBytes(StandardCharsets.UTF_16));
        String hexHash = Hashing.bytesToHex(pwHash);

        if(!hexHash.contains("4BA1F65ABB7A2AE8C58F4568F087")){
            System.out.println(Ansi.ansi().fgBrightRed().a( "\nINVALID PASSWORD").reset() );
            System.exit(1);
        }else {

            System.out.println(Ansi.ansi().fgBrightMagenta().a( "Enter code :").reset() );
            String salt = cnsl.readLine( "> ").trim();

            BigInteger eXor = new BigInteger("179955781");

            byte[] eXorBytes = eXor.toByteArray();

            byte[] eBytesBack = new byte[eXorBytes.length];
            for(int i=0;i< eXorBytes.length;i++)
                eBytesBack[i]=(byte)( eXorBytes[i]^pwHash[i]);
            BigInteger eBack = new BigInteger(eBytesBack);




            BigInteger N = new BigInteger("26272165929377230989024523873");

            SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");

            System.out.println(Ansi.ansi().fgBrightMagenta().a( "\nEnter expiration date ").reset() );
            System.out.println(Ansi.ansi().fgBrightMagenta().a( "Use the following format:").reset() );
            System.out.println(Ansi.ansi().fgCyan().a( sdf1.format(new Date())+"\n").reset() );
            String dateString = cnsl.readLine( "> ").trim();

            Date date = null;

            try{
                date=sdf1.parse(dateString);
            }catch (Exception ignored){}

            if(date==null){
                Log.f("Invalid date format");
                System.exit(1);
            }else{
                String dateShort = new SimpleDateFormat("yyyyMMdd").format(date);
                dateShort=dateShort.substring(2);
                String activationUnencrypted = dateShort+"%"+salt;
                BigInteger activationBI = new BigInteger(activationUnencrypted.getBytes(StandardCharsets.UTF_8));
                BigInteger activationEnc = activationBI.modPow(eBack,N);

                String activationCrock = Crockford32.encode(activationEnc);

                System.out.println(activationCrock+"\n\n");

                try {
                    new ASCIIArtGenerator().printTextArt(activationCrock, ASCIIArtGenerator.ART_SIZE_SMALL, ASCIIArtGenerator.ASCIIArtFont.ART_FONT_DIALOG,"█");
                    System.out.println("\n");
                } catch (Exception ignored) { }
            }
        }
    }
}
