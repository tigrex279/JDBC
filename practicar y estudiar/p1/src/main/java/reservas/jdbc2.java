package reservas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Scanner;

public class jdbc2 {

	public static final String SEPARADOR = ",";
	static String[] campos;
	private static String nombre = "Admin";
	private static String password = "admin123";
	private static String bd = "aeropuerto";

	/*
	 * Main
	 */
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		Connection conn = null;
		String url = "jdbc:mysql://localhost:3306/" + bd;
		File archivo;
		try {
			conn = DriverManager.getConnection(url, nombre, password);
			if (conn != null) {
				System.out.println("Conexion establecida con la base de datos");
				System.out.println("");
			}
			int opc;
			do {
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
					actualizar(conn);
					break;
				case 5:
					reservar(conn);
					break;
				case 6:
					mostrarVuelos(conn);
					break;
				case 7:
					System.out.println("indica tu DNI");
					String dni = sc.nextLine();
					idUsuario(dni, conn);
					mostrarReserva(idUsuario(dni, conn), conn);
					break;
				case 8:
					break;
				case 9:
					agregarFicheroVuelos("/home/tigre/Descargas/vuelos.csv", conn);
					break;
				case 10:
					System.out.println("Saliendo de la aplicacion...");
					System.out.println("Hasta otra");
					break;
				default:
					System.out.println("Opcion erronea");
					break;
				}
			} while (opc != 10);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Conexion Fallida");
			e.printStackTrace();
		} finally {
			try {
				conn.close();
				if (conn.isClosed()) {
					System.out.println("");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}// Fin main

	/*
	 * Menu de opciones
	 * 
	 * @return una opcion
	 */
	public static int menu() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Base de datos " + bd + "\n");
		System.out.println("1.Insertar");
		System.out.println("2.Borrar");
		System.out.println("3.Mostrar");
		System.out.println("4.Actualizar");
		System.out.println("5.Hacer reserva");
		System.out.println("6.Mostrar vuelos");
		System.out.println("7.Mostrar reservas");
		System.out.println("8.Borrar reservas");
		System.out.println("9.Agregar fichero");
		System.out.println("10.salir\n");
		System.out.println("Elige una de la opciones");
		return sc.nextInt();

	}// Fin menu

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

	}// Fin menuActualizar

	public static int elegirVuelo(String origen, String destino, int numPlazas, Connection conn) {
		Scanner sc = new Scanner(System.in);
		PreparedStatement sentencia = null;
		String consulta = "SELECT * FROM vuelo WHERE origen = '" + origen + "' AND destino = '" + destino
				+ "' AND (plazas_Disp != 0 AND plazas_Disp >=" + numPlazas + ");";
		try {
			sentencia = conn.prepareStatement(consulta);
			ResultSet datos = sentencia.executeQuery();
			System.out
					.println("| id | origen | O_acortado | salida | destino | D_acortado | llegada | plazas_Disp | \n");
			while (datos.next()) {
				System.out.println("| " + datos.getInt("id") + " | " + datos.getString("origen") + " | "
						+ datos.getString("O_acortado") + " | " + datos.getDate("salida") + " | "
						+ datos.getString("destino") + " | " + datos.getString("D_acortado") + " | "
						+ datos.getDate("llegada") + " | " + datos.getInt("plazas_Disp"));
			}
			System.out.println("\n Elije id de vuelo");
			return sc.nextInt();
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
		return -1;
	}// Fin elegirVuelo

	/*
	 * Metodo para obtener id de un usuario por el dni
	 * 
	 * @return: id de tabla usuario
	 */
	public static int idUsuario(String dni, Connection conn) {
		PreparedStatement sentencia = null;
		String conuslta = "SELECT * FROM usuario WHERE dni = " + '"' + dni + '"' + ";";
		int id = 0;
		try {
			sentencia = conn.prepareStatement(conuslta);
			ResultSet datos = sentencia.executeQuery();
			if (datos.next())
				id = datos.getInt("id");
			else
				System.out.println("no se ecuentra el " + dni + " en la base de datos");
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

		return id;
	}// Fin idUsuario

	/*
	 * Metodo para obtener id de un usuario por el nombre
	 * 
	 * @return: id de tabla usuario
	 */
	public static int idUsuarioNom(String nombre, Connection conn) {
		PreparedStatement sentencia = null;
		String conuslta = "SELECT * FROM usuario WHERE nombre = " + '"' + nombre + '"' + ";";
		int id = 0;
		try {
			sentencia = conn.prepareStatement(conuslta);
			ResultSet datos = sentencia.executeQuery();
			if (datos.next())
				id = datos.getInt("id");
			else
				System.out.println("no se ecuentra a " + nombre + " en la base de datos");
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

		return id;
	}// Fin idUsuario

	/*
	 * Agrega un csv externo a la base de datos en la tabla vuelo
	 */
	public static void agregarFicheroVuelos(String ruta, Connection conn) {
		BufferedReader br = null;
		PreparedStatement sentencia = null;
		try {
			// abrtimos la entrada
			br = new BufferedReader(new FileReader(ruta));
			// recogemos la primera linea
			String linea = br.readLine();
			while (linea != null) {
				// metemos en una posicion del array cada campo del string
				campos = linea.split(SEPARADOR);
				String consulta = "insert into vuelo values(" + campos[0] + "," + '"' + campos[1] + '"' + "," + '"'
						+ campos[2] + '"' + "," + "'" + campos[3] + "'" + "," + '"' + campos[4] + '"' + "," + '"'
						+ campos[5] + '"' + "," + "'" + campos[6] + "'" + "," + campos[7] + ");";
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
	}// Fin agregarFichero

	/*
	 * Metodo de insertar en la base de datos en la tabla usuarios
	 */
	public static void insetar(Connection conn) {
		Scanner sc = new Scanner(System.in);
		PreparedStatement sentencia = null;
		System.out.println("Introduce nombre");
		String nombre = sc.nextLine();
		System.out.println("Introduce apellidos");
		String apellidos = sc.nextLine();
		System.out.println("Introduce DNI");
		String dni = sc.nextLine();
		System.out.println("introduce año");
		String año = sc.nextLine();
		System.out.println("introduce mes");
		String mes = sc.nextLine();
		System.out.println("introduce dia");
		String dia = sc.nextLine();
		// Fecha de nacimiento
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
	}// Fin insertar

	public static void borrar(Connection conn) {
		Scanner sc = new Scanner(System.in);
		PreparedStatement sentencia = null;
		Savepoint SavePoint = null;
		System.out.println("introduce el DNI");
		String dni = sc.nextLine();
		int id = idUsuario(dni, conn);
		String consulta = null;
		if (id == 0) {
			System.out.println("No hay ningun usuario con ese DNI");
		} else {
			try {
				conn.setAutoCommit(false);
				SavePoint = conn.setSavepoint();
				consulta = "SELECT * FROM reservan where id_usuario = " + id + ";";
				sentencia = conn.prepareStatement(consulta);
				ResultSet datos = sentencia.executeQuery();
				sentencia = conn.prepareStatement("DELETE FROM usuario where id = " + id + ";");
				if (!datos.next())
					System.out.println("No hay reservas hechas");
				else {
					while (datos.next()) {
						borrarReservas(conn, datos.getInt("id_resv"));
					}
				}
				sentencia.executeUpdate();

				conn.commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					conn.rollback(SavePoint);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} finally {
				try {
					sentencia.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}// Fin borrar

	public static void borrarReservas(Connection conn, int reserva) {
		PreparedStatement sentencia = null;
		String consulta = "SELECT * FROM reservan WHERE id_resv = " + reserva + ";";
		Savepoint SavePoint = null;
		try {
			sentencia = conn.prepareStatement(consulta);
			ResultSet datos = sentencia.executeQuery();

			if (!datos.next()) {
				System.out.println("No tiene reservas");
				datos.first();
			} else {
				conn.setAutoCommit(false);
				SavePoint = conn.setSavepoint();

				int plazas = datos.getInt("plazas_reserv");
				int idVuelo = datos.getInt("id_vuelo");

				consulta = "DELETE FROM reservan WHERE id_resv = " + reserva + ";";
				sentencia = conn.prepareStatement(consulta);
				sentencia.executeUpdate();

				consulta = "UPDATE vuelo SET plazas_Disp = plazas_Disp + " + plazas + " WHERE id = " + idVuelo + ";";
				sentencia = conn.prepareStatement(consulta);
				sentencia.executeUpdate();
				conn.commit();
			}
		} catch (SQLException e) {
			try {
				conn.rollback(SavePoint);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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

	}// Fin borrar reserva

	public static void mostrarTodo(Connection conn) {
		PreparedStatement sentencia = null;
		String consulta = "SELECT * FROM usuario";
		try {
			sentencia = conn.prepareStatement(consulta);
			ResultSet datos = sentencia.executeQuery();
			System.out.println("| id | nombre | apellidos | dni | fecha_nac | \n");
			while (datos.next()) {
				System.out.println("| " + datos.getInt("id") + " | " + datos.getString("nombre") + " | "
						+ datos.getString("apellidos") + " | " + datos.getString("dni") + " | "
						+ datos.getDate("fecha_nac") + " | ");
			}
			System.out.println();
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
	}// Fin mostrar

	public static void actualizar(Connection conn) {
		PreparedStatement sentencia = null;
		Scanner sc = new Scanner(System.in);
		System.out.println("introduce el DNI");
		String dni = sc.nextLine();
		char resp = 'n';
		do {
			String opcCambio = menuActualizar();
			String datoCambio = sc.nextLine();
			String consulta = "UPDATE usuario SET " + opcCambio + " = ? WHERE dni = ?;";
			try {
				sentencia = conn.prepareStatement(consulta);
				if (opcCambio.equals("fecha_nac")) {
					Date date1 = null;
					try {
						date1 = new Date(new SimpleDateFormat("yyyy-MM-dd").parse(datoCambio).getTime());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					sentencia.setDate(1, date1);
				} else
					sentencia.setString(1, datoCambio);
				sentencia.setString(2, dni);

				sentencia.executeUpdate();
				System.out.println("Datos cambiados");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (opcCambio == "dni") {
				dni = datoCambio;
			}
			System.out.println("¿Quieres cambiar algun dato mas?");
			try {
				resp = (char) System.in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (resp == 's');
		try {
			sentencia.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}// Fin actualizar

	/*
	 * METODOS DE LOS VUELOS Y RESERVAS
	 */

	public static void mostrarVuelos(Connection conn) {
		PreparedStatement sentencia = null;
		String consulta = "SELECT * FROM vuelo;";
		try {
			sentencia = conn.prepareStatement(consulta);
			ResultSet datos = sentencia.executeQuery();
			System.out
					.println("| id | origen | O_acortado | salida | destino | D_acortado | llegada | plazas_Disp | \n");
			while (datos.next()) {
				System.out.println("| " + datos.getInt("id") + " | " + datos.getString("origen") + " | "
						+ datos.getString("O_acortado") + " | " + datos.getDate("salida") + " | "
						+ datos.getString("destino") + " | " + datos.getString("D_acortado") + " | "
						+ datos.getDate("llegada") + " | " + datos.getInt("plazas_Disp"));
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
	}// fin mostrarVuelos

	public static void reservar(Connection conn) {
		Scanner sc = new Scanner(System.in);
		int idUser;
		System.out.println("introduce dni");
		String dniUser = sc.nextLine();
		PreparedStatement sentencia = null;
		String consulta = "SELECT * FROM usuario";
		try {
			sentencia = conn.prepareStatement(consulta);
			ResultSet datos = sentencia.executeQuery();
			while (datos.next()) {
				if (datos.getString("dni").equals(dniUser)) {
					idUser = datos.getInt("id");
					System.out.println("Elije destino");
					String destino = sc.nextLine();
					System.out.println("Elije origen");
					String origen = sc.nextLine();
					System.out.println("¿Cuantas plazas quiere reservar?");
					int num_plazas = sc.nextInt();
					int idVuelo = elegirVuelo(origen, destino, num_plazas, conn);
					if (idVuelo != -1) {
						agregarReserva(idUser, idVuelo, num_plazas, conn);
					}
				}
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
	}// Fin reservar

	public static void agregarReserva(int user, int vuelo, int numPlazas, Connection conn) {
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		PreparedStatement sentencia = null;
		PreparedStatement actulizar = null;

		String consulta = "INSERT INTO reservan (id_usuario,id_vuelo,plazas_reserv) VALUES(" + user + "," + vuelo + ","
				+ numPlazas + ");";
		Savepoint SavePoint = null;
		try {
			conn.setAutoCommit(false);
			// creamos un punto de control
			SavePoint = conn.setSavepoint();
			// Primera sentencia
			sentencia = conn.prepareStatement(consulta);
			sentencia.executeUpdate();
			// Segunda sentencia
			consulta = "UPDATE vuelo SET plazas_Disp = (plazas_Disp-" + numPlazas + ") WHERE id =" + vuelo + ";";
			sentencia = conn.prepareStatement(consulta);
			sentencia.executeUpdate();
			// Ejecutan las sentencias
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				conn.rollback(SavePoint);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			try {
				sentencia.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}// Fin agregarReserva

	public static void mostrarReserva(int iduser, Connection conn) {
		PreparedStatement sentencia = null;
		String consulta = "SELECT u.nombre,u.apellidos,u.dni,v.origen,v.salida,v.destino,v.llegada,r.plazas_reserv FROM usuario AS u JOIN vuelo AS v JOIN reservan AS r WHERE r.id_vuelo = v.id and r.id_usuario = "
				+ iduser + " and u.id = " + iduser;
		try {
			sentencia = conn.prepareStatement(consulta);
			ResultSet datos = sentencia.executeQuery();
			System.out.println("| nombre | apellidos | dni | origen | salida | destino | llegada | plazas_reserv | \n");
			while (datos.next()) {
				System.out.println("| " + datos.getString("u.nombre") + " | " + datos.getString("u.apellidos") + " | "
						+ datos.getString("u.dni") + " | " + datos.getString("v.origen") + " | "
						+ datos.getDate("salida") + " | " + datos.getString("v.destino") + " | "
						+ datos.getDate("v.llegada") + " | " + datos.getInt("r.plazas_reserv"));
			}
			System.out.println();
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

}// Fin class
