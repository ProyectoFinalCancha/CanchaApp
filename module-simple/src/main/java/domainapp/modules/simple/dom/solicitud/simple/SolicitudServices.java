package domainapp.modules.simple.dom.solicitud.simple;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jdo.annotations.NotPersistent;

import org.apache.causeway.applib.annotation.Action;
import org.apache.causeway.applib.annotation.ActionLayout;
import org.apache.causeway.applib.annotation.DomainService;
import org.apache.causeway.applib.annotation.DomainServiceLayout;
import org.apache.causeway.applib.annotation.NatureOfService;
import org.apache.causeway.applib.annotation.PromptStyle;
import org.apache.causeway.applib.annotation.SemanticsOf;
import org.apache.causeway.applib.query.Query;
import org.apache.causeway.applib.services.repository.RepositoryService;

import lombok.RequiredArgsConstructor;

import domainapp.modules.simple.SimpleModule;
import domainapp.modules.simple.dom.jugador.Jugador;
import domainapp.modules.simple.dom.jugador.JugadorServices;
import domainapp.modules.simple.dom.partido.PartidoServices;
import domainapp.modules.simple.dom.partido.types.Estados;
import domainapp.modules.simple.dom.partido.types.Horarios;
import domainapp.modules.simple.dom.partido.types.NumeroCancha;


@Named(SimpleModule.NAMESPACE + ".SolicituService")
@DomainService(nature = NatureOfService.VIEW)
@DomainServiceLayout(named = "Solicitud")
@RequiredArgsConstructor(onConstructor_ = {@Inject} )
public class SolicitudServices {

    @Inject JugadorServices jugadorServices;
    @Inject PartidoServices partidoServices;
    @Inject @NotPersistent RepositoryService repositoryService;


    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR)
    public Solicitud crearSolicitud(final String diaString, final String telefono, final String horarioSting) {


        LocalDate dia = LocalDate.parse(diaString);
        Horarios horario = Horarios.valueOf(horarioSting);

        //Precio de la canchaaaa
        Double precio = 0.0;



        Jugador jugador = jugadorServices.buscarJugador(telefono);

        NumeroCancha numeroCancha = partidoServices.definirCancha(diaString, horarioSting);

        Solicitud solicitud = haySolicitud(horario, dia, numeroCancha, precio);
        solicitud.getJugadores().add(jugador);


        //Incrementar Jugadores despues de testear
        if (solicitud.getJugadores().size() > 2) {
            partidoServices.crearPartido(horarioSting, diaString, solicitud.getJugadores().get(0).getTelefono(), precio);
            repositoryService.removeAndFlush(solicitud);
            return null;
        } else {
            return repositoryService.persist(solicitud);
        }
    }


    public Solicitud haySolicitud(final Horarios horario, final LocalDate dia, final NumeroCancha numeroCancha, final Double precio) {
        List<Jugador> jugadores = new ArrayList<>();
        return repositoryService.uniqueMatch(
                Query.named(Solicitud.class, Solicitud.NAMED_QUERY__FIND_BY_NAME_EXACT)
                        .withParameter("horario", horario)
                        .withParameter("dia", dia)
                        .withParameter("numeroCancha", numeroCancha)
        ).orElse(
                Solicitud.crearSolicitud(dia, horario, numeroCancha, precio, Estados.MATCHMAKING, jugadores)
        );
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR)
    public List<Solicitud> verSolicitudes() {
        return repositoryService.allInstances(Solicitud.class);
    }


    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR)
    public List<Horarios> horariosRestringidos(String diaString) {

        List<Horarios> horariosRestringidos = new ArrayList<>();

        for (Horarios hora : Horarios.values()) {
            if (partidoServices.buscarPartido(String.valueOf(hora), diaString, "TRES") == null) {
                horariosRestringidos.add(hora);
            }
        }
            return horariosRestringidos;
    }





}

