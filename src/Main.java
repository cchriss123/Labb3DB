import java.sql.*;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        boolean isShowMenu = true;
        while (isShowMenu) {
            String menuText = """
                     1. Visa
                     2. Lägg till 
                     3. Uppdatera
                     4. Ta bort
                     5. Sök
         
                     e. Avsluta programmet.
                    """;
            System.out.println(menuText);

            switch (sc.nextLine()) {
                case "1" -> showMenu();
                case "2" -> addMenu();
                case "3" -> updateMenu();
                case "4" -> deleteMenu();
                case "5" -> searchMenu();

                case "e", "E" -> {
                    isShowMenu = false;
                    System.out.println("Avslutar");
                }
                default -> System.out.println("Felaktigt val");
            }
        }
    }


    // Show
    private static void showMenu() {
        System.out.println("""
				1. Visa alla tabeller
				2. Visa album och kategori
				3. Räkna album
				""");

        switch (sc.nextLine()){
            case "1" -> {selectCategory(); selectAlbum(); pressEnter();}
            case "2" -> showAlbumCategory();
            case "3" -> countAlbums();
        }

    }
    private static void selectCategory(){

        String sql = "SELECT * FROM category";


        try {
            Connection conn = connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            System.out.println("Kategorier: ");
            while (rs.next()) {
                System.out.println(
                        "Category ID " + rs.getInt("categoryId") +
                                ": " + rs.getString("categoryName"));
            }
            System.out.println();
        }
        catch (SQLException e) {
        }

    }
    private static void selectAlbum(){
        String sql = "SELECT * FROM album";

        try {
            Connection conn = connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            System.out.println("Skivor: ");
            while (rs.next()) {
                System.out.println("Album ID: " + rs.getInt("albumId") +  "\t" +
                        "Album name: " + rs.getString("albumName") + "\t" +
                        "Artist: " + rs.getString("albumArtist") + "\t" +
                        "Pris: " + rs.getString("albumPrice") + "\t" +
                        "Category ID: " + rs.getInt("albumCategoryID"));
            }
            System.out.println();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void countAlbums() {

        String sql = "SELECT COUNT(*) FROM album";

        try (Connection conn = connect();
             Statement query = conn.createStatement()) {
            ResultSet rs = query.executeQuery(sql);

            while (rs.next()) {
                System.out.println("Antal skivor i databas: " + rs.getInt("COUNT(*)"));

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        pressEnter();
    }
    private static void showAlbumCategory() {
        String sql = "SELECT * FROM album INNER JOIN category ON album.albumCategoryID = category.categoryId";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                System.out.println(rs.getString("albumName") + ":\t" +
                        "Category: " + rs.getString("categoryName")+ ".\t" +
                        "Artist: " + rs.getString("albumArtist") + ".\t" +
                        "Price: " + rs.getInt("albumPrice")+ "\n");
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        pressEnter();
    }


    // Search
    private static void searchMenu() {
        System.out.println("""
				Vad vill du söka efter?
				1. Album efter artistnamn
				2. Album efter kategorinamn
				3. Album efter kategori Id
				""");

        switch (sc.nextLine()){
            case "1" -> searchAlbumByArtist();
            case "2" -> searchAlbumByCategory();
            case "3" -> searchAlbumByCategoryId();
        }
    }
    private static void searchAlbumByArtist(){

        System.out.println("Skriv in artistens namn");
        String sql = "SELECT * FROM album WHERE albumArtist = ? ";

        try (
                Connection conn = connect();
                PreparedStatement pstmt  = conn.prepareStatement(sql)){

            String inputArist = sc.nextLine();

            pstmt.setString(1,inputArist);

            ResultSet rs  = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getString("albumName") + ".\t" +
                        rs.getString("albumArtist") + ".\t" + "Price:" +
                        rs.getString("albumPrice"));
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        pressEnter();
    }
    private static void searchAlbumByCategory() {

        System.out.println("Skriv in kategorinamn");
        String sql = "SELECT * FROM category INNER JOIN album ON category.categoryId =  album.albumCategoryID WHERE categoryName = ?";


        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String inputCaterogy = sc.nextLine();
            pstmt.setString(1,inputCaterogy);
            ResultSet rs  = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println(
                        "Category: " + rs.getString("categoryName") + "\t" +
                                "Album: " + rs.getString("albumName") + "\n");
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        pressEnter();
    }
    private static void searchAlbumByCategoryId() {

        selectCategory();
        System.out.println("Skriv in kategori ID");
        String sql = "SELECT * FROM category INNER JOIN album ON category.categoryId =  album.albumCategoryID WHERE categoryId = ?";


        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int inputCaterogy = parseInput();
            pstmt.setInt(1,inputCaterogy);
            ResultSet rs  = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println(
                        "Category: " + rs.getString("categoryName") + "\t" +
                                "Album: " + rs.getString("albumName") + "\n");
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        pressEnter();
    }


    // Add methods
    private static void addMenu() {
        System.out.println("""
				To what table do you want to add?
				1. Category
				2. Album
				
				""");

        switch (sc.nextLine()){
            case "1" -> addCategory();
            case "2" -> addAlbum();
        }
    }
    private static void addCategory() {
        selectCategory();
        System.out.println("\nEnter category name: ");
        String name = sc.nextLine();
        insertCategory(name);
    }
    private static void addAlbum() {
        selectAlbum();

        System.out.println("Skriv in namnet på skivan: ");
        String inputName = sc.nextLine();

        System.out.println("Skriv in skivans artist: ");
        String inputArtist = sc.nextLine();

        System.out.println("Skriv in pris på skivan: ");
        int inputPris = parseInput();

        selectCategory();
        System.out.println("Skriv in kategori id: ");
        int categoryId = parseInput();

        insertAlbum(inputName, inputArtist, inputPris, categoryId);
    }
    private static void insertAlbum(String titel, String artist, int pris, int categoryId) {

        String sql = "INSERT INTO album(albumName, albumArtist, albumPrice, albumCategoryID) " +
                "VALUES(?,?,?,?)";


        try{
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, titel);
            pstmt.setString(2, artist);
            pstmt.setInt(3, pris);
            pstmt.setInt(4, categoryId);

            pstmt.executeUpdate();
            System.out.println("Du har lagt till en ny skiva.");
            pressEnter();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
    private static void insertCategory(String name) {

        String sql = "INSERT INTO category(categoryName) VALUES(?)";

        try (Connection conn = connect();
             PreparedStatement query = conn.prepareStatement(sql)) {
            query.setString(1, name);

            query.executeUpdate();
            System.out.println(name + " added");
            pressEnter();

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    // Update methods
    private static void updateMenu() {
        System.out.println("""
				To what do you want to update?
				1. Category name
				2. Album price
				3. Album category
				
				""");

        switch (sc.nextLine()){
            case "1" -> updateCategoryName();
            case "2" -> updateAlbumPrice();
            case "3" -> updateAlbumCategory();
        }
    }
    private static void updateCategoryName() {
        selectCategory();

        System.out.println("\nEnter category ID: ");
        int id = parseInput();
        System.out.println("\nEnter category name: ");
        String name = sc.nextLine();

        String sql = "UPDATE category SET categoryName = ? WHERE categoryId = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            System.out.println("Du har uppdaterat valt album");
            pressEnter();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void updateAlbumPrice() {
        selectAlbum();

        System.out.println("\nEnter album ID: ");
        int id = parseInput();
        System.out.println("\nEnter new price: ");
        int price = parseInput();

        String sql = "UPDATE album SET albumPrice = ? WHERE albumId = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, price);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            System.out.println("Du har uppdaterat valt album");
            pressEnter();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void updateAlbumCategory() {
        selectAlbum();

        System.out.println("\nEnter album ID: ");
        int id = parseInput();
        selectCategory();
        System.out.println("\nEnter new category ID: ");
        int categoryId = parseInput();

        String sql = "UPDATE album SET albumCategoryID = ? WHERE albumId = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, categoryId);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            System.out.println("Du har uppdaterat valt album till att tillhöra en ny kategori");
            pressEnter();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }


    // Update methods
    private static void deleteMenu() {
        System.out.println("""
				To what do you want to delete?
				1. Album
				2. Category
				
				""");
        switch (sc.nextLine()){
            case "1" -> removeAlbum();
            case "2" -> removeCategory();
        }
    }
    private static void removeAlbum(){

        selectAlbum();
        System.out.println("Skriv in id:t på skivan som ska tas bort: ");
        int inputId = parseInput();
        deleteAlbum(inputId);
    }
    private static void removeCategory(){

        selectCategory();
        System.out.println("Skriv in id:t på skivan som ska tas bort: ");
        int inputId = parseInput();
        deleteCategory(inputId);
    }
    private static void deleteCategory(int id) {
        String sql = "DELETE FROM category WHERE categoryId = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Du har tagit bort kategorin\n");
            pressEnter();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void deleteAlbum(int id) {
        String sql = "DELETE FROM album WHERE albumId = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Du har tagit bort skivan\n");
            pressEnter();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    // Other methods
    private static Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:/Users/macbook/labb3DB.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    public static void pressEnter() {
        System.out.println("\nPress \"ENTER\" to continue...");
        sc.nextLine();
    }
    private static int parseInput() {
        while (true){
            try {
                return Integer.parseInt(sc.nextLine());
            }
            catch (Exception e) {
                System.out.println("Var vänlig skriv in ett heltal.");
            }
        }
    }


}
