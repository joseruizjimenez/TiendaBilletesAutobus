package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

/**
 * Factura de compra de billetes de autobus
 * 
 * @param id identificador de la factura
 * @param CIF de la empresa
 * @param nombreComprador de la tarjeta de credito
 * @param DNI dni del dueño de la tarjeta
 * @param email para enviar la factura
 * @param mvl para enviar el localizador
 * @param numTarjeta numero de la tarjeta de credito
 * @param calle direccion de la factura
 * @param poblacion relativo a la direccion
 * @param provincia relativo a la direccion
 * @param codPosta relativo a la direccion
 * @param transactionDate fecha de la transacción
 * @param total precio total de la compra
 */
public class Factura implements Serializable {
    private UUID id = null;
    private String CIF = null;
    private String nombreComprador = null;
    private String DNI = null;
    private String email = null;
    private String mvl = null;
    private String numTarjeta = null;
    private String calle = null;
    private String poblacion = null;
    private String provincia = null;
    private String codPostal = null;
    private Calendar transactionDate = null;
    private BigDecimal total = null;
    
    public Factura() {
        this.id = UUID.randomUUID();
        this.transactionDate = Calendar.getInstance();
        this.transactionDate.clear();
        this.transactionDate.setTimeInMillis(System.currentTimeMillis());
    }
    
    public Factura(String id){
        this.id = UUID.fromString(id);
        this.transactionDate = Calendar.getInstance();
    }
    
    public Factura(UUID id){
        this.id = id;
        this.transactionDate = Calendar.getInstance();
    }
    
    /**
     * Constructor de una nueva factura
     */
    public Factura(String CIF, String nombreComprador, String DNI, String email,
            String mvl, String numTarjeta, String calle, String poblacion,
            String provincia, String codPostal, BigDecimal total) {
        this();
        this.CIF = CIF;
        this.nombreComprador = nombreComprador;
        this.DNI = DNI;
        this.email = email;
        this.mvl = mvl;
        this.numTarjeta = numTarjeta;
        this.calle = calle;
        this.poblacion = poblacion;
        this.provincia = provincia;
        this.codPostal = codPostal;
        //this.transactionDate = transactionDate;
        this.total = total;       
    }
    
    /**
     * Constructor para recrear facturas ya existentes
     */
    public Factura(String id, String CIF, String nombreComprador, String DNI,
            String email, String mvl, String numTarjeta, String calle,
            String poblacion, String provincia, String codPostal,
            String transactionDate, BigDecimal total)
            throws ParseException {
        this(id);
        this.CIF = CIF;
        this.nombreComprador = nombreComprador;
        this.DNI = DNI;
        this.email = email;
        this.mvl = mvl;
        this.numTarjeta = numTarjeta;
        this.calle = calle;
        this.poblacion = poblacion;
        this.provincia = provincia;
        this.codPostal = codPostal;
        this.total = total;
        this.transactionDate.clear();
        DateFormat date = DateFormat.getDateTimeInstance();
        this.transactionDate.setTime(date.parse(transactionDate));    
    }
    
    @Override
    public boolean equals(Object object) {
        if(object instanceof Factura) {
            Factura factura = (Factura) object;
            return (factura != null) && (this.getId().equals(factura.getId()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public UUID getId() {
        return id;
    }
    
    public String getIdAsString() {
        return id.toString();
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Calendar getTransactionDate() {
        return transactionDate;
    }
    
    public long getTransactionDateInMillis() {
        return transactionDate.getTimeInMillis();
    }
    
    public String getTransactionDateAsString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(transactionDate.getTime());
    }

    public void setTransactionDate(Calendar transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    public void setTransactionDate(String timestamp) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.transactionDate.setTime(dateFormat.parse(timestamp));
    }

    public BigDecimal getTotal() {
        return total;
    }
    
    /**
     * @return string con el precio total sin formatear
     */
    public String getTotalAsString() {
        return total.toString();
    }
    
    /**
     * @return string con el precio total formateado correctamente, 
     * añadiendo una separaciones en los miles, millones... y añadiendo EUR
     */
    public String getTotalAsFormattedString() {
        NumberFormat n = NumberFormat.getCurrencyInstance(Locale.FRANCE); 
        double doublePrice = getTotal().doubleValue();
        return n.format(doublePrice);
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public void setTotal(String total) {
        this.total = new BigDecimal(total);
    }

    /**
     * @return the CIF
     */
    public String getCIF() {
        return CIF;
    }

    /**
     * @param CIF the CIF to set
     */
    public void setCIF(String CIF) {
        this.CIF = CIF;
    }

    /**
     * @return the nombreComprador
     */
    public String getNombreComprador() {
        return nombreComprador;
    }

    /**
     * @param nombreComprador the nombreComprador to set
     */
    public void setNombreComprador(String nombreComprador) {
        this.nombreComprador = nombreComprador;
    }

    /**
     * @return the DNI
     */
    public String getDNI() {
        return DNI;
    }

    /**
     * @param DNI the DNI to set
     */
    public void setDNI(String DNI) {
        this.DNI = DNI;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the mvl
     */
    public String getMvl() {
        return mvl;
    }

    /**
     * @param mvl the mvl to set
     */
    public void setMvl(String mvl) {
        this.mvl = mvl;
    }

    /**
     * @return the numTarjeta
     */
    public String getNumTarjeta() {
        return numTarjeta;
    }

    /**
     * @param numTarjeta the numTarjeta to set
     */
    public void setNumTarjeta(String numTarjeta) {
        this.numTarjeta = numTarjeta;
    }

    /**
     * @return the calle
     */
    public String getCalle() {
        return calle;
    }

    /**
     * @param calle the calle to set
     */
    public void setCalle(String calle) {
        this.calle = calle;
    }

    /**
     * @return the poblacion
     */
    public String getPoblacion() {
        return poblacion;
    }

    /**
     * @param poblacion the poblacion to set
     */
    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * @return the provincia
     */
    public String getProvincia() {
        return provincia;
    }

    /**
     * @param provincia the provincia to set
     */
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    /**
     * @return the codPostal
     */
    public String getCodPostal() {
        return codPostal;
    }

    /**
     * @param codPostal the codPostal to set
     */
    public void setCodPostal(String codPostal) {
        this.codPostal = codPostal;
    }

}

