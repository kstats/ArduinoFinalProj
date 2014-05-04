import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TestDB {
static int test = 10;
public static void main (String[] args) throws Exception{


    // Accessing Driver From Jar File
    Class.forName("com.mysql.jdbc.Driver");

    //DB Connection
    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test","root","");  

    // MySQL Query
    PreparedStatement statement= con.prepareStatement("INSERT INTO `systems`.`tempmoi` (`id`, `temp`, `pressure`, `humidity`,  `gps`, `Light`) "
    		+ "VALUES ('"+test+"', '34', '36', '50', '36', '35');");

    //Creating Variable to execute query
    int result=statement.executeUpdate();

   // while(result.next()){

      //  System.out.println(result.getString(2));

   // }

  }


  }
