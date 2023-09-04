package capaAyudante_controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import capa_datos.MysqlBDConexion;
import entidad.ListarDetallePedidoReport;
import entidad.ListarPedidoDetalleReport;
import interfaces.InterfaceReportDetalleVenta;

public class DaoDetalleVentaReport implements InterfaceReportDetalleVenta{

	@Override
	public List<ListarDetallePedidoReport> ListadoDetallePedidoReport(int idProducto) {
		List<ListarDetallePedidoReport> listadoDetallePedidoReport = new ArrayList<ListarDetallePedidoReport>();
		Connection c = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			c = MysqlBDConexion.getConexion();
			String sql ="CALL ListarDetallePedidoReport(?);";
			pstm = c.prepareStatement(sql);
			pstm.setInt(1, idProducto);
			rs = pstm.executeQuery();
			ListarDetallePedidoReport detallepedio=null;
			while (rs.next()) {
				detallepedio = new ListarDetallePedidoReport();
				detallepedio.setProductoId(rs.getInt(1));
				detallepedio.setProductoName(rs.getString(2));
				detallepedio.setCantidad(rs.getInt(3));
				detallepedio.setPrecioVenta(rs.getDouble(4));
				System.out.println(detallepedio.getProductoId() + ", ");
				listadoDetallePedidoReport.add(detallepedio);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (c != null)
					c.close();
				if (pstm != null)
					pstm.close();
			} catch (Exception e2) {

			}
		}
		
		return listadoDetallePedidoReport;
	}

	@Override
	public int ListarPedidoReport(ListarPedidoDetalleReport listadoPedidoReport, int idProducto) {
		int salida = 1;
		Connection c = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			c = MysqlBDConexion.getConexion();
			String sql ="CALL ListarPedidoDetalleReport(?);";
			pstm = c.prepareStatement(sql);
			pstm.setInt(1, idProducto);
			rs = pstm.executeQuery();
			while (rs.next()) {
				listadoPedidoReport.setCliente(rs.getString(1) + " " + rs.getString(2));
				listadoPedidoReport.setTelefono(rs.getString(3));
				listadoPedidoReport.setDireccion(rs.getString(4));
				listadoPedidoReport.setIdPedido(rs.getInt(5));
				listadoPedidoReport.setFechaPedido(rs.getDate(6));
				listadoPedidoReport.setPrecioTotal(rs.getDouble(7));
				listadoPedidoReport.setEstado(rs.getString(8));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (c != null)
					c.close();
				if (pstm != null)
					pstm.close();
			} catch (Exception e2) {

			}
		}
		
		return salida;
	}

}
