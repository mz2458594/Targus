package com.example.domain.ecommerce.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.example.domain.ecommerce.models.entities.Cliente;
import com.example.domain.ecommerce.models.entities.Comprobante;
import com.example.domain.ecommerce.models.entities.DetallePedido;
import com.example.domain.ecommerce.models.entities.Detalle_venta;
import com.example.domain.ecommerce.models.entities.Empleado;
import com.example.domain.ecommerce.models.entities.Proveedor;
import com.example.domain.ecommerce.models.entities.Usuario;
import com.example.domain.ecommerce.models.entities.Venta;
import com.example.domain.ecommerce.models.entities.VentaInventario;
import com.example.domain.ecommerce.models.enums.TipoComprobante;
import com.example.domain.ecommerce.repositories.ClienteDAO;
import com.example.domain.ecommerce.repositories.EmpleadoDAO;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import java.util.List;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PdfGeneratorService {

    private final ClienteDAO clienteDAO;

    private final EmpleadoDAO empleadoDAO;

    public ByteArrayOutputStream generarBoletaPDF(Comprobante comprobante) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PageSize pageSize = new PageSize(226, PageSize.A4.getHeight());
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.setDefaultPageSize(pageSize);
        Document document = new Document(pdfDoc); // Tamaño pequeño vertical

        document.setMargins(20, 20, 20, 20);

        try {
            InputStream is = getClass().getResourceAsStream("/static/images/logo.png");
            if (is == null) {
                throw new FileNotFoundException("No se encontró la imagen: /images/logo.png");
            }

            ImageData imageData = ImageDataFactory.create(StreamUtil.inputStreamToArray(is));
            Image image = new Image(imageData);
            image.scaleToFit(100, 100);
            image.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(image);
        } catch (IOException e) {
            e.printStackTrace();
            document.close();
            throw new RuntimeException("Error al generar el PDF", e);
        }

        // 2. Datos empresa
        document.add(new Paragraph("TARGUS S.A.C").setBold().setTextAlignment(TextAlignment.CENTER).setFontSize(10));
        document.add(new Paragraph("RUC: 1234567890").setTextAlignment(TextAlignment.CENTER).setFontSize(10));
        document.add(new Paragraph("CALLE LAS LOMAS 123\nTel: 987 654 321\nCorreo: Targusgaming@tgame.com")
                .setTextAlignment(TextAlignment.CENTER));

        if (comprobante.getTipo() == TipoComprobante.BOLETA) {
            document.add(new Paragraph("\nBOLETA DE VENTA ELECTRONICA")
                    .setBold().setTextAlignment(TextAlignment.CENTER).setFontSize(10));
        } else {
            document.add(new Paragraph("\nFactura DE VENTA ELECTRONICA")
                    .setBold().setTextAlignment(TextAlignment.CENTER).setFontSize(10));
        }
        document.add(new Paragraph(comprobante.getNumero()).setTextAlignment(TextAlignment.CENTER).setFontSize(10));

        // 3. Cliente
        Venta venta = comprobante.getVenta();
        Usuario usuario = venta.getUsuario();

        Optional<Cliente> cliente = clienteDAO.findByUsuario(usuario);
        Optional<Empleado> empleado = empleadoDAO.findByUsuario(usuario);

        if (cliente.isPresent()) {
            Cliente cliente2 = cliente.get();
            document.add(new Paragraph("\nVenta realizada por: " + cliente2.getNombre()).setBold());
        } else if (empleado.isPresent()) {
            Empleado empleado2 = empleado.get();
            document.add(new Paragraph("\nVenta realizada por: " + empleado2.getNombre()).setBold());
        } else {
            document.add(new Paragraph("DNI: No disponible"));
        }

        // 4. Fecha y Hora
        LocalDateTime fecha = comprobante.getFechaEmision();
        document.add(
                new Paragraph("\nFECHA: " + fecha.toLocalDate() + "    \nHORA: " + fecha.toLocalTime().withSecond(0)));

        if (comprobante.getTipo() == TipoComprobante.FACTURA) {
            document.add(
                    new Paragraph(
                            "\nRUC: " + comprobante.getRucCliente()));
        }

        // 5. Tabla de productos
        float[] columnWidths = { 50F, 100F, 50F, 60F };
        Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();
        table.addHeaderCell("Cant");
        table.addHeaderCell("Descripción");
        table.addHeaderCell("P.U.");
        table.addHeaderCell("Total");

        for (Detalle_venta item : venta.getVentaProductos()) {
            table.addCell(item.getCantidad() + " UND");
            table.addCell(item.getProducto().getNombre());
            table.addCell(item.getProducto().getPrecioVenta());
            table.addCell(String.format("%.2f", item.getSubtotal()));
        }
        document.add(table);

        // 6. Totales
        BigDecimal total = venta.getTotal();
        BigDecimal divisor = new BigDecimal("1.18");
        BigDecimal subtotal = total.divide(divisor, 2, RoundingMode.HALF_UP);
        BigDecimal igv = total.subtract(subtotal);

        document.add(new Paragraph("\nSUBTOTAL: S/ " + String.format("%.2f", subtotal)));

        if (comprobante.getTipo() == TipoComprobante.FACTURA) {
            document.add(new Paragraph("IGV (18%): S/ " + String.format("%.2f", igv)));
        }
        document.add(new Paragraph("TOTAL: S/ " + String.format("%.2f", total)).setBold());

        // 7. Método de pago y otros
        document.add(new Paragraph("\nMETODO DE PAGO: EFECTIVO"));
        if (venta instanceof VentaInventario ventaInventario) {
            document.add(new Paragraph("EFECTIVO: " + ventaInventario.getEfectivo()));
            document.add(new Paragraph("VUELTO: " + ventaInventario.getVuelto()));
        }

        // 8. Pie de página
        document.add(new Paragraph("\nRepresentación impresa de documento electrónico.")
                .setFontSize(8).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
        document.add(new Paragraph("www.targus.com.pe").setFontSize(8).setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10));

        document.close();
        return baos;
    }

    public ByteArrayInputStream generateFacturaPDF(Comprobante comprobante) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        try {
            // Imagen de encabezado
            InputStream is = getClass().getResourceAsStream("/static/images/logo.png");
            if (is == null) {
                throw new FileNotFoundException("No se encontró la imagen: /images/logo.png");
            }
            ImageData imageData = ImageDataFactory.create(StreamUtil.inputStreamToArray(is));
            Image image = new Image(imageData);
            image.scaleToFit(100, 100);
            image.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(image);

            // Encabezado general de la factura
            document.add(
                    new Paragraph("TARGUS S.A.C").setFontSize(14).setBold().setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("CALLE LAS NORMAS 123").setFontSize(10).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Teléf: 987 654 321").setFontSize(10).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Correo: Targusgaming@tgame.com").setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Web: www.targus.com").setFontSize(10).setTextAlignment(TextAlignment.CENTER));
            document.add(
                    new Paragraph("RUC: 1234567890").setFontSize(10).setBold().setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("ORDEN DE COMPRA").setBold().setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(comprobante.getNumero()).setBold().setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));

            if (comprobante.getPedidoProveedor() != null) {
                // Agrupar productos por proveedor
                Map<Proveedor, List<DetallePedido>> productosPorProveedor = comprobante.getPedidoProveedor()
                        .getDetallePedidos()
                        .stream()
                        .collect(Collectors.groupingBy(item -> item.getProducto().getProveedor()));

                for (Map.Entry<Proveedor, List<DetallePedido>> entry : productosPorProveedor.entrySet()) {
                    Proveedor proveedor = entry.getKey();
                    List<DetallePedido> detalles = entry.getValue();

                    for (DetallePedido item : detalles) {
                        // Datos del proveedor (se repiten en cada página)
                        Table proveedorTable = new Table(UnitValue.createPercentArray(new float[] { 33, 33, 34 }));
                        proveedorTable.setWidth(UnitValue.createPercentValue(100));
                        proveedorTable.addCell(getCell("Razón Social:\n" + safe(proveedor.getNombre()), true));
                        proveedorTable.addCell(getCell("RUC:\n" + safe(String.valueOf(proveedor.getRuc())), true));
                        proveedorTable.addCell(getCell("Correo:\n" + safe(proveedor.getEmail()), true));
                        document.add(proveedorTable);

                        Table table = new Table(UnitValue.createPercentArray(new float[] { 10, 10, 40, 20, 20 }));
                        table.setWidth(UnitValue.createPercentValue(100));
                        table.addHeaderCell("CANTIDAD");
                        table.addHeaderCell("U.M");
                        table.addHeaderCell("DESCRIPCIÓN");
                        table.addHeaderCell("PRECIO U.");
                        table.addHeaderCell("IMPORTE (inc. IGV)");

                        table.addCell(String.valueOf(item.getCantidad()));
                        table.addCell("UNIDAD");
                        table.addCell(safe(item.getProducto().getNombre()));
                        table.addCell(item.getProducto().getPrecioCompra());
                        table.addCell(String.format("%.2f", item.getSubtotal()));

                        document.add(table);
                        document.add(new Paragraph("\n"));

                        // Añadir página nueva solo si no es el último producto
                        if (!(entry.equals(productosPorProveedor.entrySet().toArray()[productosPorProveedor.size() - 1])
                                && item.equals(detalles.get(detalles.size() - 1)))) {
                            pdfDoc.addNewPage();
                        }

                    }
                }

            }
            // Totales generales en la última página
            Table totales = new Table(UnitValue.createPercentArray(new float[] { 80, 20 }));
            totales.setWidth(UnitValue.createPercentValue(100));
            BigDecimal opGravada = comprobante.getPedidoProveedor().getTotal().divide(BigDecimal.valueOf(1.18), 2,
                    RoundingMode.HALF_UP);
            BigDecimal igv = comprobante.getPedidoProveedor().getTotal().subtract(opGravada);
            BigDecimal total = comprobante.getPedidoProveedor().getTotal();

            totales.addCell(getCell("OP. GRAVADA (S/.)", true));
            totales.addCell(getCell(String.format("%.2f", opGravada), false));
            totales.addCell(getCell("TOTAL IGV (S/.)", true));
            totales.addCell(getCell(String.format("%.2f", igv), false));
            totales.addCell(getCell("IMPORTE TOTAL (S/.)", true));
            totales.addCell(getCell(String.format("%.2f", total), false));
            document.add(totales);

            document.close();
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
            document.close();
            throw new RuntimeException("Error al generar el PDF", e);
        }
    }

    private Cell getCell(String content, boolean bold) {
        Cell cell = new Cell().add(new Paragraph(content));
        if (bold)
            cell.setBold();
        cell.setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)); // Borde negro delgado
        return cell;
    }

    private String safe(String text) {
        return text == null ? "" : text;
    }
}
