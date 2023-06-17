package domainapp.modules.simple.dom.partido.reporte;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.causeway.applib.annotation.Action;
import org.apache.causeway.applib.annotation.ActionLayout;
import org.apache.causeway.applib.annotation.DomainService;
import org.apache.causeway.applib.annotation.DomainServiceLayout;
import org.apache.causeway.applib.annotation.NatureOfService;
import org.apache.causeway.applib.annotation.SemanticsOf;
import org.apache.causeway.applib.services.repository.RepositoryService;
import org.apache.causeway.applib.value.Blob;

import domainapp.modules.simple.dom.partido.PartidoServices;
import net.sf.jasperreports.engine.JRException;


@DomainService(nature = NatureOfService.VIEW)
@DomainServiceLayout(named = "Partido", menuBar = DomainServiceLayout.MenuBar.SECONDARY)
public class Reportes {



    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout( named = "Exportar Partidos PDF", sequence = "1")
    public Blob generarReportePartido() throws JRException, IOException {
        return partidoServices.generarReportePartido();

    }


    @Inject RepositoryService repositoryService;
    @Inject PartidoServices partidoServices;
}
