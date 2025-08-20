package it.unimol.report_management.dto.course;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Iscrizione ad un corso")
public class EnrollmentDTO {
    private String codiceCorso;
    private String matricola;
    @Schema(description="Anno accademico di frequenza", example="2023")
    private Integer aaFrequenza;

    public String getCodiceCorso() { return codiceCorso; }
    public void setCodiceCorso(String codiceCorso) { this.codiceCorso = codiceCorso; }
    public String getMatricola() { return matricola; }
    public void setMatricola(String matricola) { this.matricola = matricola; }
    public Integer getAaFrequenza() { return aaFrequenza; }
    public void setAaFrequenza(Integer aaFrequenza) { this.aaFrequenza = aaFrequenza; }
}
