package rinseg.asistp.com.models;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Carlos Ramos on 26/12/2016.
 */
public class InspeccionRO extends RealmObject {
    private int id;
    private String area;
    private Date date;
    private String dateString;

    private int companyId;
    private CompanyRO company;

    private int typeInspectionId;
    private TypeInspection typeInspection;

    private Date dateClose;
    private String dateCloseString;

    private int userId;


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
}
