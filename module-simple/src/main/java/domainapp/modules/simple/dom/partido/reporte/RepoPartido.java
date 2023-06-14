package domainapp.modules.simple.dom.partido.reporte;

import java.time.LocalDate;

import domainapp.modules.simple.dom.jugador.Jugador;
import domainapp.modules.simple.dom.partido.types.Horarios;
import domainapp.modules.simple.dom.partido.types.NumeroCancha;

@lombok.Getter @lombok.Setter
public class RepoPartido {

    private LocalDate dia;
    private Horarios horario;
    private NumeroCancha numeroCancha;
    private Jugador representante;
    private double precio;
//      private String dia;
//    private String horario;
//    private String numeroCancha;
//    private String representante;
//    private String precio;


    public RepoPartido(LocalDate dia, Horarios horario, NumeroCancha numeroCancha, Jugador representante, double precio){
        this.dia = dia;
        this.horario = horario;
        this.numeroCancha = numeroCancha;
        this.representante = representante;
        this.precio = precio;

    }
    public RepoPartido() { }
        public LocalDate getDia() {
            return this.dia;
        }
        public Horarios getHorario () {
            return horario;
        }
        public NumeroCancha numeroCancha () {
            return numeroCancha;
        }
        public Jugador getRepresentante () {
            return representante;
        }
        public double getPrecio() {
            return precio;
        }

}
