package interfaces;

import java.util.List;

import entidad.ListarDetallePedidoReport;
import entidad.ListarPedidoDetalleReport;

public interface InterfaceReportDetalleVenta {
	public List<ListarDetallePedidoReport> ListadoDetallePedidoReport(int idProducto);
	public int ListarPedidoReport(ListarPedidoDetalleReport listadoPedidoReport, int idProducto);
}
