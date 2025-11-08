package com.tusistema.sistemaventas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app") // Busca propiedades que empiecen con "app."
public class AppProperties {

    private String uploadDir = "./uploads"; // Valor por defecto si no se configura
    private String currencySymbol = "$"; // Valor por defecto para el símbolo de moneda

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

}