import java.io.*;

public class Test {

   public static void main(String args[]) {
      String Str = new String("Welcome to Tutorialspoint.com");
      String SubStr1 = new String("Tutorials" );
      String SubStr2 = new String("Sutorials" );

      System.out.print("Found Last Index :" );
      System.out.println(Str.lastIndexOf( 'o' ));
      System.out.print("Found Last Index :" );
      System.out.println(Str.lastIndexOf( 'o', 5 ));
      System.out.print("Found Last Index :" );
      System.out.println( Str.lastIndexOf( SubStr1 ));
      System.out.print("Found Last Index :" );
      System.out.println( Str.lastIndexOf( SubStr1, 15 ));
      System.out.print("Found Last Index :" );
      System.out.println(Str.lastIndexOf( SubStr2 ));
      System.out.print("Found Last Index :" );
      System.out.println(Str.lastIndexOf( "com" ,1));
      System.out.print("First Last Index :" );
      System.out.println(Str.indexOf( "com" ,20));
   }
}
