package it.unimol.report_management.dto.student;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "Esito d'esame")
public class ExamResultDTO {
    @Schema(example = "ST101")
    private String codiceCorso;
    @Schema(example = "Programmazione 1")
    private String nomeCorso;
    @Schema(example = "9")
    private int cfu;
    @Schema(example = "28")
    private int voto; // 18..30
    private boolean lode;
    private LocalDate data;
    @Schema(description = "Anno accademico di superamento", example = "2024")
    private Integer aaSuperamento;
    @Schema(description = "Anno accademico di frequenza", example = "2023")
    private Integer aaFrequenza;

    public ExamResultDTO() {}

    public String getCodiceCorso() { return codiceCorso; }
    public void setCodiceCorso(String codiceCorso) { this.codiceCorso = codiceCorso; }
    public String getNomeCorso() { return nomeCorso; }
    public void setNomeCorso(String nomeCorso) { this.nomeCorso = nomeCorso; }
    public int getCfu() { return cfu; }
    public void setCfu(int cfu) { this.cfu = cfu; }
    public int getVoto() { return voto; }
    public void setVoto(int voto) { this.voto = voto; }
    public boolean isLode() { return lode; }
    public void setLode(boolean lode) { this.lode = lode; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public Integer getAaSuperamento() { return aaSuperamento; }
    public void setAaSuperamento(Integer aaSuperamento) { this.aaSuperamento = aaSuperamento; }
    public Integer getAaFrequenza() { return aaFrequenza; }
    public void setAaFrequenza(Integer aaFrequenza) { this.aaFrequenza = aaFrequenza; }
}
