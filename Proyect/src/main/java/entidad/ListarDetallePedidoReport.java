package entidad;

public class ListarDetallePedidoReport {
	public int productoId, cantidad;
	public String productoName;
	public double precioVenta;
	
	public int getProductoId() {
		return productoId;
	}
	public void setProductoId(int productoId) {
		this.productoId = productoId;
	}
	public int getCantidad() {
		return cantidad;
	}
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	public String getProductoName() {
		return productoName;
	}
	public void setProductoName(String productoName) {
		this.productoName = productoName;
	}
	public double getPrecioVenta() {
		return precioVenta;
	}
	public void setPrecioVenta(double precioVenta) {
		this.precioVenta = precioVenta;
	}
	
	
}
