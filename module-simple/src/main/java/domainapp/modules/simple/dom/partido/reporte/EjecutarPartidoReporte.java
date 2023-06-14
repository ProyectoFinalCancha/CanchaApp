package domainapp.modules.simple.dom.partido.reporte;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.causeway.applib.value.Blob;

import domainapp.modules.simple.dom.partido.Partido;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

//@Named(SimpleModule.NAMESPACE + ".EjecutarPartidoReporte")
//@DomainService(nature = NatureOfService.REST)
public class EjecutarPartidoReporte {
    public Blob ListadoPartidosPDF(List<Partido> partidos) throws JRException, IOException{

        List<RepoPartido> repoPartidos = new ArrayList<RepoPartido>();
        repoPartidos.add(new RepoPartido());

        for (Partido partido : partidos) {
            RepoPartido repoPartido = new RepoPartido(
                    partido.getDia(),
                    partido.getHorario(),
                    partido.getNumeroCancha(),
                    partido.getRepresentante(),
                    partido.getPrecio()
            );
            repoPartidos.add(repoPartido);
        }

        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(repoPartidos);
        return GenerarArchivoPDF("repoPartidos.jrxml", "ListadoPartidos.pdf", ds);
    }
    public Blob GenerarArchivoPDF(String archivoDesing, String nombreSalida, JRBeanCollectionDataSource ds) throws JRException, IOException{

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(archivoDesing);
        JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ds", ds);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, ds);
        byte[] contentBytes = JasperExportManager.exportReportToPdf(jasperPrint);

        return new Blob(nombreSalida, "application/pdf", contentBytes);

    }

}

    /*
//    try{

    JasperReport reportePartido = (JasperReport) JRLoader.loadObject(new File("report/Partido.jasper"));


    private List<Partido> partidoReport;
    JRBeanCollectionDataSource collectionDataSource = new JRBeanCollectionDataSource(partidoReport);

    JasperPrint print =  JasperFillManager.fillReport("informe",null,collectionDataSource);

    JasperViewer visor = new JasperViewer(print);


    public ReportePartido(List<Partido> partido) throws JRException {
        List<Partido> partidoReport = new ArrayList<Partido>();
        partidoReport.add(new Partido());
//        this.partido = partido;


        visor.setVisible(true);
    }
*/


