package TallerMecanico;

import javax.swing.table.TableModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GestorArchivos {

    public void exportarCsv(TableModel modelo, File archivo) throws IOException {
        try (PrintWriter salida = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(archivo), StandardCharsets.UTF_8))) {

            salida.print('\uFEFF');
            List<String> encabezados = new ArrayList<>();
            for (int c = 0; c < modelo.getColumnCount(); c++) {
                encabezados.add(campoCsv(modelo.getColumnName(c)));
            }
            salida.println(String.join(",", encabezados));

            for (int f = 0; f < modelo.getRowCount(); f++) {
                List<String> valores = new ArrayList<>();
                for (int c = 0; c < modelo.getColumnCount(); c++) {
                    valores.add(campoCsv(texto(modelo.getValueAt(f, c))));
                }
                salida.println(String.join(",", valores));
            }
        }
    }

    public void exportarHtml(TableModel modelo, String titulo, File archivo) throws IOException {
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

        try (PrintWriter salida = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(archivo), StandardCharsets.UTF_8))) {

            salida.println("<!DOCTYPE html>");
            salida.println("<html><head><meta charset=\"UTF-8\"><title>" + html(titulo) + "</title>");
            salida.println("<style>");
            salida.println("body { font-family: Arial, sans-serif; margin: 30px; }");
            salida.println("h1 { color: #1F3864; margin-bottom: 4px; }");
            salida.println("table { border-collapse: collapse; width: 100%; margin-top: 16px; }");
            salida.println("th { background: #1F3864; color: white; padding: 8px; text-align: left; }");
            salida.println("td { border: 1px solid #ccc; padding: 6px; }");
            salida.println("tr:nth-child(even) { background: #f2f2f2; }");
            salida.println("</style></head><body>");
            salida.println("<h1>Taller El Último Arranque</h1>");
            salida.println("<h2>" + html(titulo) + "</h2>");
            salida.println("<p>Generado: " + fecha + "</p>");
            salida.println("<table><tr>");
            for (int c = 0; c < modelo.getColumnCount(); c++) {
                salida.println("<th>" + html(modelo.getColumnName(c)) + "</th>");
            }
            salida.println("</tr>");
            for (int f = 0; f < modelo.getRowCount(); f++) {
                salida.println("<tr>");
                for (int c = 0; c < modelo.getColumnCount(); c++) {
                    salida.println("<td>" + html(texto(modelo.getValueAt(f, c))) + "</td>");
                }
                salida.println("</tr>");
            }
            salida.println("</table>");
            salida.println("<p>Total de registros: " + modelo.getRowCount() + "</p>");
            salida.println("</body></html>");
        }
    }

    public List<String[]> leerCsv(File archivo) throws IOException {
        List<String[]> filas = new ArrayList<>();

        try (BufferedReader entrada = new BufferedReader(new InputStreamReader(
                new FileInputStream(archivo), StandardCharsets.UTF_8))) {

            String linea;
            while ((linea = entrada.readLine()) != null) {
                linea = linea.replace("\uFEFF", "");
                if (!linea.trim().isEmpty()) {
                    filas.add(separarCsv(linea));
                }
            }
        }
        return filas;
    }

    private String[] separarCsv(String linea) {
        List<String> campos = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean entreComillas = false;

        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (c == '"') {
                if (entreComillas && i + 1 < linea.length() && linea.charAt(i + 1) == '"') {
                    actual.append('"');
                    i++;
                } else {
                    entreComillas = !entreComillas;
                }
            } else if ((c == ',' || c == ';') && !entreComillas) {
                campos.add(actual.toString().trim());
                actual.setLength(0);
            } else {
                actual.append(c);
            }
        }
        campos.add(actual.toString().trim());
        return campos.toArray(new String[0]);
    }

    private String campoCsv(String valor) {
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }

    private String html(String valor) {
        return valor.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private String texto(Object valor) {
        return valor == null ? "" : valor.toString();
    }
}