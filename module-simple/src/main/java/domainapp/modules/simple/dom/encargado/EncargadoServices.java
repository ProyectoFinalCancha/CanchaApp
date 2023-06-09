package domainapp.modules.simple.dom.encargado;

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

@Named(SimpleModule.NAMESPACE + ".EncargadoServices")
@DomainService(nature = NatureOfService.VIEW)
@DomainServiceLayout(named = "Encargado", menuBar = DomainServiceLayout.MenuBar.PRIMARY)
@Priority(PriorityPrecedence.EARLY)
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class EncargadoServices {

    final RepositoryService repositoryService;
    final JdoSupportService jdoSupportService;


    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR, cssClassFa = "fa-plus")
    public Encargado crearEncargado(final String nombre, final String apellido, final String dni, final String localidad) {
        return repositoryService.persist(Encargado.withName(nombre, apellido, dni, localidad));
    }


    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR, cssClassFa = "fa-search")
    public Encargado buscarEncargado(final String nombre) {
        return repositoryService.firstMatch(
                        Query.named(Encargado.class, Encargado.NAMED_QUERY__FIND_BY_NAME_EXACT)
                                .withParameter("nombre", nombre))
                .orElse(null);
    }


    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR,cssClassFa = "fa-list")
    public List<Encargado> verEncargados() {
        return repositoryService.allInstances(Encargado.class);
    }

    public void ping() {
        JDOQLTypedQuery<Encargado> q = jdoSupportService.newTypesafeQuery(Encargado.class);
        final QEncargado candidate = QEncargado.candidate();
        q.range(0, 2);
        q.orderBy(candidate.nombre.asc());
        q.executeList();

    }

}
