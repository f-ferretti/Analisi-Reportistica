package it.unimol.report_management.dto.student;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlanItemDTO {
    @JsonProperty("codiceCorso") // lo stub usa questo nome
    private String codice;

    private String nome;
    private int cfu;

    @JsonProperty("anno") // lo stub usa "anno"
    private Integer annoCorso;

    private Boolean obbligatorio;

    // getter/setter...
    public String getCodice() { return codice; }
    public void setCodice(String codice) { this.codice = codice; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getCfu() { return cfu; }
    public void setCfu(int cfu) { this.cfu = cfu; }
    public Integer getAnnoCorso() { return annoCorso; }
    public void setAnnoCorso(Integer annoCorso) { this.annoCorso = annoCorso; }
    public Boolean getObbligatorio() { return obbligatorio; }
    public void setObbligatorio(Boolean obbligatorio) { this.obbligatorio = obbligatorio; }
}