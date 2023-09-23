package domainapp.modules.simple.dom.jugador;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jdo.JDOQLTypedQuery;

import javax.jdo.annotations.NotPersistent;
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
@DomainServiceLayout(named = "Jugador")
@RequiredArgsConstructor(onConstructor_ = {@Inject} )
public class JugadorServices {

    @Inject @NotPersistent RepositoryService repositoryService;


    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR)
    public Jugador crearJugador(
            final String nombre, final String apellido, final String telefono, final String mail, final String password, final LocalDate fechaDeNacimiento) {
        return repositoryService.persist(Jugador.crearJugador(nombre,apellido,telefono,mail,password,fechaDeNacimiento));
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR,cssClassFa = "fa-search")

    public Jugador buscarJugador(final String telefono) {
        return repositoryService.firstMatch(
                    Query.named(Jugador.class, Jugador.NAMED_QUERY__FIND_BY_TEL)
                        .withParameter("telefono", telefono))
                .orElse(null);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR,cssClassFa = "fa-search")
    public int getEdad(String telefono){
        Jugador jugador = buscarJugador(telefono);
        LocalDate fechaNacimiento = jugador.getFechaNacimiento();
         int edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();
         return edad;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR,cssClassFa ="fa-list ")
    public List<Jugador> verJugadores() {
        return repositoryService.allInstances(Jugador.class);
    }


}
