package p1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Scanner;

public class Conexion {
	public static final String SEPARADOR = ",";
	static String[] campos;
	static String nombre = "jdbc";
	static String password = "jdbc1";
	static String bd = "usuarios";

	public static void main(String[] args) {
		Connection conn = null;
		int opc;
		String url = "jdbc:mysql://localhost:3306/" + bd;
		try {
			// Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(url, nombre, password);
			if (conn != null) {
				System.out.println("Conexion establecida con la base de datos");
				System.out.println("");
				do {
					System.out.println("BasedeDatos " + bd);
					opc = menu();
					switch (opc) {
					case 1:
						insetar(conn);
						break;
					case 2:
						borrar(conn);
						break;
					case 3:
						mostrarTodo(conn);
						break;
					case 4:
						actualizarUsuario(conn);
						break;
					case 5:
						agregarFichero("/home/tigre/usuarios.csv", conn);
						break;
					case 6:
						System.out.println("Saliendo de la aplicacion");
						conn.close();
						if (conn.isClosed())
							System.out.println("Desconexion de la base de datos");
						break;
					default:
						System.out.println("No esta esa opcion");
						break;
					}
				} while (opc != 6);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Conexion Fallida");
			e.printStackTrace();
		}
	}

	public static int menu() {
		Scanner sc = new Scanner(System.in);
		System.out.println("1.Insertar");
		System.out.println("2.Borrar");
		System.out.println("3.Mostrar");
		System.out.println("4.Actualizar");
		System.out.println("5.InsertarArchivo");
		System.out.println("6.Salir");
		System.out.println("");
		System.out.println("Elige una opcion");
		return sc.nextInt();
	}

	public static void insetar(Connection conn) {
		Scanner sc = new Scanner(System.in);
		PreparedStatement sentencia = null;
		System.out.println("Introdece nombre");
		String nombre = sc.nextLine();
		System.out.println("Introdece apellidos");
		String apellidos = sc.nextLine();
		System.out.println("Introdece DNI");
		String dni = sc.nextLine();
		System.out.println("introduce año");
		String año = sc.nextLine();
		System.out.println("introduce mes");
		String mes = sc.nextLine();
		System.out.println("introduce dia");
		String dia = sc.nextLine();
		System.out.println("fecha nacimirnto");
		String fecha_nac = año + "-" + mes + "-" + dia;

		String consulta = "INSERT INTO usuario (nombre,apellidos,dni,fecha_nac) VALUES (" + '"' + nombre + '"' + ","
				+ '"' + apellidos + '"' + "," + '"' + dni + '"' + "," + "'" + fecha_nac + "');";
		try {
			sentencia = conn.prepareStatement(consulta);
			sentencia.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Inerccion ha fallado");
			e.printStackTrace();
		} finally {
			try {
				sentencia.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void borrar(Connection conn) {
		Scanner sc = new Scanner(System.in);
		PreparedStatement sentencia = null;
		System.out.println("introduce nombre y apellidos quire eliminar");
		System.out.println("nombre?");
		String nombre = sc.nextLine();
		System.out.println("apellido?");
		String apellido = sc.nextLine();
		String consulta = "DELETE FROM usuario where nombre = " + '"' + nombre + '"' + " and apellidos = " + '"'
				+ apellido + '"';
		try {
			sentencia = conn.prepareStatement(consulta);
			sentencia.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				sentencia.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void mostrarTodo(Connection conn) {
		PreparedStatement sentencia = null;
		String consulta = "SELECT * FROM usuario";
		try {
			sentencia = conn.prepareStatement(consulta);
			ResultSet datos = sentencia.executeQuery();
			System.out.println("| id | nombre | apellidos | dni | fecha_nac | \n");
			while (datos.next()) {
				System.out.println("| "+datos.getInt("id")+" | "+datos.getString("nombre")+" | "+datos.getString("apellidos")+" | "+datos.getString("dni")+" | "+datos.getDate("fecha_nac")+" | \n");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				sentencia.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static String menuActualizar() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Que apartado quiere cambiar?\n");
		System.out.println("1.nombre");
		System.out.println("2.apellidos");
		System.out.println("3.dni");
		System.out.println("4.fecha_nac\n\n");
		System.out.println("...?");
		int opc = sc.nextInt();
		switch (opc) {
		case 1:
			return "nombre";
		case 2:
			return "apellidos";
		case 3:
			return "dni";
		case 4:
			return "fecha_nac";
		default:
			System.out.println("no existe esa opcion");
			return null;
		}
		
		
	}
	
	public static void actualizarUsuario(Connection conn) {
		PreparedStatement sentencia = null;
		Scanner sc = new Scanner(System.in);
		System.out.println("introduce nombre y apellidos quire eliminar");
		System.out.println("nombre?");
		String nombre = sc.nextLine();
		System.out.println("apellidos?\n");
		String apellidos = sc.nextLine();
		char resp = 'n';
		do {
			String opcCambio = menuActualizar();
			String datoCambio = sc.nextLine();
			String consulta = "UPDATE usuario SET "+opcCambio+" = ? WHERE nombre = ? and apellidos = ?;";
			try {
				sentencia = conn.prepareStatement(consulta);
				if(opcCambio.equals("fecha_nac")) {
				    Date date1 = null;
					try {
						date1 = new Date(new SimpleDateFormat("yyyy-MM-dd").parse(datoCambio).getTime());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					sentencia.setDate(1, date1);
				}
				else
					sentencia.setString(1, datoCambio);
				sentencia.setString(2, nombre);
				sentencia.setString(3, apellidos);

				
				sentencia.executeUpdate();
				System.out.println("Datos cambiados");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(opcCambio == "nombre") {
				nombre = datoCambio;
			}
			else if(opcCambio == "apellidos"){
				apellidos = datoCambio;
			}
			System.out.println("¿Quieres cambiar algun dato mas?");
			try {
				resp = (char) System.in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}while(resp == 's');
	}

	public static void agregarFichero(String ruta,Connection conn) {
		BufferedReader br = null;
		PreparedStatement sentencia = null;
		try {
			//abrtimos la entrada
			br = new BufferedReader(new FileReader(ruta));
			//recogemos la primera linea 
			String linea = br.readLine();
			while (linea != null) {
				//metemos en una posicion del array cada campo del string
				campos = linea.split(SEPARADOR);
					System.out.println(Arrays.toString(campos));
					String consulta = "INSERT INTO usuario (nombre,apellidos,dni,fecha_nac) VALUES ("+'"'+campos[0]+'"'+", "+'"'+campos[1]+'"'+", "+'"'+campos[2]+'"'+", "+"'"+campos[3]+"')";
					try {
						sentencia = conn.prepareStatement(consulta);
						sentencia.executeUpdate();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						System.out.println("Inerccion ha fallado");
						e.printStackTrace();
					}
					finally {
						try {
							sentencia.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				linea = br.readLine();
			}
			System.out.println("");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
