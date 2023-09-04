package controlador;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;
import java.util.HashMap;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import capaAyudante_controlador.DaoCliente;
import capaAyudante_controlador.DaoDetalleVentaReport;
import capaAyudante_controlador.DaoPedidos;
import capaAyudante_controlador.DaoProducto;
import capa_datos.MysqlBDConexion;
import entidad.Clientes;
import entidad.ListarDetallePedidoReport;
import entidad.ListarPedidoDetalleReport;
import entidad.Pedido;
import entidad.Pedido_detalle;
import entidad.Producto;
import entidad.Usuario;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

/**
 * Servlet implementation class ServletPedidos
 */
@WebServlet("/servletpedido")
public class ServletPedidos extends HttpServlet {
	Clientes cli = new Clientes();
	Producto pro = new Producto();
	DaoProducto daoProduc = new DaoProducto();
	List<Pedido_detalle> ListaPedidoDeta = new ArrayList<Pedido_detalle>();

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String ejecutarProductos = request.getParameter("ejecutar");
		String accion = request.getParameter("accion");
		DaoPedidos dao = new DaoPedidos();
		if (ejecutarProductos.equalsIgnoreCase("listar"))
			listar(request, response);
		if (ejecutarProductos.equalsIgnoreCase("listarNuevo"))
			listarNUevo(request, response);
		if (ejecutarProductos.equalsIgnoreCase("listadoPedido"))
			listadoPedido(request, response);
		if (ejecutarProductos.equalsIgnoreCase("cancelarPedido"))
			cancelarPedido(request, response);
		if (ejecutarProductos.equalsIgnoreCase("finalizarPedido"))
			finalizarPedido(request, response);
		if (ejecutarProductos.equalsIgnoreCase("generarReporte"))
			generarReporte(request, response);
		if (ejecutarProductos.equalsIgnoreCase("enviarCorreo"))
			enviarCorreo(request, response);
		if (ejecutarProductos.equals("ejecutarVenta")) {
			HttpSession session = request.getSession();
			Usuario usuario = (Usuario) session.getAttribute("usuario");
			DaoDetalleVentaReport daoDetalleVenta = new DaoDetalleVentaReport();
			System.out.println("usuario" + usuario.getIdUsuario());
			switch (accion) {
			case "buscarClie":
				try {
					cli = null;
					int documentoIndentidad = Integer.parseInt(request.getParameter("dniCLiente"));
					cli = dao.BuscarCliente(documentoIndentidad);
					if (cli.getDni() != null) {
						request.getSession().setAttribute("TIPO", "ajs-success");
						request.getSession().setAttribute("MENSAJE",
								"Cliente : " + cli.getNombre() + " " + cli.getApellidos());
					} else {
						cli = null;
						request.getSession().setAttribute("TIPO", "ajs-error");
						request.getSession().setAttribute("MENSAJE", "Numero de Documento Incorrecto");
					}
				} catch (Exception e) {
					cli = null;
					request.getSession().setAttribute("TIPO", "ajs-error");
					request.getSession().setAttribute("MENSAJE", "Porfavor ingresa un numero de Documento");
				}
				response.sendRedirect("servletpedido?ejecutar=listar");
				break;

			case "agregarProduc":
				String cantidad = null;
				try {
					Pedido_detalle peDeta = new Pedido_detalle();
					Producto producto = new Producto();
					double precio = Double.parseDouble(request.getParameter("precioProducto"));
					producto.setCodProducto(Integer.parseInt(request.getParameter("productoCodigo")));
					producto.setNombreProducto(request.getParameter("nameProducto"));
					peDeta.setProducto(producto);

					/* NOS ASEGURAMOS Q LA CANTIDAD ENVIADA SEA MAYOR A CERO Y NO ESTE VACIO */
					cantidad = request.getParameter("cantidadProducto");
					if (cantidad != null && !cantidad.isEmpty()) {
						peDeta.setCantidad(Integer.parseInt(cantidad));

						Producto produc = dao.BuscarProducto(producto.getCodProducto());
						if (produc.getStock() < peDeta.getCantidad()) {
							peDeta.setCantidad(produc.getStock());
							request.getSession().setAttribute("TIPO", "ajs-error");
							request.getSession().setAttribute("MENSAJE", "Stock no disponible, solo se encontraron "
									+ produc.getStock() + " cantidades disponible de este producto");
						}

						double precioVenta = precio * peDeta.getCantidad();
						double precioVentaFormateado = Double.parseDouble(String.format("%.2f", precioVenta));
						peDeta.setPrecioVenta(precioVentaFormateado);
						System.out.println(peDeta.getPrecioVenta());

						int salida = dao.DescontarStock(peDeta);

						boolean productoExistente = false;
						for (Pedido_detalle detalle : ListaPedidoDeta) {
							if (detalle.getProducto().getCodProducto() == peDeta.getProducto().getCodProducto()) {
								detalle.setCantidad(detalle.getCantidad() + peDeta.getCantidad());
								detalle.setPrecioVenta(detalle.getPrecioVenta() + peDeta.getPrecioVenta());
								productoExistente = true;
								break;
							}
						}

						if (!productoExistente) {
							ListaPedidoDeta.add(peDeta);
						}

					} else {
						request.getSession().setAttribute("TIPO", "ajs-error");
						request.getSession().setAttribute("MENSAJE", "Ingrese una cantidad");
					}

				} catch (Exception e) {
					e.printStackTrace();
					request.getSession().setAttribute("TIPO", "ajs-error");
					request.getSession().setAttribute("MENSAJE", "Porfavor selecciones algun producto");
				}
				response.sendRedirect("servletpedido?ejecutar=listar");
				break;

			case "registrarVenta":
				try {
					if (cli != null) {
						System.out.println(cli.getDni());
						if (ListaPedidoDeta.size() != 0) {
							Pedido pedi = new Pedido();
							pedi.setCliente(cli);
							pedi.setUsuario(usuario);
							pedi.setMonto(Double.parseDouble(request.getParameter("totalPedcio1")));
							int salida = dao.AgregarPedido(pedi);
							for (int i = 0; i < ListaPedidoDeta.size(); i++) {
								Pedido_detalle deta = new Pedido_detalle();
								deta.setPedido(pedi);
								deta.setProducto(ListaPedidoDeta.get(i).getProducto());
								deta.setCantidad(ListaPedidoDeta.get(i).getCantidad());
								deta.setPrecioVenta(ListaPedidoDeta.get(i).getPrecioVenta());
								int ejecu = dao.AgregarDetallePedido(deta);
							}
							
							int idPedido = pedi.getCodPedido();
							ListarPedidoDetalleReport listadoPedidoReport = new ListarPedidoDetalleReport();
							int salida2 = daoDetalleVenta.ListarPedidoReport(listadoPedidoReport, idPedido);
							List<ListarDetallePedidoReport> listadoDetallePedidoReport = daoDetalleVenta.ListadoDetallePedidoReport(idPedido);

							InputStream reportFile = this.getServletConfig().getServletContext().getResourceAsStream("JasperReport/Blank_A4.jasper");

							JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportFile);
							JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(listadoDetallePedidoReport);
							System.out.println("Array " + listadoDetallePedidoReport.size());

							Map<String, Object> parameters = new HashMap<String, Object>();
							parameters.put("ds", ds);
							parameters.put("cliente", listadoPedidoReport.getCliente());
							parameters.put("telefono", listadoPedidoReport.getTelefono());
							parameters.put("direccion", listadoPedidoReport.getDireccion());
							parameters.put("idPedido", listadoPedidoReport.getIdPedido());
							parameters.put("fechaPedido", listadoPedidoReport.getFechaPedido());
							parameters.put("precioTotal", listadoPedidoReport.getPrecioTotal());
							parameters.put("estado", listadoPedidoReport.getEstado());
							System.out.println("Parametro ds " + ds.getData().size());

							JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

							JRPdfExporter exporter = new JRPdfExporter();
							exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
							ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
							exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
							exporter.exportReport();

							byte[] pdfBytes = outputStream.toByteArray();

							String correoRemitente = "spotify566546497@gmail.com";
							String password = "matcydxpwgboeltm";
							String correo = cli.getCorreo();
							System.out.println(correo);
							String asunto = "Pedidos Chicken";
							String mensaje = "Buenas Tardes, su pedido ha sido registrado con éxito";
							
							Properties props = new Properties();
							props.setProperty("mail.smtp.host", "smtp.gmail.com");
							props.setProperty("mail.smtp.starttls.enable", "true");
							props.setProperty("mail.smtp.port", "587");
							props.setProperty("mail.smtp.auth", "true");

							Session sesion = Session.getDefaultInstance(props);

							MimeMessage message = new MimeMessage(sesion);
							try {
							    message.setFrom(new InternetAddress(correoRemitente));
							    message.addRecipient(Message.RecipientType.TO, new InternetAddress(correo));
							    message.setSubject(asunto);
							    
							    BodyPart texto = new MimeBodyPart();
							    texto.setText(mensaje);
							    
							    MimeBodyPart attachmentPart = new MimeBodyPart();
							    attachmentPart.setContent(pdfBytes, "application/pdf");
							    attachmentPart.setFileName("BoletaVenta.pdf");

							    Multipart multipart = new MimeMultipart();
							    multipart.addBodyPart(texto);
							    multipart.addBodyPart(attachmentPart);

							    message.setContent(multipart);

							    Transport t = sesion.getTransport("smtp");
							    t.connect(correoRemitente, password);
							    t.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
							    t.close();

							    System.out.println("Envío de Correo Exitoso");
							    
							} catch (Exception e) {
							    System.out.println(e.getMessage());
							}
							
							ListaPedidoDeta.clear();
							cli = null;

							if (salida != 0) {
								request.getSession().setAttribute("TIPO", "ajs-success");
								request.getSession().setAttribute("MENSAJE", "Venta Registrada con Exito");
								response.sendRedirect("servletpedido?ejecutar=listadoPedido&incial=");
							} else {
								request.getSession().setAttribute("TIPO", "ajs-error");
								request.getSession().setAttribute("MENSAJE", "Error en el proceso de Venta");
								response.sendRedirect("servletpedido?ejecutar=listar");
							}
						} else {
							request.getSession().setAttribute("TIPO", "ajs-error");
							request.getSession().setAttribute("MENSAJE", "Porfavor seleccione algun producto");
							response.sendRedirect("servletpedido?ejecutar=listar");
						}
					} else {
						request.getSession().setAttribute("TIPO", "ajs-error");
						request.getSession().setAttribute("MENSAJE", "Porfavor seleccione un cliente");
						response.sendRedirect("servletpedido?ejecutar=listar");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "eliminarProducto":
				try {
					int productoId = Integer.parseInt(request.getParameter("productoId"));
					Iterator<Pedido_detalle> iterator = ListaPedidoDeta.iterator();

					while (iterator.hasNext()) {
						Pedido_detalle detalle = iterator.next();
						if (detalle.getProducto().getCodProducto() == productoId) {
							Producto produc = dao.BuscarProducto(detalle.getProducto().getCodProducto());
							if (produc.getActivo() == 0) {
								produc.setStock(detalle.getCantidad());
								int sa6 = daoProduc.activarProducto(produc);
							} else {
								int salida5 = dao.AumentarStock(detalle);
							}
							iterator.remove();
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				response.sendRedirect("servletpedido?ejecutar=listar");
				break;

			case "cancelar":
				try {
					for (int i = 0; i < ListaPedidoDeta.size(); i++) {
						Pedido_detalle detalle = ListaPedidoDeta.get(i);
						Producto produc = dao.BuscarProducto(detalle.getProducto().getCodProducto());
						if (produc.getActivo() == 0) {
							produc.setStock(detalle.getCantidad());
							int sa6 = daoProduc.activarProducto(produc);
						} else {
							int salida5 = dao.AumentarStock(detalle);
						}
					}
					ListaPedidoDeta.clear();
					cli = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
				response.sendRedirect("servletpedido?ejecutar=listar");
				break;
			default:
				break;
			}

		}
	}

	private void enviarCorreo(HttpServletRequest request, HttpServletResponse response) {
		try {
			String correoRemitente = "spotify566546497@gmail.com";
			String password = "matcydxpwgboeltm";
			String correo = "josephmorenojoseph@gmail.com";
			String asunto = "Hola, correo enviado desde java";
			String mensaje = "Tarao no te comas todo";

			Properties props = new Properties();
			props.setProperty("mail.smtp.host","smtp.gmail.com");
			props.setProperty("mail.smtp.starttls.enable","true");
			props.setProperty("mail.smtp.port","587");
			props.setProperty("mail.smtp.auth","true");
			
			Session sesion = Session.getDefaultInstance(props);

			MimeMessage message = new MimeMessage(sesion);
			try {
				message.setFrom(new InternetAddress(correoRemitente));
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(correo));
				message.setSubject(asunto);
				message.setText(mensaje);
				Transport t = sesion.getTransport("smtp");
				t.connect(correoRemitente, password);
				t.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
				t.close();

				System.out.println("Envio de Correo Exitoso");
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		

	}

	private void generarReporte(HttpServletRequest request, HttpServletResponse response) throws IOException {
		DaoDetalleVentaReport daoDetalleVenta = new DaoDetalleVentaReport();
		try {
			int idPedido = Integer.parseInt(request.getParameter("codigo"));
			ListarPedidoDetalleReport listadoPedidoReport = new ListarPedidoDetalleReport();
			int salida = daoDetalleVenta.ListarPedidoReport(listadoPedidoReport, idPedido);
			List<ListarDetallePedidoReport> listadoDetallePedidoReport = daoDetalleVenta
					.ListadoDetallePedidoReport(idPedido);

			/*
			 * InputStream reportFile =
			 * getServletContext().getResourceAsStream("/JasperReport/BoletaVenta.jasper");
			 */

			InputStream reportFile = this.getServletConfig().getServletContext()
					.getResourceAsStream("JasperReport/BoletaVenta.jasper");

			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportFile);
			JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(listadoDetallePedidoReport);
			System.out.println("Array " + listadoDetallePedidoReport.size());

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("ds", ds);
			parameters.put("cliente", listadoPedidoReport.getCliente());
			parameters.put("telefono", listadoPedidoReport.getTelefono());
			parameters.put("direccion", listadoPedidoReport.getDireccion());
			parameters.put("idPedido", listadoPedidoReport.getIdPedido());
			parameters.put("fechaPedido", listadoPedidoReport.getFechaPedido());
			parameters.put("precioTotal", listadoPedidoReport.getPrecioTotal());
			parameters.put("estado", listadoPedidoReport.getEstado());
			System.out.println("Parametro ds " + ds.getData().size());

			response.setContentType("application/pdf");
			response.setHeader("Content-disposition", "inline; filename=BoletaVenta.pdf");
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
			ServletOutputStream out = response.getOutputStream();
			JasperExportManager.exportReportToPdfStream(jasperPrint, out);
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void finalizarPedido(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		DaoPedidos dao = new DaoPedidos();
		if (usuario != null) {
			try {
				int idPedido = Integer.parseInt(request.getParameter("codPedidoFina"));
				int estadoPedido = Integer.parseInt(request.getParameter("estapedidofin"));
				if (estadoPedido != 3) {
					if (estadoPedido != 2) {
						int salida = dao.FinalizarPedido(idPedido);
						request.getSession().setAttribute("TIPO", "ajs-success");
						request.getSession().setAttribute("MENSAJE", "El pedido ha sido finalizado correctamente");
					} else {
						request.getSession().setAttribute("TIPO", "ajs-error");
						request.getSession().setAttribute("MENSAJE", "El pedido esta cancelado");
					}
				} else {
					request.getSession().setAttribute("TIPO", "ajs-error");
					request.getSession().setAttribute("MENSAJE", "El pedido ya ha sido finalizado");
				}

				response.sendRedirect("servletpedido?ejecutar=listadoPedido&incial=");

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			request.getSession().setAttribute("TIPO", "ajs-error");
			request.getSession().setAttribute("MENSAJE", "Porfavor ingrese un usuario");
			response.sendRedirect("index.jsp");
		}
	}

	private void cancelarPedido(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		DaoPedidos dao = new DaoPedidos();
		DaoDetalleVentaReport daoDetalleVenta = new DaoDetalleVentaReport();
		if (usuario != null) {
			try {
				int idPedido = Integer.parseInt(request.getParameter("codpedidoCancel"));
				int estadoPedido = Integer.parseInt(request.getParameter("estapedidoCancel"));
				if (estadoPedido != 3) {
					if (estadoPedido != 2) {
						List<ListarDetallePedidoReport> listadoDetallePedidoReport = daoDetalleVenta.ListadoDetallePedidoReport(idPedido);
						for (int i = 0; i < listadoDetallePedidoReport.size(); i++) {
							ListarDetallePedidoReport detalle = listadoDetallePedidoReport.get(i);
							Pedido_detalle detalle2 = new Pedido_detalle();
							Producto prod = new Producto();
							prod = dao.BuscarProducto(detalle.getProductoId());
							detalle2.setCantidad(detalle.getCantidad());
							detalle2.setProducto(prod);
							
							if(prod.getActivo() == 0) {
								prod.setStock(detalle.getCantidad());
								int sa6 = daoProduc.activarProducto(prod);
							}else {
								int salida2 = dao.AumentarStock(detalle2);
							}
						}
						int salida = dao.CancelarPedido(idPedido);
						request.getSession().setAttribute("TIPO", "ajs-error");
						request.getSession().setAttribute("MENSAJE", "El pedido ha sido cancelado correctamente");
					} else {
						request.getSession().setAttribute("TIPO", "ajs-error");
						request.getSession().setAttribute("MENSAJE", "El pedido ya estaba cancelado");
					}
				} else {
					request.getSession().setAttribute("TIPO", "ajs-error");
					request.getSession().setAttribute("MENSAJE", "El pedido ya ha sido finalizado");
				}

				response.sendRedirect("servletpedido?ejecutar=listadoPedido&incial=");

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			request.getSession().setAttribute("TIPO", "ajs-error");
			request.getSession().setAttribute("MENSAJE", "Porfavor ingrese un usuario");
			response.sendRedirect("index.jsp");
		}

	}

	private void listadoPedido(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		DaoPedidos dao = new DaoPedidos();
		if (usuario != null) {
			try {
				String inicial = request.getParameter("incial");
				List<Pedido> listaPed = dao.ListadoPedido(inicial);
				request.setAttribute("listaPed", listaPed);
				request.getRequestDispatcher("ListarPedidos.jsp").forward(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			request.getSession().setAttribute("TIPO", "ajs-error");
			request.getSession().setAttribute("MENSAJE", "Porfavor ingrese un usuario");
			response.sendRedirect("index.jsp");
		}

	}

	private void listarNUevo(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		if (usuario != null) {
			try {
				ListaPedidoDeta.clear();
				cli = null;
				response.sendRedirect("servletpedido?ejecutar=listar");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			response.sendRedirect("index.jsp");
		}
	}

	private void listar(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		if (usuario != null) {
			try {
				List<Producto> listaProducto = daoProduc.ListadoProducto("");
				request.setAttribute("produc", listaProducto);
				request.setAttribute("cliente", cli);
				request.setAttribute("listaventas", ListaPedidoDeta);
				request.getRequestDispatcher("RegistrarPedidos.jsp").forward(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			response.sendRedirect("index.jsp");
		}
	}

}
