import java.sql.*;
import java.util.Scanner;

public class proyectoBD{
    private static final String URL = "jdbc:mysql://localhost:3306/BD_Tienda";
    private static final String USER = "root";
    private static final String PASSWORD = "patrici0";
    static Scanner teclado = new Scanner(System.in);

    public static void inicioSesion() {
        String[] users = {"admin", "usuario"};
        String[] passwords = {"admin", "usuario"};
        boolean inicioExitoso = false;

        System.out.println("Bienvenido al Sistema");
        System.out.print("Ingrese su usuario: ");
        String usuario = teclado.nextLine();
        System.out.print("Ingrese su password: ");
        String password = teclado.nextLine();
        //Verifica los datos ingresados
        for(int i = 0; i < users.length; i++) {
            if(usuario.equals(users[i]) && password.equals(passwords[i])) {
                inicioExitoso = true;
                System.out.println("Inicio de sesión exitoso!");
                menuPrincipal();
                break;
            }
        }
        //si no coinciden los usuarios
        if(!inicioExitoso) {
            System.out.println("Error: Usuario o contraseña incorrectos");
            System.out.println("El programa se cerrará por seguridad");
            System.exit(0);
        }
    }

    public static Connection conectar() {
        Connection conexion = null;
        try {
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a la base de datos");
        } catch (SQLException e) {
            System.out.println("Error al conectar: " + e.getMessage());
        }
        return conexion;
    }

    public static void insertarProducto() {
        String codigoProducto; String nombreProducto;  double precioUnitario;  int cantidadProducto;
        System.out.println("INGRESO DE DATOS A LA BASE DE DATOS DE LA TIENDA");
        System.out.print("Ingrese el número de productos a ingresar: ");
        int cant= teclado.nextInt();
        teclado.nextLine();
        for (int i = 0; i <cant; i++) {
            System.out.print("Ingrese el código del producto: ");
            codigoProducto = teclado.nextLine();
            System.out.print("Ingrese el nombre de producto: ");
            nombreProducto = teclado.nextLine();
            System.out.print("Ingrese el precio del producto: ");
            precioUnitario = teclado.nextDouble();
            System.out.print("Ingrese la cantidad del producto: ");
            cantidadProducto = teclado.nextInt();
            teclado.nextLine();
            String query = "INSERT INTO productos (codigoProducto, nombreProducto, precioUnitario, cantidadProducto) VALUES (?,?, ?, ?)";
            try (Connection con = proyectoBD.conectar(); PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, codigoProducto);
                pst.setString(2, nombreProducto);
                pst.setDouble(3, precioUnitario);
                pst.setInt(4, cantidadProducto);
                pst.executeUpdate();
                System.out.println("Producto insertado correctamente");
            } catch (SQLException e) {
            }
        }
    }

    public static void mostrarProductos() {
        String query = "select * from productos;";
        try (Connection con = proyectoBD.conectar(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(query)) {
            boolean hayResultados = false;
            while (rs.next()) {
                hayResultados = true;
                System.out.println("Código: " + rs.getString("codigoProducto"));
                System.out.println("Nombre: " + rs.getString("nombreProducto"));
                System.out.println("Precio: " + rs.getDouble("precioUnitario"));
                System.out.println("Cantidad: " + rs.getInt("cantidadProducto"));
                System.out.println("");
            }
            if (!hayResultados) {
                System.out.println("No hay productos disponibles.");
            }//fin if

        } catch (SQLException e) {
        }//fin catch
    }

    public static void buscarProducto() {
        System.out.print("Ingrese el codigo del producto: ");
        String codigoProducto = teclado.nextLine();
        String query = "SELECT * FROM productos WHERE codigoProducto = ?";
        try (Connection con = conectar();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, codigoProducto);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    System.out.println("PRODUCTO ENCONTRADO:");
                    System.out.println("Código: " + rs.getString("codigoProducto"));
                    System.out.println("Nombre: " + rs.getString("nombreProducto"));
                    System.out.println("Precio: " + rs.getDouble("precioUnitario"));
                    System.out.println("Cantidad: " + rs.getInt("cantidadProducto"));
                } else {
                    System.out.println("Producto no encontrado.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar producto: " + e.getMessage());
        }
    }

    public static void buscarPorNombre() {
        System.out.print("Ingrese el nombre del producto: ");
        String nombreProducto = teclado.nextLine().trim();
        String query = "SELECT * FROM productos WHERE LOWER(nombreProducto) = LOWER(?)";
        try (Connection con = conectar();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, nombreProducto);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    System.out.println("PRODUCTO ENCONTRADO:");
                    System.out.println("Código: " + rs.getString("codigoProducto"));
                    System.out.println("Nombre: " + rs.getString("nombreProducto"));
                    System.out.println("Precio: " + rs.getDouble("precioUnitario"));
                    System.out.println("Cantidad: " + rs.getInt("cantidadProducto"));
                } else {
                    System.out.println("Producto no encontrado.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar producto: " + e.getMessage());
        }
    }

    public static void actualizarProducto() {
        String codigoProducto = ""; String nombre = ""; double precio = 0; int cantidad = 0;
        System.out.print("Ingrese el codigo del producto: ");
        codigoProducto = teclado.nextLine();
        // First, check if the product exists
        String checkQuery = "SELECT * FROM productos WHERE codigoProducto = ?";
        try (Connection con = conectar();
             PreparedStatement checkPst = con.prepareStatement(checkQuery)) {
            checkPst.setString(1, codigoProducto);
            try (ResultSet rs = checkPst.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("El producto con código " + codigoProducto + " no existe.");
                    return; // Exit the method if the product doesn't exist
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar la existencia del producto: " + e.getMessage());
            return; // Sale del metodo si hay error
        }

        //corre el programa si no hay error
        System.out.print("Ingrese el nuevo nombre: ");
        nombre = teclado.nextLine();
        System.out.print("Ingrese el nuevo precio: ");
        precio = teclado.nextDouble();
        System.out.print("Ingrese la nueva cantidad: ");
        cantidad = teclado.nextInt();
        String updateQuery = "UPDATE productos SET nombreProducto = ?, precioUnitario = ?, cantidadProducto = ? WHERE codigoProducto = ?";
        try (Connection con = conectar();
             PreparedStatement pst = con.prepareStatement(updateQuery)) {
            pst.setString(1, nombre);
            pst.setDouble(2, precio);
            pst.setInt(3, cantidad);
            pst.setString(4, codigoProducto);
            int filasAfectadas = pst.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Producto actualizado correctamente");
            } else {
                System.out.println("No se pudo actualizar el producto. Por favor, intente de nuevo.");
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar producto: " + e.getMessage());
        }
    }

    public static void eliminarProducto() {
        System.out.print("Ingrese el código del producto a eliminar: ");
        String codigoProducto = teclado.nextLine();

        //se verifica si el codigo existe
        String checkQuery = "SELECT nombreProducto FROM productos WHERE codigoProducto = ?";
        try (Connection con = conectar();
             PreparedStatement checkPst = con.prepareStatement(checkQuery)) {
            checkPst.setString(1, codigoProducto);
            try (ResultSet rs = checkPst.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("No se encontró el producto con el código especificado");
                    return; //sale de la verificacion si no se encuentra
                }
                String nombreProducto = rs.getString("nombreProducto");

                // confirma la eliminacion
                System.out.printf("¿Está seguro que desea eliminar el producto '%s' (código: %s)? (si/no): ",
                        nombreProducto, codigoProducto);
                String confirmacion = teclado.nextLine().trim().toLowerCase();

                if (!confirmacion.equals("si")) {
                    System.out.println("Operación de eliminación cancelada.");
                    return; // se cancela la eliminacion
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar la existencia del producto: " + e.getMessage());
            return; //catch si hay error en la query
        }
        // si el usuario confirma
        String deleteQuery = "DELETE FROM productos WHERE codigoProducto = ?";
        try (Connection con = conectar();
             PreparedStatement pst = con.prepareStatement(deleteQuery)) {
            pst.setString(1, codigoProducto);
            int filasAfectadas = pst.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Producto eliminado correctamente");
            } else {
                System.out.println("No se pudo eliminar el producto. Por favor, intente de nuevo.");
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar producto: " + e.getMessage());
        }
    }

    public static void realizarVenta() {
        System.out.println("REALIZAR NUEVA VENTA");
        System.out.print("Ingrese el código del producto: ");
        String codigoProducto = teclado.nextLine();

        // Primero verificamos si existe el producto y hay stock suficiente
        try (Connection con = conectar()) {
            String checkQuery = "SELECT * FROM productos WHERE codigoProducto = ?";
            try (PreparedStatement checkPst = con.prepareStatement(checkQuery)) {
                checkPst.setString(1, codigoProducto);
                ResultSet rs = checkPst.executeQuery();

                if (!rs.next()) {
                    System.out.println("Producto no encontrado.");
                    return;
                }

                int stockActual = rs.getInt("cantidadProducto");
                double precioUnitario = rs.getDouble("precioUnitario");
                String nombreProducto = rs.getString("nombreProducto");

                System.out.println("\nProducto encontrado: " + nombreProducto);
                System.out.println("Stock disponible: " + stockActual);
                System.out.println("Precio unitario: Q" + precioUnitario);

                System.out.print("\nIngrese la cantidad a vender: ");
                int cantidadVenta = teclado.nextInt();
                teclado.nextLine(); // Limpiar buffer

                if (cantidadVenta > stockActual) {
                    System.out.println("Error: No hay suficiente stock disponible.");
                    return;
                }

                double precioTotal = cantidadVenta * precioUnitario;

                // Registrar la venta
                String insertVentaQuery = "INSERT INTO ventas (codigoProducto, cantidadVendida, precioTotal) VALUES (?, ?, ?)";
                try (PreparedStatement ventaPst = con.prepareStatement(insertVentaQuery)) {
                    ventaPst.setString(1, codigoProducto);
                    ventaPst.setInt(2, cantidadVenta);
                    ventaPst.setDouble(3, precioTotal);
                    ventaPst.executeUpdate();
                }

                // Actualizar el stock
                String updateStockQuery = "UPDATE productos SET cantidadProducto = cantidadProducto - ? WHERE codigoProducto = ?";
                try (PreparedStatement updatePst = con.prepareStatement(updateStockQuery)) {
                    updatePst.setInt(1, cantidadVenta);
                    updatePst.setString(2, codigoProducto);
                    updatePst.executeUpdate();
                }

                System.out.println("\nVenta realizada con éxito!");
                System.out.println("Total de la venta: Q" + precioTotal);

            }
        } catch (SQLException e) {
            System.out.println("Error al realizar la venta: " + e.getMessage());
        }
    }

    public static void mostrarTodasLasVentas() {
        // Método para mostrar todas las ventas
        String query = "SELECT v.*, p.nombreProducto FROM ventas v " +
                "JOIN productos p ON v.codigoProducto = p.codigoProducto " +
                "ORDER BY v.fechaVenta DESC";

        try (Connection con = conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            boolean hayVentas = false;
            double totalVentas = 0;

            System.out.println("\nREPORTE DE VENTAS");
            System.out.println("--------------------");

            while (rs.next()) {
                hayVentas = true;
                System.out.println("ID Venta: " + rs.getInt("idVenta"));
                System.out.println("Producto: " + rs.getString("nombreProducto"));
                System.out.println("Cantidad: " + rs.getInt("cantidadVendida"));
                System.out.println("Total: Q" + rs.getDouble("precioTotal"));
                System.out.println("Fecha: " + rs.getTimestamp("fechaVenta"));
                System.out.println("--------------------");

                totalVentas += rs.getDouble("precioTotal");
            }

            if (hayVentas) {
                System.out.println("\nTotal de todas las ventas: Q" + totalVentas);
            } else {
                System.out.println("No hay ventas registradas.");
            }

        } catch (SQLException e) {
            System.out.println("Error al mostrar las ventas: " + e.getMessage());
        }
    }

    public static void menuPrincipal(){
        Scanner teclado = new Scanner(System.in);
        int opcion=0;
        do{
            System.out.println("BIENVENIDO AL SISTEMA DE DATOS DE DATOS DE TIENDA");
            System.out.println("MENU PRINCIPAL");
            System.out.println("1. Ingresar Producto");
            System.out.println("2. Mostrar Productos");
            System.out.println("3. Buscar Producto por Código");
            System.out.println("4. Buscar Producto por Nombre");
            System.out.println("5. Modificar Producto");
            System.out.println("6. Eliminar Producto");
            System.out.println("7. Realizar Venta");;
            System.out.println("8. Mostrar Todas las Ventas");
            System.out.println("9. Salir del Menú Principal");
            System.out.print("Ingrese una opción del menu: ");
            opcion= teclado.nextInt();
            switch(opcion){
                case 1: insertarProducto();
                    break;
                case 2: mostrarProductos();
                    break;
                case 3: buscarProducto();
                    break;
                case 4: buscarPorNombre();
                    break;
                case 5: actualizarProducto();
                    break;
                case 6: eliminarProducto();
                    break;
                case 7: realizarVenta();
                    break;
                case 8: mostrarTodasLasVentas();
                    break;
            }
        }while(opcion!=9);
        System.out.println("Saliendo... ¡Gracias por utilizar el sistema de gestion de base de datos!");
    }

    public static void main(String[] args) {
        inicioSesion();
    }
}

