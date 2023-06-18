package domainapp.modules.simple.dom.partido;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jdo.JDOQLTypedQuery;
import javax.jdo.annotations.NotPersistent;

import domainapp.modules.simple.dom.jugador.Jugador;
import domainapp.modules.simple.dom.jugador.JugadorServices;
import domainapp.modules.simple.dom.partido.reporte.EjecutarPartidoReporte;
import domainapp.modules.simple.dom.partido.types.Estados;
import domainapp.modules.simple.dom.partido.types.Horarios;
import domainapp.modules.simple.dom.partido.types.NumeroCancha;

import domainapp.modules.simple.dom.so.SimpleObject;

import net.sf.jasperreports.engine.JRException;

import org.apache.causeway.applib.annotation.Action;
import org.apache.causeway.applib.annotation.ActionLayout;
import org.apache.causeway.applib.annotation.DomainService;
import org.apache.causeway.applib.annotation.DomainServiceLayout;
import org.apache.causeway.applib.annotation.NatureOfService;
import org.apache.causeway.applib.annotation.PriorityPrecedence;
import org.apache.causeway.applib.annotation.Programmatic;
import org.apache.causeway.applib.annotation.PromptStyle;
import org.apache.causeway.applib.annotation.Publishing;
import org.apache.causeway.applib.annotation.SemanticsOf;
import org.apache.causeway.applib.query.Query;
import org.apache.causeway.applib.services.message.MessageService;
import org.apache.causeway.applib.services.repository.RepositoryService;
import org.apache.causeway.applib.value.Blob;
import org.apache.causeway.persistence.jdo.applib.services.JdoSupportService;

import lombok.RequiredArgsConstructor;

import domainapp.modules.simple.SimpleModule;

import static org.apache.causeway.applib.annotation.SemanticsOf.IDEMPOTENT;


@Named(SimpleModule.NAMESPACE + ".PartidoServices")
@DomainService(nature = NatureOfService.VIEW)
@DomainServiceLayout(named = "Partido", menuBar = DomainServiceLayout.MenuBar.PRIMARY)
@Priority(PriorityPrecedence.EARLY)
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PartidoServices {

    @Inject
    final RepositoryService repositoryService;
    final JdoSupportService jdoSupportService;
    final JugadorServices jugadorServices;
    final MessageService messageService;





    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR, cssClassFa = "fa-plus")
    public Partido crearPartido(final Horarios horario, final LocalDate dia, final String telefono, final double precio) {
        NumeroCancha numeroCancha = definirCancha(dia, horario);
        Jugador representante = jugadorServices.buscarJugador(telefono);
        return repositoryService.persist(Partido.crearTurno(horario, dia, numeroCancha, representante, precio));
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR, cssClassFa = "fa-plus")
    public Partido sacarTurno(final Horarios horario, final LocalDate dia, final String telefono) {
        Jugador representante = jugadorServices.buscarJugador(telefono);

        if (representante == null) {
            messageService.warnUser("NO EXISTE NINGUNA CUENTA ASOCIADA A ESE NUMERO DE TELEFONO");
            return null;
        }

        if (!hayPartido(telefono).isEmpty()) {
            messageService.warnUser("YA EXISTE UN PARTIDO RESERVADO A TU NOMBRE");
            return hayPartido(telefono).get(0);
        }

        NumeroCancha numeroCancha = definirCancha(dia, horario);
        return repositoryService.persist(Partido.pedirTurno(horario, dia, numeroCancha, representante));
    }


    @Action
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR, cssClassFa = "fa-search")
    public Partido buscarPartido(final Horarios horario, final LocalDate dia, final NumeroCancha numeroCancha) {
        return repositoryService.uniqueMatch(
                Query.named(Partido.class, Partido.NAMED_QUERY__FIND_BY_NAME_EXACT)
                        .withParameter("horario", horario)
                        .withParameter("dia", dia)
                        .withParameter("numeroCancha", numeroCancha)
        ).orElse(null);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR, cssClassFa = "fa-search")
    public List<Partido> buscarPartidoPorRepresentante(final String telefono) {
        Jugador representante = jugadorServices.buscarJugador(telefono);
        return repositoryService.allMatches(
                Query.named(Partido.class, Partido.NAMED_QUERY__FIND_BY_REPRESENTANTE)
                        .withParameter("representante", representante));
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR, cssClassFa = "fa-list")
    public List<Partido> verPartidos() {
        return repositoryService.allInstances(Partido.class);
    }


    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR, cssClassFa = "fa-search")
    public List<Partido> buscarPartidosPorEstados(final Estados estados) {
        return repositoryService.allMatches(
                Query.named(Partido.class, Partido.NAMED_QUERY__FIND_BY_ESTADO)
                        .withParameter("estados", estados));
    }


//    @Action(semantics = SemanticsOf.SAFE)
//    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR, cssClassFa = "fa-search")
    public List<Partido> hayPartido(final String telefono) {
        Jugador jugador = jugadorServices.buscarJugador(telefono);
        return (repositoryService.allMatches(Query.named(Partido.class, Partido.NAMED_QUERY__FIND_BY_ESTADO_AND_REPRESENTANTE)
                .withParameter("representante", jugador)
                .withParameter("estados", Estados.CONFIRMADO)
                .withParameter("estados2", Estados.ESPERA)));
    }

    public NumeroCancha definirCancha(final LocalDate dia, final Horarios horario) {
        NumeroCancha numeroCancha = NumeroCancha.Tres;
        if (buscarPartido(horario, dia, NumeroCancha.Uno) == null) numeroCancha = NumeroCancha.Uno;
        else if (buscarPartido(horario, dia, NumeroCancha.Dos) == null) numeroCancha = NumeroCancha.Dos;
        return numeroCancha;
    }


///////////////////////////////////////////////////////////////
/*                  REPORTE PARTIDOS                           */
////////////////////////////////////////////////////////////////
//REPORTE ARTICULO



//    @Programmatic
//    @Action(semantics = IDEMPOTENT,commandPublishing = Publishing.ENABLED,executionPublishing = Publishing.ENABLED)
////    ActionLayout(position = ActionLayout.Position.PANEL)
//    public EjecutarPartidoReporte ImprimirReporte(){
//        return this.ImprimirReporte();
//    }


    // @Programmatic
    // @Action(semantics = IDEMPOTENT,commandPublishing = Publishing.ENABLED,executionPublishing = Publishing.ENABLED)
    // public Blob generarReportePartido() throws JRException, IOException {
    //     List<Partido> partidos = new ArrayList<Partido>();
    //     EjecutarPartidoReporte ejecutarPartidoReporte = new EjecutarPartidoReporte();
    //     partidos = repositoryService.allInstances(Partido.class);
    //     return ejecutarPartidoReporte.ListadoPartidosPDF(partidos);
    // }
//    @Programmatic
//    @Action(semantics = IDEMPOTENT,commandPublishing = Publishing.ENABLED,executionPublishing = Publishing.ENABLED)
//    public Blob ImprimirReporte() throws JRException, IOException {
//        return generarReportePartido();
//    }



///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // public void ping() {
    //     JDOQLTypedQuery<SimpleObject> q = jdoSupportService.newTypesafeQuery(SimpleObject.class);
    //     final QPartido candidate = QPartido.candidate();
    //     q.range(0, 2);
    //     q.orderBy(candidate.horario.asc());
    //     q.executeList();

    // }

}
