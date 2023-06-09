package domainapp.modules.simple.dom.jugador;

import java.util.List;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jdo.JDOQLTypedQuery;


import org.apache.causeway.applib.annotation.Action;
import org.apache.causeway.applib.annotation.ActionLayout;
import org.apache.causeway.applib.annotation.DomainService;
import org.apache.causeway.applib.annotation.DomainServiceLayout;
import org.apache.causeway.applib.annotation.NatureOfService;
import org.apache.causeway.applib.annotation.PriorityPrecedence;
import org.apache.causeway.applib.annotation.PromptStyle;
import org.apache.causeway.applib.annotation.SemanticsOf;
import org.apache.causeway.applib.query.Query;
import org.apache.causeway.applib.services.repository.RepositoryService;
import org.apache.causeway.persistence.jdo.applib.services.JdoSupportService;

import lombok.RequiredArgsConstructor;

import domainapp.modules.simple.SimpleModule;

@Named(SimpleModule.NAMESPACE + ".JugadorServices")
@DomainService(nature = NatureOfService.VIEW)
@DomainServiceLayout(named = "Jugador",menuBar = DomainServiceLayout.MenuBar.PRIMARY)
@Priority(PriorityPrecedence.EARLY)
@RequiredArgsConstructor(onConstructor_ = {@Inject} )
public class JugadorServices {

    final RepositoryService repositoryService;
    final JdoSupportService jdoSupportService;




    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR,cssClassFa = "fa-plus")
    public Jugador crearJugador(
            final String nombre,final String apellido,final String telefono,final String mail,final String password) {
        return repositoryService.persist(Jugador.crearJugador(nombre,apellido,telefono,mail,password));
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR,cssClassFa = "fa-search")

    public Jugador buscarJugador(final String telefono) {
        return repositoryService.firstMatch(
                    Query.named(Jugador.class, Jugador.NAMED_QUERY__FIND_BY_TEL)
                        .withParameter("telefono", telefono))
                .orElse(null);
    }

//    public List<Jugador> findByPartido(Partido partido){
//        return partido.getJugadores();
//    }
//    public Partido verPartido(){
//        Jugador representante = buscarJugador();
//        return partidoServices.buscarPartidoXRepresentante(representante);
//    }
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR,cssClassFa ="fa-list ")
    public List<Jugador> verJugadores() {
        return repositoryService.allInstances(Jugador.class);
    }

    public void ping() {
        JDOQLTypedQuery<Jugador> q = jdoSupportService.newTypesafeQuery(Jugador.class);
        final QJugador candidate = QJugador.candidate();
        q.range(0,2);
        q.orderBy(candidate.telefono.asc());
        q.executeList();
    }
}
