package it.unimol.report_management.dto.student;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Anagrafica studente")
public class StudentDTO {
    @Schema(example = "123456")
    private String matricola;
    @Schema(example = "Mario Rossi")
    private String nome;
    @Schema(example = "2023")
    private int coorte;
    private List<PlanItemDTO> pianoStudio;

    public StudentDTO() {}
    public StudentDTO(String matricola, String nome, int coorte, List<PlanItemDTO> pianoStudio) {
        this.matricola = matricola; this.nome = nome; this.coorte = coorte; this.pianoStudio = pianoStudio;
    }
    public String getMatricola() { return matricola; }
    public void setMatricola(String matricola) { this.matricola = matricola; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getCoorte() { return coorte; }
    public void setCoorte(int coorte) { this.coorte = coorte; }
    public List<PlanItemDTO> getPianoStudio() { return pianoStudio; }
    public void setPianoStudio(List<PlanItemDTO> pianoStudio) { this.pianoStudio = pianoStudio; }
}
