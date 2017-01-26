package rinseg.asistp.com.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Carlos Ramos on 26/12/2016.
 * Clase inspección
 */

public class InspeccionRO extends RealmObject {

    private int id;

    private String area;

    private Date date;

    private String dateString;

    @SerializedName("company_id")
    private int companyId;

    private CompanyRO company;

    private String companyString;

    @SerializedName("inspection_type_id")
    private int typeInspectionId;

    private TypeInspection typeInspection;

    private Date dateClose;

    @SerializedName("date_close")
    private String dateCloseString;

    private int userId;

    private String tmpId;

    @SerializedName("inspector")
    public RealmList<InspectorRO> listaInspectores;

    @SerializedName("responsible")
    public RealmList<InspectorRO> listaResponsables;

    @SerializedName("inspection_item")
    public RealmList<IncidenciaRO> listaIncidencias;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public CompanyRO getCompany() {
        return company;
    }

    public void setCompany(CompanyRO company) {
        this.company = company;
    }

    public int getTypeInspectionId() {
        return typeInspectionId;
    }

    public void setTypeInspectionId(int typeInspectionId) {
        this.typeInspectionId = typeInspectionId;
    }

    public TypeInspection getTypeInspection() {
        return typeInspection;
    }

    public void setTypeInspection(TypeInspection typeInspection) {
        this.typeInspection = typeInspection;
    }

    public Date getDateClose() {
        return dateClose;
    }

    public void setDateClose(Date dateClose) {
        this.dateClose = dateClose;
    }

    public String getDateCloseString() {
        return dateCloseString;
    }

    public void setDateCloseString(String dateCloseString) {
        this.dateCloseString = dateCloseString;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTmpId() {
        return tmpId;
    }

    public void setTmpId(String tmpId) {
        this.tmpId = tmpId;
    }

    public String getCompanyString() {
        return companyString;
    }

    public void setCompanyString(String companyString) {
        this.companyString = companyString;
    }

    @Override
    public String toString() {
        String inspectores = "[";
        for ( InspectorRO i : listaInspectores ) {
            inspectores += i.toString();
        }
        inspectores += "]";

        String responsables = "[";
        for ( InspectorRO i : listaResponsables ) {
            responsables += i.toString();
        }
        responsables += "]";

        String incidencias = "[";
        for ( IncidenciaRO i : listaIncidencias ) {
            incidencias += i.toString();
        }
        incidencias += "]";


        return "InspeccionRO{" +
                "id=" + id +
                ", area='" + area + '\'' +
                ", date=" + date +
                ", dateString='" + dateString + '\'' +
                ", companyId=" + companyId +
                ", company=" + company +
                ", companyString='" + companyString + '\'' +
                ", typeInspectionId=" + typeInspectionId +
                ", typeInspection=" + typeInspection +
                ", dateClose=" + dateClose +
                ", dateCloseString='" + dateCloseString + '\'' +
                ", userId=" + userId +
                ", tmpId='" + tmpId + '\'' +
                ", listaInspectores=" + inspectores +
                ", listaResponsables=" + responsables +
                ", listaIncidencias=" + incidencias +
                '}';
    }
}
