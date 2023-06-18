package domainapp.modules.simple.dom.partido;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import domainapp.modules.simple.dom.jugador.Jugador;
import domainapp.modules.simple.dom.partido.reporte.EjecutarPartidoReporte;
import domainapp.modules.simple.dom.partido.reporte.Reportes;
import domainapp.modules.simple.dom.partido.reporte.RepoPartido;
import domainapp.modules.simple.dom.partido.types.Estados;

import domainapp.modules.simple.dom.partido.types.Horarios;
import domainapp.modules.simple.dom.partido.types.NumeroCancha;

import domainapp.modules.simple.dom.partido.PartidoServices;

import org.apache.causeway.applib.annotation.Action;
import org.apache.causeway.applib.annotation.ActionLayout;
import org.apache.causeway.applib.annotation.DomainObject;
import org.apache.causeway.applib.annotation.DomainObjectLayout;
import org.apache.causeway.applib.annotation.Editing;
import org.apache.causeway.applib.annotation.Optionality;
import org.apache.causeway.applib.annotation.Programmatic;
import org.apache.causeway.applib.annotation.Property;
import org.apache.causeway.applib.annotation.PropertyLayout;
import org.apache.causeway.applib.annotation.Publishing;
import org.apache.causeway.applib.annotation.TableDecorator;
import org.apache.causeway.applib.annotation.Title;
import org.apache.causeway.applib.jaxb.PersistentEntityAdapter;
import org.apache.causeway.applib.layout.LayoutConstants;
import org.apache.causeway.applib.services.message.MessageService;
import org.apache.causeway.applib.services.repository.RepositoryService;
import org.apache.causeway.applib.services.title.TitleService;
import org.apache.causeway.applib.value.Blob;

import static org.apache.causeway.applib.annotation.SemanticsOf.IDEMPOTENT;
import static org.apache.causeway.applib.annotation.SemanticsOf.IDEMPOTENT_ARE_YOU_SURE;
import static org.apache.causeway.applib.annotation.SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import net.sf.jasperreports.engine.JRException;
import domainapp.modules.simple.SimpleModule;


@PersistenceCapable(
        schema = SimpleModule.SCHEMA,
        identityType = IdentityType.DATASTORE)
@Unique(
        name = "Partido__name__UNQ", members = {"horario", "dia", "numeroCancha"}
)
@Queries({

        @Query(
                name = Partido.NAMED_QUERY__FIND_BY_NAME_EXACT,
                value = "SELECT " +
                        "FROM domainapp.modules.simple.dom.partido.Partido " +
                        "WHERE horario == :horario && dia == :dia && numeroCancha == :numeroCancha"
        ),

        @Query(
                name = Partido.NAMED_QUERY__FIND_BY_ESTADO,
                value = "SELECT " +
                        "FROM domainapp.modules.simple.dom.partido.Partido " +
                        "WHERE estados == :estados"
        ),
        @Query(
                name = Partido.NAMED_QUERY__FIND_BY_REPRESENTANTE,
                value = "SELECT " +
                        "FROM domainapp.modules.simple.dom.partido.Partido " +
                        "WHERE representante == :representante"
        ),
        @Query(
                name = Partido.NAMED_QUERY__FIND_BY_ESTADO_AND_REPRESENTANTE,
                value = "SELECT " +
                        "FROM domainapp.modules.simple.dom.partido.Partido " +
                        "WHERE representante == :representante && (estados == :estados || estados == :estados2)"
        ),

})
@DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "id")
@Version(strategy = VersionStrategy.DATE_TIME, column = "version")
@Named(SimpleModule.NAMESPACE + ".Partido")
@DomainObject(entityChangePublishing = Publishing.ENABLED)
@DomainObjectLayout(tableDecorator = TableDecorator.DatatablesNet.class)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
@ToString(onlyExplicitlyIncluded = true)
public class Partido implements Comparable<Partido> {


    static final String NAMED_QUERY__FIND_BY_NAME_EXACT = "Partido.findByNameExact";
    static final String NAMED_QUERY__FIND_BY_ESTADO = "Partido.findByEstado";
    static final String NAMED_QUERY__FIND_BY_REPRESENTANTE = "Partido.findByRepresentante";

    static final String NAMED_QUERY__FIND_BY_ESTADO_AND_REPRESENTANTE = "Partido.findByEstados";

    public static Partido crearTurno(final Horarios horario, final LocalDate dia, final NumeroCancha numeroCancha, final Jugador representante, final double precio) {
        val partido = new Partido();
            partido.setDia(dia);
            partido.setHorario(horario);
            partido.setNumeroCancha(numeroCancha);
            partido.setRepresentante(representante);
            partido.setEstados(Estados.CONFIRMADO);
            partido.setPrecio(precio);
            return partido;
    }

    public static Partido pedirTurno(final Horarios horario, final LocalDate dia, final NumeroCancha numeroCancha, final Jugador representante) {
            val partido = new Partido();
            partido.setDia(dia);
            partido.setHorario(horario);
            partido.setNumeroCancha(numeroCancha);
            partido.setRepresentante(representante);
            partido.setEstados(Estados.ESPERA);
            partido.setPrecio(0);
            return partido;
    }


    @Inject @NotPersistent RepositoryService repositoryService;
    @Inject @NotPersistent TitleService titleService;
    @Inject @NotPersistent MessageService messageService;

    public String iconName() {
        return this.getEstados().name().toLowerCase();
    }



    //VER IC0NMANE

    @Title
    @Getter @Setter @ToString.Include
    @PropertyLayout(fieldSetId = LayoutConstants.FieldSetId.IDENTITY, sequence = "1")
    private LocalDate dia;

    @Title
    @Getter @Setter
    @PropertyLayout(fieldSetId = LayoutConstants.FieldSetId.DETAILS, sequence = "2")
    private Horarios horario;

    @Title
    @Getter @Setter
    @PropertyLayout(fieldSetId = LayoutConstants.FieldSetId.DETAILS, sequence = "3")
    private NumeroCancha numeroCancha;

    @Property(editing = Editing.ENABLED)
    @Getter @Setter
    @PropertyLayout(fieldSetId = LayoutConstants.FieldSetId.DETAILS, sequence = "4")
    private double precio;

    @Title
    @Getter @Setter
    @PropertyLayout(fieldSetId = LayoutConstants.FieldSetId.DETAILS, sequence = "5")
    private Estados estados;

    @Property(optionality = Optionality.OPTIONAL)
    @PropertyLayout(fieldSetId = LayoutConstants.FieldSetId.DETAILS, sequence = "6")
    @Getter@Setter
    @Column(allowsNull = "true")
    private Jugador representante;


//    @Property(optionality = Optionality.OPTIONAL, editing = Editing.ENABLED)
//    @PropertyLayout(fieldSetId = LayoutConstants.FieldSetId.DETAILS, sequence = "6")
//    @Column(allowsNull = "true")
//    @Persistent(mappedBy = "partido", defaultFetchGroup = "true")
//    @Getter @Setter
//    private List<Jugador> jugadores;

//    public List<Jugador> autoCompleteJugador(@MinLength(4) final String search){
//        return jugadorServices.verJugadores();
//    }
//    @Action()
//    @ActionLayout(associateWith = "jugadores", position = ActionLayout.Position.PANEL)
//    public void añadirJugador(String telefono){
//         this.jugadores.add(jugadorServices.buscarJugador(telefono));
//    }

    static final String PROHIBITED_CHARACTERS = "&%$!";

    @Action(semantics = IDEMPOTENT, commandPublishing = Publishing.ENABLED, executionPublishing = Publishing.ENABLED)
    @ActionLayout(position = ActionLayout.Position.PANEL)
    public Partido completar() {
        setEstados(Estados.COMPLETADO);
        return this;
    }
    @Action(semantics = IDEMPOTENT, commandPublishing = Publishing.ENABLED, executionPublishing = Publishing.ENABLED)
    @ActionLayout(position = ActionLayout.Position.PANEL)
    public Partido confirmar() {
        setEstados(Estados.CONFIRMADO);
        return this;
    }

    @Action(semantics = IDEMPOTENT_ARE_YOU_SURE, commandPublishing = Publishing.ENABLED, executionPublishing = Publishing.ENABLED)
    @ActionLayout(position = ActionLayout.Position.PANEL)
    public Partido rechazar() {
        setEstados(Estados.RECHAZADO);
        return this;
    }



    @Action(semantics = NON_IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(
            fieldSetId = LayoutConstants.FieldSetId.IDENTITY,
            position = ActionLayout.Position.PANEL,
            describedAs = "Deletes this object from the persistent datastore")
    public List<Partido> darDeBaja() {
        final String title = titleService.titleOf(this);
        messageService.informUser(String.format("'%s' deleted", title));
        repositoryService.removeAndFlush(this);
        return repositoryService.allInstances(Partido.class);
    }

    private final static Comparator<Partido> comparator =
            Comparator.comparing(Partido::getHorario);




///////////////////////////////////////////////////////////////
    /*                  REPORTE PARTIDOS                           */
////////////////////////////////////////////////////////////////


//     @Action(semantics = IDEMPOTENT,commandPublishing = Publishing.ENABLED,executionPublishing = Publishing.ENABLED)
// //    ActionLayout(position = ActionLayout.Position.PANEL)
//     public EjecutarPartidoReporte ImprimirReporte(){
//         return this.ImprimirReporte();

// //        return
//     }


@Programmatic
    @Action(semantics = IDEMPOTENT,commandPublishing = Publishing.ENABLED,executionPublishing = Publishing.ENABLED)
    public Blob generarReportePartido() throws JRException, IOException {
        List<Partido> partidos = new ArrayList<Partido>();
        EjecutarPartidoReporte ejecutarPartidoReporte = new EjecutarPartidoReporte();
        partidos = repositoryService.allInstances(Partido.class);
        return ejecutarPartidoReporte.ListadoPartidosPDF(partidos);
    }

/////////////////////////////////////////////////////////////////////////////

    @Override
    public int compareTo(final Partido other) {
        return comparator.compare(this, other);
    }

//    @PdfJsViewer
//    @Getter @Setter
//    @Persistent(defaultFetchGroup = "false", columns = {
//            @Column(name = "attachment_name"),
//            @Column(name = "attachment_mimetype"),
//            @Column(name = "attachment_bytes")
//    })
//    @Property()
//    @PropertyLayout(fieldSetId = "content", sequence = "1")
//    private Blob attachment;

}
