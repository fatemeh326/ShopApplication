package source.products;

import java.sql.*;

public class ProductDataBase {
    //TODO make sure connection between all contributors works properly which won't right now
    private static Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/shop", "root", "6X0db8y3L&&J");
    }

    public static void insertProduct(Product product) {
        String SQL = "INSERT INTO shop.products (name, brand, price, quantity, image_address, product_type, description) values (?, ?, ?, ?, ?, ?, ?);";
        int id;
        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getBrand());
            preparedStatement.setDouble(3, product.getPrice());
            preparedStatement.setInt(4, product.getQuantity());
            preparedStatement.setString(5, product.getImageAddress());
            preparedStatement.setInt(6, product.getType());
            preparedStatement.setString(7, product.getDescription());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getInt(1);
                        product.setId(id - 1);
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static Product getProduct(int id) {
        String SQL = "SELECT * FROM products WHERE product_id = " + id;
        Product result = null;
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            ResultSet resultSet;
            resultSet = statement.executeQuery(SQL);
            if (resultSet.next()) {
                result = new Product(id, resultSet.getString("name").trim(), resultSet.getString("brand").trim(), resultSet.getDouble("price"), resultSet.getInt("quantity"), resultSet.getString("image_address").trim(), resultSet.getDouble("score"), resultSet.getInt("vote_count"), resultSet.getInt("product_type"), resultSet.getString("description").trim());
                resultSet.close();
                statement.close();
                connection.close();
            } else {
                //TODO: product with this id doesn't exist
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }

    public static Product[] getAllProductsMainInfo(String extra) {
        String SQL = "SELECT count(product_id) FROM products";
        Product[] result;
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            ResultSet resultSet;
            resultSet = statement.executeQuery(SQL);
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                System.out.println(count);
                result = new Product[count];
                //System.out.println(count);
                SQL = "SELECT product_id ,name, price, score, image_address FROM products" + " " + extra;
                System.out.println(SQL);
                resultSet = statement.executeQuery(SQL);
                for (int i = 0; i < count; i++) {
                    resultSet.next();
                    result[i] = new Product(resultSet.getInt("product_id"), resultSet.getString("name").trim(), null, resultSet.getDouble("price"), 0, resultSet.getString("image_address").trim(), resultSet.getDouble("score"), 0, 0, null);
                    //System.out.println(result[i] + "\n");
                }
                resultSet.close();
                statement.close();
                connection.close();
                return result;
            }
            else {
                //TODO: no product exist.
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }
}